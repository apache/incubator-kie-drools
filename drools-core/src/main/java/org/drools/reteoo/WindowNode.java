/*
 * Copyright 2011 JBoss Inc
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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;

/**
 * <code>WindowNodes</code> are nodes in the <code>Rete</code> network used
 * to manage windows. They support multiple types of windows, like 
 * sliding windows, tumbling windows, etc.
 *
 */
public class WindowNode extends ObjectSource 
                        implements ObjectSinkNode,
                                   NodeMemory {

    private static final long              serialVersionUID = 540l;

    private List<AlphaNodeFieldConstraint> constraints;
    private List<Behavior>                 behaviors;

    private ObjectSinkNode                 previousRightTupleSinkNode;
    private ObjectSinkNode                 nextRightTupleSinkNode;

    public WindowNode() {
    }

    /**
     * Construct a <code>WindowNode</code> with a unique id using the provided
     * list of <code>AlphaNodeFieldConstraint</code> and the given <code>ObjectSource</code>.
     * 
     * @param id Node's ID
     * @param constraints Node's constraints
     * @param behaviors list of behaviors for this window node
     * @param objectSource Node's object source
     */
    public WindowNode(final int id,
            final List<AlphaNodeFieldConstraint> constraints,
            final List<Behavior> behaviors, 
            final ObjectSource objectSource,
            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               objectSource,
               context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.constraints = constraints;
        this.behaviors = behaviors;
    }

    @SuppressWarnings("unchecked")
    public void readExternal( ObjectInput in ) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        constraints = (List<AlphaNodeFieldConstraint>) in.readObject();
        behaviors = (List<Behavior>) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( constraints );
        out.writeObject( behaviors );
    }

    /**
     * Returns the <code>FieldConstraints</code>
     *
     * @return <code>FieldConstraints</code>
     */
    public List<AlphaNodeFieldConstraint> getConstraints() {
        return this.constraints;
    }

    /**
     * Returns the list of behaviors for this window node
     * @return
     */
    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.source.addObjectSink( this );
    }

    public void attach( final InternalWorkingMemory[] workingMemories ) {
        attach();

        for (int i = 0, length = workingMemories.length; i < length; i++) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl(
                                                                                      workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }

    public void assertObject( final InternalFactHandle factHandle,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory ) {

        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory( this );
        int index = 0;
        for (AlphaNodeFieldConstraint constraint : constraints) {
            if (!constraint.isAllowed( factHandle,
                                       workingMemory,
                                       memory.context[index++] )) {
                return;
            }
        }
        // process the behavior

        // propagate
        memory.facts.add( factHandle );
        this.sink.propagateAssertObject( factHandle,
                                         context,
                                         workingMemory );
    }

    public void modifyObject( final InternalFactHandle factHandle,
            final ModifyPreviousTuples modifyPreviousTuples,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory ) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory( this );

        int index = 0;
        boolean isAllowed = true;
        for (AlphaNodeFieldConstraint constraint : constraints) {
            if (!constraint.isAllowed( factHandle,
                                       workingMemory,
                                       memory.context[index++] )) {
                isAllowed = false;
                break;
            }
        }

        // behavior modify

        // propagate
        if (isAllowed) {
            if (memory.facts.contains( factHandle )) {
                this.sink.propagateModifyObject( factHandle,
                                                 modifyPreviousTuples,
                                                 context,
                                                 workingMemory );
            } else {
                memory.facts.add( factHandle );
                this.sink.propagateAssertObject( factHandle,
                                                 context,
                                                 workingMemory );
            }
        } else {
            memory.facts.remove( factHandle );
            // no need to propagate retract if it is no longer allowed
            // because the algorithm will automatically retract facts
            // based on the ModifyPreviousTuples parameters 
        }
    }

    /**
     * Retract the <code>FactHandle</code> from the <code>WindowNode</code>.
     * This method is for the node benefit only as the node itself will not 
     * propagate retracts down the network, relying on the standard Rete
     * retract algorithm implemented by the ObjectTypeNode to do it.
     *
     * @param factHandle    The fact handle.
     * @param context       The propagation context
     * @param workingMemory The working memory session.
     */
    public void retractObject(final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory( this );
        
        // behavior retract
        
        // memory retract
        memory.facts.remove( factHandle );
        
        // as noted in the javadoc, this node will not propagate retracts, relying
        // on the standard algorithm to do it instead.
    }

    public void updateSink( final ObjectSink sink,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory ) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory( this );

        Iterator it = memory.facts.iterator();
        for (ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next()) {
            sink.assertObject( (InternalFactHandle) entry.getValue(),
                               context,
                               workingMemory );
        }

    }

    /**
     * Creates the WindowNode's memory.
     */
    public Object createMemory( final RuleBaseConfiguration config ) {
        WindowMemory memory = new WindowMemory();
        memory.context = new ContextEntry[this.constraints.size()];
        int index = 0;
        for (AlphaNodeFieldConstraint alpha : constraints) {
            memory.context[index++] = alpha.createContextEntry();
        }
        return memory;
    }

    public String toString() {
        return "[WindowNode(" + this.id + ") constraints=" + this.constraints + "]";
    }

    public int hashCode() {
        return this.source.hashCode() * 17 + ( ( this.constraints != null ) ? this.constraints.hashCode() : 0 );
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object object ) {
        if (this == object) {
            return true;
        }

        if (object == null || !( object instanceof WindowNode )) {
            return false;
        }

        final WindowNode other = (WindowNode) object;

        return this.source.equals( other.source ) && this.constraints.equals( other.constraints );
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextRightTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode( final ObjectSinkNode next ) {
        this.nextRightTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousRightTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode( final ObjectSinkNode previous ) {
        this.previousRightTupleSinkNode = previous;
    }

    public static class WindowMemory implements Externalizable {

        private static final long serialVersionUID = 540l;

        public ObjectHashSet      facts            = new ObjectHashSet();
        public ContextEntry[]     context;

        public void readExternal( ObjectInput in ) throws IOException,
                ClassNotFoundException {
            context = (ContextEntry[]) in.readObject();
            facts = (ObjectHashSet) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( context );
            out.writeObject( facts );
        }
    }
}
