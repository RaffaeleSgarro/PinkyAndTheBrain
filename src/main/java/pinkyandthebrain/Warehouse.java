package pinkyandthebrain;

public class Warehouse {

    private final int id;
    private final Point2D location;
    private final int productQuantity[];

    public Warehouse(int id, int row, int col, int numberOfProducts) {
        this.id = id;
        this.location = new Point2D(row, col);
        this.productQuantity = new int[numberOfProducts];
    }

    public int getId() {
        return id;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setProductQuantity(int productId, int productCount) {
        productQuantity[productId] = productCount;
    }

    public int queryProductQuantity(int productId) {
        return productQuantity[productId];
    }
}
