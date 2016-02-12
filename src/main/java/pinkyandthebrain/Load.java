package pinkyandthebrain;

public class Load implements Command {

    private final Warehouse warehouse;
    private final Product product;
    private final int quantity;

    private int planned;
    private int executed;

    public Load(Warehouse warehouse, Product product, int quantity) {
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public int execute(Drone drone) {
        if (planned == 0) {
            planned = 1 + Functions.turnsForDistance(drone.getPosition().distanceTo(warehouse.getLocation()));
        }

        if (executed == planned - 1) {
            warehouse.retrieve(product, quantity);
            drone.load(product, quantity);
        } else {
            drone.flyToward(warehouse.getLocation());
        }

        executed++;

        return planned - executed;
    }

    @Override
    public String toString() {
        return "L" + " " + warehouse.getId() + " " + product.getId() + " " + quantity;
    }
}
