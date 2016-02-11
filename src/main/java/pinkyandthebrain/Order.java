package pinkyandthebrain;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private final int orderId;
    private final int destinationRow;
    private final int destinationColumn;

    private final List<Product> products = new ArrayList<>();

    public Order(int orderId, int destinationRow, int destinationColumn) {
        this.orderId = orderId;
        this.destinationRow = destinationRow;
        this.destinationColumn = destinationColumn;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getDestinationRow() {
        return destinationRow;
    }

    public int getDestinationColumn() {
        return destinationColumn;
    }

    public void addProduct(Product product) {
        products.add(product);
    }
}
