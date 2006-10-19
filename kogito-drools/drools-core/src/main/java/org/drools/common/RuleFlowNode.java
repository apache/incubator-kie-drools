package org.drools.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.conflict.DepthConflictResolver;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.util.BinaryHeapQueue;
import org.drools.util.Queueable;

public class RuleFlowNode
    implements
    AgendaGroup {

    private static final long     serialVersionUID = 320L;

    private final String          name;

    private final InternalAgenda  agenda;

    /** @todo Maybe this should just be a LinkedList and we sort on use? */
    private final BinaryHeapQueue queue;

    private List                  childNodes       = Collections.EMPTY_LIST;

    private final Object                lock;

    /**
     * Construct an <code>RuleFlowNode</code> with the given name.
     * 
     * @param name
     *      The <RuleFlowNode> name.
     */
    public RuleFlowNode(final String name,
                        final InternalAgenda agenda) {
        this.name = name;
        this.agenda = agenda;
        this.queue = new BinaryHeapQueue( DepthConflictResolver.getInstance() );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.RuleFlowNode#getName()
     */
    public String getName() {
        return this.name;
    }

    public void addChildNode(final RuleFlowNode child) {
        if ( this.childNodes == Collections.EMPTY_LIST ) {
            this.childNodes = new ArrayList( 1 );
        }
        this.childNodes.add( child );
    }

    public boolean removeChildNode(final RuleFlowNode child) {
        return this.childNodes.remove( child );
    }

    public void clear() {
        synchronized ( this.lock ) {
            this.queue.clear();
        }
    }

    /* (non-Javadoc)
     * @see org.drools.spi.RuleFlowNode#size()
     */
    public int size() {
        synchronized ( this.lock ) {
            return this.queue.size();
        }
    }

    public void add(final Activation activation) {
        synchronized ( this.lock ) {
            this.queue.enqueue( (Queueable) activation );
        }
    }

    public Activation getNext() {
        synchronized ( this.lock ) {
            return (Activation) this.queue.dequeue();
        }
    }

    /**
     * Iterates a PriorityQueue removing empty entries until it finds a populated entry and return true,
     * otherwise it returns false;
     * 
     * @param priorityQueue
     * @return
     */
    public boolean isEmpty() {
        synchronized ( this.lock ) {
            return this.queue.isEmpty();
        }
    }

    public Activation[] getActivations() {
        synchronized ( this.lock ) {
            return (Activation[]) this.queue.toArray( new AgendaItem[this.queue.size()] );
        }
    }

    public Queueable[] getQueueable() {
        return this.queue.getQueueable();
    }

    public void activate() {
        Activation[] activations = null;
        int i = 0;
        // We need to make a sorted copy of the queue as an array
        synchronized ( this.lock ) {
            activations = new Activation[this.queue.size()];
            while ( !this.queue.isEmpty() ) {
                activations[i++] = (Activation) this.queue.dequeue();
            }
        }

        // Make a runnable execution so we can fire all the activations
        final ExecuteRuleFlowNode execute = new ExecuteRuleFlowNode( this.agenda,
                                                               (RuleFlowNode[]) this.childNodes.toArray( new RuleFlowNode[this.childNodes.size()] ),
                                                               activations );
        final Thread thread = new Thread( execute );
        thread.start();
    }

    public String toString() {
        return "RuleFlowNode '" + this.name + "'";
    }

    public boolean equal(final Object object) {
        if ( (object == null) || !(object instanceof RuleFlowNode) ) {
            return false;
        }

        if ( ((RuleFlowNode) object).name.equals( this.name ) ) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public static class ExecuteRuleFlowNode
        implements
        Runnable {
        private final InternalAgenda agenda;
        private final RuleFlowNode[] nodes;
        private final Activation[]   activations;

        public ExecuteRuleFlowNode(final InternalAgenda agenda,
                                   final RuleFlowNode[] nodes,
                                   final Activation[] activations) {
            this.agenda = agenda;
            this.nodes = nodes;
            this.activations = activations;
        }

        public void run() {
            for ( int i = 0, length = this.activations.length; i < length; i++ ) {
                this.agenda.fireActivation( this.activations[i] );
            }

            for ( int i = 0, length = this.nodes.length; i < length; i++ ) {
                this.nodes[i].activate();
            }
        }
    }
}
