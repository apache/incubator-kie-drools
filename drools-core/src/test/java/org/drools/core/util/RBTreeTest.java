package org.drools.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.core.util.RBTree.Node;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class RBTreeTest {

    @Test @Ignore
    public void test1() {
        // produces duplicate entry
        generateAndTest( 68469, 82104, 100000, 5 );
    }

    @Test
    public void testVerySmallRangeData() {
        generateAndTest( 50, 100, 5 );
    }

    @Test
    public void testSmallRangeData() {
        generateAndTest( 500, 1000000, 5 );
    }

    @Test
    public void testMediumRangeData() {
        generateAndTest( 10000, 100, 5 );
    }

    @Test @Ignore
    public void testLargeData() {
        // produces duplicate entry, isolated in test1
        generateAndTest( 100000, 1, 5 );
    }

    public void generateAndTest(int range,
                                int iterations,
                                int increment) {
        java.util.Random gen = new java.util.Random();

        for ( int i = 0; i < iterations; i++ ) {

            int x = gen.nextInt( range );
            int y = gen.nextInt( range );

            if ( x < y ) {
                generateAndTest( x, y, range, increment );
            } else {
                generateAndTest( y, x, range, increment );
            }
        }
    }

    public void generateAndTest(int start,
                                int end,
                                int range,
                                int increment) {
        System.out.println( "generate tree" );
        RBTree<Integer, String> tree = new RBTree<Integer, String>();

        for ( int i = 0; i <= range; i = i + increment ) {
            tree.insert( i, "" + i );
        }

        System.out.println( "test data with tree" );
        checkResults( tree,
                      range,
                      start,
                      end,
                      increment );

        //tree.print();        
    }

    public void checkResults(RBTree<Integer, String> tree,
                             int range,
                             int start,
                             int end,
                             int increment) {
        FastIterator it = tree.range( start, true, end, true );
        Entry entry = null;
        int i = 0;
        List<Integer> actual = new ArrayList<Integer>();

        System.out.println( start + ":" + end + ":" + (((end - start) / increment) + 1) );

        while ( (entry = it.next( null )) != null ) {
            Node<Integer, Integer> node = (Node<Integer, Integer>) entry;
            if ( actual.contains( node.key ) ) {
                fail( "duplicate entry:" + node.key );
            }
            actual.add( node.key );
            i++;
            //System.out.println( entry );
            if ( i > range ) {
                fail( "could not find end" );
            }
        }

        while ( start % increment != 0 ) {
            start++;
        }

        while ( end % increment != 0 ) {
            end--;
        }

        if ( start > end ) {
            end = start;
        }

        assertEquals( ((end - start) / increment) + 1, actual.size() );
        List<Integer> expected = new ArrayList<Integer>();
        for ( int j = start; j <= end; j = j + increment ) {
            expected.add( j );
        }

        Collections.sort( expected );
        Collections.sort( actual );
        assertEquals( expected, actual );
        //System.out.println();
    }
}
