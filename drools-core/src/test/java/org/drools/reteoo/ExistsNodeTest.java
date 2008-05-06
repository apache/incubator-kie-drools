/*
 * Copyright 2006 JBoss Inc
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

package org.drools.reteoo;

import java.beans.IntrospectionException;

import junit.framework.Assert;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.MockConstraint;
import org.drools.spi.PropagationContext;

/**
 * @author etirelli
 *
 */
public class ExistsNodeTest extends DroolsTestCase {
    Rule                  rule;
    PropagationContext    context;
    ReteooWorkingMemory   workingMemory;
    MockObjectSource      objectSource;
    MockTupleSource       tupleSource;
    MockLeftTupleSink         sink;
    ExistsNode            node;
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
                                                   null,
                                                   null );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ruleBase.getReteooBuilder().getIdGenerator() );

        this.workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final RuleBaseConfiguration configuration = new RuleBaseConfiguration();

        // string1Declaration is bound to pattern 3 
        this.node = new ExistsNode( 15,
                                    new MockTupleSource( 5 ),
                                    new MockObjectSource( 8 ),
                                    new DefaultBetaConstraints( new BetaNodeFieldConstraint[]{this.constraint},
                                                                configuration ),
                                    Behavior.EMPTY_BEHAVIOR_LIST,
                                    buildContext );

        this.sink = new MockLeftTupleSink();
        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testExistsStandard() throws FactException {
        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( cheddar );

        final LeftTuple tuple1 = new LeftTuple( f0,
                                                this.node,
                                                true );

        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        // no matching objects, so should not propagate
        assertLength( 0,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        // LeftTuple is not matched so should still be in memory
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );

        // assert will match, so should propagate
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( brie );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // LeftTuple is now matched so should not be in memory
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );

        // check a single assertion
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        assertEquals( new LeftTuple( f0,
                                     this.sink,
                                     true ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        // assert tuple, will have matches, so propagate
        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.insert( new Cheese( "gouda",
                                                                                                10 ) );
        final LeftTuple tuple2 = new LeftTuple( f2,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple2,
                                   this.context,
                                   this.workingMemory );

        // check propagations 
        assertLength( 2,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        // both LeftTuples should match, so no LeftTupleMemory
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

        // When this is retracter both tuples should be retracted
        this.node.retractRightTuple( f1.getRightTuple(),
                                     this.context,
                                     this.workingMemory );

        // both LeftTuples are no longer matched, so should have LeftTupleMemory
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );

        // check retracts 
        assertLength( 2,
                      this.sink.getAsserted() );

        assertLength( 2,
                      this.sink.getRetracted() );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testExistsWithConstraints() throws FactException {
        this.constraint.isAllowed = false;

        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( cheddar );

        final LeftTuple tuple1 = new LeftTuple( f0,
                                                this.node,
                                                true );

        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        // no matching objects, so don't propagate
        assertLength( 0,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        // assert will not match, so activation should stay propagated
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( brie );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // no matches, so no propagations still
        assertLength( 0,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        // assert tuple, will have matches, so do assert propagation
        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.insert( new Cheese( "gouda",
                                                                                                10 ) );
        final LeftTuple tuple2 = new LeftTuple( f2,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple2,
                                   this.context,
                                   this.workingMemory );

        assertLength( 0,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );
    }

    /**
     * Tests memory consistency after insert/update/retract calls
     * 
     * @throws AssertionException
     */
    public void testExistsMemoryManagement() throws FactException {
        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( cheddar );
        final LeftTuple tuple1 = new LeftTuple( f0,
                                                this.node,
                                                true );

        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        // not blocked, so should be in memory
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );

        // assert will match, so should propagate
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( brie );

        // Initially, no objects in right memory
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // blocked, so should not be in memory
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );

        // Now, needs to have 1 object in right memory
        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

        // simulate modify
        this.node.retractRightTuple( f1.getRightTuple(),
                                     this.context,
                                     this.workingMemory );

        // not blocked, so should be in memory
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // blocked again, so should not be in memory
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );

        // Memory should not change
        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

        // When this is retracter the tuple should assert
        this.node.retractRightTuple( f1.getRightTuple(),
                                     this.context,
                                     this.workingMemory );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );

        // not blocked, so should be in memory
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );

        // simulate modify
        this.node.retractLeftTuple( tuple1,
                                    this.context,
                                    this.workingMemory );
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        this.node.retractLeftTuple( tuple1,
                                    this.context,
                                    this.workingMemory );
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
    }

}
