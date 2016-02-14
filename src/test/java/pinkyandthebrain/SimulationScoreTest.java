package pinkyandthebrain;

import org.testng.Assert;
import org.testng.annotations.Test;
import pinkyandthebrain.players.StaticPlayer;

public class SimulationScoreTest {

    private Order order;
    private Product product;
    private Warehouse warehouse;

    @Test
    public void simplest() {

        StaticPlayer player = new StaticPlayer();
        Simulation simulation = new Simulation(1000, 1000, 1, Integer.MAX_VALUE, 1, player);

        product = simulation.createProduct(0, 1);

        warehouse = simulation.addWarehouse(0, 0, 0);
        warehouse.setProductQuantity(product.getId(), 1);

        order = simulation.addOrder(0, 0, 100);
        order.add(product);

        player.submit(0, new Load(warehouse, product, 1));
        player.submit(0, new Deliver(order, product, 1));

        simulation.start();
    }


}
