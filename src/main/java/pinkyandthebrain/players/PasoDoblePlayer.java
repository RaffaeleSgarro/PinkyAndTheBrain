package pinkyandthebrain.players;

import com.google.common.base.Preconditions;
import pinkyandthebrain.*;

import java.util.*;

public class PasoDoblePlayer implements Player, RetrieveListener {

    private int[][] reserved;
    private LinkedList<Item> unscheduled;
    private Simulation simulation;

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
        reserved = new int[simulation.getWarehouses().size()][simulation.getProducts().size()];
        unscheduled = new LinkedList<>();

        List<Order> orders = new ArrayList<>();
        orders.addAll(simulation.getOrders());

        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order thisOrder, Order thatOrder) {
                return thisOrder.getItems().size() - thatOrder.getItems().size();
            }
        });

        for (Order order : orders) {
            for (Item item : order.getItems()) {
                unscheduled.add(item);
            }
        }

        for (Warehouse warehouse : simulation.getWarehouses()) {
            warehouse.addRetrieveListener(this);
        }
    }

    @Override
    public void move(Simulation simulation) {
        if (unscheduled.isEmpty()) {
            return;
        }

        for (Drone drone : simulation.getDrones()) {
            if (drone.isBusy()) {
                continue;
            }

            if (unscheduled.isEmpty()) {
                return;
            }

            Item item = null;
            Warehouse warehouse = null;
            double bestDistance = Double.MAX_VALUE;
            int availableCapacity = drone.getAvailableCapacity();
            List<Deliver> maybeDeliverCommands = new ArrayList<>();

            // 50 is a magic constant
            for (int i = 0; i < Math.min(50, unscheduled.size()); i++) {
                Item currentItem = unscheduled.get(i);

                List<Warehouse> availableWarehouses = findWarehouseWith(currentItem.getProduct(), 1);
                for (Warehouse available : availableWarehouses) {
                    double distance = drone.getPosition().distanceTo(available.getLocation())
                            + available.getLocation().distanceTo(currentItem.getOrder().getDestination());
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        warehouse = available;
                        item = currentItem;
                    }
                }
            }

            availableCapacity -= item.getProduct().getWeight();
            unscheduled.remove(item);
            reserve(warehouse, item.getProduct(), 1);
            drone.submit(new Load(warehouse, item.getProduct(), 1));


            List<Item> item2OrderedByCost = new ArrayList<>();
            // 0.1 is magic constant
            double cutoff = .1 * .5* (simulation.getColumns() + simulation.getRows());

            for (Item maybeAdd : unscheduled) {
                if (maybeAdd.getOrder().getDestination().distanceTo(item.getOrder().getDestination()) < cutoff) {
                    item2OrderedByCost.add(maybeAdd);
                }
            }

            final Item stupidJavaItem = item;

            Collections.sort(item2OrderedByCost, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if (o1.getOrder().getItems().size() != o2.getOrder().getItems().size()) {
                        return o1.getOrder().getItems().size() - o2.getOrder().getItems().size();
                    }

                    int d1 = (int) o1.getOrder().getDestination().distanceTo(stupidJavaItem.getOrder().getDestination());
                    int d2 = (int) o2.getOrder().getDestination().distanceTo(stupidJavaItem.getOrder().getDestination());

                    return d1 - d2;
                }
            });




            for (Item item2 : item2OrderedByCost) {
                if (findAvailableProduct(item2.getProduct(), warehouse) > 0) {
                    if (item2.getProduct().getWeight() <= availableCapacity) {
                        availableCapacity -= item2.getProduct().getWeight();
                        unscheduled.remove(item2);
                        reserve(warehouse, item2.getProduct(), 1);
                        drone.submit(new Load(warehouse, item2.getProduct(), 1));
                        maybeDeliverCommands.add(new Deliver(item2.getOrder(), item2.getProduct(), 1));
                    }
                }
            }

            drone.submit(new Deliver(item.getOrder(), item.getProduct(), 1));

            for (Deliver cmd : maybeDeliverCommands) {
                drone.submit(cmd);
            }
        }
    }

    public List<Warehouse> findWarehouseWith(Product product, int quantity) {
        List<Warehouse> available = new ArrayList<>();
        for (Warehouse warehouse : simulation.getWarehouses()) {
            int availableProductQuantity = findAvailableProduct(product, warehouse);
            if (availableProductQuantity >= quantity) {
                available.add(warehouse);
            }
        }

        Preconditions.checkArgument(!available.isEmpty(), "Could not find a warehouse with " + product);
        return available;
    }

    private int findAvailableProduct(Product product, Warehouse warehouse) {
        return warehouse.queryProductQuantity(product.getId()) - reserved[warehouse.getId()][product.getId()];
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
