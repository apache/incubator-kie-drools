package org.drools.core.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
    
    @Test
    public void testMassAddRemove() {
        TripleStore store = new TripleStore( );
        
        int instanceLength = 1 * 1000 * 30;
        int tripleLength = 70;
        
        Triple t = null;
        List<Individual> inds = new ArrayList<Individual>(instanceLength);
        for ( int i = 0; i < instanceLength; i++) {
            Individual ind = new Individual();
            inds.add( ind );
            for (int j = 0; j < tripleLength; j++) {  
                t = new TripleImpl(ind, getPropertyName(j), i*j);            
                assertFalse( store.put( t ) );                
            }
        }
        
        assertEquals( instanceLength * tripleLength, store.size() );
        
        for ( int i = 0; i < instanceLength; i++) {
            for (int j = 0; j < tripleLength; j++) {  
                t = new TripleImpl(inds.get( i ),getPropertyName(j), null);            
                assertTrue( store.remove( t ) );                
            }
        }        
        
        assertEquals( 0,  store.size()  );    
    }
    
    public String getPropertyName(int i) {
        char c1 = (char) (65+(i/3));
        char c2 = (char) (97+(i/3));        
        return c1 + "bl" + i + "" + c2 + "blah";
    }
    
    public static class Individual {
        
    }
}
