/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.drools.core.base.ClassFieldAccessorCache;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.MockObjectSink;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.common.PropagationContext;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.model.Cheese;
import org.drools.mvel.model.MockObjectSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AlphaNodeTest {
    
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    private final boolean useLambdaConstraint;

    public AlphaNodeTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testLiteralConstraintAssertObjectWithoutMemory() throws Exception {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase, Collections.emptyList() );
        buildContext.setRule(new RuleImpl("test"));

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final RuleImpl rule = new RuleImpl( "test-rule" );
        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, null);

        final MockObjectSource source = new MockObjectSource( buildContext.getNextNodeId() );

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(store, "cheddar", useLambdaConstraint);

        // With Memory
        final AlphaNode alphaNode = new AlphaNode( buildContext.getNextNodeId(),
                                                   constraint,
                                                   source,
                                                   buildContext ); // no memory

        final MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        final DefaultFactHandle f0 = (DefaultFactHandle) ksession.insert( cheddar );

        // check sink is empty
        assertThat((Collection) sink.getAsserted()).hasSize(0);

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                ksession );

        assertThat(sink.getAsserted().size()).isEqualTo(1);

        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertThat(ksession.getObject((DefaultFactHandle) list[0])).isSameAs(cheddar);

        final Cheese stilton = new Cheese( "stilton",
                                           6 );
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            stilton );

        // object should NOT assert as it does not pass test
        alphaNode.assertObject( f1,
                                context,
                                ksession );

        assertThat((Collection) sink.getAsserted()).hasSize(1);

        list = (Object[]) sink.getAsserted().get( 0 );
        assertThat(ksession.getObject((DefaultFactHandle) list[0])).isSameAs(cheddar);
    }

    /*
     *  This just test AlphaNode With a different Constraint type.
     */
    @Test
    public void testReturnValueConstraintAssertObject() throws Exception {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase, Collections.emptyList() );
        buildContext.setRule(new RuleImpl("test"));

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final RuleImpl rule = new RuleImpl( "test-rule" );
        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, null);

        final MockObjectSource source = new MockObjectSource( buildContext.getNextNodeId() );

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(store, "cheddar", useLambdaConstraint);

        final AlphaNode alphaNode = new AlphaNode( buildContext.getNextNodeId(),
                                                   constraint,
                                                   source,
                                                   buildContext );
        final MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final DefaultFactHandle f0 = (DefaultFactHandle) ksession.insert( cheddar );

        assertThat((Collection) sink.getAsserted()).hasSize(0);

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                ksession );

        assertThat((Collection) sink.getAsserted()).hasSize(1);
        final Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertThat(ksession.getObject((DefaultFactHandle) list[0])).isSameAs(cheddar);

        final Cheese stilton = new Cheese( "stilton",
                                           6 );
        f0.setObject( stilton );

        sink.getAsserted().clear();

        // object should not assert as it does not pass text
        alphaNode.assertObject( f0,
                                context,
                                ksession );

        assertThat((Collection) sink.getAsserted()).hasSize(0);
    }

    @Test
    public void testUpdateSinkWithoutMemory() {
        // An AlphaNode should try and repropagate from its source
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext buildContext = new BuildContext( kBase, Collections.emptyList() );
        buildContext.setRule(new RuleImpl("test"));

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final RuleImpl rule = new RuleImpl( "test-rule" );
        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, null);

        final MockObjectSource source = new MockObjectSource( buildContext.getNextNodeId() );

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(store, "cheddar", useLambdaConstraint);

        final AlphaNode alphaNode = new AlphaNode( buildContext.getNextNodeId(),
                                                   constraint,
                                                   source,
                                                   buildContext ); // no memory

        alphaNode.attach(buildContext);

        final MockObjectSink sink1 = new MockObjectSink();
        alphaNode.addObjectSink( sink1 );

        // Assert a single fact which should be in the AlphaNode memory and also
        // propagated to the
        // the tuple sink
        final Cheese cheese = new Cheese( "cheddar",
                                          0 );
        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 cheese );
        // adding handle to the mock source
        source.addFact( handle1 );

        alphaNode.assertObject( handle1,
                                context,
                                ksession );

        // Create a fact that should not be propagated, since the alpha node restriction will filter it out
        final Cheese stilton = new Cheese( "stilton",
                                           10 );
        final DefaultFactHandle handle2 = new DefaultFactHandle( 2,
                                                                 stilton );
        // adding handle to the mock source
        source.addFact( handle2 );

        alphaNode.assertObject( handle2,
                                context,
                                ksession );

        assertThat((Collection) sink1.getAsserted()).hasSize(1);

        // Attach a new tuple sink
        final MockObjectSink sink2 = new MockObjectSink();

        // Tell the alphanode to update the new node. Make sure the first sink1
        // is not updated
        // likewise the source should not do anything
        alphaNode.updateSink( sink2,
                              context,
                              ksession );

        assertThat((Collection) sink1.getAsserted()).hasSize(1);
        assertThat((Collection) sink2.getAsserted()).hasSize(1);
        assertThat(source.getUdated()).isEqualTo(1);
    }

}
