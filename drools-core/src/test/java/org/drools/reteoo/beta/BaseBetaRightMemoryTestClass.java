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
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.RuleBaseImpl;
import org.drools.reteoo.WorkingMemoryImpl;
import org.drools.spi.FactHandleFactory;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * 
 * BaseBetaRightMemoryTestClass
 * A base class for test cases testing BetaRightMemory implementations
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public abstract class BaseBetaRightMemoryTestClass extends TestCase {
    protected WorkingMemoryImpl  workingMemory;
    protected BetaRightMemory  memory;
    protected DummyValueObject obj0;
    protected DummyValueObject obj1;
    protected FactHandleFactory factory;
    protected FactHandleImpl f0;
    protected FactHandleImpl f1;
    protected ObjectMatches matches0;
    protected ObjectMatches matches1;
    protected ReteTuple tuple0;
    protected ReteTuple tuple1;
    
    public BaseBetaRightMemoryTestClass() {
        this.memory = null;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        obj0 = new DummyValueObject(true, "string1", 10, "object1");
        obj1 = new DummyValueObject(true, "string2", 20, "object2");
        
        factory = new DefaultFactHandleFactory();
        f0 = (FactHandleImpl) factory.newFactHandle(0);
        f0.setObject(obj0);
        
        f1 = (FactHandleImpl) factory.newFactHandle(1);
        f1.setObject(obj1);
        
        matches0 = new ObjectMatches( f0 );
        matches1 = new ObjectMatches( f1 );
        
        tuple0 = new ReteTuple(f0);
        tuple1 = new ReteTuple(f1);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.add(WorkingMemory, ObjectMatches)'
     */
    public void testAddWorkingMemoryObjectMatches() {
        this.memory.add(this.workingMemory, matches0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.add(this.workingMemory, matches1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.remove(WorkingMemory, ObjectMatches)'
     */
    public void testRemoveWorkingMemoryObjectMatches() {
        this.memory.add(this.workingMemory, matches0);
        this.memory.add(this.workingMemory, matches1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
        
        this.memory.remove(this.workingMemory, this.matches0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());

        this.memory.remove(this.workingMemory, this.matches1);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.add(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testAddWorkingMemoryMultiLinkedListNodeWrapper() {
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(matches0);
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper(matches1);
        
        this.memory.add(this.workingMemory, wrapper0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.add(this.workingMemory, wrapper1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.remove(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testRemoveWorkingMemoryMultiLinkedListNodeWrapper() {
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(matches0);
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper(matches1);
        
        this.memory.add(this.workingMemory, wrapper0);
        this.memory.add(this.workingMemory, wrapper1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
        
        this.memory.remove(this.workingMemory, wrapper1);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.remove(this.workingMemory, wrapper0);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.remove(WorkingMemory, ObjectMatches)'
     */
    public void testRemoveUnexistingObjectMatches() {
        try {
            this.memory.remove(this.workingMemory, this.matches0);

            MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(matches0);
            this.memory.remove(this.workingMemory, wrapper0);
            
            this.memory.remove(this.workingMemory, (ObjectMatches) null);
            this.memory.remove(this.workingMemory, (MultiLinkedListNodeWrapper) null);
        } catch (Exception e) {
            Assert.fail("Remove call should not throw exceptions");
        }
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.isEmpty()'
     */
    public void testIsEmpty() {
        Assert.assertTrue("Memory should be empty", this.memory.isEmpty());
        
        this.memory.add(this.workingMemory, matches0);
        Assert.assertFalse("Memory should not be empty", this.memory.isEmpty());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.size()'
     */
    public void testSize() {
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
        
        this.memory.add(this.workingMemory, matches0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.add(this.workingMemory, matches1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());

        this.memory.remove(this.workingMemory, this.matches0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());

        this.memory.remove(this.workingMemory, this.matches1);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
    }
    
    public void testParameterlessIterator() {
        try {
            this.memory.add(this.workingMemory, matches0);
            this.memory.add(this.workingMemory, matches1);
            Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
            
            Iterator i = this.memory.iterator(); 
            Assert.assertTrue("There should be a next match", i.hasNext());
            ObjectMatches matches = (ObjectMatches) i.next();
            Assert.assertSame("Wrong returned match", matches0, matches);
            
            Assert.assertTrue("There should be a next match", i.hasNext());
            matches = (ObjectMatches) i.next();
            Assert.assertSame("Wrong returned match", matches1, matches);
            
            Assert.assertFalse("There should not be a next match", i.hasNext());
        } catch ( UnsupportedOperationException e ) {
            Assert.fail("Beta memory was not supposed to throw any exception: "+e.getMessage());
        } catch ( ClassCastException e ) {
            Assert.fail("BetaRightMemory was not supposed to throw ClassCastException: "+e.getMessage());
        }
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
}
