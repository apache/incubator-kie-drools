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

import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.QueryArgument;
import org.drools.core.rule.QueryElement;
import org.drools.core.spi.PropagationContext;
import org.drools.core.test.model.DroolsTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.assertEquals;

public class QueryElementNodeTest extends DroolsTestCase {
    private PropagationContext  context;
    private StatefulKnowledgeSessionImpl workingMemory;
    private InternalKnowledgeBase kBase;
    private BuildContext        buildContext;

    @Before
    public void setUp() {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase,
                                              kBase.getReteooBuilder().getIdGenerator() );
        this.buildContext.setRule(new RuleImpl());
        PropagationContextFactory pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, null);

        this.workingMemory = new InstrumentedWorkingMemory( 0, this.kBase );
    }

    @Test
    public void testAttach() throws Exception {
        QueryElement queryElement = new QueryElement(null, null, new QueryArgument[0], null, null, false, false);

        final MockTupleSource source = new MockTupleSource( 12 );

        final QueryElementNode node = new QueryElementNode( 18,
                                                            source,
                                                            queryElement,
                                                            true,
                                                            false,
                                                            buildContext );

        assertEquals( 18,
                      node.getId() );

        assertEquals( 0,
                      source.getAttached() );

        node.attach(buildContext);

        assertEquals( 1,
                      source.getAttached() );

    }

    public static class InstrumentedWorkingMemory extends StatefulKnowledgeSessionImpl {

        public InstrumentedWorkingMemory( final int id,
                                          final InternalKnowledgeBase kBase ) {
            super( new Long( id ),
                   kBase );
        }
    }
}
