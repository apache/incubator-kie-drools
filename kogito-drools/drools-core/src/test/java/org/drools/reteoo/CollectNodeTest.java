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

package org.drools.reteoo;

import java.util.Collection;
import java.util.LinkedList;

import junit.framework.Assert;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.CollectNode.CollectMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.Collect;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * @author etirelli
 *
 */
public class CollectNodeTest extends DroolsTestCase {
    Rule                rule;
    PropagationContext  contextAssert;
    PropagationContext  contextRetract;
    ReteooWorkingMemory workingMemory;
    MockObjectSource    objectSource;
    MockTupleSource     tupleSource;
    MockLeftTupleSink   sink;
    BetaNode            node;
    CollectMemory       memory;
    Collect             collect;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new Rule( "test-rule" );
        this.contextAssert = new PropagationContextImpl( 0,
                                                         PropagationContext.ASSERTION,
                                                         null,
                                                         null,
                                                         null );
        this.contextRetract = new PropagationContextImpl( 0,
                                                          PropagationContext.RETRACTION,
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

        final ObjectType srcObjType = new ClassObjectType( String.class );
        final Pattern sourcePattern = new Pattern( 0,
                                                   srcObjType );
        final ObjectType resultObjType = new ClassObjectType( LinkedList.class );
        final Pattern resultPattern = new Pattern( 1,
                                                   resultObjType );
        this.collect = new Collect( sourcePattern,
                                    resultPattern );

        this.node = new CollectNode( 15,
                                     this.tupleSource,
                                     this.objectSource,
                                     new AlphaNodeFieldConstraint[0],
                                     EmptyBetaConstraints.getInstance(),
                                     EmptyBetaConstraints.getInstance(),
                                     Behavior.EMPTY_BEHAVIOR_LIST,
                                     this.collect,
                                     false,
                                     buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = (CollectMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.betaMemory.getRightTupleMemory().size() );
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUpdateNewNode() {
        this.node.updateSink( this.sink,
                              this.contextAssert,
                              this.workingMemory );
        Assert.assertEquals( "No tuple should be propagated",
                             0,
                             this.sink.getAsserted().size() );

        this.node.assertLeftTuple( new LeftTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                           null,
                                                                                                           null ),
                                                  this.node,
                                                  true ),
                                   this.contextAssert,
                                   this.workingMemory );
        this.node.assertLeftTuple( new LeftTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                           null,
                                                                                                           null ),
                                                  this.node,
                                                  true ),
                                   this.contextAssert,
                                   this.workingMemory );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );

        final MockLeftTupleSink otherSink = new MockLeftTupleSink();

        this.node.addTupleSink( otherSink );
        this.node.updateSink( otherSink,
                              this.contextAssert,
                              this.workingMemory );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             otherSink.getAsserted().size() );
    }

    public void testAssertTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null );
        final LeftTuple tuple0 = new LeftTuple( f0,
                                                this.node,
                                                true );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.contextAssert,
                                   this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.betaMemory.getRightTupleMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).isEmpty() );

        // assert tuple, should add left memory 
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null );

        final LeftTuple tuple1 = new LeftTuple( f1,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.contextAssert,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).isEmpty() );

        assertTrue( this.memory.betaMemory.getLeftTupleMemory().contains( tuple0 ) );
        assertTrue( this.memory.betaMemory.getLeftTupleMemory().contains( tuple1 ) );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    public void testAssertTupleWithObjects() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null );

        final LeftTuple tuple0 = new LeftTuple( f0,
                                                this.node,
                                                true );

        this.node.assertObject( f0,
                                this.contextAssert,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.contextAssert,
                                this.workingMemory );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.contextAssert,
                                   this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        assertEquals( 2,
                      this.memory.betaMemory.getRightTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).size() );

        // assert tuple, should add left memory 
        final LeftTuple tuple1 = new LeftTuple( f1,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.contextAssert,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).size() );

        assertTrue( this.memory.betaMemory.getLeftTupleMemory().contains( tuple0 ) );
        assertTrue( this.memory.betaMemory.getLeftTupleMemory().contains( tuple1 ) );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    public void testRetractTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null );

        final LeftTuple tuple0 = new LeftTuple( f0,
                                                this.node,
                                                true );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.contextAssert,
                                   this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.betaMemory.getRightTupleMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).isEmpty() );

        this.node.retractLeftTuple( tuple0,
                                    this.contextRetract,
                                    this.workingMemory );
        assertEquals( 0,
                      this.memory.betaMemory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.sink.getRetracted().size() );
        assertEquals( 1,
                      this.sink.getAsserted().size() );
    }

    public void testMemory() {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ruleBase.getReteooBuilder().getIdGenerator() );

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final MockObjectSource objectSource = new MockObjectSource( 1 );
        final MockTupleSource tupleSource = new MockTupleSource( 1 );

        final CollectNode collectNode = new CollectNode( 2,
                                                         tupleSource,
                                                         objectSource,
                                                         new AlphaNodeFieldConstraint[0],
                                                         EmptyBetaConstraints.getInstance(),
                                                         EmptyBetaConstraints.getInstance(),
                                                         Behavior.EMPTY_BEHAVIOR_LIST,
                                                         this.collect,
                                                         false,
                                                         buildContext );

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( collectNode );

        assertNotNull( memory );
    }

    public void testAssertTupleSequentialMode() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );

        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf ) );

        this.memory = (CollectMemory) this.workingMemory.getNodeMemory( this.node );
        this.node.setLeftTupleMemoryEnabled( false );

        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese",
                                                                                                                  null,
                                                                                                                  null );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese",
                                                                                                                  null,
                                                                                                                  null );

        final LeftTuple tuple0 = new LeftTuple( f0,
                                                this.node,
                                                true );

        this.node.assertObject( f0,
                                this.contextAssert,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.contextAssert,
                                this.workingMemory );

        // assert tuple, should not add to left memory, since we are in sequential mode
        this.node.assertLeftTuple( tuple0,
                                   this.contextAssert,
                                   this.workingMemory );
        // check memories 
        assertNull( this.memory.betaMemory.getLeftTupleMemory() );
        assertEquals( 2,
                      this.memory.betaMemory.getRightTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).size() );

        // assert tuple, should not add to left memory, since we are in sequential mode
        final LeftTuple tuple1 = new LeftTuple( f1,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple1,
                                   this.contextAssert,
                                   this.workingMemory );
        assertNull( this.memory.betaMemory.getLeftTupleMemory() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).size() );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

}
