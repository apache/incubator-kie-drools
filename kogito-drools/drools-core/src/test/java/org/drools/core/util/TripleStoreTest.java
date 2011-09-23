/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.runtime.rule.Variable;
import org.junit.Test;

public class TripleStoreTest {

    private Variable V = Variable.v;
    
    @Test
    public void testPutAndGet() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");
        
        assertFalse( store.put( t ) );
        
        Triple tKey = new TripleImpl(ind, "hasName", V );
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
        
        Triple tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertEquals("mark", t.getValue() );
        
        t = new TripleImpl(ind, "hasName", "davide");
        
        assertTrue( store.put( t ) );
        
        tKey = new TripleImpl(ind, "hasName", V );
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
        
        Triple tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertEquals("mark", t.getValue() );
        
        t = new TripleImpl(ind, "hasName", V );
        assertEquals( 1, store.removeAll( t ) );
        
        assertFalse( store.remove( t ) ); // try again and make sure it's false.
        
        
        tKey = new TripleImpl(ind, "hasName", V );
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
                t = new TripleImpl(inds.get( i ),getPropertyName(j), V );
                store.removeAll( t );
            }
        }        
        
        assertEquals( 0,  store.size()  );    
    }
    
    public String getPropertyName(int i) {
        char c1 = (char) (65+(i/3));
        char c2 = (char) (97+(i/3));        
        return c1 + "bl" + i + "" + c2 + "blah";
    }




    @Test
    public void testQueryVariable() {
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();

        Triple t1 = new TripleImpl(ind, "hasName", "mark");
        assertFalse( store.put( t1 ) );

        Triple t2 = new TripleImpl(ind, "hasAge", "35");
        assertFalse( store.put( t2 ) );

        Triple t3 = new TripleImpl(ind, "hasCity", "london");
        assertFalse( store.put( t3 ) );

        Individual ind2 = new Individual();

        Triple t4 = new TripleImpl(ind2, "hasCity", "bologna");
        assertFalse( store.put( t4 ) );

        Triple t5 = new TripleImpl(ind2, "hasCar", "lancia" );
        assertFalse( store.put( t5 ) );

        Triple t6 = new TripleImpl(ind2, "hasWeapon", "lancia");
        assertFalse( store.put( t6 ) );


        Triple tKey;
        Triple t;
        Collection<Triple> coll;

        tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertEquals("mark", t.getValue() );

        tKey = new TripleImpl(ind2, "hasCity", V );
        t = store.get( tKey );
        assertEquals("bologna", t.getValue() );

        tKey = new TripleImpl(ind, "hasCar", V );
        t = store.get( tKey );
        assertNull(t);

        tKey = new TripleImpl(ind2, "hasCar", V );
        t = store.get( tKey );
        assertEquals("lancia", t.getValue());



        tKey = new TripleImpl( V, "hasCity", V );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( t3, t4 ) ) );
        assertEquals( 2, coll.size() );


        tKey = new TripleImpl( ind, V, V );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( t1, t2, t3 ) ) );
        assertEquals( 3, coll.size() );


        tKey = new TripleImpl( ind2, V, "lancia" );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( t5, t6 ) ) );
        assertEquals( 2, coll.size() );


        tKey = new TripleImpl( V, V, "lancia" );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( t5, t6 ) ) );
        assertEquals( 2, coll.size() );

        tKey = new TripleImpl( V, V, V );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( t1, t2, t3, t4, t5, t6 ) ) );
        assertEquals( 6, coll.size() );


    }






    @Test
    public void testAddNary() {
        TripleStore store = new TripleStore(200, 0.6f );
        Individual ind = new Individual();

        Triple t1 = new TripleImpl(ind, "hasName", "marc");
        assertFalse( store.put( t1 ) );

        Triple t2 = new TripleImpl(ind, "hasName", "mark");
        assertTrue(store.put(t2));


        Triple t3 = new TripleImpl(ind, "hasName", "daniel");
        store.add(t3);

        Triple t4 = new TripleImpl(ind, "hasCar", "mini");
        store.add( t4 );

        Triple t5 = new TripleImpl(ind, "hasName", "oscar");
        store.add(t5);

        Triple t6 = new TripleImpl(ind, "hasCar", "ferrari");
        store.add( t6 );




        Triple tKey;
        Triple t;
        Collection<Triple> coll;

        tKey = new TripleImpl( ind, "hasName", V );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( new TripleImpl(ind, "hasName", "oscar"),
                                                     new TripleImpl(ind, "hasName", "mark"),
                                                     new TripleImpl(ind, "hasName", "daniel") ) ) );

        assertFalse( store.contains( new TripleImpl(ind, "hasName", "marc") ) );
        assertTrue( store.contains( new TripleImpl(ind, "hasName", "mark") ) );
        assertTrue( store.contains( new TripleImpl(ind, "hasName", "daniel") ) );
        assertTrue( store.contains( new TripleImpl(ind, "hasCar", "mini") ) );
        assertTrue( store.contains( new TripleImpl(ind, "hasName", "oscar") ) );
        assertTrue( store.contains( new TripleImpl(ind, "hasCar", "ferrari") ) );
        assertTrue( store.contains( new TripleImpl(ind, "hasName", "oscar") ) );


        tKey = new TripleImpl( ind, "hasCar", V );
        coll = store.getAll(tKey);
        assertTrue( coll.containsAll( Arrays.asList( new TripleImpl(ind, "hasCar", "mini"),
                                                     new TripleImpl(ind, "hasCar", "ferrari") ) ) );

        store.remove( new TripleImpl(ind, "hasCar", "mini") );

        tKey = new TripleImpl( ind, "hasCar", V );
        coll = store.getAll(tKey);
        assertEquals( 1, coll.size() );

        store.remove( new TripleImpl(ind, "hasCar", "ferrari") );

        tKey = new TripleImpl( ind, "hasCar", V );
        coll = store.getAll(tKey);
        assertEquals( 0, coll.size() );

    }



    
    public static class Individual {
        
    }
}
