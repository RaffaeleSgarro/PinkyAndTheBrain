package pinkyandthebrain.players;

import com.google.common.base.Preconditions;
import pinkyandthebrain.*;

import java.util.ArrayList;
import java.util.List;

public class DummyPlayer implements Player, RetrieveListener {

    private int[][] reserved;
    private Simulation simulation;

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
        reserved = new int[simulation.getWarehouses().size()][simulation.getProducts().size()];
        for (Warehouse warehouse : simulation.getWarehouses()) {
            warehouse.addRetrieveListener(this);
        }
    }

    @Override
    public void move(Simulation simulation) {
        for (Drone drone : simulation.getDrones()) {
            if (drone.isBusy()) {
                continue;
            }

            Item item = null;

            for (Order order : simulation.getOrders()) {
                if (!order.isScheduled()) {
                    item = order.findFirstUnscheduled();
                }
            }

            List<Warehouse> availableWarehouses = findWarehouseWith(item.getProduct(), 1);
            Warehouse warehouse = null;
            double bestDistance = Double.MAX_VALUE;
            for (Warehouse available : availableWarehouses) {
                double distance = drone.getPosition().distanceTo(available.getLocation())
                        + available.getLocation().distanceTo(item.getOrder().getDestination());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    warehouse = available;
                }
            }

            if (simulation.getTurn() + 1 + Math.ceil(bestDistance) + 1 >= simulation.getDeadline()) {
                continue;
            }

            item.setScheduled(true);
            reserve(warehouse, item.getProduct(), 1);

            drone.submit(new Load(warehouse, item.getProduct(), 1));
            drone.submit(new Deliver(item.getOrder(), item.getProduct(), 1));
        }
    }


    public List<Warehouse> findWarehouseWith(Product product, int quantity) {
        List<Warehouse> available = new ArrayList<>();
        for (Warehouse warehouse : simulation.getWarehouses()) {
            int availableProductQuantity = warehouse.queryProductQuantity(product.getId()) - reserved[warehouse.getId()][product.getId()];
            if (availableProductQuantity >= quantity) {
                available.add(warehouse);
            }
        }

        Preconditions.checkArgument(!available.isEmpty(), "Could not find a warehouse with " + product);
        return available;
    }

    public void reserve(Warehouse warehouse, Product product, int quantity) {
        Preconditions.checkArgument(warehouse.queryProductQuantity(product.getId()) >= quantity,
                "This warehouse only contains "
                        + warehouse.queryProductQuantity(product.getId())
                        + " of " + product
                        + " but " + quantity
                        + " was requested");

        reserved[warehouse.getId()][product.getId()] += quantity;
    }

    @Override
    public void onProductRetrieved(Warehouse warehouse, Product product, int quantity) {
        reserved[warehouse.getId()][product.getId()] -= quantity;
    }
}
