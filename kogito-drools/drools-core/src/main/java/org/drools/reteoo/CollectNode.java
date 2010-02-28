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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.ArrayUtils;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.Collect;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PropagationContext;

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
    private Pattern[]                  requiredPatterns;

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

        List<Declaration> declrs = new ArrayList<Declaration>();
        int maxIndex = 0;
        for ( BetaNodeFieldConstraint constraint : getConstraints() ) {
            for ( Declaration declr : constraint.getRequiredDeclarations() ) {
                if ( declr.getPattern().getOffset() > maxIndex ) {
                    maxIndex = declr.getPattern().getOffset();
                }
                declrs.add( declr );
            }
        }
        requiredPatterns = new Pattern[maxIndex + 1];
        for ( Declaration declr : declrs ) {
            requiredPatterns[declr.getPattern().getOffset()] = declr.getPattern();
        }
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
    @SuppressWarnings("unchecked")
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        final Collection<Object> result = (Collection<Object>) this.collect.instantiateResultObject( workingMemory );
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
                addMatch( leftTuple,
                          rightTuple,
                          colctx );
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );

        evaluateResultConstraints( ActivitySource.LEFT,
                                   leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   colctx );
    }

    /**
     * @inheritDoc
     * 
     * When retracting the left tuple, clear all matches from the right tuples
     * and if previously propagated as an assert, propagate a retract
     */
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {

        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().remove( leftTuple );

        removePreviousMatchesForLeftTuple( leftTuple,
                                           colctx );

        if ( colctx.propagated ) {
            // if tuple was previously propagated, retract it
            this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                                  context,
                                                                  workingMemory );
        } else {
            // if not propagated, just destroy the result fact handle
            workingMemory.getFactHandleFactory().destroyFactHandle( colctx.resultTuple.getFactHandle() );

        }
    }

    /**
     * @inheritDoc
     *
     *  When a new object is asserted into a CollectNode, do this:
     *
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, add the new match and evaluate the result constraints
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

        for ( LeftTuple leftTuple = memory.betaMemory.getLeftTupleMemory().getFirst( rightTuple ); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        leftTuple ) ) {
                final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                addMatch( leftTuple,
                          rightTuple,
                          colctx );
                evaluateResultConstraints( ActivitySource.RIGHT,
                                           leftTuple,
                                           context,
                                           workingMemory,
                                           memory,
                                           colctx );
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
        if ( context.getType() == PropagationContext.EXPIRATION ) {
            ((PropagationContextImpl) context).setFactHandle( null );
        }

        behavior.retractRightTuple( memory.betaMemory.getBehaviorContext(),
                                    rightTuple,
                                    workingMemory );
        memory.betaMemory.getRightTupleMemory().remove( rightTuple );

        removePreviousMatchesForRightTuple( rightTuple,
                                            context,
                                            workingMemory,
                                            memory,
                                            rightTuple.firstChild );

        if ( context.getType() == PropagationContext.EXPIRATION ) {
            ((PropagationContextImpl) context).setFactHandle( origin );
        }

    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );
        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        memory.betaMemory.getLeftTupleMemory().add( leftTuple );

        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );
        LeftTuple childLeftTuple = getFirstMatch( leftTuple,
                                                  colctx,
                                                  false );

        RightTupleMemory rightMemory = memory.betaMemory.getRightTupleMemory();

        RightTuple rightTuple = rightMemory.getFirst( leftTuple );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        if ( childLeftTuple != null && rightMemory.isIndexed() && rightTuple != rightMemory.getFirst( childLeftTuple.getRightParent() ) ) {
            // our index has changed, so delete all the previous matchings

            removePreviousMatchesForLeftTuple( leftTuple,
                                               colctx );

            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // we can't do anything if RightTupleMemory is empty
        if ( rightTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();
                    if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                               handle ) ) {
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  colctx );

                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();

                    if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                               handle ) ) {
                        if ( childLeftTuple != null && childLeftTuple.getRightParent() != rightTuple ) {
                            // add a new match
                            addMatch( leftTuple,
                                      rightTuple,
                                      colctx );
                        } else {
                            // we must re-add this to ensure deterministic iteration
                            LeftTuple temp = childLeftTuple.getLeftParentNext();
                            childLeftTuple.reAddLeft();
                            childLeftTuple = temp;
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
                        LeftTuple temp = childLeftTuple.getLeftParentNext();
                        // remove the match
                        removeMatch( rightTuple,
                                     childLeftTuple,
                                     colctx );
                        childLeftTuple = temp;
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );
        evaluateResultConstraints( ActivitySource.LEFT,
                                   leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   colctx );
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        final CollectMemory memory = (CollectMemory) workingMemory.getNodeMemory( this );

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.betaMemory.getRightTupleMemory().remove( rightTuple );
        memory.betaMemory.getRightTupleMemory().add( rightTuple );

        // WTD here
        //                if ( !behavior.assertRightTuple( memory.getBehaviorContext(),
        //                                                 rightTuple,
        //                                                 workingMemory ) ) {
        //                    // destroy right tuple
        //                    rightTuple.unlinkFromRightParent();
        //                    return;
        //                }

        LeftTuple childLeftTuple = rightTuple.firstChild;

        LeftTupleMemory leftMemory = memory.betaMemory.getLeftTupleMemory();

        LeftTuple leftTuple = leftMemory.getFirst( rightTuple );

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        if ( childLeftTuple != null && leftMemory.isIndexed() && leftTuple != leftMemory.getFirst( childLeftTuple.getLeftParent() ) ) {
            // our index has changed, so delete all the previous matches
            removePreviousMatchesForRightTuple( rightTuple,
                                                context,
                                                workingMemory,
                                                memory,
                                                childLeftTuple );
            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // if LeftTupleMemory is empty, there are no matches to modify
        if ( leftTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
                    if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                                leftTuple ) ) {
                        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  colctx );
                        evaluateResultConstraints( ActivitySource.RIGHT,
                                                   leftTuple,
                                                   context,
                                                   workingMemory,
                                                   memory,
                                                   colctx );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
                    if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                                leftTuple ) ) {
                        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                        if ( childLeftTuple == null || childLeftTuple.getLeftParent() == leftTuple ) {
                            // we must re-add this to ensure deterministic iteration
                            childLeftTuple.reAddRight();
                            removeMatch( rightTuple,
                                         childLeftTuple,
                                         colctx );
                        }
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  colctx );
                        evaluateResultConstraints( ActivitySource.RIGHT,
                                                   leftTuple,
                                                   context,
                                                   workingMemory,
                                                   memory,
                                                   colctx );
                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {

                        LeftTuple temp = childLeftTuple.getRightParentNext();
                        final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                        // remove the match
                        removeMatch( rightTuple,
                                     childLeftTuple,
                                     colctx );
                        evaluateResultConstraints( ActivitySource.RIGHT,
                                                   leftTuple,
                                                   context,
                                                   workingMemory,
                                                   memory,
                                                   colctx );

                        childLeftTuple = temp;
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }

        this.constraints.resetFactHandle( memory.betaMemory.getContext() );
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
    private void evaluateResultConstraints(final ActivitySource source,
                                           final LeftTuple leftTuple,
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
            if ( !this.resultsBinder.isAllowedCachedLeft( memory.resultsContext,
                                                          colctx.resultTuple.getFactHandle() ) ) {
                isAllowed = false;
            }

            this.resultsBinder.resetTuple( memory.resultsContext );
        }

        // temporarily break the linked list to avoid wrong interactions
        LeftTuple[] matchings = splitList( leftTuple,
                                           colctx,
                                           false );

        if ( colctx.propagated == true ) {
            if ( isAllowed ) {
                // modify 
                if ( ActivitySource.LEFT.equals( source ) ) {
                    this.sink.propagateModifyChildLeftTuple( leftTuple.firstChild,
                                                             leftTuple,
                                                             context,
                                                             workingMemory,
                                                             this.tupleMemoryEnabled );
                } else {
                    this.sink.propagateModifyChildLeftTuple( leftTuple.firstChild,
                                                             colctx.resultTuple,
                                                             context,
                                                             workingMemory,
                                                             this.tupleMemoryEnabled );
                }
            } else {
                // retract
                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory );
                colctx.propagated = false;
            }
        } else {
            if ( isAllowed ) {
                // assert
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    colctx.resultTuple,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
                colctx.propagated = true;
            }
        }

        // restore the matchings list
        restoreList( leftTuple,
                     matchings );
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
                                    final CollectContext colctx,
                                    final boolean isUpdatingSink) {
        // unlink all right matches 
        LeftTuple child = leftTuple.firstChild;

        if ( colctx.propagated ) {
            // To do that, we need to skip the first N children that are in fact
            // the propagated tuples
            int target = isUpdatingSink ? this.sink.size() - 1 : this.sink.size();
            for ( int i = 0; i < target; i++ ) {
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
            if ( colctx.propagated ) {
                // temporarily break the linked list to avoid wrong interactions
                LeftTuple[] matchings = splitList( leftTuple,
                                                   colctx,
                                                   true );
                sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                     colctx.resultTuple,
                                                     sink,
                                                     this.tupleMemoryEnabled ),
                                      context,
                                      workingMemory );
                restoreList( leftTuple,
                             matchings );
            }
        }
    }

    protected void doRemove(final InternalWorkingMemory workingMemory,
                            final CollectMemory memory) {
        Iterator it = memory.betaMemory.getCreatedHandles().iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            CollectContext ctx = (CollectContext) entry.getValue();
            workingMemory.getFactHandleFactory().destroyFactHandle( ctx.resultTuple.getFactHandle() );
        }
    }

    public short getType() {
        return NodeTypeEnums.CollectNode;
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

    /**
     * Adds a match between left and right tuple
     * 
     * @param leftTuple
     * @param rightTuple
     * @param colctx
     */
    @SuppressWarnings("unchecked")
    private void addMatch(final LeftTuple leftTuple,
                          final RightTuple rightTuple,
                          final CollectContext colctx) {
        InternalFactHandle handle = rightTuple.getFactHandle();
        if ( this.unwrapRightObject ) {
            handle = ((LeftTuple) handle.getObject()).getLastHandle();
        }
        ((Collection<Object>) colctx.resultTuple.getFactHandle().getObject()).add( handle.getObject() );

        // in sequential mode, we don't need to keep record of matched tuples
        if ( this.tupleMemoryEnabled ) {
            // linking left and right by creating a new left tuple
            new LeftTuple( leftTuple,
                           rightTuple,
                           this,
                           this.tupleMemoryEnabled );
        }
    }

    /**
     * Removes a match between left and right tuple
     *
     * @param rightTuple
     * @param match
     * @param result
     */
    @SuppressWarnings("unchecked")
    public void removeMatch(final RightTuple rightTuple,
                            final LeftTuple match,
                            final CollectContext colctx) {
        if ( match != null ) {
            // removing link between left and right
            match.unlinkFromLeftParent();
            match.unlinkFromRightParent();
        }

        // if there is a subnetwork, we need to unwrap the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        if ( this.unwrapRightObject ) {
            handle = ((LeftTuple) handle.getObject()).getLastHandle();
        }

        ((Collection<Object>) colctx.resultTuple.getFactHandle().getObject()).remove( handle.getObject() );
    }

    @SuppressWarnings("unchecked")
    private void removePreviousMatchesForLeftTuple(final LeftTuple leftTuple,
                                                   final CollectContext colctx) {
        // It is cheaper to simply wipe out the matchings from the end of the list than
        // going through element by element doing proper removal

        // so we just split the list keeping the head 
        LeftTuple[] matchings = splitList( leftTuple,
                                           colctx,
                                           false );
        for ( LeftTuple match = matchings[0]; match != null; match = match.getLeftParentNext() ) {
            // no need to unlink from the left parent as the left parent is being wiped out
            match.unlinkFromRightParent();
        }
        // since there are no more matches, we need to clear the result collection
        ((Collection<Object>) colctx.resultTuple.getFactHandle().getObject()).clear();

    }

    private void removePreviousMatchesForRightTuple(RightTuple rightTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory,
                                                    final CollectMemory memory,
                                                    LeftTuple firstChild) {
        for ( LeftTuple match = firstChild; match != null; ) {
            final LeftTuple tmp = match.getRightParentNext();
            final LeftTuple parent = match.getLeftParent();
            final CollectContext colctx = (CollectContext) memory.betaMemory.getCreatedHandles().get( parent );
            removeMatch( rightTuple,
                         match,
                         colctx );
            evaluateResultConstraints( ActivitySource.RIGHT,
                                       parent,
                                       context,
                                       workingMemory,
                                       memory,
                                       colctx );
            match = tmp;
        }
    }

    protected LeftTuple[] splitList(final LeftTuple parent,
                                    final CollectContext colctx,
                                    final boolean isUpdatingSink) {
        LeftTuple[] matchings = new LeftTuple[2];

        // save the matchings list
        matchings[0] = getFirstMatch( parent,
                                      colctx,
                                      isUpdatingSink );
        matchings[1] = parent.lastChild;

        // update the tuple for the actual propagations
        if ( matchings[0] != null ) {
            if ( parent.firstChild == matchings[0] ) {
                parent.firstChild = null;
            }
            parent.lastChild = matchings[0].getLeftParentPrevious();
            if ( parent.lastChild != null ) {
                parent.lastChild.setLeftParentNext( null );
                matchings[0].setLeftParentPrevious( null );
            }
        }

        return matchings;
    }

    private void restoreList(final LeftTuple parent,
                             final LeftTuple[] matchings) {
        // concatenate matchings list at the end of the children list
        if ( parent.firstChild == null ) {
            parent.firstChild = matchings[0];
            parent.lastChild = matchings[1];
        } else if ( matchings[0] != null ) {
            parent.lastChild.setLeftParentNext( matchings[0] );
            matchings[0].setLeftParentPrevious( parent.lastChild );
            parent.lastChild = matchings[1];
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

    private static enum ActivitySource {
        LEFT, RIGHT
    }

}
