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

package org.drools.reteoo.nodes;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.MockEvalCondition;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class EvalConditionNodeTest extends DroolsTestCase {
    private PropagationContext    context;
    private StatefulKnowledgeSessionImpl workingMemory;
    private InternalKnowledgeBase kBase;
    private BuildContext          buildContext;

    @Before
    public void setUp() {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext(kBase,
                                             kBase.getReteooBuilder().getIdGenerator());

        PropagationContextFactory pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
    }

    @Test
    public void testAttach() throws Exception {
        final MockTupleSource source = new MockTupleSource(12);

        final EvalConditionNode node = new EvalConditionNode(18,
                                                             source,
                                                             new MockEvalCondition(true),
                                                             buildContext);

        assertEquals(18,
                     node.getId());

        assertEquals(0,
                     source.getAttached());

        node.attach(buildContext);

        assertEquals( 1,
                      source.getAttached() );

    }

    @Test
    public void testMemory() {
        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl( 1, (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase() );

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
     */
    @Test
    public void testAssertedAllowed() {
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
    public void testAssertedAllowedThenRetract() {
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

        // Now test that the fact is deleted correctly
        node.retractLeftTuple( tuple0,
                               this.context,
                               this.workingMemory );

        // make sure retractions were propagated
        assertEquals( 1,
                      sink.getRetracted().size() );

        // Now test that the fact is deleted correctly
        node.retractLeftTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // make sure retractions were propagated
        assertEquals( 2,
                      sink.getRetracted().size() );
    }

    @Test
    public void testAssertedNotAllowed() {
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
     */
    @Test
    public void testDoRemove() {
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
                                                       null, true);

        // Tuple should pass and propagate 
        node.assertLeftTuple( tuple0,
                              this.context,
                              this.workingMemory );

        // make sure assertions were propagated
        assertEquals( 1,
                      sink.getAsserted().size() );
    }
}
