package pinkyandthebrain;

public class Item {

    private final Order order;
    private final Product product;

    private boolean scheduled;
    private boolean delivered;

    public Item(Order order, Product product) {
        this.order = order;
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public boolean isDelivered() {
        return delivered;
    }
}
