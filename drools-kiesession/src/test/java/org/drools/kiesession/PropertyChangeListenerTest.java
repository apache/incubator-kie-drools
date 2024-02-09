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

import java.beans.PropertyChangeSupport;
import java.util.Collections;

import org.drools.base.base.ClassObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.MockObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyChangeListenerTest {
    private InternalKnowledgeBase kBase;
    private BuildContext buildContext;
    private EntryPointNode entryPoint;
    
    @Before
    public void setUp() throws Exception {
        this.kBase = KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase, Collections.emptyList() );

        this.entryPoint = buildContext.getRuleBase().getRete().getEntryPointNodes().values().iterator().next();;
    }
    
    @Test
    public void test1() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( State.class ),
                                                                  buildContext );

        objectTypeNode.attach(buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final State a = new State( "go" );
        ksession.insert( a, true );
        ksession.fireAllRules();

        assertThat(sink.getAsserted().size()).isEqualTo(1);

        a.setState( "stop" );

    }

    public static class State {
        private final PropertyChangeSupport changes = new PropertyChangeSupport( this );

        private String                      state;

        public State(final String state) {
            this.state = state;
        }

        public String getState() {
            return this.state;
        }

        public void setState(final String newState) {
            final String oldState = this.state;
            this.state = newState;
            this.changes.firePropertyChange( "state",
                                             oldState,
                                             newState );
        }
    }
}
