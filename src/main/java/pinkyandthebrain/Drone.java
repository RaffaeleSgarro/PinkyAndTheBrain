package pinkyandthebrain;

public class Drone {

    private final int id;
    private final int capacity;
    private Point2D currentLocation;
    private int busyUntilTurn;

    public Drone(int id, int capacity, Point2D initialLocation) {
        this.id = id;
        this.capacity = capacity;
        this.currentLocation = initialLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public int distanceTo(Warehouse warehouse) {
        return currentLocation.distanceTo(warehouse.getLocation());
    }

    public void setBusyUntilTurn(int turn) {
        this.busyUntilTurn = turn;
    }

    public boolean isBusy(int turn) {
        boolean b = turn <= busyUntilTurn;
        return b;
    }

    public int getId() {
        return id;
    }
}
