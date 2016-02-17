package pinkyandthebrain;

import com.google.common.base.Preconditions;

public class Unload implements Command {

    private final Warehouse warehouse;
    private final Product product;
    private final int quantity;

    private Route route;
    private int executed;

    public Unload(Warehouse warehouse, Product product, int quantity) {
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public int execute(Drone drone) {
        if (route == null) {
            route = new Route(drone.getPosition(), warehouse.getLocation());
            drone.setRoute(route);
        }

        Preconditions.checkArgument(drone.getPosition().equals(route.getCurrentPosition()), "Drone out of route!");

        // TODO use route.turns() ?
        if (executed == planned() - 1) {
            drone.unload(product, quantity);
            warehouse.store(product, quantity);
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
        return "U" + " " + warehouse.getId() + " " + product.getId() + " " + quantity;
    }
}
