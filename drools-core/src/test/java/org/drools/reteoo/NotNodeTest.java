package org.drools.reteoo;

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

import java.beans.IntrospectionException;

import junit.framework.Assert;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.common.BetaConstraints;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.MockConstraint;
import org.drools.spi.PropagationContext;

public class NotNodeTest extends DroolsTestCase {
    Rule                  rule;
    PropagationContext    context;
    ReteooWorkingMemory   workingMemory;
    MockObjectSource      objectSource;
    MockTupleSource       tupleSource;
    MockTupleSink         sink;
    NotNode               node;
    RightInputAdapterNode ria;
    BetaMemory            memory;
    MockConstraint        constraint = new MockConstraint();

    /**
     * Setup the BetaNode used in each of the tests
     * @throws IntrospectionException 
     */
    public void setUp() throws IntrospectionException {
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );
        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        RuleBaseConfiguration configuration = new RuleBaseConfiguration();
        
        // string1Declaration is bound to column 3 
        this.node = new NotNode( 15,
                                 new MockTupleSource( 5 ),
                                 new MockObjectSource( 8 ),
                                 new DefaultBetaConstraints( new BetaNodeFieldConstraint[] { this.constraint }, configuration ) );

        this.sink = new MockTupleSink();
        this.node.addTupleSink( this.sink );

        //        this.ria = new RightInputAdapterNode( 2,
        //                                              0,
        //                                              this.node );
        //        this.ria.attach();
        //
        //        this.sink = new MockObjectSink();
        //        this.ria.addObjectSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testNotStandard() throws FactException {
        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.assertObject( cheddar );

        final ReteTuple tuple1 = new ReteTuple( f0 );

        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        assertEquals( new ReteTuple( f0 ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        // assert will match, so propagated tuple should be retracted
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.assertObject( brie );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // check no as assertions, but should be one retraction
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );

        assertEquals( new ReteTuple( f0 ),
                      ((Object[]) this.sink.getRetracted().get( 0 ))[0] );

        // assert tuple, will have matches, so no propagation
        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.assertObject( new Cheese( "gouda",
                                                                                                      10 ) );
        final ReteTuple tuple2 = new ReteTuple( f2 );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // check no propagations 
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );

        // check memory sizes
        assertEquals( 2,
                      this.memory.getTupleMemory().size() );
        assertEquals( 1,
                      this.memory.getFactHandleMemory().size() );

        // When this is retracter both tuples should assert
        this.node.retractObject( f1,
                                 this.context,
                                 this.workingMemory );

        // check propagations 
        assertLength( 3,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testNotWithConstraints() throws FactException {
        this.constraint.isAllowed = false;

        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.assertObject( cheddar );

        final ReteTuple tuple1 = new ReteTuple( f0 );

        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        assertEquals( new ReteTuple( f0 ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        // assert will not match, so activation should stay propagated
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.assertObject( brie );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // check no as assertions, but should be one retraction
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        // assert tuple, will have no matches, so do assert propagation
        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.assertObject( new Cheese( "gouda",
                                                                                                      10 ) );
        final ReteTuple tuple2 = new ReteTuple( f2 );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // check no as assertions, but should be one retraction
        assertLength( 2,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );
    }

    /**
     * Tests memory consistency after assert/modify/retract calls
     * 
     * @throws AssertionException
     */
    public void testNotMemoryManagement() throws FactException {
        try {
            // assert tuple
            final Cheese cheddar = new Cheese( "cheddar",
                                               10 );
            final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.assertObject( cheddar );
            final ReteTuple tuple1 = new ReteTuple( f0 );

            this.node.assertTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

            // assert will match, so propagated tuple should be retracted
            final Cheese brie = new Cheese( "brie",
                                            10 );
            final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.assertObject( brie );

            // Initially, no objects in right memory
            assertEquals( 0,
                          this.memory.getFactHandleMemory().size() );
            this.node.assertObject( f1,
                                    this.context,
                                    this.workingMemory );

            // Now, needs to have 1 object in right memory
            assertEquals( 1,
                          this.memory.getFactHandleMemory().size() );

            // simulate modify
            this.node.retractObject( f1,
                                     this.context,
                                     this.workingMemory );
            this.node.assertObject( f1,
                                    this.context,
                                    this.workingMemory );
            // Memory should not change
            assertEquals( 1,
                          this.memory.getFactHandleMemory().size() );

            // When this is retracter both tuples should assert
            this.node.retractObject( f1,
                                     this.context,
                                     this.workingMemory );
            assertEquals( 0,
                          this.memory.getFactHandleMemory().size() );

            // check memory sizes
            assertEquals( 1,
                          this.memory.getTupleMemory().size() );

            // simulate modify
            this.node.retractTuple( tuple1,
                                    this.context,
                                    this.workingMemory );
            this.node.assertTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
            assertEquals( 1,
                          this.memory.getTupleMemory().size() );
            this.node.retractTuple( tuple1,
                                    this.context,
                                    this.workingMemory );
            assertEquals( 0,
                          this.memory.getTupleMemory().size() );
        } catch ( final Exception e ) {
            Assert.fail( "No exception should be raised in this procedure, but got: " + e.toString() );
        }
    }
    

    public void testGetConstraints_ReturnsNullEvenWithEmptyBinder() {
        BetaConstraints nullConstraints = EmptyBetaConstraints.getInstance();
        NotNode notNode = new NotNode( 1,
                                                this.tupleSource,
                                                this.objectSource, nullConstraints);        
        BetaNodeFieldConstraint[] constraints = notNode.getConstraints();
        assertEquals( 0, constraints.length );        
    }

}