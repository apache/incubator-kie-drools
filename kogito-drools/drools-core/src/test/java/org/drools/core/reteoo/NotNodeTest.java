/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.IntrospectionException;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

@Ignore("phreak")
public class NotNodeTest extends DroolsTestCase {
    RuleImpl                rule;
    PropagationContext      context;
    StatefulKnowledgeSessionImpl workingMemory;
    MockObjectSource        objectSource;
    MockTupleSource         tupleSource;
    MockLeftTupleSink       sink;
    NotNode                 node;
    RightInputAdapterNode   ria;
    BetaMemory              memory;
    BetaNodeFieldConstraint constraint;

    private PropagationContextFactory pctxFactory;

    /**
     * Setup the BetaNode used in each of the tests
     * @throws IntrospectionException
     */
    @Before
    public void setUp() throws IntrospectionException {
        // create mock objects
        constraint = mock(BetaNodeFieldConstraint.class);
        final ContextEntry c = mock(ContextEntry.class);

        when(constraint.createContextEntry()).thenReturn(c);

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        this.rule = new RuleImpl("test-rule");
        pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);
        this.workingMemory = new StatefulKnowledgeSessionImpl(1L, kBase);

        final RuleBaseConfiguration configuration = new RuleBaseConfiguration();

        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext(kBase,
                                                     kBase.getReteooBuilder().getIdGenerator());

        this.objectSource = new MockObjectSource(155);
        this.tupleSource = new MockTupleSource(60);

        // string1Declaration is bound to pattern 3 
        this.node = new NotNode(15,
                                new MockTupleSource(5),
                                new MockObjectSource(8),
                                new DefaultBetaConstraints(new BetaNodeFieldConstraint[]{this.constraint},
                                                           configuration),
                                buildContext);

        this.sink = new MockLeftTupleSink();
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


    public void testNotStandard() {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ), any( InternalFactHandle.class ) )).thenReturn(true);
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ), any( ContextEntry.class ) )).thenReturn(true);

        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( cheddar );

        final LeftTupleImpl tuple1 = new LeftTupleImpl( f0,
                                                this.node,
                                                true );

        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        assertEquals( new LeftTupleImpl( f0,
                                     this.sink,
                                     true ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        // LeftTuple has no matches and has propagated, so should be in memory
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );

        // assert will match, so propagated tuple should be deleted
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( brie );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // check no as assertions, but should be one retraction
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );

        assertEquals( new LeftTupleImpl( f0,
                                     this.sink,
                                     true ),
                      ((Object[]) this.sink.getRetracted().get( 0 ))[0] );

        //LeftTuple is now matched and is not propagated, so should not be in memory
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );

        // assert tuple, will have matches, so no propagation
        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.insert( new Cheese( "gouda",
                                                                                                10 ) );
        final LeftTupleImpl tuple2 = new LeftTupleImpl( f2,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple2,
                                   this.context,
                                   this.workingMemory );

        // check no propagations 
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );

        // both LeftTuples are matched, so neither should be in the memory 
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

        // When this is deleted both tuples should assert
        this.node.retractRightTuple( f1.getFirstRightTuple(),
                                     this.context,
                                     this.workingMemory );

        // neither LeftTuple is matched, both should be in the memory
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );

        // check propagations 
        assertLength( 3,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );
    }

    @Test
    public void testNotWithConstraints() {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ), any( InternalFactHandle.class ) )).thenReturn(false);
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ), any( ContextEntry.class ) )).thenReturn(false);

        // assert tuple
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( cheddar );

        final LeftTupleImpl tuple1 = new LeftTupleImpl( f0,
                                                this.node,
                                                true );

        this.node.assertLeftTuple( tuple1,
                                   this.context,
                                   this.workingMemory );

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        assertEquals( new LeftTupleImpl( f0,
                                     this.sink,
                                     true ),
                      ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        // assert will not match, so activation should stay propagated
        final Cheese brie = new Cheese( "brie",
                                        10 );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( brie );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // check no as assertions, but should be one retraction
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );

        // assert tuple, will have no matches, so do assert propagation
        final DefaultFactHandle f2 = (DefaultFactHandle) this.workingMemory.insert( new Cheese( "gouda",
                                                                                                10 ) );
        final LeftTupleImpl tuple2 = new LeftTupleImpl( f2,
                                                this.node,
                                                true );
        this.node.assertLeftTuple( tuple2,
                                   this.context,
                                   this.workingMemory );

        // check no as assertions, but should be one retraction
        assertLength( 2,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );
    }

    /**
     * Tests memory consistency after insert/update/retract calls
     *
     */
    public void TestNotMemoryManagement() {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ), any( InternalFactHandle.class ) )).thenReturn(true);
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ), any( ContextEntry.class ) )).thenReturn(true);

        try {
            // assert tuple
            final Cheese cheddar = new Cheese( "cheddar",
                                               10 );
            final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.insert( cheddar );
            final LeftTupleImpl tuple1 = new LeftTupleImpl( f0,
                                                    this.node,
                                                    true );

            this.node.assertLeftTuple( tuple1,
                                       this.context,
                                       this.workingMemory );

            // assert will match, so propagated tuple should be deleted
            final Cheese brie = new Cheese( "brie",
                                            10 );
            final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.insert( brie );

            // Initially, no objects in right memory
            assertEquals( 0,
                          this.memory.getRightTupleMemory().size() );
            this.node.assertObject( f1,
                                    this.context,
                                    this.workingMemory );

            // Now, needs to have 1 object in right memory
            assertEquals( 1,
                          this.memory.getRightTupleMemory().size() );

            // simulate modify
            this.node.retractRightTuple( f1.getFirstRightTuple(),
                                         this.context,
                                         this.workingMemory );
            this.node.assertObject( f1,
                                    this.context,
                                    this.workingMemory );
            // Memory should not change
            assertEquals( 1,
                          this.memory.getRightTupleMemory().size() );

            // When this is retracter both tuples should assert
            this.node.retractRightTuple( f1.getFirstRightTuple(),
                                         this.context,
                                         this.workingMemory );
            assertEquals( 0,
                          this.memory.getRightTupleMemory().size() );

            // check memory sizes
            assertEquals( 1,
                          this.memory.getLeftTupleMemory().size() );

            // simulate modify
            this.node.retractLeftTuple( tuple1,
                                        this.context,
                                        this.workingMemory );
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
        } catch ( final Exception e ) {
            fail( "No exception should be raised in this procedure, but got: " + e.toString() );
        }
    }

    @Test
    public void testGetConstraints_ReturnsNullEvenWithEmptyBinder() {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ), any( InternalFactHandle.class ) )).thenReturn(true);
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ), any( ContextEntry.class ) )).thenReturn(true);

        final BetaConstraints nullConstraints = EmptyBetaConstraints.getInstance();

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase,
                                                      kBase.getReteooBuilder().getIdGenerator() );

        final NotNode notNode = new NotNode( 1,
                                             this.tupleSource,
                                             this.objectSource,
                                             nullConstraints,
                                             buildContext );
        final BetaNodeFieldConstraint[] constraints = notNode.getConstraints();
        assertEquals( 0,
                      constraints.length );
    }

    /**
     * Test just tuple assertions
     *
     */
    @Test
    public void testAssertTupleSequentialMode() throws Exception {
        when( constraint.isAllowedCachedLeft( any( ContextEntry.class ), any( InternalFactHandle.class ) )).thenReturn(true);
        when( constraint.isAllowedCachedRight( any( LeftTupleImpl.class ), any( ContextEntry.class ) )).thenReturn(true);

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setPhreakEnabled(false);
        conf.setSequential( true );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);

        this.workingMemory = new StatefulKnowledgeSessionImpl( 1L, kBase );

        BuildContext buildContext = new BuildContext( kBase,
                                                      kBase.getReteooBuilder().getIdGenerator() );

        buildContext.setTupleMemoryEnabled( false );
        buildContext.setObjectTypeNodeMemoryEnabled( false );

        // override setup, so its working in sequential mode
        this.node = new NotNode( 15,
                                 this.tupleSource,
                                 this.objectSource,
                                 new DefaultBetaConstraints( new BetaNodeFieldConstraint[]{this.constraint},
                                                             conf ),
                                 buildContext );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "cheese" );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
                                                this.node,
                                                true );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple
        this.node.assertLeftTuple( tuple0,
                                   pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, f0),
                                   this.workingMemory );

        assertEquals( 0,
                      this.sink.getAsserted().size() );

        assertNull( this.memory.getLeftTupleMemory() );

        assertEquals( 1,
                      this.memory.getRightTupleMemory().size() );

    }

}
