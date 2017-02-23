package hashcode2016;

public class Product {

    private final int id;
    private final int weight;

    public Product(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Product#" + id;
    }
}
