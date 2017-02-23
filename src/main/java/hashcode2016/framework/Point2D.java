package hashcode2016.framework;

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

    public double distanceTo(Point2D that) {
        return Math.sqrt(Math.pow(row - that.row, 2) + Math.pow(col - that.col, 2));
    }

    @Override
    public String toString() {
        return "row: " + row + ", col: " + col;
    }

    @Override
    public int hashCode() {
        return row + col;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point2D) {
            Point2D that = (Point2D) o;
            return that.row == row && that.col == col;
        } else {
            return false;
        }
    }
}
