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
package org.drools.kiesession;

import java.util.Collections;

import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.QueryArgument;
import org.drools.base.rule.QueryElement;
import org.drools.core.common.PropagationContext;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryElementNodeTest {
    private PropagationContext  context;
    private StatefulKnowledgeSessionImpl workingMemory;
    private InternalKnowledgeBase kBase;
    private BuildContext        buildContext;

    @Before
    public void setUp() {
        this.kBase = KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase, Collections.emptyList() );
        this.buildContext.setRule(new RuleImpl());
        PropagationContextFactory pctxFactory = new PhreakPropagationContextFactory();
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.Type.INSERTION, null, null, null);

        this.workingMemory = new InstrumentedWorkingMemory( 0, this.kBase );
    }

    @Test
    public void testAttach() throws Exception {
        QueryElement queryElement = new QueryElement(null, null, new QueryArgument[0], null, null, false, false);

        final MockTupleSource source = new MockTupleSource(12, buildContext);

        final QueryElementNode node = new QueryElementNode( 18,
                                                            source,
                                                            queryElement,
                                                            true,
                                                            false,
                                                            buildContext );

        assertThat(node.getId()).isEqualTo(18);

        assertThat(source.getAttached()).isEqualTo(0);

        node.attach(buildContext);

        assertThat(source.getAttached()).isEqualTo(1);

    }

    public static class InstrumentedWorkingMemory extends StatefulKnowledgeSessionImpl {

        public InstrumentedWorkingMemory( final int id,
                                          final InternalKnowledgeBase kBase) {
            super( new Long( id ),
                   kBase );
        }
    }
}
