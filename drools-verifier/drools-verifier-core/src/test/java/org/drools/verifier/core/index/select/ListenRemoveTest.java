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
package org.drools.verifier.core.index.select;

import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ListenRemoveTest {

    private Listen<Person>                        listen;
    private MultiMap<Value, Person, List<Person>> map;

    private Collection<Person> all;
    private Person             first;
    private Person             last;
    private Person             baby;
    private Person             teenager;
    private Person             grandpa;

    @Before
    public void setUp() throws Exception {
        map = MultiMapFactory.make( true );

        baby = new Person( 0,
                           "baby" );
        teenager = new Person( 15,
                               "teenager" );
        grandpa = new Person( 100,
                              "grandpa" );

        map.put( new Value( 0 ),
                 baby );
        map.put( new Value( 15 ),
                 teenager );
        map.put( new Value( 100 ),
                 grandpa );


        listen = new Listen<>( map,
                               new ExactMatcher( KeyDefinition.newKeyDefinition().withId( "ID" ).build(),
                                                 "notInTheList",
                                                 true ) );

        listen.all( new AllListener<Person>() {
            @Override
            public void onAllChanged( final Collection<Person> all ) {
                ListenRemoveTest.this.all = all;
            }
        } );

        listen.first( new FirstListener<Person>() {
            @Override
            public void onFirstChanged( final Person first ) {
                ListenRemoveTest.this.first = first;
            }
        } );

        listen.last( new LastListener<Person>() {
            @Override
            public void onLastChanged( final Person last ) {
                ListenRemoveTest.this.last = last;
            }
        } );
    }

    @Test
    public void testBeginning() throws Exception {
        map.remove( new Value( 0 ) );

        assertEquals( teenager,
                      first );
        assertNull( last );
        assertEquals( 2, all.size() );
    }

    @Test
    public void testEnd() throws Exception {
        map.remove( new Value( 100 ) );

        assertNull( first );
        assertEquals( teenager,
                      last );
        assertEquals( 2, all.size() );
    }

    @Test
    public void testMiddle() throws Exception {
        map.remove( new Value( 15 ) );

        assertNull( first );
        assertNull( last );
        assertEquals( 2, all.size() );
    }

    class Person {
        int    age;
        String name;

        public Person( final int age,
                       final String name ) {
            this.age = age;
            this.name = name;
        }
    }
}