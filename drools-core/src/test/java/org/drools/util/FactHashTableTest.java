package org.drools.util;

import junit.framework.TestCase;

import org.drools.util.ObjectHashMap.ObjectEntry;

public class FactHashTableTest extends TestCase {
    public void testEmptyIterator() {
        final FactHashTable map = new FactHashTable();
        final Iterator it = map.iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            fail( "Map is empty, there should be no iteration" );
        }
    }
}