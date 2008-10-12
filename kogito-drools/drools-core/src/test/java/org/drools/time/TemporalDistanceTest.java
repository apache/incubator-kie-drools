package org.drools.time;

import junit.framework.TestCase;

/**
 * Test class for the time distance calculation algorithm
 */
public class TemporalDistanceTest  extends TestCase {
    public static final long MIN = Long.MIN_VALUE;
    public static final long MAX = Long.MAX_VALUE;

    public void testTemporalDistance() {
        Interval[][] matrix = new Interval[][] {
                { new Interval(0,0), new Interval(-2,2), new Interval(-3, 4), new Interval(MIN, MAX), new Interval(MIN, MAX) },
                { new Interval(-2,2), new Interval(0,0), new Interval(MIN, MAX), new Interval(1,2), new Interval(MIN, MAX) },
                { new Interval(-4,3), new Interval(MIN,MAX), new Interval(0, 0), new Interval(2, 3), new Interval(MIN, MAX) },
                { new Interval(MIN,MAX), new Interval(-2,-1), new Interval(-3, -2), new Interval(0, 0), new Interval(-2, -1) },
                { new Interval(MIN,MAX), new Interval(MIN,MAX), new Interval(MIN,MAX), new Interval(1, 2), new Interval(0,0) }
        };
        Interval[][] expected = new Interval[][] {
                { new Interval(0,0), new Interval(-2,2), new Interval(-3, 2), new Interval(-1, 4), new Interval(-3, 3) },
                { new Interval(-2,2), new Interval(0,0), new Interval(-2, 0), new Interval(1,2), new Interval(-1, 1) },
                { new Interval(-2,3), new Interval(0,2), new Interval(0, 0), new Interval(2, 3), new Interval(0,2) },
                { new Interval(-4,1), new Interval(-2,-1), new Interval(-3, -2), new Interval(0, 0), new Interval(-2, -1) },
                { new Interval(-3,3), new Interval(-1,1), new Interval(-2,0), new Interval(1, 2), new Interval(0,0) }
        };
        Interval[][] result = TimeUtils.calculateTemporalDistance( matrix );
        assertEqualsMatrix( expected, result );
    }

    public void assertEqualsMatrix( Interval[][] expected, Interval[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ) {
            for( int j = 0; j < matrix[i].length; j++ ) {
                assertEquals( "Wrong value at ("+i+", "+j, expected[i][j], matrix[i][j] );
            }
        }
    }

    public void printMatrix( Interval[][] matrix ) {
        System.out.println("------------------------------------------------------------------");
        for( int i = 0; i < matrix.length; i++ ) {
            System.out.print("|  ");
            for( int j = 0; j < matrix[i].length; j++ ) {
                System.out.print( matrix[i][j] + "  ");
            }
            System.out.println("|");
        }
        System.out.println("------------------------------------------------------------------");
    }

}
