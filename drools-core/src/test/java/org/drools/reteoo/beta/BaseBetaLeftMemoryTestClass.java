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

import org.drools.reteoo.DefaultFactHandleFactory;
import org.drools.reteoo.FactHandleImpl;
import org.drools.reteoo.ReteTuple;
import org.drools.reteoo.RuleBaseImpl;
import org.drools.reteoo.WorkingMemoryImpl;
import org.drools.spi.FactHandleFactory;
import org.drools.util.MultiLinkedListNodeWrapper;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * BaseBetaLeftMemoryTest
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public abstract class BaseBetaLeftMemoryTestClass extends TestCase {
    protected WorkingMemoryImpl  workingMemory;
    protected BetaLeftMemory  memory;
    protected DummyValueObject obj0;
    protected DummyValueObject obj1;
    protected FactHandleFactory factory;
    protected FactHandleImpl f0;
    protected FactHandleImpl f1;
    protected ReteTuple tuple0;
    protected ReteTuple tuple1;
    
    public BaseBetaLeftMemoryTestClass() {
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
        
        tuple0 = new ReteTuple( f0 );
        tuple1 = new ReteTuple( f1 );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.add(WorkingMemory, ReteTuple)'
     */
    public void testAddWorkingMemoryReteTuple() {
        this.memory.add(this.workingMemory, tuple0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.add(this.workingMemory, tuple1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, ReteTuple)'
     */
    public void testRemoveWorkingMemoryReteTuple() {
        this.memory.add(this.workingMemory, tuple0);
        this.memory.add(this.workingMemory, tuple1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
        
        this.memory.remove(this.workingMemory, this.tuple0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());

        this.memory.remove(this.workingMemory, this.tuple1);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
    }
    
    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, ReteTuple)'
     */
    public void testRemoveUnexistingTuple() {
        try {
            this.memory.remove( this.workingMemory, this.tuple0 );
            
            MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(tuple0);
            this.memory.remove(this.workingMemory, wrapper0);
            
            this.memory.remove(this.workingMemory, (ReteTuple) null);
            this.memory.remove(this.workingMemory, (MultiLinkedListNodeWrapper) null);
        } catch (Exception e) {
            Assert.fail("Left memory is not supposed to throw exception: " + e.getMessage());
        }
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.add(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testAddWorkingMemoryMultiLinkedListNodeWrapper() {
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(tuple0);
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper(tuple1);
        
        this.memory.add(this.workingMemory, wrapper0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.add(this.workingMemory, wrapper1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.remove(WorkingMemory, MultiLinkedListNodeWrapper)'
     */
    public void testRemoveWorkingMemoryMultiLinkedListNodeWrapper() {
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(tuple0);
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper(tuple1);
        
        this.memory.add(this.workingMemory, wrapper0);
        this.memory.add(this.workingMemory, wrapper1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
        
        this.memory.remove(this.workingMemory, wrapper1);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.remove(this.workingMemory, wrapper0);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.isEmpty()'
     */
    public void testIsEmpty() {
        Assert.assertTrue("Memory should be empty", this.memory.isEmpty());
        
        this.memory.add(this.workingMemory, tuple0);
        Assert.assertFalse("Memory should not be empty", this.memory.isEmpty());
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaLeftMemory.size()'
     */
    public void testSize() {
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
        
        this.memory.add(this.workingMemory, tuple0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());
        
        this.memory.add(this.workingMemory, tuple1);
        Assert.assertEquals("Memory should have size 2", 2, this.memory.size());

        this.memory.remove(this.workingMemory, this.tuple0);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());

        this.memory.remove(this.workingMemory, this.tuple1);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
    }
    
    public abstract void testIterator();
    
    public abstract void testSelectPossibleMatches();
    
    /**
     * Add an object to the index, then change indexed attribute, then do 
     * a remove;
     */
    public abstract void testModifyObjectAttribute();
    
}
