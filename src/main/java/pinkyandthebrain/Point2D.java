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
}
