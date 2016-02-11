package pinkyandthebrain;

public class Warehouse {

    private final int id;
    private final int row;
    private final int col;
    private final int productQuantity[];

    public Warehouse(int id, int row, int col, int numberOfProducts) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.productQuantity = new int[numberOfProducts];
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

    public void setProductQuantity(int productId, int productCount) {
        productQuantity[productId] = productCount;
    }

    public int queryProductQuantity(int productId) {
        return productQuantity[productId];
    }
}
