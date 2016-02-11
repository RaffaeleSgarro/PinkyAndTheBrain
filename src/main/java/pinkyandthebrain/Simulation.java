package pinkyandthebrain;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private final int rows;
    private final int columns;
    private final int drones;
    private final int deadline;
    private final int droneCapacity;

    private final List<Product> products = new ArrayList<>();
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    public Simulation(int rows, int columns, int drones, int deadline, int droneCapacity) {
        this.rows = rows;
        this.columns = columns;
        this.drones = drones;
        this.deadline = deadline;
        this.droneCapacity = droneCapacity;
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
        throw new RuntimeException("Not implemented yet! :P");
    }
}
