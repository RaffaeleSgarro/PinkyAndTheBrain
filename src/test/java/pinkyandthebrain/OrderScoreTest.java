package pinkyandthebrain;

import hashcode2016.framework.Point2D;
import hashcode2016.Order;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OrderScoreTest {

    private Order order;

    @BeforeMethod
    public void setUp() {
        order = new Order(0, new Point2D(0, 0));
    }

    @Test
    public void test1() {
        order.setCompletedOnTurn(15);
        Assert.assertEquals(order.getScore(160), 91);
    }

    @Test
    public void test2() {
        order.setCompletedOnTurn(159);
        Assert.assertEquals(order.getScore(160), 1);
    }

}