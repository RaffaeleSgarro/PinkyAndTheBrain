package pinkyandthebrain;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Point2DTest {

    @Test
    public void testDistance1() {
        Point2D here = new Point2D(5, 5);
        assertEquals(here.distanceTo(here), 0);
    }

    @Test
    public void testDistance2() {
        Point2D here = new Point2D(3, 5);
        assertEquals(here.distanceTo(new Point2D(2, 2)), 4);
    }
}