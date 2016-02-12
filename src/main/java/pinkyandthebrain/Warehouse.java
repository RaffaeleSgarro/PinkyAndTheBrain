package pinkyandthebrain;

import com.google.common.base.Preconditions;

public class Warehouse {

    private final int id;
    private final Point2D location;
    private final int available[];
    private final int reserved[];

    public Warehouse(int id, int row, int col, int numberOfProducts) {
        this.id = id;
        this.location = new Point2D(row, col);
        this.available = new int[numberOfProducts];
        this.reserved = new int[numberOfProducts];
    }

    public int getId() {
        return id;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setProductQuantity(int productId, int productCount) {
        available[productId] = productCount;
    }

    public int queryProductQuantity(int productId) {
        return available[productId];
    }

    public void retrieve(Product product, int quantity) {
        Preconditions.checkArgument(reserved[product.getId()] >= quantity);
        reserved[product.getId()] -= quantity;
    }

    public void reserve(Product product, int quantity) {
        Preconditions.checkArgument(available[product.getId()] >= quantity,
                "This warehouse only contains "
                        + available[product.getId()]
                        + " of Product#" + product.getId()
                        + " but " + quantity
                        + " was requested");

        available[product.getId()] -= quantity;
        reserved[product.getId()] += quantity;
    }
}
