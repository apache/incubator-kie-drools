/**
 * Copyright 2010 JBoss Inc
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

import junit.framework.Assert;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Accumulate;
import org.drools.rule.Behavior;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.spi.Accumulator;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

/**
 * A test case for AccumulateNode
 * 
 * @author etirelli
 */
public class AccumulateNodeTest extends DroolsTestCase {

    Rule                    rule;
    PropagationContext      context;
    ReteooWorkingMemory     workingMemory;
    MockObjectSource        objectSource;
    MockTupleSource         tupleSource;
    MockLeftTupleSink       sink;
    BetaNode                node;
    BetaMemory              memory;
    MockAccumulator         accumulator;
    Accumulate              accumulate;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
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

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockLeftTupleSink();

        this.accumulator = new MockAccumulator();

        final ObjectType srcObjType = new ClassObjectType( String.class );
        final Pattern sourcePattern = new Pattern( 0,
                                                   srcObjType );
        this.accumulate = new Accumulate( sourcePattern,
                                          new Declaration[0],
                                          new Declaration[0],
                                          new Accumulator[] { this.accumulator });

        this.node = new AccumulateNode( 15,
                                        this.tupleSource,
                                        this.objectSource,
                                        new AlphaNodeFieldConstraint[0],
                                        EmptyBetaConstraints.getInstance(),
                                        EmptyBetaConstraints.getInstance(),
                                        Behavior.EMPTY_BEHAVIOR_LIST,
                                        this.accumulate,
                                        false,
                                        buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = ((AccumulateMemory) this.workingMemory.getNodeMemory( this.node )).betaMemory;

        // check memories are empty
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link org.drools.reteoo.AccumulateNode#updateNewNode(InternalWorkingMemory, org.drools.spi.PropagationContext)}.
     */
    public void testUpdateSink() {
        this.node.updateSink( this.sink,
                              this.context,
                              this.workingMemory );
        Assert.assertEquals( "No tuple should be propagated",
                             0,
                             this.sink.getAsserted().size() );

        this.node.assertLeftTuple( new LeftTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                           null,
                                                                                                           null,
                                                                                                           workingMemory ),
                                                  null,
                                                  true ),
                                   this.context,
                                   this.workingMemory );
        this.node.assertLeftTuple( new LeftTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                           null,
                                                                                                           null,
                                                                                                           workingMemory ),
                                                  null,
                                                  true ),
                                   this.context,
                                   this.workingMemory );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );

        final MockLeftTupleSink otherSink = new MockLeftTupleSink();

        this.node.addTupleSink( otherSink );
        this.node.updateSink( otherSink,
                              this.context,
                              this.workingMemory );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             otherSink.getAsserted().size() );
    }

    /**
     * Test method for {@link org.drools.reteoo.AccumulateNode#assertLeftTuple(org.drools.reteoo.LeftTuple, org.drools.spi.PropagationContext, org.drools.reteoo.ReteooWorkingMemory)}.
     */
    public void testAssertTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory );
        final LeftTuple tuple0 = new LeftTuple( f0,
                                                null,
                                                true );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.context,
                                   this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );
        Assert.assertTrue( "An empty matching objects list should be propagated",
                           this.accumulator.getMatchingObjects().isEmpty() );

        // assert tuple, should add left memory 
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory );

        final LeftTuple tuple1 = new LeftTuple( f1,
                                                null,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );
        Assert.assertTrue( "An empty matching objects list should be propagated",
                           this.accumulator.getMatchingObjects().isEmpty() );

        final LeftTupleMemory memory = this.memory.getLeftTupleMemory();
        assertTrue( memory.contains( tuple0 ) );
        assertTrue( memory.contains( tuple1 ) );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    /**
     * Test method for {@link org.drools.reteoo.AccumulateNode#assertLeftTuple(org.drools.reteoo.LeftTuple, org.drools.spi.PropagationContext, org.drools.reteoo.ReteooWorkingMemory)}.
     */
    public void testAssertTupleWithObjects() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory);

        final LeftTuple tuple0 = new LeftTuple( f0,
                                                null,
                                                true );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.context,
                                   this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 2,
                      this.memory.getRightTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        // assert tuple, should add left memory 
        final LeftTuple tuple1 = new LeftTuple( f1,
                                                null,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        final LeftTupleMemory memory = this.memory.getLeftTupleMemory();
        assertTrue( memory.contains( tuple0 ) );
        assertTrue( memory.contains( tuple1 ) );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    /**
     * Test method for {@link org.drools.reteoo.AccumulateNode#retractLeftTuple(org.drools.reteoo.LeftTuple, org.drools.spi.PropagationContext, org.drools.reteoo.ReteooWorkingMemory)}.
     */
    public void testRetractTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  null );

        final LeftTuple tuple0 = new LeftTuple( f0,
                                                null,
                                                true );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.context,
                                   this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );
        Assert.assertTrue( "An empty matching objects list should be propagated",
                           this.accumulator.getMatchingObjects().isEmpty() );

        this.node.retractLeftTuple( tuple0,
                                    this.context,
                                    this.workingMemory );
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.sink.getRetracted().size() );
        assertEquals( 1,
                      this.sink.getAsserted().size() );
    }

    public void testMemory() {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ruleBase.getReteooBuilder().getIdGenerator() );

        this.workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final MockObjectSource objectSource = new MockObjectSource( 1 );
        final MockTupleSource tupleSource = new MockTupleSource( 1 );

        final AccumulateNode accumulateNode = new AccumulateNode( 2,
                                                                  tupleSource,
                                                                  objectSource,
                                                                  new AlphaNodeFieldConstraint[0],
                                                                  EmptyBetaConstraints.getInstance(),
                                                                  EmptyBetaConstraints.getInstance(),
                                                                  Behavior.EMPTY_BEHAVIOR_LIST,
                                                                  this.accumulate,
                                                                  false,
                                                                  buildContext );

        final BetaMemory memory = ((AccumulateMemory) this.workingMemory.getNodeMemory( accumulateNode )).betaMemory;

        assertNotNull( memory );
    }

    /**
     * Test just tuple assertions
     * 
     * @throws AssertionException
     */
    public void testAssertTupleSequentialMode() throws Exception {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ruleBase.getReteooBuilder().getIdGenerator() );
        buildContext.setTupleMemoryEnabled( false );
        // overide the original node, so we an set the BuildContext
        this.node = new AccumulateNode( 15,
                                        this.tupleSource,
                                        this.objectSource,
                                        new AlphaNodeFieldConstraint[0],
                                        EmptyBetaConstraints.getInstance(),
                                        EmptyBetaConstraints.getInstance(),
                                        Behavior.EMPTY_BEHAVIOR_LIST,
                                        this.accumulate,
                                        false,
                                        buildContext );

        this.node.addTupleSink( this.sink );

        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf ) );

        this.memory = ((AccumulateMemory) this.workingMemory.getNodeMemory( this.node )).betaMemory;

        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  null );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  null );

        final LeftTuple tuple0 = new LeftTuple( f0,
                                                null,
                                                true );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // assert tuple, should not add to left memory, since we are in sequential mode
        this.node.assertLeftTuple( tuple0,
                                   new PropagationContextImpl( 0,
                                                               PropagationContext.ASSERTION,
                                                               null,
                                                               null,
                                                               f0 ),
                                   this.workingMemory );
        // check memories 
        assertNull( this.memory.getLeftTupleMemory() );
        assertEquals( 2,
                      this.memory.getRightTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        // assert tuple, should not add left memory 
        final LeftTuple tuple1 = new LeftTuple( f1,
                                                null,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   new PropagationContextImpl( 0,
                                                               PropagationContext.ASSERTION,
                                                               null,
                                                               null,
                                                               f1 ),
                                   this.workingMemory );
        assertNull( this.memory.getLeftTupleMemory() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

}
