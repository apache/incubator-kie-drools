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

import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.keys.Values;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeHandledMultiMapPreExistingDataTest {


    private MultiMap<Value, String, List<String>> map;
    private MultiMapChangeHandler.ChangeSet<Value, String> changeSet;

    private int timesCalled = 0;

    @Before
    public void setUp() throws Exception {
        this.timesCalled = 0;

        this.map = MultiMapFactory.make(true );

        this.map.put( new Value( "hello" ), "a" );
        this.map.put( new Value( "ok" ), "b" );
        this.map.put( new Value( "ok" ), "c" );

        this.map.addChangeListener( new MultiMapChangeHandler<Value, String>() {
            @Override
            public void onChange( final ChangeSet<Value, String> changeSet ) {
                ChangeHandledMultiMapPreExistingDataTest.this.changeSet = changeSet;
                timesCalled++;
            }
        } );
    }

    @Test
    public void move() throws Exception {
        map.move( new Values<>(new Value("ok" ) ),
                  new Values<>( new Value( "hello" ) ),
                  "b" );

        assertEquals( 1, timesCalled );

        // Check data moved
        assertEquals( 2, map.get( new Value( "hello" ) ).size() );
        assertTrue( map.get( new Value( "hello" ) ).contains( "a" ) );
        assertTrue( map.get( new Value( "hello" ) ).contains( "b" ) );
        assertEquals( 1, map.get( new Value( "ok" ) ).size() );
        assertTrue( map.get( new Value( "ok" ) ).contains( "c" ) );

        // Updates should be up to date
        assertEquals( 1, changeSet.getRemoved().get( new Value( "ok" ) ).size() );
        assertEquals( 1, changeSet.getAdded().get( new Value( "hello" ) ).size() );
    }

    @Test
    public void testRemove() throws Exception {
        map.remove( new Value( "ok" ) );

        assertEquals( 2, changeSet.getRemoved().get( new Value( "ok" ) ).size() );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testRemoveValue() throws Exception {
        map.removeValue( new Value( "ok" ),
                         "b" );

        assertEquals( 1, changeSet.getRemoved().get( new Value( "ok" ) ).size() );
        assertTrue( changeSet.getRemoved().get( new Value( "ok" ) ).contains( "b" ) );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testClear() throws Exception {
        map.clear();

        assertEquals( 1, changeSet.getRemoved().get( new Value( "hello" ) ).size() );
        assertTrue( changeSet.getRemoved().get( new Value( "hello" ) ).contains( "a" ) );
        assertEquals( 2, changeSet.getRemoved().get( new Value( "ok" ) ).size() );
        assertTrue( changeSet.getRemoved().get( new Value( "ok" ) ).contains( "b" ) );
        assertTrue( changeSet.getRemoved().get( new Value( "ok" ) ).contains( "c" ) );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testMerge() throws Exception {
        final MultiMap<Value, String, List<String>> other = MultiMapFactory.make();
        other.put( new Value( "hello" ), "d" );
        other.put( new Value( "ok" ), "e" );
        other.put( new Value( "newOne" ), "f" );

        MultiMap.merge( map,
                        other );

        assertEquals( 1, changeSet.getAdded().get( new Value( "hello" ) ).size() );
        assertTrue( changeSet.getAdded().get( new Value( "hello" ) ).contains( "d" ) );
        assertEquals( 1, changeSet.getAdded().get( new Value( "ok" ) ).size() );
        assertTrue( changeSet.getAdded().get( new Value( "ok" ) ).contains( "e" ) );
        assertEquals( 1, changeSet.getAdded().get( new Value( "newOne" ) ).size() );
        assertTrue( changeSet.getAdded().get( new Value( "newOne" ) ).contains( "f" ) );

        assertEquals( 1, timesCalled );
    }
}