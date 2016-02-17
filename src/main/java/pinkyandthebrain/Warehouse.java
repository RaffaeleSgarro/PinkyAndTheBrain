package pinkyandthebrain;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {

    private final int id;
    private final Point2D location;
    private final int available[];
    private final List<RetrieveListener> retrieveListeners = new ArrayList<>();

    public Warehouse(int id, Point2D location, int numberOfProducts) {
        this.id = id;
        this.location = location;
        this.available = new int[numberOfProducts];
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
        Preconditions.checkArgument(quantity > 0, "Quantity must be positive");
        Preconditions.checkArgument(available[product.getId()] >= quantity);
        available[product.getId()] -= quantity;
        for (RetrieveListener listener : retrieveListeners) {
            listener.onProductRetrieved(this, product, quantity);
        }
    }

    public void addRetrieveListener(RetrieveListener retrieveListener) {
        this.retrieveListeners.add(retrieveListener);
    }

    public void store(Product product, int quantity) {
        Preconditions.checkArgument(quantity > 0, "Quantity must be positive");
        available[product.getId()] += quantity;
    }
}
