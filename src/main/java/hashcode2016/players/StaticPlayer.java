package hashcode2016.players;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import hashcode2016.Command;
import hashcode2016.Drone;
import hashcode2016.Player;
import hashcode2016.Simulation;

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
