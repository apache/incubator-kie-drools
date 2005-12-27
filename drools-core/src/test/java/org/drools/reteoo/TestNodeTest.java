package org.drools.reteoo;

/*
 * $Id: TestNodeTest.java,v 1.4 2005/08/14 22:44:12 mproctor Exp $
 *
 * Copyright 2003-2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.rule.Rule;
import org.drools.spi.MockCondition;
import org.drools.spi.PropagationContext;
import org.drools.spi.TestException;

public class TestNodeTest extends DroolsTestCase {
    private PropagationContext context;
    private WorkingMemoryImpl  workingMemory;

    public TestNodeTest(String name){
        super( name );
    }

    public void setUp(){
        this.context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                   null,
                                                   null );

        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
    }

    public void testAttach() throws Exception{
        MockTupleSource source = new MockTupleSource( 12 );

        TestNode node = new TestNode( 18,
                                      source,
                                      new MockCondition( null,
                                                         true ),
                                      false );

        assertEquals( 18,
                      node.getId() );

        assertLength( 0,
                      source.getTupleSinks() );

        node.attach();

        assertLength( 1,
                      source.getTupleSinks() );

        assertSame( node,
                    source.getTupleSinks().get( 0 ) );
    }

    public void testMemory(){
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockTupleSource source = new MockTupleSource( 12 );

        TestNode node = new TestNode( 18,
                                      source,
                                      new MockCondition( null,
                                                         true ),
                                      false );

        Map memory = (Map) workingMemory.getNodeMemory( node );

        assertNotNull( memory );
    }

    /**
     * If a condition allows an incoming Object, then the Object MUST be
     * propagated.
     * 
     * @throws FactException
     */
    public void testAllowedWithoutMemory() throws FactException{

        /* Create a test node that always returns true */
        TestNode node = new TestNode( 1,
                                      new MockTupleSource( 15 ),
                                      new MockCondition( null,
                                                         true ),
                                      false );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        /* Create the Tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( 0,
                                         f0,
                                         this.workingMemory );

        /* Tuple should pass and propagate */
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        /* Check it propagated */
        List asserted = sink.getAsserted();
        assertEquals( 1,
                      asserted.size() );

        /* Check propagated item is correct */
        Object[] list = (Object[]) asserted.get( 0 );
        assertSame( tuple,
                    list[0] );

        /* make sure nothing was retracted */
        assertEquals( 0,
                      sink.getRetracted().size() );
    }

    /**
     * If a condition allows an incoming Object, then the Object MUST be
     * propagated. This tests that the memory is updated
     * 
     * @throws FactException
     */
    public void testAllowedWithMemory() throws FactException{

        /* Create a test node that always returns true */
        TestNode node = new TestNode( 1,
                                      new MockTupleSource( 15 ),
                                      new MockCondition( null,
                                                         true ),
                                      true );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        /* Create the Tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( 0,
                                         f0,
                                         this.workingMemory );

        /* Tuple should pass and propagate */
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        /* Check memory was populated */
        Map map = (Map) this.workingMemory.getNodeMemory( node );

        assertLength( 1,
                      map.keySet() );
        assertContains( tuple.getKey(),
                        map.keySet() );

        // Now test that the fact is retracted correctly
        node.retractTuples( tuple.getKey(),
                            this.context,
                            this.workingMemory );
        assertLength( 0,
                      map.keySet() );
    }

    /**
     * If a Condition does not allow an incoming Object, then the object MUST
     * NOT be propagated.
     * 
     * @throws FactException
     */
    public void testNotAllowed() throws FactException{
        /* Create a test node that always returns false */
        TestNode node = new TestNode( 1,
                                      new MockTupleSource( 15 ),
                                      new MockCondition( null,
                                                         false ),
                                      false );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        /* Create the Tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( 0,
                                         f0,
                                         this.workingMemory );

        /* Tuple should pass and propagate */
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        /* make sure no assertions were propagated */
        assertEquals( 0,
                      sink.getAsserted().size() );

        /* make sure no retractions were propagated */
        assertEquals( 0,
                      sink.getRetracted().size() );

    }

    /**
     * Retract Keys
     * 
     * @throws FactException
     */
    public void testRetract() throws FactException{
        /*
         * Create a test node that always returns false Although as we are
         * retracting it doesn't matter what it returns
         */
        TestNode node = new TestNode( 1,
                                      new MockTupleSource( 15 ),
                                      new MockCondition( null,
                                                         false ),
                                      false );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        /* Create the TupleKey */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        TupleKey key = new TupleKey( 0,
                                     f0 );

        /* Propagate the key */
        node.retractTuples( key,
                            this.context,
                            this.workingMemory );

        /* Check nothing was asserted */
        assertEquals( 0,
                      sink.getAsserted().size() );

        /* Make sure only one object as propagated */
        List retracted = sink.getRetracted();
        assertEquals( 1,
                      retracted.size() );

        /* Check its the same key we asserted */
        Object[] list = (Object[]) retracted.get( 0 );
        assertSame( key,
                    list[0] );

    }

    public void testException() throws FactException{
        /* Create a condition that will always throw an exception */
        MockCondition condition = new MockCondition( null,
                                                     true );
        condition.setTestException( true );

        /* Create the TestNode */
        TestNode node = new TestNode( 1,
                                      new MockTupleSource( 15 ),
                                      condition,
                                      false );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        /* Create the Tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( 0,
                                         f0,
                                         this.workingMemory );

        /* When asserting the node should throw an exception */
        try {
            node.assertTuple( tuple,
                              this.context,
                              this.workingMemory );
            fail( "Should have thrown TestException" );
        }
        catch ( TestException e ) {
            // should throw exception
        }
    }

    public void testUpdateNewNodeWithoutMemory() throws FactException{
        // An AlphaNode without memory needs to inform the parent ObjectTypeNode
        // to repropagate its memory

        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        // Creat the object source so we can detect the alphaNode telling it to
        // propate its contents
        MockTupleSource source = new MockTupleSource( 1 );

        /* Create a test node that always returns true */
        TestNode node = new TestNode( 1,
                                      source,
                                      new MockCondition( null,
                                                         true ),
                                      false );
        node.attach();

        // check that the update propagation requests are 0
        assertEquals( 0,
                      source.getUdated() );

        node.updateNewNode( workingMemory,
                            null );

        // now they should be 1, we don't need to test actual repropagation as
        // thats tested in
        // ObjectSourceTest
        assertEquals( 1,
                      source.getUdated() );
    }

    public void testUpdateWithMemory() throws FactException{
        // If no child nodes have children then we need to re-process the left
        // and right memories
        // as a joinnode does not store the resulting tuples
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        // Creat the object source so we can detect the alphaNode telling it to
        // propate its contents
        MockTupleSource source = new MockTupleSource( 1 );

        /* Create a test node that always returns true */
        TestNode testNode = new TestNode( 1,
                                          source,
                                          new MockCondition( null,
                                                             true ),
                                          true );

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        MockTupleSink sink1 = new MockTupleSink( 2 );
        testNode.addTupleSink( sink1 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "string0" );

        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          workingMemory );

        testNode.assertTuple( tuple1,
                              this.context,
                              workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Add the new sink, this should be updated from the re-processed
        // joinnode memory
        MockTupleSink sink2 = new MockTupleSink( 3 );
        testNode.addTupleSink( sink2 );
        assertLength( 0,
                      sink2.getAsserted() );

        testNode.updateNewNode( workingMemory,
                                this.context );

        assertLength( 1,
                      sink2.getAsserted() );
    }
}
