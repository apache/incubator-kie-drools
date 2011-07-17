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

import static org.mockito.Mockito.mock;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.RuleBaseFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.definition.rule.Rule;
import org.drools.reteoo.EvalConditionNode.EvalMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvalConditionNodeTest extends DroolsTestCase {
    private PropagationContext  context;
    private ReteooWorkingMemory workingMemory;
    private ReteooRuleBase      ruleBase;
    private BuildContext        buildContext;

    @Before
    public void setUp() {
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase,
                                              ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );

        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null,
                                                   null );

        this.workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();
    }

    @Test
    public void testAttach() throws Exception {
        final MockTupleSource source = new MockTupleSource( 12 );

        final EvalConditionNode node = new EvalConditionNode( 18,
                                                              source,
                                                              new MockEvalCondition( true ),
                                                              buildContext );

        assertEquals( 18,
                      node.getId() );

        assertEquals( 0,
                      source.getAttached() );

        node.attach();

        assertEquals( 1,
                      source.getAttached() );

    }

    @Test
    public void testMemory() {
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final MockTupleSource source = new MockTupleSource( 12 );

        final EvalConditionNode node = new EvalConditionNode( 18,
                                                              source,
                                                              new MockEvalCondition( true ),
                                                              buildContext );

        final EvalMemory memory = (EvalMemory) workingMemory.getNodeMemory( node );

        assertNotNull( memory );
    }

    /**
     * If a eval allows an incoming Object, then the Object MUST be
     * propagated. This tests that the memory is updated
     * 
     * @throws FactException
     */
    @Test
    public void testAssertedAllowed() throws FactException {
        final MockEvalCondition eval = new MockEvalCondition( true );

        // Create a test node that always returns false 
        final EvalConditionNode node = new EvalConditionNode( 1,
                                                              new MockTupleSource( 15 ),
                                                              eval,
                                                              buildContext );

        final MockLeftTupleSink sink = new MockLeftTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "stilton" );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
                                                sink,
                                                true );

        // Tuple should pass and propagate 
        node.assertLeftTuple( tuple0,
                              this.context,
                              this.workingMemory );

        // Create the Tuple
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            "cheddar" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1,
                                                sink,
                                                true );

        // Tuple should pass and propagate 
        node.assertLeftTuple( tuple1,
                              this.context,
                              this.workingMemory );

        // make sure assertions were propagated
        assertEquals( 2,
                      sink.getAsserted().size() );
    }

    @Test
    public void testAssertedAllowedThenRetract() throws FactException {
        final MockEvalCondition eval = new MockEvalCondition( true );

        // Create a test node that always returns false 
        final EvalConditionNode node = new EvalConditionNode( 1,
                                                              new MockTupleSource( 15 ),
                                                              eval,
                                                              buildContext );

        final MockLeftTupleSink sink = new MockLeftTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "stilton" );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
                                                sink,
                                                true );

        // Tuple should pass and propagate 
        node.assertLeftTuple( tuple0,
                              this.context,
                              this.workingMemory );

        // we create and retract two tuples, checking the linkedtuples is null for JBRULES-246 "NPE on retract()"        
        // Create the Tuple
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            "cheddar" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1,
                                                sink,
                                                true );

        // Tuple should pass and propagate 
        node.assertLeftTuple( tuple1,
                              this.context,
                              this.workingMemory );

        // make sure assertions were propagated
        assertEquals( 2,
                      sink.getAsserted().size() );

        // Now test that the fact is retracted correctly
        node.retractLeftTuple( tuple0,
                               this.context,
                               this.workingMemory );

        // make sure retractions were propagated
        assertEquals( 1,
                      sink.getRetracted().size() );

        // Now test that the fact is retracted correctly
        node.retractLeftTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // make sure retractions were propagated
        assertEquals( 2,
                      sink.getRetracted().size() );
    }

    @Test
    public void testAssertedNotAllowed() throws FactException {
        final MockEvalCondition eval = new MockEvalCondition( false );

        // Create a test node that always returns false 
        final EvalConditionNode node = new EvalConditionNode( 1,
                                                              new MockTupleSource( 15 ),
                                                              eval,
                                                              buildContext );

        final MockLeftTupleSink sink = new MockLeftTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "stilton" );
        final LeftTupleImpl tuple0 = new LeftTupleImpl( f0,
                                                sink,
                                                true );

        // Tuple should fail and not propagate
        node.assertLeftTuple( tuple0,
                              this.context,
                              this.workingMemory );

        // Create the Tuple
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            "cheddar" );
        final LeftTupleImpl tuple1 = new LeftTupleImpl( f1,
                                                sink,
                                                true );

        // Tuple should fail and not propagate 
        node.assertLeftTuple( tuple1,
                              this.context,
                              this.workingMemory );

        // Check memory was not populated
        this.workingMemory.getNodeMemory( node );

        // test no propagations
        assertEquals( 0,
                      sink.getAsserted().size() );
        assertEquals( 0,
                      sink.getRetracted().size() );
    }

    /**
     * If a eval allows an incoming Object, then the Object MUST be
     * propagated. This tests that the memory is updated
     * 
     * @throws FactException
     */
    @Test
    public void testDoRemove() throws FactException {
        final MockEvalCondition eval = new MockEvalCondition( true );

        final EvalConditionNode parent = new EvalConditionNode( 1,
                                                                new MockTupleSource( 15 ),
                                                                eval,
                                                                buildContext );

        // Create a test node that always returns false 
        final EvalConditionNode node = new EvalConditionNode( 2,
                                                              parent,
                                                              eval,
                                                              buildContext );

        parent.addTupleSink( node );

        final MockLeftTupleSink sink = new MockLeftTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            "stilton" );
        // an eval node always has at least a LIAN before it, so, tuples that reach it 
        // always have at least one tuple parent
        final LeftTupleImpl parentTuple = new LeftTupleImpl( f0,
                                                     null,
                                                     true );
        final LeftTuple tuple0 = sink.createLeftTuple( parentTuple,
                                                       sink,
                                                       true );

        // Tuple should pass and propagate 
        node.assertLeftTuple( tuple0,
                              this.context,
                              this.workingMemory );

        // make sure assertions were propagated
        assertEquals( 1,
                      sink.getAsserted().size() );

        RuleRemovalContext removalContext = new RuleRemovalContext( mock( Rule.class ));
        InternalWorkingMemory[] workingMemories = new InternalWorkingMemory[]{this.workingMemory};

        // This use to throw ClassCastException JBRULES-1719
        node.remove( removalContext,
                     this.ruleBase.getReteooBuilder(),
                     sink,
                     workingMemories );
    }
}
