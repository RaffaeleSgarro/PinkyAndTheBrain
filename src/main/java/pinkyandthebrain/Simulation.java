package pinkyandthebrain;

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
        Warehouse warehouse = new Warehouse(id, row, column);
        warehouses.add(warehouse);
        return warehouse;
    }
}
