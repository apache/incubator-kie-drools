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

import javax.naming.OperationNotSupportedException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.ReteooFactHandleFactory;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.Tuple;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * 
 * BaseBetaRightMemoryTestClass
 * A base class for test cases testing BetaRightMemory implementations
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public abstract class BaseBetaRightMemoryTestClass extends TestCase {
    protected ReteooWorkingMemory workingMemory;
    protected BetaRightMemory     memory;
    protected MockBetaRightMemory child;
    protected DummyValueObject    obj0;
    protected DummyValueObject    obj1;
    protected DummyValueObject    obj2;
    protected FactHandleFactory   factory;
    protected DefaultFactHandle   f0;
    protected DefaultFactHandle   f1;
    protected DefaultFactHandle   f2;
    protected ObjectMatches       matches0;
    protected ObjectMatches       matches1;
    protected ObjectMatches       matches2;
    protected Tuple           tuple0;
    protected Tuple           tuple1;
    protected Tuple           tuple2;

    public BaseBetaRightMemoryTestClass() {
        this.memory = null;
        this.child = new MockBetaRightMemory();
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.workingMemory = new ReteooWorkingMemory( 0, 
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        this.obj0 = new DummyValueObject( true,
                                          "string1",
                                          10,
                                          "object1" );
        this.obj1 = new DummyValueObject( true,
                                          "string2",
                                          20,
                                          "object2" );
        this.obj2 = new DummyValueObject( true,
                                          null,
                                          20,
                                          null );

        this.factory = new ReteooFactHandleFactory();
        this.f0 = (DefaultFactHandle) this.factory.newFactHandle( this.obj0 );

        this.f1 = (DefaultFactHandle) this.factory.newFactHandle( this.obj1 );

        this.f2 = (DefaultFactHandle) this.factory.newFactHandle( this.obj2 );

        this.matches0 = new ObjectMatches( this.f0 );
        this.matches1 = new ObjectMatches( this.f1 );
        this.matches2 = new ObjectMatches( this.f2 );

        this.tuple0 = new ReteTuple( this.f0 );
        this.tuple1 = new ReteTuple( this.f1 );
        this.tuple2 = new ReteTuple( this.f2 );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.add(WorkingMemory, ObjectMatches)'
     */
    public void testAddWorkingMemoryObjectMatches() {
        this.memory.add( this.workingMemory,
                         this.matches0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.matches1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.matches2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.remove(WorkingMemory, ObjectMatches)'
     */
    public void testRemoveWorkingMemoryObjectMatches() {
        this.memory.add( this.workingMemory,
                         this.matches0 );
        this.memory.add( this.workingMemory,
                         this.matches1 );
        this.memory.add( this.workingMemory,
                         this.matches2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.matches0 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.matches2 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.matches1 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.add(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testAddWorkingMemoryMultiLinkedListNodeWrapper() {
        final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.matches0 );
        final MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( this.matches1 );
        final MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( this.matches2 );

        this.memory.add( this.workingMemory,
                         wrapper0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         wrapper2 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         wrapper1 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.remove(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testRemoveWorkingMemoryMultiLinkedListNodeWrapper() {
        final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.matches0 );
        final MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( this.matches1 );
        final MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( this.matches2 );

        this.memory.add( this.workingMemory,
                         wrapper0 );
        this.memory.add( this.workingMemory,
                         wrapper1 );
        this.memory.add( this.workingMemory,
                         wrapper2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            wrapper1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            wrapper2 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            wrapper0 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.remove(WorkingMemory, ObjectMatches)'
     */
    public void testRemoveUnexistingObjectMatches() {
        try {
            this.memory.remove( this.workingMemory,
                                this.matches0 );
            Assert.fail( "Trying to remove an element that does not exist in the memory should throw an exception" );
        } catch ( final Exception e ) {
            // everything is fine
        }
        try {
            final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.matches0 );
            this.memory.remove( this.workingMemory,
                                wrapper0 );
            Assert.fail( "Trying to remove an element that does not exist in the memory should throw an exception" );
        } catch ( final Exception e ) {
            // everything is fine
        }
        try {
            this.memory.remove( this.workingMemory,
                                (ObjectMatches) null );
            Assert.fail( "Trying to remove a null element from memory should throw an exception" );
        } catch ( final Exception e ) {
            // everything is fine
        }
        try {
            this.memory.remove( this.workingMemory,
                                (MultiLinkedListNodeWrapper) null );
            Assert.fail( "Trying to remove a null element from memory should throw an exception" );
        } catch ( final Exception e ) {
            // everything is fine
        }
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.isEmpty()'
     */
    public void testIsEmpty() {
        Assert.assertTrue( "Memory should be empty",
                           this.memory.isEmpty() );

        this.memory.add( this.workingMemory,
                         this.matches0 );
        Assert.assertFalse( "Memory should not be empty",
                            this.memory.isEmpty() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.size()'
     */
    public void testSize() {
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.matches0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.matches1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.matches0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.matches1 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
    }

    public void testParameterlessIterator() {
        try {
            this.memory.add( this.workingMemory,
                             this.matches0 );
            this.memory.add( this.workingMemory,
                             this.matches1 );
            this.memory.add( this.workingMemory,
                             this.matches2 );
            Assert.assertEquals( "Memory should have size 3",
                                 3,
                                 this.memory.size() );

            final Iterator i = this.memory.iterator();
            Assert.assertTrue( "There should be a next match",
                               i.hasNext() );
            ObjectMatches matches = (ObjectMatches) i.next();
            Assert.assertSame( "Wrong returned match",
                               this.matches0,
                               matches );

            Assert.assertTrue( "There should be a next match",
                               i.hasNext() );
            matches = (ObjectMatches) i.next();
            Assert.assertSame( "Wrong returned match",
                               this.matches1,
                               matches );

            Assert.assertTrue( "There should be a next match",
                               i.hasNext() );
            matches = (ObjectMatches) i.next();
            Assert.assertSame( "Wrong returned match",
                               this.matches2,
                               matches );

            Assert.assertFalse( "There should not be a next match",
                                i.hasNext() );
        } catch ( final UnsupportedOperationException e ) {
            Assert.fail( "Beta memory was not supposed to throw any exception: " + e.getMessage() );
        } catch ( final ClassCastException e ) {
            Assert.fail( "BetaRightMemory was not supposed to throw ClassCastException: " + e.getMessage() );
        }
    }

    public void testSelectPossibleMatches2() {
        final int counter = this.child.getCounter();
        this.memory.selectPossibleMatches( this.workingMemory,
                                           this.tuple0 );
        Assert.assertEquals( "Should have called inner memory",
                             counter + 1,
                             this.child.getCounter() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.selectPossibleMatches(WorkingMemory, ReteTuple)'
     */
    public abstract void testSelectPossibleMatches();

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.iterator(WorkingMemory, ReteTuple)'
     */
    public abstract void testIterator();

    /**
     * Add an object to the index, then change indexed attribute, then do 
     * a remove;
     */
    public abstract void testModifyObjectAttribute();

    public static class MockBetaRightMemory
        implements
        BetaRightMemory {
        private int callCounter = 0;

        public void add(final WorkingMemory workingMemory,
                        final ObjectMatches matches) {
            this.callCounter++;
        }

        public void add(final WorkingMemory workingMemory,
                        final MultiLinkedListNodeWrapper matches) {
            this.callCounter++;
        }

        public boolean isEmpty() {
            this.callCounter++;
            return false;
        }

        public boolean isPossibleMatch(final MultiLinkedListNodeWrapper matches) {
            this.callCounter++;
            return true;
        }

        public Iterator iterator(final WorkingMemory workingMemory,
                                 final Tuple tuple) {
            this.callCounter++;
            return null;
        }

        public Iterator iterator() {
            this.callCounter++;
            return null;
        }

        public void remove(final WorkingMemory workingMemory,
                           final ObjectMatches matches) {
            this.callCounter++;
        }

        public void remove(final WorkingMemory workingMemory,
                           final MultiLinkedListNodeWrapper matches) {
            this.callCounter++;
        }

        public void selectPossibleMatches(final WorkingMemory workingMemory,
                                          final Tuple tuple) {
            this.callCounter++;
        }

        public int size() {
            return 0;
        }

        public int getCounter() {
            return this.callCounter;
        }

        public BetaRightMemory getInnerMemory() throws OperationNotSupportedException {
            return null;
        }

        public void setInnerMemory(final BetaRightMemory innerMemory) throws OperationNotSupportedException {
        }
    }
}
