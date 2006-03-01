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
import org.drools.base.EvaluatorFactory;
import org.drools.reteoo.FactHandleImpl;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Evaluator;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * 
 * ObjectNotEqualConstrLeftMemoryTest
 * TestCase for ObjectNotEqualConstrLeftMemory
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public class ObjectNotEqualConstrLeftMemoryTest extends BaseBetaLeftMemoryTestClass {

    protected void setUp() throws Exception {
        super.setUp();
        ClassFieldExtractor extractor = new ClassFieldExtractor(
                                DummyValueObject.class,
                                "objectAttr");
        Declaration         declaration = new Declaration(
                                "myObject",
                                extractor,
                                0);
        Evaluator           evaluator   = EvaluatorFactory.getEvaluator(
                                Evaluator.OBJECT_TYPE,
                                Evaluator.NOT_EQUAL);
         
        this.memory = new ObjectNotEqualConstrLeftMemory(
                                extractor,
                                declaration,
                                evaluator);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.ObjectNotEqualConstrLeftMemory.iterator(WorkingMemory, FactHandleImpl)'
     */
    public void testIterator() {
        try {
            this.memory.add(this.workingMemory, tuple0);
            this.memory.add(this.workingMemory, tuple1);
            Assert.assertEquals("Memory should have size 2", 2, this.memory.size());
            
            Iterator iterator = this.memory.iterator(workingMemory, f0);
            
            Assert.assertTrue("There should be a next element", iterator.hasNext());
            ReteTuple t1 = (ReteTuple) iterator.next();
            Assert.assertSame("The first object to return should have been tuple1", tuple1, t1);
            
            iterator.remove();
            Assert.assertEquals("Memory should have size 1", 1, this.memory.size());

            Assert.assertFalse("There should not be a next element", iterator.hasNext());
            
            try {
                iterator.next();
                Assert.fail("Iterator is supposed to throw an Exception when there are no more elements");
            } catch (NoSuchElementException nse) {
                // working fine
            }

            DummyValueObject obj2 = new DummyValueObject(false, "string3", 30, "object3");
            FactHandleImpl  f2   = (FactHandleImpl) this.factory.newFactHandle(2);
            f2.setObject(obj2);
            
            iterator = this.memory.iterator(workingMemory, f2);
            Assert.assertTrue("There should be a next element", iterator.hasNext());
            
            ReteTuple t0 = (ReteTuple) iterator.next();
            Assert.assertSame("The first object to return should have been tuple0", tuple0, t0);
            
            Assert.assertFalse("There should not be a next element", iterator.hasNext());
            
            try {
                iterator.next();
                Assert.fail("Iterator is supposed to throw an Exception when there are no more elements");
            } catch (NoSuchElementException nse) {
                // working fine
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            Assert.fail("Memory is not supposed to throw any exception during iteration");
        }

    }

    /*
     * Test method for 'org.drools.reteoo.beta.ObjectNotEqualConstrLeftMemory.selectPossibleMatches(WorkingMemory, FactHandleImpl)'
     */
    public void testSelectPossibleMatches() {
        MultiLinkedListNodeWrapper wrapper0 = new MultiLinkedListNodeWrapper(tuple0);
        MultiLinkedListNodeWrapper wrapper1 = new MultiLinkedListNodeWrapper(tuple1);
        MultiLinkedListNodeWrapper wrapper2 = new MultiLinkedListNodeWrapper(tuple1);

        this.memory.add(this.workingMemory, wrapper0);
        this.memory.add(this.workingMemory, wrapper1);
        
        this.memory.selectPossibleMatches(workingMemory, f0);
        
        Assert.assertFalse("Wrapper0 was not a possible match", this.memory.isPossibleMatch(wrapper0));
        Assert.assertTrue("Wrapper1 was a possible match", this.memory.isPossibleMatch(wrapper1));
        Assert.assertFalse("Wrapper2 was not a possible match", this.memory.isPossibleMatch(wrapper2));
        
        DummyValueObject obj2 = new DummyValueObject(false, "string3", 30, "object3");
        FactHandleImpl  f2   = (FactHandleImpl) this.factory.newFactHandle(2);
        f2.setObject(obj2);
        
        this.memory.selectPossibleMatches(workingMemory, f2);
        Assert.assertTrue("Wrapper0 was a possible match", this.memory.isPossibleMatch(wrapper0));
        Assert.assertTrue("Wrapper1 was a possible match", this.memory.isPossibleMatch(wrapper1));
        Assert.assertFalse("Wrapper2 was not a possible match", this.memory.isPossibleMatch(wrapper2));
    }

    public void testModifyObjectAttribute() {
        DummyValueObject obj2   = new DummyValueObject(true, "string20", 20, "object20");
        FactHandleImpl   f2     = (FactHandleImpl) factory.newFactHandle(2);
        f2.setObject(obj2);
        ReteTuple        tuple2 = new ReteTuple( f2 ); 

        this.memory.add(this.workingMemory, tuple2);
        Assert.assertEquals("Memory should have size 1", 1, this.memory.size());

        obj2.setObjectAttr("object20-modified");
        
        this.memory.remove(this.workingMemory, tuple2);
        Assert.assertEquals("Memory should have size 0", 0, this.memory.size());
        Assert.assertTrue("Memory is leaking object references", ((ObjectNotEqualConstrLeftMemory)this.memory).isClean());
    }
    
}
