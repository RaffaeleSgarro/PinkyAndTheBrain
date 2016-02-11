package pinkyandthebrain;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private final int orderId;
    private final Point2D destination;

    private final List<Product> products = new ArrayList<>();

    public Order(int orderId, int destinationRow, int destinationColumn) {
        this.orderId = orderId;
        this.destination = new Point2D(destinationRow, destinationColumn);
    }

    public int getOrderId() {
        return orderId;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public Point2D getDestination() {
        return destination;
    }
}
