package pinkyandthebrain;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static final Logger log = LoggerFactory.getLogger("app");

    public static void main(String... args) throws Exception {

        Preconditions.checkArgument(args.length > 1, "Usage: Main busy_day.in redundancy.in");

        int score = 0;

        for (String arg : args) {
            String resourceName = "/dataset/" + arg;

            InputStream in = Main.class.getResourceAsStream(resourceName);
            Preconditions.checkNotNull(in, "Could not find resource " + resourceName);
            Scanner scanner = new Scanner(new InputStreamReader(in, "ASCII"));

            int rows = scanner.nextInt();
            int columns = scanner.nextInt();
            int drones = scanner.nextInt();
            int deadline = scanner.nextInt();
            int droneCapacity = scanner.nextInt();

            Simulation simulation = new Simulation(rows, columns, drones, deadline, droneCapacity);

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

            log.info("Loaded simulation parameters from  {}", resourceName);

            simulation.start();

            log.info("Simulation {} took {} turns and scored: {}", resourceName, simulation.countTurns(), simulation.getScore());

            if ("true".equals(System.getProperty("save"))) {
                PrintWriter out = new PrintWriter(new FileOutputStream(new File(args[0] + ".commands")));
                simulation.printCommands(out);
                out.flush();
                out.close();
            }

            score += simulation.getScore();
        }

        log.info("Total score: {}", score);
    }
}
