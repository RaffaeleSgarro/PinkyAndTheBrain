package pinkyandthebrain;

import com.google.common.base.Preconditions;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Simulation implements OrderCompletedListener {

    private final int rows;
    private final int columns;
    private final int numberOfDrones;
    private final int deadline;
    private final int droneCapacity;
    private final Player player;

    private final List<Drone> drones = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    private int turn;

    public Simulation(int rows, int columns, int numberOfDrones, int deadline, int droneCapacity, Player player) {
        this.rows = rows;
        this.columns = columns;
        this.numberOfDrones = numberOfDrones;
        this.deadline = deadline;
        this.droneCapacity = droneCapacity;
        this.player = player;
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
        Point2D initialDronePosition = warehouses.get(0).getLocation();
        for (int droneId = 0; droneId < numberOfDrones; droneId++) {
            drones.add(new Drone(droneId, droneCapacity, initialDronePosition));
        }

        for (Order order : orders) {
            order.addCompletedListener(this);
        }

        turn = -1;

        while (hasPendingOrders()) {
            if (turn < deadline - 1) {
                turn++;
                if (hasUnscheduledOrders()) {
                    player.move(this);
                }
            } else {
                break;
            }

            for (Drone drone : drones) {
                if (!drone.isShutDown()) {
                    drone.executeNextCommand();
                }
            }
        }
    }

    private boolean hasUnscheduledOrders() {
        for (Order order : orders) {
            if (!order.isScheduled()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPendingOrders() {
        for (Order order : orders) {
            if (!order.isCompleted())
                return true;
        }
        return false;
    }

    public List<Warehouse> findWarehouseWith(Product product, int quantity) {
        List<Warehouse> available = new ArrayList<>();
        for (Warehouse warehouse : warehouses) {
            if (warehouse.queryProductQuantity(product.getId()) >= quantity) {
                available.add(warehouse);
            }
        }

        Preconditions.checkArgument(!available.isEmpty(), "Could not find a warehouse with " + product);
        return available;
    }

    public int getScore() {
        int score = 0;
        for (Order order : orders) {
            if (order.isCompleted()) {
                score += order.getScoreAtTurn(turn);
            }
        }
        return score;
    }

    public void printCommands(PrintWriter out) {
        List<String> commands = new ArrayList<>();

        for (Drone drone : drones) {
            for (Command command : drone.getExecutedCommands()) {
                commands.add(drone.getId() + " " + command);
            }
        }

        out.println(commands.size());

        for (String command : commands) {
            out.println(command);
        }
    }

    public int countTurns() {
        return turn + 1;
    }

    @Override
    public void onOrderCompleted(Order order) {
        order.setCompletedOnTurn(turn);
    }

    public int getTurn() {
        return turn;
    }

    public int getDeadline() {
        return deadline;
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
