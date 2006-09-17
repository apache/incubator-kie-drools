/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo.beta;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.DefaultFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.spi.Tuple;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * 
 * ObjectNotEqualConstrLeftMemoryTest
 * TestCase for ObjectNotEqualConstrLeftMemory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public class ObjectNotEqualConstrLeftMemoryTest extends BaseBetaLeftMemoryTestClass {

    protected void setUp() throws Exception {
        super.setUp();
        final ClassFieldExtractor extractor = new ClassFieldExtractor( DummyValueObject.class,
                                                                       "objectAttr" );
        
        Column column = new  Column(0, new ClassObjectType( DummyValueObject.class ) );
        
        final Declaration declaration = new Declaration( "myObject",
                                                         extractor,
                                                         column );
        final Evaluator evaluator = ValueType.OBJECT_TYPE.getEvaluator( Operator.NOT_EQUAL );

        this.memory = new ObjectNotEqualConstrLeftMemory( extractor,
                                                          declaration,
                                                          evaluator,
                                                          this.child );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.ObjectNotEqualConstrLeftMemory.iterator(WorkingMemory, FactHandleImpl)'
     */
    public void testIterator() {
        try {
            this.memory.add( this.workingMemory,
                             this.tuple0 );
            this.memory.add( this.workingMemory,
                             this.tuple1 );
            Assert.assertEquals( "Memory should have size 2",
                                 2,
                                 this.memory.size() );

            Iterator iterator = this.memory.iterator( this.workingMemory,
                                                      this.f0 );

            Assert.assertTrue( "There should be a next element",
                               iterator.hasNext() );
            final Tuple t1 = (Tuple) iterator.next();
            Assert.assertSame( "The first object to return should have been tuple1",
                               this.tuple1,
                               t1 );

            iterator.remove();
            Assert.assertEquals( "Memory should have size 1",
                                 1,
                                 this.memory.size() );

            Assert.assertFalse( "There should not be a next element",
                                iterator.hasNext() );

            try {
                iterator.next();
                Assert.fail( "Iterator is supposed to throw an Exception when there are no more elements" );
            } catch ( final NoSuchElementException nse ) {
                // working fine
            }

            final DummyValueObject obj2 = new DummyValueObject( false,
                                                                "string3",
                                                                30,
                                                                "object3" );
            final DefaultFactHandle f2 = (DefaultFactHandle) this.factory.newFactHandle( obj2 );

            iterator = this.memory.iterator( this.workingMemory,
                                             f2 );
            Assert.assertTrue( "There should be a next element",
                               iterator.hasNext() );

            final Tuple t0 = (Tuple) iterator.next();
            Assert.assertSame( "The first object to return should have been tuple0",
                               this.tuple0,
                               t0 );

            Assert.assertFalse( "There should not be a next element",
                                iterator.hasNext() );

            try {
                iterator.next();
                Assert.fail( "Iterator is supposed to throw an Exception when there are no more elements" );
            } catch ( final NoSuchElementException nse ) {
                // working fine
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
            Assert.fail( "Memory is not supposed to throw any exception during iteration" );
        }

    }

    /*
     * Test method for 'org.drools.reteoo.beta.ObjectNotEqualConstrLeftMemory.selectPossibleMatches(WorkingMemory, FactHandleImpl)'
     */
    public void testSelectPossibleMatches() {
        final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.tuple0 );
        final MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( this.tuple1 );
        final MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( this.tuple1 );

        this.memory.add( this.workingMemory,
                         wrapper0 );
        this.memory.add( this.workingMemory,
                         wrapper1 );

        this.memory.selectPossibleMatches( this.workingMemory,
                                           this.f0 );

        Assert.assertFalse( "Wrapper0 was not a possible match",
                            this.memory.isPossibleMatch( wrapper0 ) );
        Assert.assertTrue( "Wrapper1 was a possible match",
                           this.memory.isPossibleMatch( wrapper1 ) );
        Assert.assertFalse( "Wrapper2 was not a possible match",
                            this.memory.isPossibleMatch( wrapper2 ) );

        final DummyValueObject obj2 = new DummyValueObject( false,
                                                            "string3",
                                                            30,
                                                            "object3" );
        final DefaultFactHandle f2 = (DefaultFactHandle) this.factory.newFactHandle( obj2 );

        this.memory.selectPossibleMatches( this.workingMemory,
                                           f2 );
        Assert.assertTrue( "Wrapper0 was a possible match",
                           this.memory.isPossibleMatch( wrapper0 ) );
        Assert.assertTrue( "Wrapper1 was a possible match",
                           this.memory.isPossibleMatch( wrapper1 ) );
        Assert.assertFalse( "Wrapper2 was not a possible match",
                            this.memory.isPossibleMatch( wrapper2 ) );
    }

    public void testModifyObjectAttribute() {
        final DummyValueObject obj2 = new DummyValueObject( true,
                                                            "string20",
                                                            20,
                                                            "object20" );
        final DefaultFactHandle f2 = (DefaultFactHandle) this.factory.newFactHandle( obj2 );
        final ReteTuple tuple2 = new ReteTuple( f2 );

        this.memory.add( this.workingMemory,
                         tuple2 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        obj2.setObjectAttr( "object20-modified" );

        this.memory.remove( this.workingMemory,
                            tuple2 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
        Assert.assertTrue( "Memory is leaking object references",
                           ((ObjectNotEqualConstrLeftMemory) this.memory).isClean() );
    }

}
