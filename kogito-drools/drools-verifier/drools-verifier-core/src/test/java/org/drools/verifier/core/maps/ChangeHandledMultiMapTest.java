/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.maps;

import java.util.ArrayList;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ChangeHandledMultiMapTest {

    private MultiMap<Value, String, List<String>> map;
    private MultiMapChangeHandler.ChangeSet<Value, String> changeSet;

    private int timesCalled = 0;

    @Before
    public void setUp() throws Exception {
        this.timesCalled = 0;

        this.map = MultiMapFactory.make(true );
        this.map.addChangeListener( new MultiMapChangeHandler<Value, String>() {
            @Override
            public void onChange( final ChangeSet<Value, String> changeSet ) {
                ChangeHandledMultiMapTest.this.changeSet = changeSet;
                timesCalled++;
            }
        } );
    }

    @Test
    public void testSize() throws Exception {
        assertNull( changeSet );
        assertEquals( 0, timesCalled );
    }

    @Test
    public void testPut() throws Exception {
        map.put( new Value( "hello" ),
                 "test" );

        assertTrue( changeSet.getAdded().get( new Value( "hello" ) ).contains( "test" ) );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testAddAllValues() throws Exception {
        final ArrayList<String> list = new ArrayList<>();
        list.add( "a" );
        list.add( "b" );
        list.add( "c" );

        map.addAllValues( new Value( "hello" ),
                          list );

        assertEquals( 3, changeSet.getAdded().get( new Value( "hello" ) ).size() );
        assertTrue( changeSet.getAdded().get( new Value( "hello" ) ).contains( "a" ) );
        assertTrue( changeSet.getAdded().get( new Value( "hello" ) ).contains( "b" ) );
        assertTrue( changeSet.getAdded().get( new Value( "hello" ) ).contains( "c" ) );

        assertEquals( 1, timesCalled );
    }
}