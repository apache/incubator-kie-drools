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
package org.drools.mvel.extractors;

import org.drools.base.rule.accessor.RightTupleValueExtractor;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.TupleImpl ;
import org.drools.core.reteoo.RightTuple;
import org.drools.base.rule.Declaration;
import org.drools.core.reteoo.Tuple;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.AbstractHashTable;
import org.drools.base.util.IndexedValueReader;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.index.TupleList;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldIndexEntryTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testSingleEntry() {
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                                  "type" );

        final IndexedValueReader fieldIndex = new IndexedValueReader(new Declaration("id", extractor, null), new RightTupleValueExtractor(extractor));
        final SingleIndex singleIndex = new SingleIndex( new IndexedValueReader[]{fieldIndex},
                                                         1 );

        Tuple tuple = new RightTuple(new DefaultFactHandle(1, new Cheese("stilton", 10) ) );
        final TupleList index = new AbstractHashTable.IndexTupleList( singleIndex, new AbstractHashTable.SingleHashEntry("stilton".hashCode(), "stilton") );

        // Test initial construction
        assertThat(index.getFirst()).isNull();

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );

        // test add
        TupleImpl  h1RightTuple = new RightTuple(h1, null );
        index.add( h1RightTuple );

        final Tuple entry1 = index.getFirst();
        assertThat(entry1.getFactHandle()).isSameAs(h1);
        assertThat(entry1.getNext()).isNull();
        assertThat(index.get(h1)).isSameAs(entry1);

        // test get
        final Tuple entry2 = index.get( new RightTuple(h1, null ));
        assertThat(entry2).isSameAs(entry1);

        // test remove
        index.remove( h1RightTuple );
        assertThat(index.getFirst()).isNull();
    }

    @Test
    public void testTwoEntries() {
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                                  "type" );
        final IndexedValueReader fieldIndex = new IndexedValueReader(new Declaration("id", extractor, null), new RightTupleValueExtractor(extractor));
        final SingleIndex singleIndex = new SingleIndex( new IndexedValueReader[]{fieldIndex},
                                                         1 );

        Tuple tuple = new RightTuple(new DefaultFactHandle(1, new Cheese("stilton", 10) ) );
        final TupleList index = new AbstractHashTable.IndexTupleList( singleIndex, new AbstractHashTable.SingleHashEntry("stilton".hashCode(), "stilton") );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h2 = new DefaultFactHandle( 2,
                                                             stilton2 );
        
        TupleImpl  h1RightTuple = new RightTuple(h1, null );
        TupleImpl  h2RightTuple = new RightTuple(h2, null );

        // test add
        index.add( h1RightTuple );
        index.add( h2RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h1);
        assertThat(index.getFirst().getNext().getFactHandle()).isEqualTo(h2);

        // test get
        assertThat(index.get(h1).getFactHandle()).isEqualTo(h1);
        assertThat(index.get(h2).getFactHandle()).isEqualTo(h2);

        // test removal for combinations
        // remove first
        index.remove( h2RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h1RightTuple.getFactHandle());

        // remove second
        index.add( h2RightTuple );
        index.remove( h1RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h2RightTuple.getFactHandle());

        // check index type does not change, as this fact is removed
        stilton1.setType( "cheddar" );
    }

    @Test
    public void testThreeEntries() {
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                                  "type" );
        final IndexedValueReader fieldIndex = new IndexedValueReader(new Declaration("id", extractor, null), new RightTupleValueExtractor(extractor));
        final SingleIndex singleIndex = new SingleIndex( new IndexedValueReader[]{fieldIndex},
                                                         1 );

        Tuple tuple = new RightTuple(new DefaultFactHandle(1, new Cheese("stilton", 10) ) );
        final TupleList index = new AbstractHashTable.IndexTupleList( singleIndex, new AbstractHashTable.SingleHashEntry("stilton".hashCode(), "stilton") );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h2 = new DefaultFactHandle( 2,
                                                             stilton2 );
        final Cheese stilton3 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h3 = new DefaultFactHandle( 3,
                                                             stilton3 );

        TupleImpl  h1RightTuple = new RightTuple(h1, null );
        TupleImpl  h2RightTuple = new RightTuple(h2, null );
        TupleImpl  h3RightTuple = new RightTuple(h3, null );
        
        // test add
        index.add( h1RightTuple );
        index.add( h2RightTuple );
        index.add( h3RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h1);
        assertThat(index.getFirst().getNext().getFactHandle()).isEqualTo(h2);
        assertThat(index.getFirst().getNext().getNext().getFactHandle()).isEqualTo(h3);

        // test get
        assertThat(index.get(h1).getFactHandle()).isEqualTo(h1);
        assertThat(index.get(h2).getFactHandle()).isEqualTo(h2);
        assertThat(index.get(h3).getFactHandle()).isEqualTo(h3);

        // test removal for combinations
        //remove first
        index.remove( h3RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h1);
        assertThat(index.getFirst().getNext().getFactHandle()).isEqualTo(h2);

        index.add( h3RightTuple );
        index.remove( h2RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h1);
        assertThat(index.getFirst().getNext().getFactHandle()).isEqualTo(h3);

        index.add( h2RightTuple );
        index.remove( h1RightTuple );
        assertThat(index.getFirst().getFactHandle()).isEqualTo(h3);
        assertThat(index.getFirst().getNext().getFactHandle()).isEqualTo(h2);

        index.remove( index.getFirst() );
        // check index type does not change, as this fact is removed
        stilton2.setType( "cheddar" );
    }
}
