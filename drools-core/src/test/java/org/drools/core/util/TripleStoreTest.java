package org.drools.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TripleStoreTest {
    
    @Test
    public void testPutAndGet() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");
        
        assertFalse( store.put( t ) );
        
        Triple tKey = new TripleImpl(ind, "hasName", null );
        t = store.get( tKey );
        assertEquals("mark", t.getValue() );
    }
    
    @Test
    public void testPutAndGetWithExisting() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");
        
        assertFalse( store.put( t ) );
        
        Triple tKey = new TripleImpl(ind, "hasName", null );
        t = store.get( tKey );
        assertEquals("mark", t.getValue() );
        
        t = new TripleImpl(ind, "hasName", "davide");
        
        assertTrue( store.put( t ) );
        
        tKey = new TripleImpl(ind, "hasName", null );
        t = store.get( tKey );
        assertEquals("davide", t.getValue() );        
    }  
    
    @Test
    public void testPutAndGetandRemove() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");
        
        assertFalse( store.put( t ) );
        
        Triple tKey = new TripleImpl(ind, "hasName", null );
        t = store.get( tKey );
        assertEquals("mark", t.getValue() );
        
        t = new TripleImpl(ind, "hasName", null);
        assertTrue( store.remove( t ) );
        
        assertFalse( store.remove( t ) ); // try again and make sure it's false.
        
        
        tKey = new TripleImpl(ind, "hasName", null );
        assertNull( store.get( tKey ) );        
    }     
    
    public static class Individual {
        
    }
}
