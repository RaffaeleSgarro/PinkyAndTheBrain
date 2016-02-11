package pinkyandthebrain;

public class Point2D {

    private final int row;
    private final int col;

    public Point2D(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public int distanceTo(Point2D that) {
        return (int) Math.ceil(Math.sqrt(Math.pow(row - that.row, 2) + Math.pow(col - that.col, 2)));
    }
}
