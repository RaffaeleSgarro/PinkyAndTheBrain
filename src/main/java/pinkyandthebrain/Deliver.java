package pinkyandthebrain;

public class Deliver implements Command {

    private final Order order;
    private final Product product;
    private final int quantity;

    private int planned;
    private int executed;

    public Deliver(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public int execute(Drone drone) {
        if (planned == 0) {
            planned = 1 + Functions.turnsForDistance(drone.getPosition().distanceTo(order.getDestination()));
        }

        if (executed == planned - 1) {
            drone.deliver(product, order);
        } else {
            drone.flyToward(order.getDestination());
        }

        executed++;

        return planned - executed;
    }

    @Override
    public String toString() {
        return "D" + " " + order.getOrderId() + " " + product.getId() + " " + quantity;
    }
}
