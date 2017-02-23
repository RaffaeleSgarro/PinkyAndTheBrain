package pinkyandthebrain;

import com.google.common.base.Preconditions;
import framework.Point2D;

public class Route {

    private final Point2D from;
    private final Point2D to;

    private Point2D currentPosition;
    private int turn = -1;

    public Route(Point2D from, Point2D to) {
        this.from = from;
        this.to = to;
        currentPosition = from;
    }

    public Point2D getCurrentPosition() {
        return currentPosition;
    }

    public void advance() {
        int length = turns();
        Preconditions.checkArgument(turn < length - 1, "Cannot advance. Arrived to destination");
        turn++;
        if (turn == length - 1) {
            currentPosition = to;
        } else{
            int row = (to.row() - from.row()) * (turn + 1) / length;
            int col = (to.col() - from.col()) * (turn + 1) / length;
            currentPosition = new Point2D(from.row() + row, from.col() + col);
        }
    }

    public int turns() {
        if (from.equals(to))
            return 0;

        return (int) (Math.ceil(from.distanceTo(to)));
    }

    public Point2D getFrom() {
        return from;
    }

    public Point2D getTo() {
        return to;
    }
}
