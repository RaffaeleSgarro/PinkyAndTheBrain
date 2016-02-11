package pinkyandthebrain;

public class Warehouse {

    private final int id;
    private final int row;
    private final int col;

    public Warehouse(int id, int row, int col) {
        this.id = id;
        this.row = row;
        this.col = col;
    }

    public int getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setProductCount(int productId, int productCount) {

    }
}
