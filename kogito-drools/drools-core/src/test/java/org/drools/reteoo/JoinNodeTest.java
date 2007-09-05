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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.MockConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.FactEntry;
import org.drools.util.Iterator;

public class JoinNodeTest extends DroolsTestCase {
    Rule                rule;
    PropagationContext  context;
    ReteooWorkingMemory workingMemory;
    MockObjectSource    objectSource;
    MockTupleSource     tupleSource;
    MockTupleSink       sink;
    BetaNode            node;
    BetaMemory          memory;
    MockConstraint      constraint = new MockConstraint();

    /**
     * Setup the BetaNode used in each of the tests
     */
    public void setUp() {
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );
        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockTupleSink();

        final RuleBaseConfiguration configuration = new RuleBaseConfiguration();
        
        ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );

        this.node = new JoinNode( 15,
                                  this.tupleSource,
                                  this.objectSource,
                                  new DefaultBetaConstraints( new BetaNodeFieldConstraint[]{this.constraint},
                                                              configuration ),
                                  buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      this.memory.getTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getFactHandleMemory().size() );

    }

    public void testAttach() throws Exception {
        final Field objectFfield = ObjectSource.class.getDeclaredField( "sink" );
        objectFfield.setAccessible( true );
        ObjectSinkPropagator objectSink = (ObjectSinkPropagator) objectFfield.get( this.objectSource );

        final Field tupleField = TupleSource.class.getDeclaredField( "sink" );
        tupleField.setAccessible( true );
        TupleSinkPropagator tupleSink = (TupleSinkPropagator) tupleField.get( this.tupleSource );

        assertEquals( 15,
                      this.node.getId() );

        assertNotNull( objectSink );
        assertNotNull( tupleSink );

        this.node.attach();

        objectSink = (ObjectSinkPropagator) objectFfield.get( this.objectSource );
        tupleSink = (TupleSinkPropagator) tupleField.get( this.tupleSource );

        assertEquals( 1,
                      objectSink.getSinks().length );

        assertEquals( 1,
                      tupleSink.getSinks().length );

        assertSame( this.node,
                    objectSink.getSinks()[0] );

        assertSame( this.node,
                    tupleSink.getSinks()[0] );
    }

    public void testMemory() {
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final MockObjectSource objectSource = new MockObjectSource( 1 );
        final MockTupleSource tupleSource = new MockTupleSource( 1 );

        ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );        
        final JoinNode joinNode = new JoinNode( 2,
                                                tupleSource,
                                                objectSource,
                                                EmptyBetaConstraints.getInstance(),
                                                buildContext );

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( joinNode );

        assertNotNull( memory );
    }

    /**
     * Test just tuple assertions
     * 
     * @throws AssertionException
     */
    public void testAssertTuple() throws Exception {
        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "cheese" );
        final ReteTuple tuple0 = new ReteTuple( f0 );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories, left memory is populated, right memory is emptys
        assertEquals( 1,
                      this.memory.getTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getFactHandleMemory().size() );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            "cheese" );
        final ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 2,
                      this.memory.getTupleMemory().size() );

        final Iterator it = this.memory.getTupleMemory().iterator();
        assertEquals( tuple0,
                      it.next() );
        assertEquals( tuple1,
                      it.next() );
    }

    /**
     * Test just tuple assertions
     * 
     * @throws AssertionException
     */
    public void testAssertTupleSequentialMode() throws Exception {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );

        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf ) );

        ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );
        buildContext.setHasLeftMemory( false );
        buildContext.setHasObjectTypeMemory( false );
        
        // override setup, so its working in sequential mode
        this.node = new JoinNode( 15,
                                  this.tupleSource,
                                  this.objectSource,
                                  new DefaultBetaConstraints( new BetaNodeFieldConstraint[]{this.constraint},
                                                              conf ),
                                  buildContext);

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "cheese" );
        final ReteTuple tuple0 = new ReteTuple( f0 );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );

        assertEquals( 1,
                      this.sink.getAsserted().size() );

        assertNull( this.memory.getTupleMemory() );

        assertEquals( 1,
                      this.memory.getFactHandleMemory().size() );

        assertEquals( new ReteTuple( tuple0,
                                     f0 ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );
    }

    /**
     * Test just object assertions
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( "test0" );

        // assert object, should add one to right memory
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 0,
                      this.memory.getTupleMemory().size() );
        assertEquals( 1,
                      this.memory.getFactHandleMemory().size() );

        // check new objects/handles still assert
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( "test1" );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      this.memory.getFactHandleMemory().size() );

        final Iterator it = this.memory.getFactHandleMemory().iterator( new ReteTuple( f0 ) );

        final InternalFactHandle rf0 = ((FactEntry) it.next()).getFactHandle();
        final InternalFactHandle rf1 = ((FactEntry) it.next()).getFactHandle();

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
    public void testAssertPropagations() throws Exception {
        // assert first right object
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            "cheese" );
        final ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        assertEquals( 1,
                      this.sink.getAsserted().size() );

        assertEquals( new ReteTuple( tuple1,
                                     f0 ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        final DefaultFactHandle f2 = new DefaultFactHandle( 2,
                                                            "cheese" );
        final ReteTuple tuple2 = new ReteTuple( f2 );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        assertEquals( 2,
                      this.sink.getAsserted().size() );
        assertEquals( new ReteTuple( tuple2,
                                     f0 ),
                      ((Object[]) this.sink.getAsserted().get( 1 ))[0] );

        final DefaultFactHandle f3 = (DefaultFactHandle) this.workingMemory.insert( "test2" );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );

        assertEquals( 4,
                      this.sink.getAsserted().size() );

        final List tuples = new ArrayList();
        tuples.add( ((Object[]) this.sink.getAsserted().get( 2 ))[0] );
        tuples.add( ((Object[]) this.sink.getAsserted().get( 3 ))[0] );

        assertTrue( tuples.contains( new ReteTuple( tuple1,
                                                    f3 ) ) );
        assertTrue( tuples.contains( new ReteTuple( tuple2,
                                                    f3 ) ) );
    }

    /**
     * Test Tuple retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractTuple() throws Exception {
        // setup 2 tuples 3 fact handles
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( "test1" );
        final ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.insert( "test2" );
        final ReteTuple tuple2 = new ReteTuple( f2 );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        final DefaultFactHandle f3 = (DefaultFactHandle) this.workingMemory.insert( "test3" );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );

        final DefaultFactHandle f4 = (DefaultFactHandle) this.workingMemory.insert( "test4" );
        this.node.assertObject( f4,
                                this.context,
                                this.workingMemory );

        assertLength( 6,
                      this.sink.getAsserted() );

        // Double check the item is in memory
        final BetaMemory memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
        assertTrue( memory.getFactHandleMemory().contains( f0 ) );

        // Retract an object, check propagations  and memory
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );
        assertLength( 2,
                      this.sink.getRetracted() );

        List tuples = new ArrayList();
        tuples.add( ((Object[]) this.sink.getRetracted().get( 0 ))[0] );
        tuples.add( ((Object[]) this.sink.getRetracted().get( 1 ))[0] );

        assertTrue( tuples.contains( new ReteTuple( tuple1,
                                                    f0 ) ) );
        assertTrue( tuples.contains( new ReteTuple( tuple1,
                                                    f0 ) ) );

        // Now check the item  is no longer in memory
        assertFalse( memory.getFactHandleMemory().contains( f0 ) );

        this.node.retractTuple( tuple2,
                                this.context,
                                this.workingMemory );
        assertEquals( 4,
                      this.sink.getRetracted().size() );

        tuples = new ArrayList();
        tuples.add( ((Object[]) this.sink.getRetracted().get( 2 ))[0] );
        tuples.add( ((Object[]) this.sink.getRetracted().get( 3 ))[0] );

        assertTrue( tuples.contains( new ReteTuple( tuple2,
                                                    f3 ) ) );
        assertTrue( tuples.contains( new ReteTuple( tuple2,
                                                    f4 ) ) );
    }

    public void testConstraintPropagations() throws Exception {
        this.constraint.isAllowed = false;
        // assert first right object
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add left memory should be 2
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            "cheese" );
        final ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // Should be no assertions
        assertLength( 0,
                      this.sink.getAsserted() );

        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );
        assertLength( 0,
                      this.sink.getRetracted() );
    }

    public void testUpdateSink() {
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );
        
        final JoinNode joinNode = new JoinNode( 1,
                                                this.tupleSource,
                                                this.objectSource,
                                                EmptyBetaConstraints.getInstance(),
                                                buildContext );

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        final MockTupleSink sink1 = new MockTupleSink( 2 );
        joinNode.addTupleSink( sink1 );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "string0" );

        final ReteTuple tuple1 = new ReteTuple( f0 );

        joinNode.assertTuple( tuple1,
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
        final MockTupleSink sink2 = new MockTupleSink( 3 );
        assertLength( 0,
                      sink2.getAsserted() );

        joinNode.updateSink( sink2,
                             this.context,
                             workingMemory );

        assertLength( 1,
                      sink2.getAsserted() );
    }

}