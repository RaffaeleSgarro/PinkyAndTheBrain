package pinkyandthebrain;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Order {

    private final int orderId;
    private final Point2D destination;

    private final List<Item> items = new ArrayList<>();
    private int completedOnTurn = -1;
    private List<OrderCompletedListener> listeners = new LinkedList<>();

    public Order(int orderId, Point2D destination) {
        this.orderId = orderId;
        this.destination = destination;
    }

    public int getOrderId() {
        return orderId;
    }

    public void add(Product product) {
        items.add(new Item(this, product));
    }

    public Point2D getDestination() {
        return destination;
    }

    public int getCompletedOnTurn() {
        return completedOnTurn;
    }

    public void setCompletedOnTurn(int completedOnTurn) {
        this.completedOnTurn = completedOnTurn;
    }

    public int getScoreAtTurn(int turn) {
        double numberOfTurns = turn + 1;
        return (int) Math.ceil((numberOfTurns - getCompletedOnTurn()) / numberOfTurns * 100);
    }

    public void deliver(Product product) {
        Item undelivered = null;
        for (Item item : items) {
            if (!item.isDelivered() && item.getProduct().getId() == product.getId()) {
                undelivered = item;
                break;
            }
        }
        Preconditions.checkNotNull(undelivered);
        undelivered.setDelivered(true);

        if (isCompleted()) {
            for (OrderCompletedListener listener : listeners) {
                listener.onOrderCompleted(this);
            }
        }
    }

    public boolean isCompleted() {
        for (Item item : items) {
            if (!item.isDelivered()) {
                return false;
            }
        }
        return true;
    }

    public void addCompletedListener(OrderCompletedListener listener) {
        this.listeners.add(listener);
    }

    public List<Item> getItems() {
        return items;
    }
}
