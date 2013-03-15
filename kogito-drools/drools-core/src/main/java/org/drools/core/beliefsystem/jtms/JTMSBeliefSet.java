package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.SimpleLogicalDependency;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.drools.spi.PropagationContext;

public class JTMSBeliefSet  extends LinkedList implements BeliefSet {
    private BeliefSystem beliefSystem;
    
    private WorkingMemoryAction wmAction;
    
    private InternalFactHandle rootHandle;
    private InternalFactHandle positiveFactHandle;
    private InternalFactHandle negativeFactHandle;
    
    private int posCounter = 0;
    private int negCounter = 0;
    
    public JTMSBeliefSet(BeliefSystem beliefSystem, InternalFactHandle rootHandle) {
        this.beliefSystem = beliefSystem;
        this.rootHandle = rootHandle;
    }
    
    public InternalFactHandle getPositiveFactHandle() {
        return positiveFactHandle;
    }

    public void setPositiveFactHandle(InternalFactHandle positiveFactHandle) {
        this.positiveFactHandle = positiveFactHandle;
    }

    public InternalFactHandle getNegativeFactHandle() {
        return negativeFactHandle;
    }

    public void setNegativeFactHandle(InternalFactHandle negativeFactHandle) {
        this.negativeFactHandle = negativeFactHandle;
    }

    public void add( LinkedListNode node ) {
        SimpleLogicalDependency ld = (SimpleLogicalDependency) ((LinkedListEntry) node).getObject();
        boolean neg = ld.getValue() != null && MODE.NEGATIVE.getId().equals( ld.getValue().toString() );
        if ( neg ) {
            super.addLast( node ); //we add negatives to end
            negCounter++;
        } else {
            super.add( node ); // we add positied to start
            ld.setValue( MODE.POSITIVE.getId() ); // user may not have explicitely set MODE, so implicitely it's negative
            posCounter++;
        }
    }
    
    public void remove( LinkedListNode node ) {
        super.remove(node);

        LogicalDependency ld = (LogicalDependency) ((LinkedListEntry) node).getObject();

        boolean neg = ld.getValue() != null && MODE.NEGATIVE.getId().equals( ld.getValue().toString() );
        if ( neg ) {
            negCounter--;
        } else {
            posCounter--;
        }

    }    
    
    public BeliefSystem getBeliefSystem() {
        return beliefSystem;
    }

    public InternalFactHandle getFactHandle() {
        return this.rootHandle;
    }
    
    public boolean isNegated() {
        return posCounter == 0 && negCounter > 0;
    }

    public boolean isConflicting() {
        return posCounter > 0 && negCounter > 0;
    }

    public boolean isPositive() {
        return negCounter == 0 && posCounter > 0;
    }    
    
    public enum MODE {
        POSITIVE( "pos" ),
        NEGATIVE( "neg" );

        private String string;
        MODE( String string ) {
            this.string = string;
        }

        public String toExternalForm() {
            return this.string;
        }

        public String toString() {
            return this.string;
        }

        public String getId() {
            return this.string;
        }

        public static MODE resolve( Object id ) {
            if ( id == null ) {
                return null;
            } else if( NEGATIVE == id || NEGATIVE.getId().equalsIgnoreCase( id.toString() ) ) {
                return NEGATIVE;
            } else if( POSITIVE == id || POSITIVE.getId().equalsIgnoreCase( id .toString()) ) {
                return POSITIVE;
            } else {
                return null;
            }
        }
    }

    public void cancel(PropagationContext context) {        
        // get all but last, as that we'll do via the BeliefSystem, for cleanup
        // note we don't update negative, conflict counters. It's needed for the last cleanup operation
        for ( LinkedListEntry entry = (LinkedListEntry) getFirst(); entry != getLast();  ) {
            LinkedListEntry temp = (LinkedListEntry) entry.getNext(); // get next, as we are about to remove it
            final LogicalDependency node = (LogicalDependency) entry.getObject();
            node.getJustifier().getLogicalDependencies().remove( node );
            remove( entry );
            entry = temp;
        }
        
        LinkedListEntry last = (LinkedListEntry) getFirst();
        final LogicalDependency node = (LogicalDependency) last.getObject();
        node.getJustifier().getLogicalDependencies().remove( node );
        beliefSystem.delete( node, this, context );
        positiveFactHandle = null;
        negativeFactHandle = null;
    }
    
    public void clear(PropagationContext context) { 
        // remove all, but don't allow the BeliefSystem to clean up, the FH is most likely going to be used else where
        for ( LinkedListEntry entry = (LinkedListEntry) getFirst(); entry != null;  ) {
            LinkedListEntry temp = (LinkedListEntry) entry.getNext(); // get next, as we are about to remove it
            final LogicalDependency node = (LogicalDependency) entry.getObject();
            node.getJustifier().getLogicalDependencies().remove( node );
            remove( entry );
            entry = temp;
        }
    }    

    public void setWorkingMemoryAction(WorkingMemoryAction wmAction) {
        this.wmAction = wmAction;
    }    
    
    public WorkingMemoryAction getWorkingMemoryAction() {
        return this.wmAction;
    }
}
