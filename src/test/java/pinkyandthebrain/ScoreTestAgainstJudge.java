package pinkyandthebrain;

import com.google.common.base.Preconditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pinkyandthebrain.players.StaticPlayer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ScoreTestAgainstJudge {

    private StaticPlayer player;
    private Simulation simulation;
    private Scanner fileScanner;

    @Test
    public void busyDay() throws Exception {
        run("busy_day");
        expectScore(63254);
    }

    @Test
    public void motherOfAllWarehouses() throws Exception {
        run("mother_of_all_warehouses");
        expectScore(59041);
    }

    @Test
    public void redundancy() throws Exception {
        run("redundancy");
        expectScore(78308);
    }

    private void run(String resource) throws Exception {
        player = new StaticPlayer();
        Loader loader = new Loader(player);
        simulation = loader.loadFromResource(resource + ".in");

        String submissionResourceName = "/submissions/" + resource + ".in.txt";
        InputStream in = ScoreTestAgainstJudge.class.getResourceAsStream(submissionResourceName);
        Preconditions.checkNotNull(in, "Could not find resource " + submissionResourceName);
        fileScanner = new Scanner(new InputStreamReader(in, "ASCII"));
        int lines = fileScanner.nextInt();
        for (int i = 0; i < lines; i++) {
            int droneId = fileScanner.nextInt();
            String command = fileScanner.next();
            switch (command) {
                case "L":
                    int loadWarehouseId = fileScanner.nextInt();
                    int loadProductId = fileScanner.nextInt();
                    int loadQuantity = fileScanner.nextInt();
                    player.submit(droneId, new Load(simulation.getWarehouses().get(loadWarehouseId)
                                                  , simulation.getProducts().get(loadProductId)
                                                  , loadQuantity));
                    break;
                case "U":
                    int unloadWarehouseId = fileScanner.nextInt();
                    int unloadProductId = fileScanner.nextInt();
                    int unloadQuantity = fileScanner.nextInt();
                    player.submit(droneId, new Unload(simulation.getWarehouses().get(unloadWarehouseId)
                            , simulation.getProducts().get(unloadProductId)
                            , unloadQuantity));
                    break;
                case "D":
                    int orderId = fileScanner.nextInt();
                    int deliverProductId = fileScanner.nextInt();
                    int deliverQuantity = fileScanner.nextInt();
                    player.submit(droneId, new Deliver(simulation.getOrders().get(orderId)
                                                  , simulation.getProducts().get(deliverProductId)
                                                  , deliverQuantity));
                    break;
                default:
                    throw new RuntimeException("Unknown command symbol: " + command);
            }
        }

        fileScanner.close();

        simulation.start();
    }

    private void expectScore(int expectedScore) {
        Assert.assertEquals(simulation.getScore(), expectedScore, "Wrong score");
    }
}
