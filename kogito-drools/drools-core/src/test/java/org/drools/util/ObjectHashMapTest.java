package org.drools.util;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.core.util.Entry;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashMap.ObjectEntry;

public class ObjectHashMapTest extends TestCase {
    public void testChechExistsFalse() {
        final ObjectHashMap map = new ObjectHashMap();
        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        map.put( new Integer( 1 ),
                 stilton,
                 false );

        Cheese c = (Cheese) map.get( new Integer( 1 ) );
        assertSame( stilton,
                    c );

        // we haven't told the map to check if the key exists, so we should end up with two entries.
        // the second one is nolonger reacheable
        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        map.put( new Integer( 1 ),
                 cheddar,
                 false );
        c = (Cheese) map.get( new Integer( 1 ) );
        assertSame( cheddar,
                    c );

        Entry entry = map.getBucket( new Integer( 1 ) );
        int size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }

        assertEquals( 2,
                      size );

        // Check remove works, should leave one unreachable key
        map.remove( new Integer( 1 ) );
        entry = map.getBucket( new Integer( 1 ) );
        size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }

        assertEquals( 1,
                      size );
    }

    public void testChechExistsTrue() {
        final ObjectHashMap map = new ObjectHashMap();
        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        map.put( new Integer( 1 ),
                 stilton,
                 true );

        Cheese c = (Cheese) map.get( new Integer( 1 ) );
        assertSame( stilton,
                    c );

        // we haven't told the map to check if the key exists, so we should end up with two entries.
        // the second one is nolonger reacheable
        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        map.put( new Integer( 1 ),
                 cheddar );
        c = (Cheese) map.get( new Integer( 1 ) );
        assertSame( cheddar,
                    c );

        Entry entry = map.getBucket( new Integer( 1 ) );
        int size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }

        assertEquals( 1,
                      size );

        // Check remove works
        map.remove( new Integer( 1 ) );
        entry = map.getBucket( new Integer( 1 ) );
        size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }

        assertEquals( 0,
                      size );
    }

    public void testEmptyIterator() {
        final ObjectHashMap map = new ObjectHashMap();
        final Iterator it = map.iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            fail( "Map is empty, there should be no iteration" );
        }
    }

    public void testStringData() {
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val );
            assertEquals( val,
                          map.get( key ) );
        }
    }

    public void testIntegerData() {
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final Integer key = new Integer( idx );
            final Integer val = new Integer( idx );
            map.put( key,
                     val );
            assertEquals( val,
                          map.get( key ) );
        }
    }
}
