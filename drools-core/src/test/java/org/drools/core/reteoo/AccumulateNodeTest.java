/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.SingleAccumulate;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.test.model.DroolsTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.*;

/**
 * A test case for AccumulateNode
 */
@Ignore("phreak")
public class AccumulateNodeTest extends DroolsTestCase {

    RuleImpl              rule;
    PropagationContext    context;
    StatefulKnowledgeSessionImpl workingMemory;
    MockObjectSource      objectSource;
    MockTupleSource       tupleSource;
    MockLeftTupleSink     sink;
    BetaNode              node;
    BetaMemory            memory;
    MockAccumulator       accumulator;
    Accumulate            accumulate;
    private PropagationContextFactory pctxFactory;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        this.rule = new RuleImpl("test-rule");

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, null);

        BuildContext buildContext = new BuildContext(kBase,
                                                     kBase.getReteooBuilder().getIdGenerator());

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        this.tupleSource = new MockTupleSource(4);
        this.objectSource = new MockObjectSource(4);
        this.sink = new MockLeftTupleSink();

        this.accumulator = new MockAccumulator();

        final ObjectType srcObjType = new ClassObjectType(String.class);
        final Pattern sourcePattern = new Pattern(0,
                                                  srcObjType);
        this.accumulate = new SingleAccumulate( sourcePattern,
                                                new Declaration[0],
                                                this.accumulator );

        this.node = new AccumulateNode(15,
                                       this.tupleSource,
                                       this.objectSource,
                                       new AlphaNodeFieldConstraint[0],
                                       EmptyBetaConstraints.getInstance(),
                                       EmptyBetaConstraints.getInstance(),
                                        this.accumulate,
                                        false,
                                        buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = ((AccumulateMemory) this.workingMemory.getNodeMemory( this.node )).getBetaMemory();

        // check memories are empty
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );
    }

    @Test
    public void testUpdateSink() {
        this.node.updateSink( this.sink,
                              this.context,
                              this.workingMemory );
        assertEquals( "No tuple should be propagated",
                             0,
                             this.sink.getAsserted().size() );

        this.node.assertLeftTuple( new LeftTupleImpl( this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                           null,
                                                                                                           null,
                                                                                                           workingMemory ),
                                                  null,
                                                  true ),
                                   this.context,
                                   this.workingMemory );
        this.node.assertLeftTuple( new LeftTupleImpl( this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                           null,
                                                                                                           null,
                                                                                                           workingMemory ),
                                                  null,
                                                  true ),
                                   this.context,
                                   this.workingMemory );

        assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );

        final MockLeftTupleSink otherSink = new MockLeftTupleSink();

        this.node.addTupleSink( otherSink );
        this.node.updateSink( otherSink,
                              this.context,
                              this.workingMemory );

        assertEquals( "Two tuples should have been propagated",
                             2,
                             otherSink.getAsserted().size() );
    }

    @Test
    public void testAssertTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
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
        assertTrue( "An empty matching objects list should be propagated",
                    this.accumulator.getMatchingObjects().isEmpty() );

        // assert tuple, should add left memory 
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory );

        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1,
                                                null,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );
        assertTrue( "An empty matching objects list should be propagated",
                           this.accumulator.getMatchingObjects().isEmpty() );

        final TupleMemory memory = this.memory.getLeftTupleMemory();
        assertTrue( memory.contains( tuple0 ) );
        assertTrue( memory.contains( tuple1 ) );

        assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    @Test
    public void testAssertTupleWithObjects() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  workingMemory);

        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
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
        assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        // assert tuple, should add left memory 
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1,
                                                null,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        final TupleMemory memory = this.memory.getLeftTupleMemory();
        assertTrue( memory.contains( tuple0 ) );
        assertTrue( memory.contains( tuple1 ) );

        assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    @Test
    public void testRetractTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  null );

        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
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
        assertTrue( "An empty matching objects list should be propagated",
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

    @Test
    public void testMemory() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase,
                                                      kBase.getReteooBuilder().getIdGenerator() );

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final MockObjectSource objectSource = new MockObjectSource( 1 );
        final MockTupleSource tupleSource = new MockTupleSource( 1 );

        final AccumulateNode accumulateNode = new AccumulateNode( 2,
                                                                  tupleSource,
                                                                  objectSource,
                                                                  new AlphaNodeFieldConstraint[0],
                                                                  EmptyBetaConstraints.getInstance(),
                                                                  EmptyBetaConstraints.getInstance(),
                                                                  this.accumulate,
                                                                  false,
                                                                  buildContext );

        final BetaMemory memory = ((AccumulateMemory) this.workingMemory.getNodeMemory( accumulateNode )).getBetaMemory();

        assertNotNull( memory );
    }

    @Test
    public void testAssertTupleSequentialMode() throws Exception {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setPhreakEnabled(false);
        conf.setSequential( true );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase,
                                                      kBase.getReteooBuilder().getIdGenerator() );
        buildContext.setTupleMemoryEnabled( false );
        // overide the original node, so we an set the BuildContext
        this.node = new AccumulateNode( 15,
                                        this.tupleSource,
                                        this.objectSource,
                                        new AlphaNodeFieldConstraint[0],
                                        EmptyBetaConstraints.getInstance(),
                                        EmptyBetaConstraints.getInstance(),
                                        this.accumulate,
                                        false,
                                        buildContext );

        this.node.addTupleSink( this.sink );

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)KnowledgeBaseFactory.newKnowledgeBase(conf).newStatefulKnowledgeSession();

        this.memory = ((AccumulateMemory) this.workingMemory.getNodeMemory( this.node )).getBetaMemory();

        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  null );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null,
                                                                                                                  null );

        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
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
                                   pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, f0),
                                   this.workingMemory );
        // check memories 
        assertNull( this.memory.getLeftTupleMemory() );
        assertEquals( 2,
                      this.memory.getRightTupleMemory().size() );
        assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        // assert tuple, should not add left memory 
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1,
                                                null,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, f1),
                                   this.workingMemory );
        assertNull( this.memory.getLeftTupleMemory() );
        assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             this.accumulator.getMatchingObjects().size() );

        assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

}
