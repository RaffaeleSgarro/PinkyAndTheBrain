package pinkyandthebrain.players;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import pinkyandthebrain.Command;
import pinkyandthebrain.Drone;
import pinkyandthebrain.Player;
import pinkyandthebrain.Simulation;

public class StaticPlayer implements Player {

    private final ListMultimap<Integer, Command> commands = LinkedListMultimap.create();

    @Override
    public void initialize(Simulation simulation) {
        for (Integer droneId : commands.keySet()) {
            Drone drone = simulation.getDrones().get(droneId);
            for (Command command : commands.get(droneId)) {
                drone.submit(command);
            }
        }
    }

    @Override
    public void move(Simulation simulation) {}

    public void submit(int droneId, Command command) {
        commands.put(droneId, command);
    }
}
