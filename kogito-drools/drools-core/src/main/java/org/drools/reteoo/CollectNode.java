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

import org.drools.RuleBaseConfiguration;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.Collect;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.ArrayUtils;
import org.drools.util.Entry;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap.ObjectEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author etirelli
 *
 */
public class CollectNode extends BetaNode {
    private static final long          serialVersionUID = 400L;

    private Collect                    collect;
    private AlphaNodeFieldConstraint[] resultConstraints;
    private BetaConstraints            resultsBinder;
    private boolean                    unwrapRightObject;

    public CollectNode() {
    }

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
                       final LeftTupleSource leftInput,
                       final ObjectSource rightInput,
                       final AlphaNodeFieldConstraint[] resultConstraints,
                       final BetaConstraints sourceBinder,
                       final BetaConstraints resultsBinder,
                       final Behavior[] behaviors,
                       final Collect collect,
                       final boolean unwrapRight,
                       final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               sourceBinder,
               behaviors );
        this.resultsBinder = resultsBinder;
        this.resultConstraints = resultConstraints;
        this.collect = collect;
        this.unwrapRightObject = unwrapRight;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        collect = (Collect) in.readObject();
        resultConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        resultsBinder = (BetaConstraints) in.readObject();
        unwrapRightObject = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( collect );
        out.writeObject( resultConstraints );
        out.writeObject( resultsBinder );
        out.writeBoolean( unwrapRightObject );
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
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        final Collection result = this.collect.instantiateResultObject( workingMemory );
        final InternalFactHandle resultHandle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                                                    workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                                                          result ),
                                                                                                    workingMemory );

        final CollectContext colctx = new CollectContext();
        colctx.resultTuple = new RightTuple( resultHandle,
                                             this );

        // do not add tuple and result to the memory in sequential mode
        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
            memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                       colctx,
                                                       false );
        }

        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );

        for ( RightTuple rightTuple = memory.betaMemory.getRightTupleMemory().getFirst( leftTuple ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
            InternalFactHandle handle = rightTuple.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                       handle ) ) {
                if ( this.unwrapRightObject ) {
                    handle = ((LeftTuple) handle.getObject()).getLastHandle();
                }
                result.add( handle.getObject() );

                // in sequential mode, we don't need to keep record of matched tuples
                if ( this.tupleMemoryEnabled ) {
                    // linking left and right by creating a new left tuple
                    new LeftTuple( leftTuple,
                                   rightTuple,
                                   this,
                                   this.tupleMemoryEnabled );
                }
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );

        evaluateResultConstraints( leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   colctx );
    }

    /**
     * @inheritDoc
     */
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().remove( leftTuple );

        LeftTuple child = getFirstMatch( leftTuple,
                                         colctx );

        // Now, unlink the matches 
        while ( child != null ) {
            LeftTuple tmp = child.getLeftParentNext();
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = tmp;
        }

        if ( colctx.propagated ) {
            // if tuple was previously propagated, retract it
            this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                                  context,
                                                                  workingMemory );
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
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        final RightTuple rightTuple = new RightTuple( factHandle,
                                                      this );

        if ( !behavior.assertRightTuple( memory.betaMemory.getBehaviorContext(),
                                         rightTuple,
                                         workingMemory ) ) {
            // destroy right tuple
            rightTuple.unlinkFromRightParent();
            return;
        }

        memory.betaMemory.getRightTupleMemory().add( rightTuple );

        if ( !this.tupleMemoryEnabled ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               factHandle );

        // need to clone the tuples to avoid concurrent modification exceptions
        // @TODO: now that we use linked lists, can we avoid the cloning?
        Entry[] tuples = memory.betaMemory.getLeftTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            LeftTuple tuple = (LeftTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        tuple ) ) {
                modifyTuple( true,
                             tuple,
                             rightTuple,
                             context,
                             workingMemory,
                             memory );
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
    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        
        final InternalFactHandle origin = (InternalFactHandle) context.getFactHandleOrigin();
        if( context.getType() == PropagationContext.EXPIRATION ) {
            ((PropagationContextImpl)context).setFactHandle( null );
        }
        
        behavior.retractRightTuple( memory.betaMemory.getBehaviorContext(), rightTuple, workingMemory );
        memory.betaMemory.getRightTupleMemory().remove( rightTuple );

        for ( LeftTuple leftTuple = rightTuple.getBetaChildren(); leftTuple != null; ) {
            LeftTuple tmp = leftTuple.getRightParentNext();
            this.modifyTuple( false,
                              leftTuple.getParent(),
                              rightTuple,
                              context,
                              workingMemory,
                              memory );
            leftTuple = tmp;
        }

        if( context.getType() == PropagationContext.EXPIRATION ) {
            ((PropagationContextImpl)context).setFactHandle( origin );
        }

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
                            final LeftTuple leftTuple,
                            final RightTuple rightTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory,
                            final CollectMemory memory) {

        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );

        // if tuple was propagated
        if ( colctx.propagated ) {
            LeftTuple firstMatch = getFirstMatch( leftTuple, colctx );
            
            // we may have no matches yet
            if( firstMatch != null ) { 
                // temporarily break the linked list to avoid wrong retracts
                firstMatch.getLeftParentPrevious().setLeftParentNext( null );
                firstMatch.setLeftParentPrevious( null );
            }
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
            // now set the beta children to the first match
            leftTuple.setBetaChildren( firstMatch );
            colctx.propagated = false;
        }

        if( isAssert ) {
            // linking left and right by creating a new left tuple
            new LeftTuple( leftTuple,
                           rightTuple,
                           this,
                           this.tupleMemoryEnabled );
        } else {
            if( leftTuple.getBetaChildren() != null ) {
                // removing link between left and right
                LeftTuple match = leftTuple.getBetaChildren();
                while( match.getRightParent() != rightTuple ) {
                    match = match.getLeftParentNext();
                }
                match.unlinkFromLeftParent();
                match.unlinkFromRightParent();
            }
        }
        
        // if there is a subnetwork, we need to unwrapp the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        if ( this.unwrapRightObject ) {
            handle = ((LeftTuple) handle.getObject()).getLastHandle();
        }

        if ( context.getType() == PropagationContext.ASSERTION ) {
            ((Collection) colctx.resultTuple.getFactHandle().getObject()).add( handle.getObject() );
        } else if ( context.getType() == PropagationContext.RETRACTION || context.getType() == PropagationContext.EXPIRATION ) {
            ((Collection) colctx.resultTuple.getFactHandle().getObject()).remove( handle.getObject() );
        } else if ( context.getType() == PropagationContext.MODIFICATION || context.getType() == PropagationContext.RULE_ADDITION || context.getType() == PropagationContext.RULE_REMOVAL ) {
            if ( isAssert ) {
                ((Collection) colctx.resultTuple.getFactHandle().getObject()).add( handle.getObject() );
            } else {
                ((Collection) colctx.resultTuple.getFactHandle().getObject()).remove( handle.getObject() );
            }
        }
        
        evaluateResultConstraints( leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   colctx );
    }

    /**
     * Evaluate result constraints and propagate tuple if it evaluates to true
     * 
     * @param leftTuple
     * @param context
     * @param workingMemory
     * @param memory
     * @param colctx
     */
    private void evaluateResultConstraints(final LeftTuple leftTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory,
                                           final CollectMemory memory,
                                           final CollectContext colctx) {
        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( colctx.resultTuple.getFactHandle(),
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
                                                         colctx.resultTuple.getFactHandle() ) ) {
                colctx.propagated = true;
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    colctx.resultTuple,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            }

            this.resultsBinder.resetTuple( memory.resultsContext );
        }
    }

    /**
     * Skips the propagated tuple handles and return the first handle
     * in the list that correspond to a match
     * 
     * @param leftTuple
     * @param colctx
     * @return
     */
    private LeftTuple getFirstMatch(final LeftTuple leftTuple,
                                    final CollectContext colctx) {
        // unlink all right matches 
        LeftTuple child = leftTuple.getBetaChildren();

        if ( colctx.propagated ) {
            // To do that, we need to skip the first N children that are in fact
            // the propagated tuples
            for ( int i = 0; i < this.sink.size(); i++ ) {
                child = child.getLeftParentNext();
            }
        }
        return child;
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
            if( colctx.propagated ) {
                sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                     colctx.resultTuple,
                                                     sink,
                                                     this.tupleMemoryEnabled ),
                                      context,
                                      workingMemory );
            }
        }
    }
    
    protected void doRemove(final InternalWorkingMemory workingMemory,
                            final CollectMemory memory) {
          Iterator it = memory.betaMemory.getCreatedHandles().iterator();
          for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
              CollectContext ctx = ( CollectContext ) entry.getValue();
              workingMemory.getFactHandleFactory().destroyFactHandle( ctx.resultTuple.getFactHandle() );              
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
        memory.betaMemory.setBehaviorContext( this.behavior.createBehaviorContext() );
        return memory;
    }
    
    public short getType() {
        return NodeTypeEnums.CollectNode;
    }   

    public static class CollectMemory
        implements
        Externalizable {
        private static final long serialVersionUID = 400L;
        public BetaMemory         betaMemory;
        public ContextEntry[]     resultsContext;
        public ContextEntry[]     alphaContexts;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            betaMemory = (BetaMemory) in.readObject();
            resultsContext = (ContextEntry[]) in.readObject();
            alphaContexts = (ContextEntry[]) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( betaMemory );
            out.writeObject( resultsContext );
            out.writeObject( alphaContexts );
        }
    }

    public static class CollectContext
        implements
        Externalizable {
        private static final long serialVersionUID = -3076306175989410574L;
        public RightTuple         resultTuple;
        public boolean            propagated;
        
        public void readExternal(ObjectInput in) throws IOException,
                                                  ClassNotFoundException {
            resultTuple = (RightTuple) in.readObject();
            propagated = in.readBoolean();
        }
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( resultTuple );
            out.writeBoolean( propagated );
        }

    }

}
