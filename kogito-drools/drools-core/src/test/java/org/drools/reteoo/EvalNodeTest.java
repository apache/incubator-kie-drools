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

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;

public class EvalNodeTest extends DroolsTestCase {
    private PropagationContext context;
    private WorkingMemoryImpl  workingMemory;

    public EvalNodeTest(String name) {
        super( name );
    }

    public void setUp() {
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );

        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
    }

    public void testAttach() throws Exception {
        MockTupleSource source = new MockTupleSource( 12 );

        EvalConditionNode node = new EvalConditionNode( 18,
                                                        source,
                                                        new MockEvalCondition( true ) );

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

    public void testMemory() {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockTupleSource source = new MockTupleSource( 12 );

        EvalConditionNode node = new EvalConditionNode( 18,
                                                        source,
                                                        new MockEvalCondition( true ) );

        LinkedList memory = (LinkedList) workingMemory.getNodeMemory( node );

        assertNotNull( memory );
    }

    /**
     * If a eval allows an incoming Object, then the Object MUST be
     * propagated. This tests that the memory is updated
     * 
     * @throws FactException
     */
    public void testAllowed() throws FactException {

        // Create a test node that always returns true 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( true ) );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple 
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( f0 );

        // Tuple should pass and propagate
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        // Check memory was populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );

        assertEquals( 1,
                      memory.size() );
        assertEquals( tuple,
                      memory.getFirst() );

        // Now test that the fact is retracted correctly
        node.retractTuple( tuple,
                           context,
                           workingMemory );

        // Now test that the fact is retracted correctly
        assertEquals( 0,
                      memory.size() );
    }

    /**
     * If a Condition does not allow an incoming Object, then the object MUST
     * NOT be propagated.
     * 
     * @throws FactException
     */
    public void testNotAllowed() throws FactException {
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( false ) );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        // make sure no assertions were propagated
        assertEquals( 0,
                      sink.getAsserted().size() );

    }

    public void testRetractNotAllowed() throws Exception {
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( false ) );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        node.retractTuple( tuple,
                           context,
                           workingMemory );

        // make sure no assertions were propagated
        assertEquals( 0,
                      sink.getRetracted().size() );
    }

    public void testRetractAllowed() throws Exception {
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( true ) );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        node.retractTuple( tuple,
                           context,
                           workingMemory );

        // make sure no assertions were propagated
        assertEquals( 1,
                      sink.getRetracted().size() );
    }

    public void testModifyNotAllowed() throws Exception {
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( false ) );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        node.modifyTuple( tuple,
                          context,
                          workingMemory );

        // make sure no assertions were propagated
        assertEquals( 0,
                      sink.getRetracted().size() );
        assertEquals( 0,
                      sink.getModified().size() );
        assertEquals( 0,
                      sink.getAsserted().size() );
    }

    public void testModifyAllowed() throws Exception {
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( true ) );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple,
                          this.context,
                          this.workingMemory );

        node.modifyTuple( tuple,
                          context,
                          workingMemory );

        // make sure no assertions were propagated
        assertEquals( 1,
                      sink.getModified().size() );
    }

    //    public void testException() throws FactException {
    //        // Create a eval that will always throw an exception
    //        MockCondition eval = new MockCondition( true );
    //        eval.setTestException( true );
    //
    //        // Create the TestNode 
    //        EvalConditionNode node = new EvalConditionNode( 1,
    //                                      new MockTupleSource( 15 ),
    //                                      eval );
    //
    //        MockTupleSink sink = new MockTupleSink();
    //        node.addTupleSink( sink );
    //
    //        /* Create the Tuple */
    //        FactHandleImpl f0 = new FactHandleImpl( 0 );
    //        ReteTuple tuple = new ReteTuple( f0 );
    //
    //        /* When asserting the node should throw an exception */
    //        try {
    //            node.assertTuple( tuple,
    //                              this.context,
    //                              this.workingMemory );
    //            fail( "Should have thrown TestException" );
    //        } catch ( TestException e ) {
    //            // should throw exception
    //        }
    //    }

    public void testUpdateWithMemory() throws FactException {
        // If no child nodes have children then we need to re-process the left
        // and right memories
        // as a joinnode does not store the resulting tuples
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        // Creat the object source so we can detect the alphaNode telling it to
        // propate its contents
        MockTupleSource source = new MockTupleSource( 1 );

        /* Create a test node that always returns true */
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( true ) );

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        MockTupleSink sink1 = new MockTupleSink( 2 );
        node.addTupleSink( sink1 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "string0" );

        ReteTuple tuple1 = new ReteTuple( f0 );

        node.assertTuple( tuple1,
                          this.context,
                          workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Add the new sink, this should be updated from the re-processed
        // joinnode memory
        MockTupleSink sink2 = new MockTupleSink( 3 );
        node.addTupleSink( sink2 );
        assertLength( 0,
                      sink2.getAsserted() );

        node.updateNewNode( workingMemory,
                            this.context );

        assertLength( 1,
                      sink2.getAsserted() );
    }
}
