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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.reteoo.DefaultFactHandleFactory;
import org.drools.reteoo.FactHandleImpl;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.RuleBaseImpl;
import org.drools.reteoo.WorkingMemoryImpl;
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
    protected WorkingMemoryImpl workingMemory;
    protected BetaLeftMemory    memory;
    protected DummyValueObject  obj0;
    protected DummyValueObject  obj1;
    protected DummyValueObject  obj2;
    protected FactHandleFactory factory;
    protected FactHandleImpl    f0;
    protected FactHandleImpl    f1;
    protected FactHandleImpl    f2;
    protected ReteTuple         tuple0;
    protected ReteTuple         tuple1;
    protected ReteTuple         tuple2;

    public BaseBetaLeftMemoryTestClass() {
        this.memory = null;
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        obj0 = new DummyValueObject( true,
                                     "string1",
                                     10,
                                     "object1" );
        obj1 = new DummyValueObject( true,
                                     "string2",
                                     20,
                                     "object2" );
        obj2 = new DummyValueObject( false,
                                     null,
                                     0,
                                     null );

        factory = new DefaultFactHandleFactory();
        f0 = (FactHandleImpl) factory.newFactHandle( 0 );
        f0.setObject( obj0 );

        f1 = (FactHandleImpl) factory.newFactHandle( 1 );
        f1.setObject( obj1 );

        f2 = (FactHandleImpl) factory.newFactHandle( 2 );
        f2.setObject( obj2 );

        tuple0 = new ReteTuple( f0 );
        tuple1 = new ReteTuple( f1 );
        tuple2 = new ReteTuple( f2 );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.add(WorkingMemory, ReteTuple)'
     */
    public void testAddWorkingMemoryReteTuple() {
        this.memory.add( this.workingMemory,
                         tuple0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         tuple1 );
        Assert.assertEquals( "Memory should have size 2",
                             2,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         tuple2 );
        Assert.assertEquals( "Memory should have size 3",
                             3,
                             this.memory.size() );
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, ReteTuple)'
     */
    public void testRemoveWorkingMemoryReteTuple() {
        this.memory.add( this.workingMemory,
                         tuple0 );
        this.memory.add( this.workingMemory,
                         tuple1 );
        this.memory.add( this.workingMemory,
                         tuple2 );
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
        } catch ( Exception e ) {
            // everything is fine
        }
        try {
            MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( tuple0 );
            this.memory.remove( this.workingMemory,
                                wrapper0 );
            Assert.fail( "Trying to remove an element that is not in the memory should throw an exception" );
        } catch ( Exception e ) {
            // everything is fine
        }
        try {
            this.memory.remove( this.workingMemory,
                                (ReteTuple) null );
            Assert.fail( "Trying to remove a null element from memory should throw an exception" );
        } catch ( Exception e ) {
            // everything is fine
        }
        try {
            this.memory.remove( this.workingMemory,
                                (MultiLinkedListNodeWrapper) null );
            Assert.fail( "Trying to remove a null element from memory should throw an exception" );
        } catch ( Exception e ) {
            // everything is fine
        }
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.add(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testAddWorkingMemoryMultiLinkedListNodeWrapper() {
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( tuple0 );
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( tuple1 );
        MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( tuple2 );

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
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper( tuple0 );
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper( tuple1 );
        MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper( tuple2 );

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
                         tuple0 );
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
                         tuple0 );
        Assert.assertEquals( "Memory should have size 1",
                             1,
                             this.memory.size() );

        this.memory.add( this.workingMemory,
                         tuple1 );
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
            DummyValueObject obj3 = new DummyValueObject( false,
                                                          "string2",
                                                          20,
                                                          "object2" );
            FactHandleImpl f3 = (FactHandleImpl) factory.newFactHandle( 3 );
            f3.setObject( obj3 );
            ReteTuple tuple3 = new ReteTuple( f3 );

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

            Iterator i = this.memory.iterator();
            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            ReteTuple tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               tuple0,
                               tuple );

            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               tuple1,
                               tuple );

            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               tuple2,
                               tuple );

            Assert.assertTrue( "There should be a next tuple",
                               i.hasNext() );
            tuple = (ReteTuple) i.next();
            Assert.assertSame( "Wrong returned tuple",
                               tuple3,
                               tuple );

            Assert.assertFalse( "There should not be a next tuple",
                                i.hasNext() );
        } catch ( UnsupportedOperationException e ) {
            Assert.fail( "Beta memory was not supposed to throw any exception: " + e.getMessage() );
        } catch ( ClassCastException e ) {
            Assert.fail( "BetaRightMemory was not supposed to throw ClassCastException: " + e.getMessage() );
        }
    }

    public abstract void testIterator();

    public abstract void testSelectPossibleMatches();

    /**
     * Add an object to the index, then change indexed attribute, then do 
     * a remove;
     */
    public abstract void testModifyObjectAttribute();

}
