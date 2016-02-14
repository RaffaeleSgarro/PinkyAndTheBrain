package pinkyandthebrain.debugger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pinkyandthebrain.*;

public class VisualDebugger extends Application implements Ticker, TurnListener {

    public static void main(String... args) {
        launch(VisualDebugger.class, args);
    }

    private final Slider currentTurn = new Slider();
    private final SimulationCanvas canvas = new SimulationCanvas();
    private final Button start = new Button("Start");
    private final Button pause = new Button("Pause");

    private volatile boolean paused;
    private volatile int turnsPerSecond = 500;

    private long lastTickNanos = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new Group(), 800, 600);

        HBox controls = new HBox();
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().add(start);
        controls.getChildren().add(pause);
        controls.getChildren().add(currentTurn);
        HBox.setHgrow(currentTurn, Priority.ALWAYS);
        HBox.setMargin(currentTurn, new Insets(10, 10, 10, 10));
        HBox.setMargin(start, new Insets(10, 10, 10, 10));
        HBox.setMargin(pause, new Insets(10, 10, 10, 10));

        VBox player = new VBox();
        StackPane canvasWrapper = new StackPane();
        player.getChildren().add(canvasWrapper);
        canvasWrapper.getChildren().add(canvas);
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());

        player.getChildren().add(controls);
        VBox.setVgrow(canvasWrapper, Priority.ALWAYS);
        VBox.setVgrow(controls, Priority.SOMETIMES);

        scene.setRoot(player);

        primaryStage.setTitle("Visual debugger");
        primaryStage.setScene(scene);
        primaryStage.show();

        pause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                paused = !paused;
                pause.setText(paused ? "Play" : "Pause");
            }
        });

        Thread simulationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Simulation simulation = Loader.load("busy_day.in");
                    canvas.setSimulation(simulation);
                    simulation.setTicker(VisualDebugger.this);
                    currentTurn.setMin(0);
                    currentTurn.setMax(simulation.getDeadline() - 1);
                    simulation.addTurnListener(VisualDebugger.this);
                    simulation.start();
                    System.out.println("Simulation ended");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        simulationThread.setDaemon(true);
        simulationThread.setName("simulation-thread");
        simulationThread.start();
    }

    @Override
    public void tick() {
        // Thread.sleep() is unreliable with nanoseconds
        while (true) {
            long target = 1_000_000_000 / turnsPerSecond; // 1 seconds in nanoseconds
            long now = System.nanoTime();

            if (!paused && now - lastTickNanos >= target) {
                lastTickNanos = now;
                return;
            }
        }
    }

    @Override
    public void onSimulationTurn(final Simulation simulation) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currentTurn.adjustValue(simulation.getTurn());
            }
        });
    }

    private class SimulationCanvas extends Canvas implements ChangeListener<Number>, TurnListener {

        private final Object renderLock = new Object();
        private final long lastRenderTimestamp = 0;

        private Simulation simulation;

        private volatile int fps = 24;
        private volatile boolean rendered;

        public SimulationCanvas() {
            widthProperty().addListener(this);
            heightProperty().addListener(this);
        }

        public void setSimulation(Simulation simulation) {
            if (this.simulation != null) {
                this.simulation.removeTurnListener(this);
            }

            simulation.addTurnListener(this);

            this.simulation = simulation;
        }

        public void render() {
            if (simulation == null)
                return;
            GraphicsContext ctx = getGraphicsContext2D();

            ctx.clearRect(0, 0, getWidth(), getHeight());

            for (Warehouse warehouse : simulation.getWarehouses()) {
                ctx.setFill(Color.BLUE);
                ctx.fillOval(warehouse.getLocation().col(), warehouse.getLocation().row(), 4, 4);
            }

            for (Order order : simulation.getOrders()) {
                ctx.setFill(order.isCompleted() ? Color.GREEN : Color.ORANGE);
                ctx.fillOval(order.getDestination().col(), order.getDestination().row(), 2, 2);
            }

            for (Drone drone : simulation.getDrones()) {
                ctx.setStroke(Color.GREY);
                ctx.setLineDashes(3, 3);
                Point2D from = drone.getRoute().getFrom();
                Point2D to = drone.getRoute().getTo();
                ctx.strokeLine(from.col(), from.row(), to.col(), to.row());
            }

            for (Drone drone : simulation.getDrones()) {
                ctx.setFill(Color.BLACK);
                ctx.fillOval(drone.getPosition().col(), drone.getPosition().row(), 3, 3);
            }

            ctx.setFont(Font.font(20));
            ctx.fillText("Score: " + simulation.getScore() + ", turn " + simulation.getTurn() + 1 + " of " + simulation.getDeadline(), 30, 30);
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return -1;
        }

        @Override
        public double prefHeight(double width) {
            return -1;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            render();
        }

        @Override
        public void onSimulationTurn(Simulation simulation) {
            long currentTimeMillis = System.currentTimeMillis();
            long timeSinceLastRenderMillis = currentTimeMillis - lastRenderTimestamp;
            if (timeSinceLastRenderMillis >= 1000 / fps) {
                rendered = false;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (renderLock) {
                            render();
                            rendered = true;
                            renderLock.notifyAll();
                        }
                    }
                });

                synchronized (renderLock) {
                    try {
                        while (!rendered) {
                            renderLock.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
