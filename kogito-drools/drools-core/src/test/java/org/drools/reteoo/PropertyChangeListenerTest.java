package org.drools.reteoo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import junit.framework.TestCase;

import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.reteoo.builder.BuildContext;

public class PropertyChangeListenerTest extends TestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext buildContext;
    
    protected void setUp() throws Exception {
        this.ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase, ((ReteooRuleBase)ruleBase).getReteooBuilder().getIdGenerator() );
    }
    
    public void test1() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( State.class ),
                                                                  buildContext );

        objectTypeNode.attach();

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final State a = new State( "go" );
        workingMemory.insert( a,
                                    true );

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
