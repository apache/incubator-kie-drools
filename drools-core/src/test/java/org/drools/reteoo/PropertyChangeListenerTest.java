package org.drools.reteoo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import junit.framework.TestCase;

import org.drools.base.ClassObjectType;

public class PropertyChangeListenerTest extends TestCase {
    public void test1() {
        RuleBaseImpl ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        Rete rete = ruleBase.getRete();

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( State.class ),
                                                            rete );

        objectTypeNode.attach();

        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        State a = new State( "go" );
        workingMemory.assertObject( a,
                                    true );

        assertEquals( 1,
                      sink.getAsserted().size() );
        assertEquals( 0,
                      sink.getModified().size() );

        a.setState( "stop" );

        assertEquals( 1,
                      sink.getModified().size() );
    }

    public static class State {
        private PropertyChangeSupport changes = new PropertyChangeSupport( this );

        private String                state;

        public State(String state) {
            this.state = state;
        }

        public String getState() {
            return this.state;
        }

        public void setState(String newState) {
            String oldState = this.state;
            this.state = newState;
            this.changes.firePropertyChange( "state",
                                             oldState,
                                             newState );
        }

        public void addPropertyChangeListener(PropertyChangeListener l) {
            changes.addPropertyChangeListener( l );
        }

        public void removePropertyChangeListener(PropertyChangeListener l) {
            changes.removePropertyChangeListener( l );
        }
    }
}
