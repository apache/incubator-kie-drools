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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.index.LeftTupleList;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.junit.Before;
import org.junit.Test;

public class JoinNodeTest extends DroolsTestCase {

    Rule                    rule;
    PropagationContext      context;
    ReteooWorkingMemory     workingMemory;
    MockObjectSource        objectSource;
    MockTupleSource         tupleSource;
    MockLeftTupleSink       sink;
    BetaNode                node;
    BetaMemory              memory;
    BetaNodeFieldConstraint constraint;

    /**
     * Setup the BetaNode used in each of the tests
     */
    @Before
    public void setUp() {
        // create mock objects
        constraint = mock( BetaNodeFieldConstraint.class );
        final ContextEntry c = mock( ContextEntry.class );

        when( constraint.createContextEntry() ).thenReturn( c );

        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION, null, null, null );
        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockLeftTupleSink();

        final RuleBaseConfiguration configuration = new RuleBaseConfiguration();

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory
                .newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase
                .getReteooBuilder().getIdGenerator() );

        this.node = new JoinNode( 15, this.tupleSource, this.objectSource,
                                  new DefaultBetaConstraints(
                                                              new BetaNodeFieldConstraint[]{this.constraint},
                                                              configuration ),
                                  buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );

    }

    @Test
    public void testAttach() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        final Field objectFfield = ObjectSource.class.getDeclaredField( "sink" );
        objectFfield.setAccessible( true );
        ObjectSinkPropagator objectSink = (ObjectSinkPropagator) objectFfield
                .get( this.objectSource );

        final Field tupleField = LeftTupleSource.class.getDeclaredField( "sink" );
        tupleField.setAccessible( true );
        LeftTupleSinkPropagator tupleSink = (LeftTupleSinkPropagator) tupleField
                .get( this.tupleSource );

        assertEquals( 15,
                      this.node.getId() );

        assertNotNull( objectSink );
        assertNotNull( tupleSink );

        final ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        BuildContext context = new BuildContext(ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );

        this.node.attach(context);

        objectSink = (ObjectSinkPropagator) objectFfield.get( this.objectSource );
        tupleSink = (LeftTupleSinkPropagator) tupleField.get( this.tupleSource );

        assertEquals( 1,
                      objectSink.getSinks().length );

        assertEquals( 1,
                      tupleSink.getSinks().length );

        assertSame( this.node,
                    objectSink.getSinks()[0] );

        assertSame( this.node,
                    tupleSink.getSinks()[0] );
    }

    @Test
    public void testMemory() {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory(
                                                                           1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final MockObjectSource objectSource = new MockObjectSource( 1 );
        final MockTupleSource tupleSource = new MockTupleSource( 1 );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory
                .newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase
                .getReteooBuilder().getIdGenerator() );
        final JoinNode joinNode = new JoinNode( 2, tupleSource, objectSource,
                                                EmptyBetaConstraints.getInstance(),
                                                buildContext );

        final BetaMemory memory = (BetaMemory) workingMemory
                .getNodeMemory( joinNode );

        assertNotNull( memory );
    }

    /**
     * Test just tuple assertions
     *
     * @throws AssertionException
     */
    @Test
    public void testAssertTuple() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0, "cheese" );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0, this.node, true );

        // assert tuple, should add one to left memory
        this.node.assertLeftTuple( tuple0,
                                   this.context,
                                   this.workingMemory );
        // check memories, left memory is populated, right memory is emptys
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightTupleMemory().size() );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1, "cheese" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1, this.node, true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );

        LeftTuple leftTuple = ( (LeftTupleList) this.memory.getLeftTupleMemory() ).getFirst();

        assertEquals( tuple0,
                      leftTuple );
        assertEquals( tuple1,
                      leftTuple.getNext() );
    }

    /**
     * Test just tuple assertions
     *
     * @throws AssertionException
     */
    @Test
    public void testAssertTupleSequentialMode() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );

        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf ) );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory
                .newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase
                .getReteooBuilder().getIdGenerator() );
        buildContext.setTupleMemoryEnabled( false );
        buildContext.setObjectTypeNodeMemoryEnabled( false );

        // override setup, so its working in sequential mode
        this.node = new JoinNode(
                                  15,
                                  this.tupleSource,
                                  this.objectSource,
                                  new DefaultBetaConstraints(
                                                              new BetaNodeFieldConstraint[]{this.constraint}, conf ),
                                  buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0, "cheese" );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0, this.node, true );

        this.node.assertObject( f0,
                                new PropagationContextImpl( 0,
                                                            PropagationContext.ASSERTION, null, null, f0 ),
                                this.workingMemory );

        // assert tuple
        this.node.assertLeftTuple( tuple0,
                                   new PropagationContextImpl( 0,
                                                               PropagationContext.ASSERTION, null, null, f0 ),
                                   this.workingMemory );

        assertEquals( 1,
                      this.sink.getAsserted().size() );

        assertNull( this.memory.getLeftTupleMemory() );

        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

        assertEquals( new LeftTupleImpl( tuple0, f0.getFirstRightTuple(), this.sink,
                                         true ),
                      ( (Object[]) this.sink.getAsserted().get( 0 ) )[0] );
    }

    /**
     * Test just object assertions
     *
     * @throws Exception
     */
    @Test
    public void testAssertObject() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory
                .insert( "test0" );

        // assert object, should add one to right memory
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

        // check new objects/handles still assert
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory
                .insert( "test1" );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      this.memory.getRightTupleMemory().size() );

        RightTuple rightTuple = this.memory.getRightTupleMemory().getFirst( new LeftTupleImpl( f0, this.node, true ), null, null );

        final InternalFactHandle rf0 = rightTuple.getFactHandle();
        final InternalFactHandle rf1 = ( (RightTuple) rightTuple.getNext() )
                .getFactHandle();

        assertEquals( f0,
                      rf0 );
        assertEquals( f1,
                      rf1 );
    }

    /**
     * Test assertion with both Objects and Tuples
     *
     * @throws Exception
     */
    @Test
    public void testAssertPropagations() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        // assert first right object
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory
                .insert( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1, "cheese" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1, this.node, true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        assertEquals( 1,
                      this.sink.getAsserted().size() );

        assertEquals( new LeftTupleImpl( tuple1, f0.getFirstRightTuple(), this.sink,
                                         true ),
                      ( (Object[]) this.sink.getAsserted().get( 0 ) )[0] );

        final DefaultFactHandle f2 = new DefaultFactHandle( 2, "cheese" );
        final LeftTupleImpl tuple2 = new LeftTupleImpl( f2, this.node, true );
        this.node.assertLeftTuple( tuple2,
                                   this.context,
                                   this.workingMemory );

        assertEquals( 2,
                      this.sink.getAsserted().size() );
        assertEquals( new LeftTupleImpl( tuple2, f0.getFirstRightTuple(), this.sink,
                                         true ),
                      ( (Object[]) this.sink.getAsserted().get( 1 ) )[0] );

        final DefaultFactHandle f3 = (DefaultFactHandle) this.workingMemory
                .insert( "test2" );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );

        assertEquals( 4,
                      this.sink.getAsserted().size() );

        final List tuples = new ArrayList();
        tuples.add( ( (Object[]) this.sink.getAsserted().get( 2 ) )[0] );
        tuples.add( ( (Object[]) this.sink.getAsserted().get( 3 ) )[0] );

        assertTrue( tuples.contains( new LeftTupleImpl( tuple1, f3
                .getFirstRightTuple(), this.sink, true ) ) );
        assertTrue( tuples.contains( new LeftTupleImpl( tuple2, f3
                .getFirstRightTuple(), this.sink, true ) ) );
    }

    /**
     * Test Tuple retraction
     *
     * @throws Exception
     * @throws RetractionException
     */
    @Test
    public void testRetractTuple() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        // setup 2 tuples 3 fact handles
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory
                .insert( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory
                .insert( "test1" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1, this.node, true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory
                .insert( "test2" );
        final LeftTupleImpl tuple2 = new LeftTupleImpl( f2, this.node, true );
        this.node.assertLeftTuple( tuple2,
                                   this.context,
                                   this.workingMemory );

        final DefaultFactHandle f3 = (DefaultFactHandle) this.workingMemory
                .insert( "test3" );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );

        final DefaultFactHandle f4 = (DefaultFactHandle) this.workingMemory
                .insert( "test4" );
        this.node.assertObject( f4,
                                this.context,
                                this.workingMemory );

        assertLength( 6,
                      this.sink.getAsserted() );

        // Double check the item is in memory
        final BetaMemory memory = (BetaMemory) this.workingMemory
                .getNodeMemory( this.node );
        assertTrue( memory.getRightTupleMemory().contains(
                                                           f0.getFirstRightTuple() ) );

        // Retract an object, check propagations and memory
        this.node.retractRightTuple( f0.getFirstRightTuple(),
                                     this.context,
                                     this.workingMemory );
        assertLength( 2,
                      this.sink.getRetracted() );

        List tuples = new ArrayList();
        tuples.add( ( (Object[]) this.sink.getRetracted().get( 0 ) )[0] );
        tuples.add( ( (Object[]) this.sink.getRetracted().get( 1 ) )[0] );

        assertTrue( tuples.contains( new LeftTupleImpl( tuple1, f0
                .getFirstRightTuple(), this.sink, true ) ) );
        assertTrue( tuples.contains( new LeftTupleImpl( tuple1, f0
                .getFirstRightTuple(), this.sink, true ) ) );

        // Now check the item is no longer in memory
        assertFalse( memory.getRightTupleMemory().contains(
                                                            f0.getFirstRightTuple() ) );

        this.node.retractLeftTuple( tuple2,
                                    this.context,
                                    this.workingMemory );
        assertEquals( 4,
                      this.sink.getRetracted().size() );

        tuples = new ArrayList();
        tuples.add( ( (Object[]) this.sink.getRetracted().get( 2 ) )[0] );
        tuples.add( ( (Object[]) this.sink.getRetracted().get( 3 ) )[0] );

        assertTrue( tuples.contains( new LeftTupleImpl( tuple2, f3
                .getFirstRightTuple(), this.sink, true ) ) );
        assertTrue( tuples.contains( new LeftTupleImpl( tuple2, f4
                .getFirstRightTuple(), this.sink, true ) ) );
    }

    @Test
    public void testConstraintPropagations() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( false );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( false );

        // assert first right object
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory
                .insert( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1, "cheese" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1, this.node, true );
        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        // Should be no assertions
        assertLength( 0,
                      this.sink.getAsserted() );

        this.node.retractRightTuple( f0.getFirstRightTuple(),
                                     this.context,
                                     this.workingMemory );
        assertLength( 0,
                      this.sink.getRetracted() );
    }

    @Test
    public void testUpdateSink() {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ),
                                              any( InternalFactHandle.class ) ) ).thenReturn( true );
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ),
                                               any( ContextEntry.class ) ) ).thenReturn( true );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory(
                                                                           1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory
                .newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase
                .getReteooBuilder().getIdGenerator() );

        final JoinNode joinNode = new JoinNode( 1, this.tupleSource,
                                                this.objectSource, EmptyBetaConstraints.getInstance(),
                                                buildContext );

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        final MockLeftTupleSink sink1 = new MockLeftTupleSink( 2 );
        joinNode.addTupleSink( sink1 );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0, "string0" );

        final LeftTupleImpl tuple1 = new LeftTupleImpl( f0, this.node, true );

        joinNode.assertLeftTuple( tuple1,
                                  this.context,
                                  workingMemory );

        final String string1 = "string1";
        final DefaultFactHandle string1Handle = new DefaultFactHandle( 1,
                                                                       string1 );

        joinNode.assertObject( string1Handle,
                               this.context,
                               workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Add the new sink, this should be updated from the re-processed
        // joinnode memory
        final MockLeftTupleSink sink2 = new MockLeftTupleSink( 3 );
        assertLength( 0,
                      sink2.getAsserted() );

        joinNode.updateSink( sink2,
                             this.context,
                             workingMemory );

        assertLength( 1,
                      sink2.getAsserted() );
    }

    @Test
    public void testSlotSpecific() {
        PropagationContext contextPassAll = new PropagationContextImpl(0,
                PropagationContext.ASSERTION, null, null, null, 0, 0, EntryPoint.DEFAULT, Long.MAX_VALUE);
        PropagationContext contextPassNothing = new PropagationContextImpl(0,
                PropagationContext.ASSERTION, null, null, null, 0, 0, EntryPoint.DEFAULT, 0);
        PropagationContext contextPass2And3 = new PropagationContextImpl(0,
                PropagationContext.ASSERTION, null, null, null, 0, 0, EntryPoint.DEFAULT, 6);

        when( constraint.isAllowedCachedLeft(any(ContextEntry.class), any(InternalFactHandle.class))).thenReturn(true);
        when( constraint.isAllowedCachedRight(any(LeftTupleImpl.class), any(ContextEntry.class))).thenReturn(true);

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory(1,
                (ReteooRuleBase) RuleBaseFactory.newRuleBase());

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory
                .newRuleBase();
        BuildContext buildContext = new BuildContext(ruleBase, ruleBase
                .getReteooBuilder().getIdGenerator());

        final JoinNode joinNode = new JoinNode(1, this.tupleSource,
                this.objectSource, EmptyBetaConstraints.getInstance(), buildContext);

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        final MockLeftTupleSink sink1 = new MockLeftTupleSink(2);
        joinNode.addTupleSink(sink1);

        final DefaultFactHandle f0 = new DefaultFactHandle(0, "string0");

        final LeftTupleImpl tuple1 = new LeftTupleImpl(f0, this.node, true);

        joinNode.assertLeftTuple(tuple1, this.context, workingMemory);

        final String string1 = "string1";
        final DefaultFactHandle string1Handle = new DefaultFactHandle(1, string1);

        ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(null, null);

        assertLength(0, sink1.getAsserted());

        joinNode.modifyObject(string1Handle, modifyPreviousTuples, contextPassNothing, workingMemory);
        assertLength(0, sink1.getAsserted());

        joinNode.setRightDeclaredMask(0);
        joinNode.initInferredMask();
        joinNode.modifyObject(string1Handle, modifyPreviousTuples, contextPass2And3, workingMemory);
        assertLength(0, sink1.getAsserted());

        joinNode.setRightDeclaredMask(9);
        joinNode.initInferredMask();
        joinNode.modifyObject(string1Handle, modifyPreviousTuples, contextPass2And3, workingMemory);
        assertLength(0, sink1.getAsserted());

        joinNode.setRightDeclaredMask(3);
        joinNode.initInferredMask();
        joinNode.modifyObject(string1Handle, modifyPreviousTuples, contextPass2And3, workingMemory);
        assertLength(1, sink1.getAsserted());

        joinNode.modifyObject(string1Handle, modifyPreviousTuples, contextPassAll, workingMemory);
        assertLength(2, sink1.getAsserted());
    }
}
