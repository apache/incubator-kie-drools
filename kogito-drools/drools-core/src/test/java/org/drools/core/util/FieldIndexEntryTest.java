/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.Tuple;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.AbstractHashTable.SingleIndex;
import org.drools.core.util.index.TupleList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      null,
                                                      MvelConstraint.INDEX_EVALUATOR );
        final SingleIndex singleIndex = new SingleIndex( new FieldIndex[]{fieldIndex},
                                                         1 );

        final TupleList index = new TupleList( singleIndex, "stilton".hashCode() );

        // Test initial construction
        assertNull( index.getFirst() );
        assertEquals( "stilton".hashCode(),
                      index.hashCode() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );

        // test add
        RightTuple h1RightTuple = new RightTupleImpl( h1, null );
        index.add( h1RightTuple );

        final Tuple entry1 = index.getFirst();
        assertSame( h1,
                    entry1.getFactHandle() );
        assertNull( entry1.getNext() );
        assertSame( entry1,
                    index.get( h1 ) );

        // test get
        final Tuple entry2 = index.get( new RightTupleImpl( h1, null ) );
        assertSame( entry1,
                    entry2 );

        // test remove
        index.remove( h1RightTuple );
        assertNull( index.getFirst() );
    }

    @Test
    public void testTwoEntries() {
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                                  "type" );
        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      null,
                                                      MvelConstraint.INDEX_EVALUATOR );
        final SingleIndex singleIndex = new SingleIndex( new FieldIndex[]{fieldIndex},
                                                         1 );

        final TupleList index = new TupleList( singleIndex, "stilton".hashCode() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h2 = new DefaultFactHandle( 2,
                                                             stilton2 );
        
        RightTuple h1RightTuple = new RightTupleImpl( h1, null );
        RightTuple h2RightTuple = new RightTupleImpl( h2, null );

        // test add
        index.add( h1RightTuple );
        index.add( h2RightTuple );
        assertEquals( h1,
                      index.getFirst().getFactHandle() );
        assertEquals( h2,
                      ((RightTuple) index.getFirst().getNext()).getFactHandle() );

        // test get
        assertEquals( h1,
                      index.get( h1 ).getFactHandle() );
        assertEquals( h2,
                      index.get( h2 ).getFactHandle() );

        // test removal for combinations
        // remove first
        index.remove( h2RightTuple );
        assertEquals( h1RightTuple.getFactHandle(),
                      index.getFirst().getFactHandle() );

        // remove second
        index.add( h2RightTuple );
        index.remove( h1RightTuple );
        assertEquals( h2RightTuple.getFactHandle(),
                      index.getFirst().getFactHandle() );

        // check index type does not change, as this fact is removed
        stilton1.setType( "cheddar" );
    }

    @Test
    public void testThreeEntries() {
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                                  "type" );
        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      null,
                                                      MvelConstraint.INDEX_EVALUATOR );
        final SingleIndex singleIndex = new SingleIndex( new FieldIndex[]{fieldIndex},
                                                         1 );

        final TupleList index = new TupleList( singleIndex, "stilton".hashCode() );

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

        RightTuple h1RightTuple = new RightTupleImpl( h1, null );
        RightTuple h2RightTuple = new RightTupleImpl( h2, null );
        RightTuple h3RightTuple = new RightTupleImpl( h3, null );
        
        // test add
        index.add( h1RightTuple );
        index.add( h2RightTuple );
        index.add( h3RightTuple );
        assertEquals( h1,
                      index.getFirst().getFactHandle() );
        assertEquals( h2,
                      ((RightTuple) index.getFirst().getNext()).getFactHandle() );
        assertEquals( h3,
                      ((RightTuple) index.getFirst().getNext().getNext()).getFactHandle() );

        // test get
        assertEquals( h1,
                      index.get( h1 ).getFactHandle() );
        assertEquals( h2,
                      index.get( h2 ).getFactHandle() );
        assertEquals( h3,
                      index.get( h3 ).getFactHandle() );

        // test removal for combinations
        //remove first
        index.remove( h3RightTuple );
        assertEquals( h1,
                      index.getFirst().getFactHandle() );
        assertEquals( h2,
                      ((RightTuple) index.getFirst().getNext()).getFactHandle() );

        index.add( h3RightTuple );
        index.remove( h2RightTuple );
        assertEquals( h1,
                      index.getFirst().getFactHandle() );
        assertEquals( h3,
                      ((RightTuple) index.getFirst().getNext()).getFactHandle() );

        index.add( h2RightTuple );
        index.remove( h1RightTuple );
        assertEquals( h3,
                      index.getFirst().getFactHandle() );
        assertEquals( h2,
                      ((RightTuple) index.getFirst().getNext()).getFactHandle() );

        index.remove( index.getFirst() );
        // check index type does not change, as this fact is removed
        stilton2.setType( "cheddar" );
    }
}
