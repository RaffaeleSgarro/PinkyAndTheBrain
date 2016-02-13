package pinkyandthebrain;

import com.google.common.base.Preconditions;
import pinkyandthebrain.players.DummyPlayer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Loader {

    private final Player player;

    public Loader(Player player) {
        this.player = player;
    }

    public Simulation loadFromResource(String simpleResourceName) throws Exception {
        String resourceName = "/dataset/" + simpleResourceName;

        InputStream in = Main.class.getResourceAsStream(resourceName);
        Preconditions.checkNotNull(in, "Could not find resource " + resourceName);
        Scanner scanner = new Scanner(new InputStreamReader(in, "ASCII"));

        int rows = scanner.nextInt();
        int columns = scanner.nextInt();
        int drones = scanner.nextInt();
        int deadline = scanner.nextInt();
        int droneCapacity = scanner.nextInt();

        Simulation simulation = new Simulation(rows, columns, drones, deadline, droneCapacity, player);

        int numberOfProducts = scanner.nextInt();

        for (int id = 0; id < numberOfProducts; id++) {
            int weight = scanner.nextInt();
            Preconditions.checkArgument(weight <= droneCapacity);
            simulation.createProduct(id, weight);
        }

        int numberOfWarehouses = scanner.nextInt();

        for (int id = 0; id < numberOfWarehouses; id++) {
            int row = scanner.nextInt();
            int column = scanner.nextInt();
            Warehouse warehouse = simulation.addWarehouse(id, row, column);
            for (int productId = 0; productId < numberOfProducts; productId++) {
                int productCount = scanner.nextInt();
                warehouse.setProductQuantity(productId, productCount);
            }
        }

        int numberOfOrders = scanner.nextInt();

        for (int orderId = 0; orderId < numberOfOrders; orderId++) {

            int destinationRow = scanner.nextInt();
            int destinationColumn = scanner.nextInt();
            int numberOfProductsInThisOrder = scanner.nextInt();

            Order order = simulation.addOrder(orderId, destinationRow, destinationColumn);

            for (int i = 0; i < numberOfProductsInThisOrder; i++) {
                int productType = scanner.nextInt();
                Product product = simulation.findProduct(productType);
                order.add(product);
            }
        }

        scanner.close();

        return simulation;
    }

    public static Simulation load(String simpleResourceName) throws Exception {
        return new Loader(new DummyPlayer()).loadFromResource(simpleResourceName);
    }

}
