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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MvelConstraintTestUtil;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleIndexHashTable.FieldIndexHashTableFullIterator;
import org.drools.core.util.index.TupleList;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RightTupleIndexHashTableIteratorTest {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();

    @Test
    public void test1() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "d", Operator.EQUAL, "this", Foo.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0 };
        
        RuleBaseConfiguration config = new RuleBaseConfiguration();

        BetaConstraints betaConstraints = null;
        
        betaConstraints = new SingleBetaConstraints(constraints, config);
        
        BetaMemory betaMemory = betaConstraints.createBetaMemory( config, NodeTypeEnums.JoinNode );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ss = kBase.newStatefulKnowledgeSession();

        InternalFactHandle fh1 = (InternalFactHandle) ss.insert( new Foo( "brie", 1) );
        InternalFactHandle fh2 = (InternalFactHandle) ss.insert( new Foo( "brie", 1) );
        InternalFactHandle fh3 = (InternalFactHandle) ss.insert( new Foo( "soda", 1) );
        InternalFactHandle fh4 = (InternalFactHandle) ss.insert( new Foo( "soda", 1) );
        InternalFactHandle fh5 = (InternalFactHandle) ss.insert( new Foo( "bread", 3) );
        InternalFactHandle fh6 = (InternalFactHandle) ss.insert( new Foo( "bread", 3) );
        InternalFactHandle fh7 = (InternalFactHandle) ss.insert( new Foo( "cream", 3) );
        InternalFactHandle fh8 = (InternalFactHandle) ss.insert( new Foo( "gorda", 15) );
        InternalFactHandle fh9 = (InternalFactHandle) ss.insert( new Foo( "beer", 16) );
        
        InternalFactHandle fh10 = (InternalFactHandle) ss.insert( new Foo( "mars", 0) );
        InternalFactHandle fh11 = (InternalFactHandle) ss.insert( new Foo( "snicker", 0) );
        InternalFactHandle fh12 = (InternalFactHandle) ss.insert( new Foo( "snicker", 0) );
        InternalFactHandle fh13 = (InternalFactHandle) ss.insert( new Foo( "snicker", 0) );
        
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh1, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh2, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh3, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh4, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh5, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh6, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh7, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh8, null ) );
        betaMemory.getRightTupleMemory().add( new RightTupleImpl( fh9, null ) );
        
        TupleIndexHashTable hashTable = (TupleIndexHashTable) betaMemory.getRightTupleMemory();
        // can't create a 0 hashCode, so forcing 
        TupleList rightTupleList = new TupleList();
        rightTupleList.add( new RightTupleImpl( fh10, null) );
        hashTable.getTable()[0] = rightTupleList;
        rightTupleList = new TupleList();
        rightTupleList.add( new RightTupleImpl( fh11, null ) );
        rightTupleList.add( new RightTupleImpl( fh12, null ) );
        rightTupleList.add( new RightTupleImpl( fh13, null ) );
        ((TupleList)hashTable.getTable()[0]).setNext( rightTupleList );
        
        Entry[] table = hashTable.getTable();
        List list = new ArrayList();
        for ( int i = 0; i < table.length; i++ ) {
            if ( table[i] != null ) {
                List entries = new ArrayList();
                entries.add( i );
                Entry entry = table[i];
                while ( entry != null ) {
                    entries.add( entry );
                    entry = entry.getNext();
                }
                list.add( entries.toArray() );
            }
        }
        assertEquals( 5, list.size() );
        
        Object[] entries = (Object[]) list.get( 0 );
        assertEquals( 0, entries[0]);
        assertEquals( 3, entries.length );
        
        entries = (Object[]) list.get( 1 );
        assertEquals( 102, entries[0]);
        assertEquals( 2, entries.length );
        
        entries = (Object[]) list.get( 2 );
        assertEquals( 103, entries[0]);
        assertEquals( 2, entries.length );
        
        entries = (Object[]) list.get( 3 );
        assertEquals( 115, entries[0]);
        assertEquals( 3, entries.length );
        
        entries = (Object[]) list.get( 4 );
        assertEquals( 117, entries[0]);
        assertEquals( 3, entries.length );
        
        
        //System.out.println( entries );

        list = new ArrayList<LeftTupleImpl>();
        Iterator it = betaMemory.getRightTupleMemory().iterator();
        for ( RightTuple rightTuple = ( RightTuple ) it.next(); rightTuple != null; rightTuple = ( RightTuple ) it.next() ) {
            list.add( rightTuple );
        }
        
        assertEquals( 13, list.size() );
             
    }
    
    @Test
    public void testLastBucketInTheTable() {
        // JBRULES-2574
        // setup the entry array with an element in the first bucket, one 
        // in the middle and one in the last bucket
        Entry[] entries = new Entry[10];
        entries[0] = mock( TupleList.class );
        entries[5] = mock( TupleList.class );
        entries[9] = mock( TupleList.class );

        RightTuple[] tuples = new RightTuple[]{mock( RightTuple.class ), mock( RightTuple.class ), mock( RightTuple.class )};

        // set return values for methods
        when( entries[0].getNext() ).thenReturn( null );
        when( ((TupleList) entries[0]).getFirst() ).thenReturn( tuples[0] );
        
        when( entries[5].getNext() ).thenReturn( null );
        when( ((TupleList) entries[5]).getFirst(  ) ).thenReturn( tuples[1] );

        when( entries[9].getNext() ).thenReturn( null );
        when( ((TupleList) entries[9]).getFirst( ) ).thenReturn( tuples[2] );

        // create the mock table for the iterator
        AbstractHashTable table = mock( AbstractHashTable.class );
        when( table.getTable() ).thenReturn( entries );

        // create the iterator
        FieldIndexHashTableFullIterator iterator = new FieldIndexHashTableFullIterator( table );

        // test it
        assertThat( iterator.next(),
                    sameInstance( (Object) tuples[0] ) );
        assertThat( iterator.next(),
                    sameInstance( (Object) tuples[1] ) );
        assertThat( iterator.next(),
                    sameInstance( (Object) tuples[2] ) );
        assertThat( iterator.next(),
                    is( (Object) null ) );

    }

    protected BetaNodeFieldConstraint getConstraint(String identifier,
                                                    Operator operator,
                                                    String fieldName,
                                                    Class clazz) {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        InternalReadAccessor extractor = store.getReader( clazz,
                                                          fieldName );
        Declaration declaration = new Declaration( identifier,
                                                   extractor,
                                                   new Pattern( 0,
                                                                new ClassObjectType( clazz ) ) );

        String expression = fieldName + " " + operator.getOperatorString() + " " + declaration.getIdentifier();
        return new MvelConstraintTestUtil(expression, declaration, extractor);
    }
    
    public static class Foo {
        private String val;
        private int hashCode;
        
        public Foo(String val, int hashCode) {
            this.val = val;
            this.hashCode = hashCode;
        }

        public String getVal() {
            return val;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            Foo other = (Foo) obj;
            if ( hashCode != other.hashCode ) return false;
            if ( val == null ) {
                if ( other.val != null ) return false;
            } else if ( !val.equals( other.val ) ) return false;
            return true;
        }
        
        
    }
       
}
