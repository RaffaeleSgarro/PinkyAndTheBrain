package pinkyandthebrain;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private final int rows;
    private final int columns;
    private final int numberOfDrones;
    private final int deadline;
    private final int droneCapacity;

    private final List<Drone> drones = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private int turn;
    private List<String> commands;

    public Simulation(int rows, int columns, int numberOfDrones, int deadline, int droneCapacity) {
        this.rows = rows;
        this.columns = columns;
        this.numberOfDrones = numberOfDrones;
        this.deadline = deadline;
        this.droneCapacity = droneCapacity;
    }

    public List<String> getCommands() {
        return commands;
    }

    public Product createProduct(int id, int weight) {
        Product product = new Product(id, weight);
        products.add(product);
        return product;
    }

    public Warehouse addWarehouse(int id, int row, int column) {
        Warehouse warehouse = new Warehouse(id, row, column, products.size());
        warehouses.add(warehouse);
        return warehouse;
    }

    public Order addOrder(int orderId, int destinationRow, int destinationColumn) {
        Order order = new Order(orderId, destinationRow, destinationColumn);
        orders.add(order);
        return order;
    }

    public Product findProduct(int productType) {
        Product product = products.get(productType);
        Preconditions.checkNotNull(product, "Could not find a product with ID " + productType);
        return product;
    }

    public void start() {
        // ...
        commands = new ArrayList<>();

        Point2D initialDronePosition = warehouses.get(0).getLocation();
        for (int droneId = 0; droneId < numberOfDrones; droneId++) {
            drones.add(new Drone(droneId, droneCapacity, initialDronePosition));
        }

        outer:
        for (turn = 0; turn < deadline; turn++) {
            while (true) {
                if (orders.isEmpty()) {
                    // Abbiamo finito :))))
                    return;
                }
                Drone drone = findDrone();
                if (drone == null)
                    continue outer;

                Order order = orders.get(0);

                Product product = order.getProducts().get(0);
                order.getProducts().remove(0);
                if (order.getProducts().size() == 0)
                    orders.remove(0);

                List<Warehouse> availableWarehouses = findWarehouseWith(product);
                Warehouse warehouse = null;
                int bestDistance = Integer.MAX_VALUE;
                for (Warehouse available : availableWarehouses) {
                    int distance = drone.distanceTo(available) + available.getLocation().distanceTo(order.getDestination());
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        warehouse = available;
                    }
                }

                warehouse.decreaseProductQuantity(product.getId());

                int endsOnTurn = turn + bestDistance + 2;

                if (endsOnTurn >= deadline) {
                    continue ;
                }

                drone.setBusyUntilTurn(endsOnTurn);
                drone.flightTo(order.getDestination());
                commands.add(drone.getId() + " " + "L" + " " + warehouse.getId() + " " + product.getId() + " 1");
                commands.add(drone.getId() + " " + "D" + " " + order.getOrderId() + " " + product.getId() + " 1");
            }
        }
    }

    private List<Warehouse> findWarehouseWith(Product product) {
        List<Warehouse> available = new ArrayList<>();
        for (Warehouse warehouse : warehouses) {
            if (warehouse.queryProductQuantity(product.getId()) > 0) {
                available.add(warehouse);
            }
        }

        Preconditions.checkArgument(!available.isEmpty());
        return available;
    }

    private Drone findDrone() {
        for (Drone drone : drones) {
            if (!drone.isBusy(turn))
                return drone;
        }

        return null;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getNumberOfDrones() {
        return numberOfDrones;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getDroneCapacity() {
        return droneCapacity;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Warehouse> getWarehouses() {
        return warehouses;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
