package hashcode2016;

import com.google.common.base.Preconditions;
import hashcode2016.framework.Point2D;

import java.util.*;

public class Drone {

    private final Queue<Command> pendingCommands = new LinkedList<>();
    private final List<Command> executedCommands = new ArrayList<>();
    private final Collection<Product> inventory = new ArrayList<>();

    private final int id;
    private final int capacity;
    private Route route;
    private Point2D currentLocation;
    private Command currentCommand;
    private boolean shutdown;
    private boolean started;
    private int turnsToWaitBeforeFetchingNextCommand;

    public Drone(int id, int capacity, Point2D initialLocation) {
        this.id = id;
        this.capacity = capacity;
        this.currentLocation = initialLocation;
    }

    protected void moveTo(Point2D point) {
        this.currentLocation = point;
    }

    public Point2D getPosition() {
        return currentLocation;
    }

    public boolean isBusy() {
        return turnsToWaitBeforeFetchingNextCommand > 0 || pendingCommands.size() > 0;
    }

    public int getId() {
        return id;
    }

    public void submit(Command command) {
        Preconditions.checkArgument(!shutdown, "No commands can be submitted after the drone has been shut down");
        pendingCommands.add(command);
    }

    protected List<Command> getExecutedCommands() {
        return executedCommands;
    }

    protected void executeNextCommand() {
        Preconditions.checkArgument(!started || hasMoreWork(), "This drone has been shut down!");

        if (!started) {
            started = true;
            currentCommand = pendingCommands.poll();
        } else if (turnsToWaitBeforeFetchingNextCommand == 0) {
            executedCommands.add(currentCommand);
            currentCommand = pendingCommands.poll();
        }

        if (currentCommand == null) {
            shutdown();
            return;
        }

        turnsToWaitBeforeFetchingNextCommand = currentCommand.execute(this);
        Preconditions.checkArgument(turnsToWaitBeforeFetchingNextCommand >= 0, "Negative number not expected here: " + turnsToWaitBeforeFetchingNextCommand);
    }

    private boolean hasMoreWork() {
        return currentCommand != null || pendingCommands.size() > 0;
    }

    private void shutdown() {
        Preconditions.checkArgument(started, "Drone must be started");
        Preconditions.checkArgument(!shutdown, "Drone can be shut down only once");
        this.shutdown = true;
    }

    protected void deliver(Product product, Order order) {
        boolean removed = inventory.remove(product);
        Preconditions.checkArgument(removed, "Drone " + id + " inventory does not contain " + product);
        order.deliver(product);
    }

    protected void load(Product product, int quantity) {
        Preconditions.checkArgument(totalWeight() + (product.getWeight() * quantity) <= capacity, "Exceeded drone capacity");
        for (int i = 0; i < quantity; i++) {
            inventory.add(product);
        }
    }

    protected void unload(Product product, int quantity) {
        Preconditions.checkArgument(quantity > 0);
        for (int i = 0; i < quantity; i++) {
            Preconditions.checkArgument(inventory.contains(product), "Inventory does not contain " + product);
            inventory.remove(product);
        }
    }

    private int totalWeight() {
        int weight = 0;
        for (Product product : inventory) {
            weight += product.getWeight();
        }
        return weight;
    }

    public Route getRoute() {
        return route;
    }

    protected void setRoute(Route route) {
        this.route = route;
    }

    public boolean isShutDown() {
        return shutdown;
    }

    public int getAvailableCapacity() {
        return capacity - totalWeight();
    }

}
