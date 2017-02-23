package hashcode2016.players;

import com.google.common.base.Preconditions;
import hashcode2016.Deliver;
import hashcode2016.framework.Point2D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ShortestPathFinder {

    private final List<Deliver> deliverCommands = new ArrayList<>();

    private Deliver[] buffer;
    private double bestScore = Double.MAX_VALUE;
    private Point2D start = null;
    private Point2D end = null;

    public void add(Deliver deliver) {
        deliverCommands.add(deliver);
    }

    public List<Deliver> shortest() {
        if (deliverCommands.size() > 1) {
            buffer = new Deliver[deliverCommands.size()];
            LinkedList<Integer> positions = new LinkedList<>();
            LinkedList<Deliver> delivers = new LinkedList<>();
            for (int i = 0; i < deliverCommands.size(); i++) {
                positions.add(i);
                delivers.add(deliverCommands.get(i));
            }
            next(positions, delivers);
        }

        return deliverCommands;
    }

    private void next(List<Integer> positionsToTry, List<Deliver> delivers) {
        Preconditions.checkArgument(positionsToTry.size() == delivers.size());
        if (positionsToTry.size() == 1) {
            buffer[positionsToTry.get(0)] = delivers.get(0);
            double distance = 0.0;

            if (end != null) {
                distance += start.distanceTo(buffer[0].getOrder().getDestination());
            }

            for (int i = 1; i < buffer.length; i++) {
                Point2D p1 = buffer[i].getOrder().getDestination();
                Point2D p2 = buffer[i - 1].getOrder().getDestination();
                distance += p1.distanceTo(p2);
            }

            if (end != null) {
                distance += end.distanceTo(buffer[buffer.length - 1].getOrder().getDestination());
            }

            if (distance < bestScore) {
                bestScore = distance;
                deliverCommands.clear();
                for (Deliver deliver : buffer) {
                    deliverCommands.add(deliver);
                }
            }
        } else {
            LinkedList<Integer> remainingPositions = new LinkedList();
            remainingPositions.addAll(positionsToTry);
            int position = remainingPositions.removeFirst();
            for (Deliver deliver : delivers) {
                LinkedList<Deliver> remainingDelivers = new LinkedList<>();
                remainingDelivers.addAll(delivers);
                remainingDelivers.remove(deliver);
                buffer[position] = deliver;
                next(remainingPositions, remainingDelivers);
            }
        }
    }

    public void setEnd(Point2D point2D) {
        this.end = point2D;
    }

    public void setStart(Point2D start) {
        this.start = start;
    }

}
