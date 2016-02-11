package pinkyandthebrain;

public class Drone {

    private final int capacity;
    private Point2D currentLocation;

    public Drone(int capacity, Point2D initialLocation) {
        this.capacity = capacity;
        this.currentLocation = initialLocation;
    }

}
