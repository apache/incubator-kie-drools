/*
 * Copyright 2010 JBoss Inc
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
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static org.junit.Assert.assertEquals;

public class PropertyChangeListenerTest {
    private InternalKnowledgeBase kBase;
    private BuildContext buildContext;
    private EntryPointNode entryPoint;
    
    @Before
    public void setUp() throws Exception {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase, kBase.getReteooBuilder().getIdGenerator() );

        this.entryPoint = buildContext.getKnowledgeBase().getRete().getEntryPointNodes().values().iterator().next();;
    }
    
    @Test
    public void test1() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( State.class ),
                                                                  buildContext );

        objectTypeNode.attach(buildContext);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final State a = new State( "go" );
        ksession.insert( a, true );

        assertEquals( 1,
                      sink.getAsserted().size() );

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

        public void addPropertyChangeListener(final PropertyChangeListener l) {
            this.changes.addPropertyChangeListener( l );
        }

        public void removePropertyChangeListener(final PropertyChangeListener l) {
            this.changes.removePropertyChangeListener( l );
        }
    }
}
