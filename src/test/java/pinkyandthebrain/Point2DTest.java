package pinkyandthebrain;

import hashcode2016.framework.Point2D;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Point2DTest {

    @Test
    public void testDistance1() {
        Point2D here = new Point2D(5, 5);
        assertEquals(here.distanceTo(here), 0.0);
    }

    @Test
    public void testDistance2() {
        Point2D here = new Point2D(3, 5);
        assertEquals(here.distanceTo(new Point2D(2, 2)), Math.sqrt(10.0));
    }

    @Test
    public void testDistance3() {
        Point2D here = new Point2D(0, 0);
        assertEquals(here.distanceTo(new Point2D(0, 10)), 10.0);
    }

    @Test
    public void testDistance4() {
        Point2D here = new Point2D(0, 0);
        assertEquals(here.distanceTo(new Point2D(0, 99)), 99.0);
    }
}