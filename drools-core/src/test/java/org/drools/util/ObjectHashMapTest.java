package org.drools.util;

import junit.framework.TestCase;

import org.drools.Cheese;

public class ObjectHashMapTest extends TestCase {
	public void testChechExistsFalse() {
		ObjectHashMap map = new ObjectHashMap();
		Cheese stilton = new Cheese("stilton", 5);
		map.put(new Integer(1), stilton, false);
		
		Cheese c = (Cheese) map.get(new Integer(1));
		assertSame(stilton, c);
        
        // we haven't told the map to check if the key exists, so we should end up with two entries.
        // the second one is nolonger reacheable
        Cheese cheddar = new Cheese("cheddar", 5);
        map.put(new Integer(1), cheddar);
        c = (Cheese) map.get(new Integer(1));
        assertSame(cheddar, c);
        
        Entry entry = map.getBucket( new Integer(1).hashCode() );
        int size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }
        
        assertEquals( 2, size);
        
        // Check remove works, should leave one unreachable key
        map.remove( new Integer(1) );
        entry = map.getBucket( new Integer(1).hashCode() );
        size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }
        
        assertEquals( 1, size);        
	}
    
    public void testChechExistsTrue() {
        ObjectHashMap map = new ObjectHashMap();
        Cheese stilton = new Cheese("stilton", 5);
        map.put(new Integer(1), stilton, true);
        
        Cheese c = (Cheese) map.get(new Integer(1));
        assertSame(stilton, c);
        
        // we haven't told the map to check if the key exists, so we should end up with two entries.
        // the second one is nolonger reacheable
        Cheese cheddar = new Cheese("cheddar", 5);
        map.put(new Integer(1), cheddar);
        c = (Cheese) map.get(new Integer(1));
        assertSame(cheddar, c);
        
        Entry entry = map.getBucket( new Integer(1).hashCode() );
        int size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }
        
        assertEquals( 1, size);    
        
        // Check remove works
        map.remove( new Integer(1) );
        entry = map.getBucket( new Integer(1).hashCode() );
        size = 0;
        while ( entry != null ) {
            size++;
            entry = entry.getNext();
        }
        
        assertEquals( 0, size);          
    }
}
