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

import org.drools.core.base.ClassObjectType;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);

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
    public void testMemory() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase,
                                                      kBase.getReteooBuilder().getIdGenerator() );
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

}
