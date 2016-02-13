package pinkyandthebrain;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RouteTest {

    private Point2D from;
    private Point2D to;
    private Route route;

    @Test
    public void testTurnsForDistance1() {
        from = p(0, 0);
        to = p(10, 10);
        assertEquals(turns(), Math.sqrt(200));
    }

    @Test
    public void testTurnsForDistance2() {
        from = p(0, 0);
        to = p(0, 0);
        assertEquals(turns(), 0);
    }

    @Test
    public void testTurnsForDistance3() {
        from = p(0, 0);
        to = p(0, 10);
        assertEquals(route.turns(), 10);
    }

    private Point2D p(int row, int col) {
        return new Point2D(row, col);
    }

    private int turns() {
        return new Route(from, to).turns();
    }

}
