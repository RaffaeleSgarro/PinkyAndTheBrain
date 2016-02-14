package pinkyandthebrain.debugger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import pinkyandthebrain.*;

public class VisualDebugger extends Application implements Ticker, TurnListener {

    public static void main(String... args) {
        launch(VisualDebugger.class, args);
    }

    private final ComboBox<String> input = new ComboBox<>();
    private final Slider zoom = new Slider();
    private final Spinner<Integer> fps = new Spinner<>();
    private final Spinner<Integer> tps = new Spinner<>();
    private final Slider currentTurn = new Slider();
    private final SimulationCanvas canvas = new SimulationCanvas();
    private final Button start = new Button("Start");
    private final Button pause = new Button("Pause");

    private Simulation simulation;
    private volatile boolean paused;

    private long lastTickNanos = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new Group(), 800, 600);

        HBox controls = new HBox();
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(start, spacer(), pause, spacer(), currentTurn);
        controls.setPadding(new Insets(10, 10, 10, 10));
        HBox.setHgrow(currentTurn, Priority.ALWAYS);

        VBox player = new VBox();
        StackPane canvasWrapper = new StackPane();
        player.getChildren().add(canvasWrapper);
        canvasWrapper.getChildren().add(canvas);
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());

        player.getChildren().add(controls);
        VBox.setVgrow(canvasWrapper, Priority.ALWAYS);
        VBox.setVgrow(controls, Priority.SOMETIMES);

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(10, 10, 10, 10));
        sidebar.getChildren().addAll(
                  new Label("Input"), input
                , spacer()
                , new Label("Zoom"), zoom
                , spacer()
                , new Label("Frames/second"), fps
                , spacer()
                , new Label("Turns/secnod"), tps
        );
        input.setPrefWidth(Double.MAX_VALUE);
        zoom.setPrefWidth(Double.MAX_VALUE);
        fps.setPrefWidth(Double.MAX_VALUE);
        tps.setPrefWidth(Double.MAX_VALUE);

        HBox root = new HBox();
        root.getChildren().addAll(player, sidebar);
        HBox.setHgrow(player, Priority.ALWAYS);

        scene.setRoot(root);

        primaryStage.setTitle("Visual debugger");
        primaryStage.setScene(scene);
        primaryStage.show();

        input.getItems().addAll("busy_day.in", "mother_of_all_warehouses.in", "redundancy.in");
        input.setValue("busy_day.in");

        zoom.setValue(1);
        zoom.setMin(1);
        zoom.setMax(5);

        fps.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(24, 96, 24, 24));

        tps.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 10000, 100, 1000));

        pause.setOnMouseClicked(event -> {
            paused = !paused;
            pause.setText(paused ? "Play" : "Pause");
        });

        start.setOnMouseClicked((evt) -> {
            if (simulation != null) {
                simulation.stop();
            }

            simulation = null;

            start();
        });

        start();
    }

    private void start() {

        final String simulationName = input.getValue();

        Thread simulationThread = new Thread(() -> {
            try {
                simulation = Loader.load(simulationName);
                currentTurn.setMin(0);
                currentTurn.setMax(simulation.getDeadline());
                simulation.setTicker(VisualDebugger.this);
                simulation.addTurnListener(VisualDebugger.this);
                simulation.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        simulationThread.setDaemon(true);
        simulationThread.setName("simulation-thread");
        simulationThread.start();
    }

    private static Node spacer() {
        Region node = new Region();
        node.setPrefWidth(10);
        node.setPrefHeight(10);
        return node;
    }

    @Override
    public void tick() {
        // Thread.sleep() is unreliable with nanoseconds
        while (true) {
            while (paused) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            long expectedTickDuration = 1_000_000_000 / tps.getValue();
            long now = System.nanoTime();
            long timeSinceLastTick = now - lastTickNanos;

            if (timeSinceLastTick > expectedTickDuration) {
                lastTickNanos = now;
                break;
            }
        }
    }

    @Override
    public void onSimulationTurn(final Simulation simulation) {
        canvas.onSimulationTurn(simulation);
        Platform.runLater(() -> currentTurn.adjustValue(simulation.getTurn()));
    }

    private class SimulationCanvas extends Canvas implements ChangeListener<Number>, TurnListener {

        private final Object renderLock = new Object();
        private final long lastRenderTimestamp = 0;

        private Theme theme = new Theme();

        private volatile boolean rendered;

        private double dragStartX;
        private double dragStartY;

        public SimulationCanvas() {
            widthProperty().addListener(this);
            heightProperty().addListener(this);

            setOnMousePressed(event -> {
                dragStartX = event.getX();
                dragStartY = event.getY();
            });

            setOnMouseDragged(event -> {
                double x = dragStartX - event.getX();
                double y = dragStartY - event.getY();
                getGraphicsContext2D().translate(x, y);
                dragStartX = event.getX();
                dragStartY = event.getY();
                render();
            });
        }

        public void render() {
            if (simulation == null)
                return;

            GraphicsContext ctx = getGraphicsContext2D();

            ctx.save();
            Affine identity = new Affine();
            identity.setToIdentity();
            ctx.setTransform(identity);
            ctx.setFill(theme.background);
            ctx.fillRect(0, 0, getWidth(), getHeight());
            ctx.restore();

            ctx.save();
            ctx.scale(zoom.getValue(), zoom.getValue());

            for (Drone drone : simulation.getDrones()) {
                ctx.setStroke(theme.route);
                ctx.setLineDashes(3, 3);
                Point2D from = drone.getRoute().getFrom();
                Point2D to = drone.getRoute().getTo();
                ctx.strokeLine(from.col(), from.row(), to.col(), to.row());
            }


            for (Warehouse warehouse : simulation.getWarehouses()) {
                ctx.setFill(theme.warehouse);
                ctx.fillOval(warehouse.getLocation().col() - 2, warehouse.getLocation().row() - 2, 4, 4);
            }

            for (Order order : simulation.getOrders()) {
                ctx.setFill(order.isCompleted() ? theme.orderCompleted : theme.orderPending);
                ctx.fillOval(order.getDestination().col() - 1, order.getDestination().row() - 1, 2, 2);
            }

            for (Drone drone : simulation.getDrones()) {
                ctx.setFill(theme.drone);
                ctx.fillOval(drone.getPosition().col() - 1.5, drone.getPosition().row() - 1.5, 3, 3);
            }

            ctx.restore();

            ctx.save();
            identity.setToIdentity();
            ctx.setTransform(identity);
            ctx.setFont(Font.font(20));
            ctx.setFill(theme.osd);
            ctx.fillText("Score: " + simulation.getScore() + ", turn " + (simulation.getTurn() + 1) + " of " + simulation.getDeadline(), 30, 30);
            ctx.restore();
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
            if (timeSinceLastRenderMillis >= 1000 / fps.getValue()) {
                rendered = false;

                Platform.runLater(() -> {
                    synchronized (renderLock) {
                        render();
                        rendered = true;
                        renderLock.notifyAll();
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
