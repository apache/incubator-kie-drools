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

import org.drools.WorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteooFactHandleFactory;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.spi.FactHandleFactory;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * BaseBetaLeftMemoryTest
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public abstract class BaseBetaLeftMemoryTestClass extends TestCase {
    protected ReteooWorkingMemory workingMemory;
    protected BetaLeftMemory      memory;
    protected MockBetaLeftMemory  child;
    protected DummyValueObject    obj0;
    protected DummyValueObject    obj1;
    protected DummyValueObject    obj2;
    protected FactHandleFactory   factory;
    protected DefaultFactHandle   f0;
    protected DefaultFactHandle   f1;
    protected DefaultFactHandle   f2;
    protected ReteTuple           tuple0;
    protected ReteTuple           tuple1;
    protected ReteTuple           tuple2;

    public BaseBetaLeftMemoryTestClass() {
        this.memory = null;
        this.child = new MockBetaLeftMemory();
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.workingMemory = new ReteooWorkingMemory( new ReteooRuleBase() );
        this.obj0 = new DummyValueObject( true,
                                          "string1",
                                          10,
                                          "object1" );
        this.obj1 = new DummyValueObject( true,
                                          "string2",
                                          20,
                                          "object2" );
        this.obj2 = new DummyValueObject( false,
                                          null,
                                          0,
                                          null );

        this.factory = new ReteooFactHandleFactory();
        this.f0 = (DefaultFactHandle) this.factory.newFactHandle( this.obj0 );

        this.f1 = (DefaultFactHandle) this.factory.newFactHandle( this.obj1 );

        this.f2 = (DefaultFactHandle) this.factory.newFactHandle( this.obj2 );

        this.tuple0 = new ReteTuple( this.f0 );
        this.tuple1 = new ReteTuple( this.f1 );
        this.tuple2 = new ReteTuple( this.f2 );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.add(WorkingMemory, ReteTuple)'
     */
    public void testAddWorkingMemoryReteTuple() {
        this.memory.add( this.workingMemory,
                         this.tuple0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.tuple1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.tuple2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, ReteTuple)'
     */
    public void testRemoveWorkingMemoryReteTuple() {
        this.memory.add( this.workingMemory,
                         this.tuple0 );
        this.memory.add( this.workingMemory,
                         this.tuple1 );
        this.memory.add( this.workingMemory,
                         this.tuple2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.tuple0 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.tuple2 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.tuple1 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, ReteTuple)'
     */
    public void testRemoveUnexistingTuple() {
        try {
            this.memory.remove( this.workingMemory,
                                this.tuple0 );
            Assert.fail( "Trying to remove an element that is not in the memory should throw an exception" );
        } catch ( final Exception e ) {
            // everything is fine
        }
        try {
            final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.tuple0 );
            this.memory.remove( this.workingMemory,
                                wrapper0 );
            Assert.fail( "Trying to remove an element that is not in the memory should throw an exception" );
        } catch ( final Exception e ) {
            // everything is fine
        }
        try {
            this.memory.remove( this.workingMemory,
                                (ReteTuple) null );
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
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.add(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testAddWorkingMemoryMultiLinkedListNodeWrapper() {
        final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.tuple0 );
        final MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( this.tuple1 );
        final MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( this.tuple2 );

        this.memory.add( this.workingMemory,
                         wrapper0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         wrapper1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         wrapper2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testRemoveWorkingMemoryMultiLinkedListNodeWrapper() {
        final MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( this.tuple0 );
        final MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( this.tuple1 );
        final MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( this.tuple2 );

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
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.isEmpty()'
     */
    public void testIsEmpty() {
        Assert.assertTrue( "Memory should be empty",
                           this.memory.isEmpty() );

        this.memory.add( this.workingMemory,
                         this.tuple0 );
        Assert.assertFalse( "Memory should not be empty",
                            this.memory.isEmpty() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.size()'
     */
    public void testSize() {
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.tuple0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         this.tuple1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.tuple0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.remove( this.workingMemory,
                            this.tuple1 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
    }

    public void testParameterlessIterator() {
        try {
            final DummyValueObject obj3 = new DummyValueObject( false,
                                                                "string2",
                                                                20,
                                                                "object2" );
            final DefaultFactHandle f3 = (DefaultFactHandle) this.factory.newFactHandle( obj3 );
            final ReteTuple tuple3 = new ReteTuple( f3 );

            this.memory.add( this.workingMemory,
                             this.tuple0 );
            this.memory.add( this.workingMemory,
                             this.tuple1 );
            this.memory.add( this.workingMemory,
                             this.tuple2 );
            this.memory.add( this.workingMemory,
                             tuple3 );
            Assert.assertEquals( "Memory should have size 4",
                                 4,
                                 this.memory.size() );

            final Iterator i = this.memory.iterator();
            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            ReteTuple tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               this.tuple0,
                               tuple );

            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               this.tuple1,
                               tuple );

            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               this.tuple2,
                               tuple );

            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               tuple3,
                               tuple );

            Assert.assertFalse( "There should not be a next tuple",
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
                                           this.f0 );
        Assert.assertEquals( "Should have called inner memory",
                             counter + 1,
                             this.child.getCounter() );
    }

    public abstract void testIterator();

    public abstract void testSelectPossibleMatches();

    /**
     * Add an object to the index, then change indexed attribute, then do 
     * a remove;
     */
    public abstract void testModifyObjectAttribute();

    public static class MockBetaLeftMemory
        implements
        BetaLeftMemory {
        private int callCounter = 0;

        public void add(final WorkingMemory workingMemory,
                        final MultiLinkedListNodeWrapper tuple) {
            this.callCounter++;
        }

        public void add(final WorkingMemory workingMemory,
                        final ReteTuple tuple) {
            this.callCounter++;
        }

        public boolean isEmpty() {
            this.callCounter++;
            return false;
        }

        public boolean isPossibleMatch(final MultiLinkedListNodeWrapper tuple) {
            this.callCounter++;
            return true;
        }

        public Iterator iterator(final WorkingMemory workingMemory,
                                 final InternalFactHandle handle) {
            this.callCounter++;
            return null;
        }

        public Iterator iterator() {
            this.callCounter++;
            return null;
        }

        public void remove(final WorkingMemory workingMemory,
                           final MultiLinkedListNodeWrapper tuple) {
            this.callCounter++;
        }

        public void remove(final WorkingMemory workingMemory,
                           final ReteTuple tuple) {
            this.callCounter++;
        }

        public void selectPossibleMatches(final WorkingMemory workingMemory,
                                          final InternalFactHandle handle) {
            this.callCounter++;
        }

        public int size() {
            this.callCounter++;
            return 0;
        }

        public int getCounter() {
            return this.callCounter;
        }

        public BetaLeftMemory getInnerMemory() throws OperationNotSupportedException {
            return null;
        }

        public void setInnerMemory(final BetaLeftMemory innerMemory) throws OperationNotSupportedException {
        }
    };

}
