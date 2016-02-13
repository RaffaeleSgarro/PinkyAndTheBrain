package pinkyandthebrain.players;

import pinkyandthebrain.*;

import java.util.List;

import static pinkyandthebrain.Functions.turnsForDistance;

public class DummyPlayer implements Player {

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

            List<Warehouse> availableWarehouses = simulation.findWarehouseWith(item.getProduct(), 1);
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

            if (simulation.getTurn() + 1 + turnsForDistance(bestDistance) + 1 >= simulation.getDeadline()) {
                continue;
            }

            item.setScheduled(true);
            warehouse.reserve(item.getProduct(), 1);

            drone.submit(new Load(warehouse, item.getProduct(), 1));
            drone.submit(new Deliver(item.getOrder(), item.getProduct(), 1));
        }
    }

}
