/*
 * Copyright 2005 JBoss Inc
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

import java.util.Arrays;
import java.util.Collection;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Collect;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.ArrayUtils;
import org.drools.util.Entry;
import org.drools.util.FactEntry;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap.ObjectEntry;

/**
 * @author etirelli
 *
 */
public class CollectNode extends BetaNode
    implements
    TupleSink,
    ObjectSink {

    private static final long                serialVersionUID = 400L;

    private final Collect                    collect;
    private final AlphaNodeFieldConstraint[] resultConstraints;
    private final BetaConstraints            resultsBinder;
    private final boolean                    unwrapRightObject;

    /**
     * Constructor.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param resultConstraints
     *            The alpha constraints to be applied to the resulting collection
     * @param sourceBinder
     *            The beta binder to be applied to the source facts
     * @param resultsBinder
     *            The beta binder to be applied to the resulting collection
     * @param collect
     *            The collect conditional element
     */
    public CollectNode(final int id,
                       final TupleSource leftInput,
                       final ObjectSource rightInput,
                       final AlphaNodeFieldConstraint[] resultConstraints,
                       final BetaConstraints sourceBinder,
                       final BetaConstraints resultsBinder,
                       final Collect collect,
                       final boolean unwrapRight,
                       final BuildContext context) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
        this.resultsBinder = resultsBinder;
        this.resultConstraints = resultConstraints;
        this.collect = collect;
        this.unwrapRightObject = unwrapRight;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    /**
     * @inheritDoc
     * 
     *  When a new tuple is asserted into a CollectNode, do this:
     *  
     *  1. Select all matching objects from right memory
     *  2. Add them to the resulting collection object
     *  3. Apply resultConstraints and resultsBinder to the resulting collection
     *  4. In case all of them evaluates to true do the following:
     *  4.1. Create a new InternalFactHandle for the resulting collection and add it to the tuple
     *  4.2. Propagate the tuple
     *  
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        final Collection result = this.collect.instantiateResultObject();
        final InternalFactHandle resultHandle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                                                    false,
                                                                                                    workingMemory );
        CollectResult colresult = new CollectResult();
        colresult.handle = resultHandle;
        colresult.propagated = false;

        // do not add tuple and result to the memory in sequential mode
        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getTupleMemory().add( leftTuple );
            memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                       colresult,
                                                       false );
        }

        final Iterator it = memory.betaMemory.getFactHandleMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );

        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                       handle ) ) {
                if ( this.unwrapRightObject ) {
                    handle = ((ReteTuple) handle.getObject()).getLastHandle();
                }
                result.add( handle.getObject() );
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( resultHandle,
                                                       workingMemory,
                                                       memory.alphaContexts[i]) ) {
                isAllowed = false;
                break;
            }
        }
        if ( isAllowed ) {
            this.resultsBinder.updateFromTuple( memory.resultsContext,
                                                workingMemory,
                                                leftTuple );
            if ( this.resultsBinder.isAllowedCachedLeft( memory.resultsContext,
                                                         resultHandle ) ) {
                colresult.propagated = true;
                this.sink.propagateAssertTuple( leftTuple,
                                                resultHandle,
                                                context,
                                                workingMemory );
            }
            this.resultsBinder.resetTuple( memory.resultsContext );
        }
    }

    /**
     * @inheritDoc
     */
    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getTupleMemory().remove( leftTuple );
        CollectResult result = (CollectResult) memory.betaMemory.getCreatedHandles().remove( leftTuple );
        final InternalFactHandle handle = result.handle;

        // if tuple was propagated
        if ( result.propagated ) {

            this.sink.propagateRetractTuple( leftTuple,
                                             handle,
                                             context,
                                             workingMemory );

            // Destroying the acumulate result object 
            workingMemory.getFactHandleFactory().destroyFactHandle( handle );
        }
    }

    /**
     * @inheritDoc
     * 
     *  When a new object is asserted into a CollectNode, do this:
     *  
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, call a modify tuple
     *  
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getFactHandleMemory().add( handle );

        if ( !this.tupleMemoryEnabled ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               handle );

        // need to clone the tuples to avoid concurrent modification exceptions
        Entry[] tuples = memory.betaMemory.getTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            ReteTuple tuple = (ReteTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        tuple ) ) {
                this.modifyTuple( true,
                                  tuple,
                                  handle,
                                  context,
                                  workingMemory );
            }
        }

        this.constraints.resetFactHandle( memory.betaMemory.getContext() );
    }

    /**
     *  @inheritDoc
     *  
     *  If an object is retract, call modify tuple for each
     *  tuple match.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        if ( !memory.betaMemory.getFactHandleMemory().remove( handle ) ) {
            return;
        }

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               handle );

        // need to clone the tuples to avoid concurrent modification exceptions
        Entry[] tuples = memory.betaMemory.getTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            ReteTuple tuple = (ReteTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        tuple ) ) {

                this.modifyTuple( false,
                                  tuple,
                                  handle,
                                  context,
                                  workingMemory );
            }
        }

        this.constraints.resetFactHandle( memory.betaMemory.getContext() );
    }

    /**
     * Modifies the results match for a tuple, retracting it and repropagating
     * if constraints allow it
     * 
     * @param leftTuple
     * @param handle
     * @param context
     * @param workingMemory
     */
    public void modifyTuple(final boolean isAssert,
                            final ReteTuple leftTuple,
                            InternalFactHandle handle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        CollectResult result = (CollectResult) memory.betaMemory.getCreatedHandles().get( leftTuple );

        // if tuple was propagated
        if ( result.propagated ) {
            this.sink.propagateRetractTuple( leftTuple,
                                             result.handle,
                                             context,
                                             workingMemory );
            result.propagated = false;
        }

        // if there is a subnetwork, we need to unwrapp the object from inside the tuple
        if ( this.unwrapRightObject ) {
            handle = ((ReteTuple) handle.getObject()).getLastHandle();
        }

        if ( context.getType() == PropagationContext.ASSERTION ) {
            ((Collection) result.handle.getObject()).add( handle.getObject() );
        } else if ( context.getType() == PropagationContext.RETRACTION ) {
            ((Collection) result.handle.getObject()).remove( handle.getObject() );
        } else if ( context.getType() == PropagationContext.MODIFICATION || context.getType() == PropagationContext.RULE_ADDITION || context.getType() == PropagationContext.RULE_REMOVAL ) {
            if ( isAssert ) {
                ((Collection) result.handle.getObject()).add( handle.getObject() );
            } else {
                ((Collection) result.handle.getObject()).remove( handle.getObject() );
            }
        }

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( result.handle,
                                                       workingMemory,
                                                       memory.alphaContexts[i] ) ) {
                isAllowed = false;
                break;
            }
        }
        if ( isAllowed ) {
            this.resultsBinder.updateFromTuple( memory.resultsContext,
                                                workingMemory,
                                                leftTuple );
            if ( this.resultsBinder.isAllowedCachedLeft( memory.resultsContext,
                                                         result.handle ) ) {
                result.propagated = true;
                this.sink.propagateAssertTuple( leftTuple,
                                                result.handle,
                                                context,
                                                workingMemory );
            }

            this.resultsBinder.resetTuple( memory.resultsContext );
        }
    }

    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        final Iterator it = memory.betaMemory.getCreatedHandles().iterator();

        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            CollectResult result = (CollectResult) entry.getValue();
            sink.assertTuple( new ReteTuple( (ReteTuple) entry.getKey(),
                                             result.handle ),
                              context,
                              workingMemory );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#hashCode()
     */
    public int hashCode() {
        return this.leftInput.hashCode() ^ this.rightInput.hashCode() ^ this.collect.hashCode() ^ this.resultsBinder.hashCode() ^ ArrayUtils.hashCode( this.resultConstraints );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof CollectNode) ) {
            return false;
        }

        final CollectNode other = (CollectNode) object;

        if ( this.getClass() != other.getClass() || (!this.leftInput.equals( other.leftInput )) || (!this.rightInput.equals( other.rightInput )) || (!this.constraints.equals( other.constraints )) ) {
            return false;
        }

        return this.collect.equals( other.collect ) && resultsBinder.equals( other.resultsBinder ) && Arrays.equals( this.resultConstraints,
                                                                                                                     other.resultConstraints );
    }

    public String toString() {
        return "[ " + this.getClass().getName() + "(" + this.id + ") ]";
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        CollectMemory memory = new CollectMemory();
        memory.betaMemory = this.constraints.createBetaMemory( config );
        memory.resultsContext = this.resultsBinder.createContext();
        memory.alphaContexts = new ContextEntry[this.resultConstraints.length];
        for ( int i = 0; i < this.resultConstraints.length; i++ ) {
            memory.alphaContexts[i] = this.resultConstraints[i].createContextEntry();
        }
        return memory;
    }

    public static class CollectMemory {
        private static final long serialVersionUID = 400L;
        public BetaMemory         betaMemory;
        public ContextEntry[]     resultsContext;
        public ContextEntry[]     alphaContexts;
    }

    private static class CollectResult {
        // keeping attributes public just for performance
        public InternalFactHandle handle;
        public boolean            propagated;
    }
}
