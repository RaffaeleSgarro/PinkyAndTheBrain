package pinkyandthebrain;

public class Deliver implements Command {

    private final Order order;
    private final Product product;
    private final int quantity;

    private Route route;
    private int executed;

    public Deliver(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public int execute(Drone drone) {
        if (route == null) {
            route = new Route(drone.getPosition(), order.getDestination());
            drone.setRoute(route);
        }

        // TODO use route.turns() ?
        if (executed == planned() - 1) {
            drone.deliver(product, order);
        } else {
            route.advance();
            drone.moveTo(route.getCurrentPosition());
        }

        executed++;

        return planned() - executed;
    }

    private int planned() {
        return 1 + route.turns();
    }

    @Override
    public String toString() {
        return "D" + " " + order.getOrderId() + " " + product.getId() + " " + quantity;
    }
}
