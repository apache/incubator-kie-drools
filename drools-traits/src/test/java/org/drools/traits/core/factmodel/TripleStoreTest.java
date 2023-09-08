/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.factmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.rule.Variable;

import static org.assertj.core.api.Assertions.assertThat;

public class TripleStoreTest {

    private Variable V = Variable.v;
    
    @Test
    public void testPutAndGet() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");

        assertThat(store.put(t)).isFalse();
        
        Triple tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("mark");
    }
    
    @Test
    public void testPutAndGetWithExisting() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");

        assertThat(store.put(t)).isFalse();
        
        Triple tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("mark");
        
        t = new TripleImpl(ind, "hasName", "davide");

        assertThat(store.put(t)).isTrue();
        
        tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("davide");        
    }  
    
    @Test
    public void testPutAndGetandRemove() {
        // We know it needs to hold a lot of triples, so instantiate it with huge capacity.
        // A lower capacity ensures a larger capacity per number of triples, i.e. less collision - default is 0.75f
        TripleStore store = new TripleStore(10*100*1000, 0.6f );
        Individual ind = new Individual();
        Triple t = new TripleImpl(ind, "hasName", "mark");

        assertThat(store.put(t)).isFalse();
        
        Triple tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("mark");
        
        t = new TripleImpl(ind, "hasName", V );
        assertThat(store.removeAll(t)).isEqualTo(1);

        assertThat(store.remove(t)).isFalse(); // try again and make sure it's false.
        
        
        tKey = new TripleImpl(ind, "hasName", V );
        assertThat(store.get(tKey)).isNull();        
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
                assertThat(store.put(t)).isFalse();                
            }
        }

        assertThat(store.size()).isEqualTo(instanceLength * tripleLength);
        
        for ( int i = 0; i < instanceLength; i++) {
            for (int j = 0; j < tripleLength; j++) {  
                t = new TripleImpl(inds.get( i ),getPropertyName(j), V );
                store.removeAll( t );
            }
        }

        assertThat(store.size()).isEqualTo(0);    
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
        assertThat(store.put(t1)).isFalse();

        Triple t2 = new TripleImpl(ind, "hasAge", "35");
        assertThat(store.put(t2)).isFalse();

        Triple t3 = new TripleImpl(ind, "hasCity", "london");
        assertThat(store.put(t3)).isFalse();

        Individual ind2 = new Individual();

        Triple t4 = new TripleImpl(ind2, "hasCity", "bologna");
        assertThat(store.put(t4)).isFalse();

        Triple t5 = new TripleImpl(ind2, "hasCar", "lancia" );
        assertThat(store.put(t5)).isFalse();

        Triple t6 = new TripleImpl(ind2, "hasWeapon", "lancia");
        assertThat(store.put(t6)).isFalse();


        Triple tKey;
        Triple t;
        Collection<Triple> coll;

        tKey = new TripleImpl(ind, "hasName", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("mark");

        tKey = new TripleImpl(ind2, "hasCity", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("bologna");

        tKey = new TripleImpl(ind, "hasCar", V );
        t = store.get( tKey );
        assertThat(t).isNull();

        tKey = new TripleImpl(ind2, "hasCar", V );
        t = store.get( tKey );
        assertThat(t.getValue()).isEqualTo("lancia");



        tKey = new TripleImpl( V, "hasCity", V );
        coll = store.getAll(tKey);
        assertThat(coll.containsAll(Arrays.asList(t3, t4))).isTrue();
        assertThat(coll.size()).isEqualTo(2);


        tKey = new TripleImpl( ind, V, V );
        coll = store.getAll(tKey);
        assertThat(coll.containsAll(Arrays.asList(t1, t2, t3))).isTrue();
        assertThat(coll.size()).isEqualTo(3);


        tKey = new TripleImpl( ind2, V, "lancia" );
        coll = store.getAll(tKey);
        assertThat(coll.containsAll(Arrays.asList(t5, t6))).isTrue();
        assertThat(coll.size()).isEqualTo(2);


        tKey = new TripleImpl( V, V, "lancia" );
        coll = store.getAll(tKey);
        assertThat(coll.containsAll(Arrays.asList(t5, t6))).isTrue();
        assertThat(coll.size()).isEqualTo(2);

        tKey = new TripleImpl( V, V, V );
        coll = store.getAll(tKey);
        assertThat(coll.containsAll(Arrays.asList(t1, t2, t3, t4, t5, t6))).isTrue();
        assertThat(coll.size()).isEqualTo(6);


    }






    @Test
    public void testAddNary() {
        TripleStore store = new TripleStore(200, 0.6f );
        Individual ind = new Individual();

        Triple t1 = new TripleImpl(ind, "hasName", "marc");
        assertThat(store.put(t1)).isFalse();

        Triple t2 = new TripleImpl(ind, "hasName", "mark");
        assertThat(store.put(t2)).isTrue();


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
        assertThat(coll.containsAll(Arrays.asList(new TripleImpl(ind, "hasName", "oscar"),
                new TripleImpl(ind, "hasName", "mark"),
                new TripleImpl(ind, "hasName", "daniel")))).isTrue();

        assertThat(store.contains(new TripleImpl(ind, "hasName", "marc"))).isFalse();
        assertThat(store.contains(new TripleImpl(ind, "hasName", "mark"))).isTrue();
        assertThat(store.contains(new TripleImpl(ind, "hasName", "daniel"))).isTrue();
        assertThat(store.contains(new TripleImpl(ind, "hasCar", "mini"))).isTrue();
        assertThat(store.contains(new TripleImpl(ind, "hasName", "oscar"))).isTrue();
        assertThat(store.contains(new TripleImpl(ind, "hasCar", "ferrari"))).isTrue();
        assertThat(store.contains(new TripleImpl(ind, "hasName", "oscar"))).isTrue();


        tKey = new TripleImpl( ind, "hasCar", V );
        coll = store.getAll(tKey);
        assertThat(coll.containsAll(Arrays.asList(new TripleImpl(ind, "hasCar", "mini"),
                new TripleImpl(ind, "hasCar", "ferrari")))).isTrue();

        store.remove( new TripleImpl(ind, "hasCar", "mini") );

        tKey = new TripleImpl( ind, "hasCar", V );
        coll = store.getAll(tKey);
        assertThat(coll.size()).isEqualTo(1);

        store.remove( new TripleImpl(ind, "hasCar", "ferrari") );

        tKey = new TripleImpl( ind, "hasCar", V );
        coll = store.getAll(tKey);
        assertThat(coll.size()).isEqualTo(0);

    }



    
    public static class Individual {
        
    }
}
