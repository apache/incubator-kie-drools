/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.time;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the time distance calculation algorithm
 */
public class TemporalDistanceTest {
    public static final long MIN = Long.MIN_VALUE;
    public static final long MAX = Long.MAX_VALUE;

    @Test
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

    @Test
    public void testTemporalDistance2() {
        Interval[][] matrix = new Interval[][] {
                { new Interval(0,0), new Interval(5,10), new Interval(65, MAX), new Interval(30,40), new Interval(50,55) },
                { new Interval(-10,-5), new Interval(0,0), new Interval(60, MAX), new Interval(20,35), new Interval(40,50) },
                { new Interval(MIN,-65), new Interval(MIN,-60), new Interval(0, 0), new Interval(MIN, -25), new Interval(MIN, -10) },
                { new Interval(-40,-30), new Interval(-35,-20), new Interval(25, MAX), new Interval(0, 0), new Interval(15,20) },
                { new Interval(-55,-50), new Interval(-50,-40), new Interval(10,MAX), new Interval(-20,-15), new Interval(0,0) }
        };
        Interval[][] expected = new Interval[][] {
              { new Interval(0,0), new Interval(5,10), new Interval(65, MAX), new Interval(30,40), new Interval(50,55) },
              { new Interval(-10,-5), new Interval(0,0), new Interval(60, MAX), new Interval(20,35), new Interval(40,50) },
              { new Interval(MIN,-65), new Interval(MIN,-60), new Interval(0, 0), new Interval(MIN, -25), new Interval(MIN, -10) },
              { new Interval(-40,-30), new Interval(-35,-20), new Interval(25, MAX), new Interval(0, 0), new Interval(15,20) },
              { new Interval(-55,-50), new Interval(-50,-40), new Interval(10,MAX), new Interval(-20,-15), new Interval(0,0) }
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
