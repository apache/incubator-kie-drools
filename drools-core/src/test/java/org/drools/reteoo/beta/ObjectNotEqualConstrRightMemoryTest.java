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
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * 
 * ObjectNotEqualConstrRightMemoryTest
 * A test case for ObjectNotEqualConstrRightMemory 
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public class ObjectNotEqualConstrRightMemoryTest extends BaseBetaRightMemoryTestClass {

    protected void setUp() throws Exception {
        super.setUp();
        final ClassFieldExtractor extractor = new ClassFieldExtractor( DummyValueObject.class,
                                                                       "objectAttr" );
        
        Column column = new  Column(0, new ClassObjectType( DummyValueObject.class ) );
        
        final Declaration declaration = new Declaration( "myObject",
                                                         extractor,
                                                         column );
        
        final Evaluator evaluator = ValueType.OBJECT_TYPE.getEvaluator( Operator.NOT_EQUAL );

        this.memory = new ObjectNotEqualConstrRightMemory( extractor,
                                                           declaration,
                                                           evaluator,
                                                           this.child );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.ObjectNotEqualConstrRightMemory.iterator(WorkingMemory, ReteTuple)'
     */
    public void testIterator() {
        try {
            this.memory.add( this.workingMemory,
                             this.matches0 );
            this.memory.add( this.workingMemory,
                             this.matches1 );
            Assert.assertEquals( "Memory should have size 2",
                                 2,
                                 this.memory.size() );

            Iterator iterator = this.memory.iterator( this.workingMemory,
                                                      this.tuple0 );

            Assert.assertTrue( "There should be a next element",
                               iterator.hasNext() );
            ObjectMatches om1 = (ObjectMatches) iterator.next();
            Assert.assertSame( "The first object to return should have been matches1",
                               this.matches1,
                               om1 );

            try {
                iterator.remove();
                Assert.fail( "Right side memory Iterators are not supposed to support remove()" );
            } catch ( final UnsupportedOperationException uoe ) {
                // working fine
            }
            Assert.assertEquals( "Memory should have size 2",
                                 2,
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
            final ReteTuple tuple2 = new ReteTuple( f2 );

            iterator = this.memory.iterator( this.workingMemory,
                                             tuple2 );
            Assert.assertTrue( "There should be a next element",
                               iterator.hasNext() );

            final ObjectMatches om0 = (ObjectMatches) iterator.next();
            Assert.assertSame( "The first object to return should have been matches0",
                               this.matches0,
                               om0 );

            om1 = (ObjectMatches) iterator.next();
            Assert.assertSame( "The second object to return should have been matches1",
                               this.matches1,
                               om1 );

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
     * Test method for 'org.drools.reteoo.beta.ObjectNotEqualConstrRightMemory.selectPossibleMatches(WorkingMemory, ReteTuple)'
     */
    public void testSelectPossibleMatches() {
        final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.matches0 );
        final MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( this.matches1 );
        final MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( this.matches1 );

        this.memory.add( this.workingMemory,
                         wrapper0 );
        this.memory.add( this.workingMemory,
                         wrapper1 );

        this.memory.selectPossibleMatches( this.workingMemory,
                                           this.tuple0 );

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
        final ReteTuple tuple2 = new ReteTuple( f2 );

        this.memory.selectPossibleMatches( this.workingMemory,
                                           tuple2 );
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
        final ObjectMatches matches2 = new ObjectMatches( f2 );

        this.memory.add( this.workingMemory,
                         matches2 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        obj2.setObjectAttr( "object20-modified" );

        this.memory.remove( this.workingMemory,
                            matches2 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
        Assert.assertTrue( "Memory is leaking object references",
                           ((ObjectNotEqualConstrRightMemory) this.memory).isClean() );
    }
}
