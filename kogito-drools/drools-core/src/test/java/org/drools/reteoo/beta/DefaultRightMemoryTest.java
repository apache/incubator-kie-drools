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

import org.drools.common.DefaultFactHandle;
import org.drools.reteoo.ObjectMatches;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * 
 * DefaultRightMemoryTest
 * A test case for DefaultRightMemory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public class DefaultRightMemoryTest extends BaseBetaRightMemoryTestClass {

    protected void setUp() throws Exception {
        super.setUp();
        this.memory = new DefaultRightMemory();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSelectPossibleMatches2() {
        // do nothing as there is no child memory
    }

    /*
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.iterator(WorkingMemory, ReteTuple)'
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

            final Iterator iterator = this.memory.iterator( this.workingMemory,
                                                            this.tuple0 );

            Assert.assertTrue( "There should be a next element",
                               iterator.hasNext() );
            final ObjectMatches om0 = (ObjectMatches) iterator.next();
            Assert.assertSame( "The first object to return should have been matches0",
                               this.matches0,
                               om0 );

            Assert.assertEquals( "Memory should have size 2",
                                 2,
                                 this.memory.size() );

            Assert.assertTrue( "There should be a next element",
                               iterator.hasNext() );
            final ObjectMatches om1 = (ObjectMatches) iterator.next();
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
     * Test method for 'org.drools.reteoo.beta.DefaultRightMemory.selectPossibleMatches(WorkingMemory, ReteTuple)'
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

        obj2.setBooleanAttr( false );

        this.memory.remove( this.workingMemory,
                            matches2 );
        Assert.assertEquals( "Memory should have size 0",
                             0,
                             this.memory.size() );
    }
}