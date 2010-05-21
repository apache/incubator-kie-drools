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
import java.io.Serializable;
import java.util.Arrays;

import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.base.DroolsQuery;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.ArrayUtils;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Accumulate;
import org.drools.rule.Behavior;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;

/**
 * AccumulateNode
 * A beta node capable of doing accumulate logic.
 *
 * Created: 04/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * @version $Id$
 */
public class AccumulateNode extends BetaNode {

    private static final long          serialVersionUID = 400L;

    private boolean                    unwrapRightObject;
    private Accumulate                 accumulate;
    private AlphaNodeFieldConstraint[] resultConstraints;
    private BetaConstraints            resultBinder;

    public AccumulateNode() {
    }

    public AccumulateNode(final int id,
                          final LeftTupleSource leftInput,
                          final ObjectSource rightInput,
                          final AlphaNodeFieldConstraint[] resultConstraints,
                          final BetaConstraints sourceBinder,
                          final BetaConstraints resultBinder,
                          final Behavior[] behaviors,
                          final Accumulate accumulate,
                          final boolean unwrapRightObject,
                          final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               sourceBinder,
               behaviors );
        this.resultBinder = resultBinder;
        this.resultConstraints = resultConstraints;
        this.accumulate = accumulate;
        this.unwrapRightObject = unwrapRightObject;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        unwrapRightObject = in.readBoolean();
        accumulate = (Accumulate) in.readObject();
        resultConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        resultBinder = (BetaConstraints) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeBoolean( unwrapRightObject );
        out.writeObject( accumulate );
        out.writeObject( resultConstraints );
        out.writeObject( resultBinder );
    }

    /**
     * @inheritDoc
     *
     *  When a new tuple is asserted into an AccumulateNode, do this:
     *
     *  1. Select all matching objects from right memory
     *  2. Execute the initialization code using the tuple + matching objects
     *  3. Execute the accumulation code for each combination of tuple+object
     *  4. Execute the return code
     *  5. Create a new CalculatedObjectHandle for the resulting object and add it to the tuple
     *  6. Propagate the tuple
     *
     *  The initialization, accumulation and return codes, in JBRules, are assembled
     *  into a generated method code and called once for the whole match, as you can see
     *  bellow:
     *
     *   Object result = this.accumulator.accumulate( ... );
     *
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {

        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        AccumulateContext accresult = new AccumulateContext();

        boolean useLeftMemory = true;
        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
            memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                       accresult,
                                                       false );
        } else {
            // this is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle)context.getFactHandle()).getObject();
            if ( memory.betaMemory.getLeftTupleMemory() != null && !(object instanceof DroolsQuery &&  !((DroolsQuery)object).isOpen() ) ) {                
                memory.betaMemory.getLeftTupleMemory().add( leftTuple );
                memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                           accresult,
                                                           false );
            } else {
                useLeftMemory = false;
            }
        }        

        accresult.context = this.accumulate.createContext();

        this.accumulate.init( memory.workingMemoryContext,
                              accresult.context,
                              leftTuple,
                              workingMemory );

        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );

        for ( RightTuple rightTuple = memory.betaMemory.getRightTupleMemory().getFirst( leftTuple, (InternalFactHandle) context.getFactHandle() ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
            InternalFactHandle handle = rightTuple.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                       handle ) ) {

                // add a match
                addMatch( leftTuple,
                          rightTuple,
                          null,
                          null,
                          workingMemory,
                          memory,
                          accresult,
                          useLeftMemory );
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );

        evaluateResultConstraints( ActivitySource.LEFT,
                                   leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   accresult,
                                   useLeftMemory );

    }

    /**
     * @inheritDoc
     *
     * As the accumulate node will always generate a resulting tuple,
     * we must always destroy it
     *
     */
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().remove( leftTuple );

        removePreviousMatchesForLeftTuple( leftTuple,
                                           workingMemory,
                                           memory,
                                           accctx );

        if ( accctx.propagated ) {
            // if tuple was previously propagated, retract it and destroy result fact handle
            this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                                  context,
                                                                  workingMemory );
        } else {
            // if not propagated, just destroy the result fact handle
            workingMemory.getFactHandleFactory().destroyFactHandle( accctx.result.getFactHandle() );
        }
    }

    /**
     * @inheritDoc
     *
     *  When a new object is asserted into an AccumulateNode, do this:
     *
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, call a modify tuple
     *
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

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

        if ( memory.betaMemory.getLeftTupleMemory() == null || memory.betaMemory.getLeftTupleMemory().size() == 0  ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode or for a query.
            // unless it's an "Open Query" and thus that will have left memory, so continue as normal
            return;            
        }

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               factHandle );

        for ( LeftTuple leftTuple = memory.betaMemory.getLeftTupleMemory().getFirst( rightTuple ); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        leftTuple ) ) {
                final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                addMatch( leftTuple,
                          rightTuple,
                          null,
                          null,
                          workingMemory,
                          memory,
                          accctx,
                          true );
                evaluateResultConstraints( ActivitySource.RIGHT,
                                           leftTuple,
                                           context,
                                           workingMemory,
                                           memory,
                                           accctx,
                                           true );
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
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );
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
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );
        final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        memory.betaMemory.getLeftTupleMemory().add( leftTuple );

        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );
        LeftTuple childLeftTuple = getFirstMatch( leftTuple,
                                                  accctx,
                                                  false );

        RightTupleMemory rightMemory = memory.betaMemory.getRightTupleMemory();

        RightTuple rightTuple = rightMemory.getFirst( leftTuple, (InternalFactHandle) context.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        if ( childLeftTuple != null && rightMemory.isIndexed() && rightTuple != rightMemory.getFirst( childLeftTuple.getRightParent() ) ) {
            // our index has changed, so delete all the previous matchings
            removePreviousMatchesForLeftTuple( leftTuple,
                                               workingMemory,
                                               memory,
                                               accctx );

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
                                  null,
                                  null,
                                  workingMemory,
                                  memory,
                                  accctx,
                                  true );
                    }
                }
            } else {
                boolean isDirty = false;
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();

                    if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                               handle ) ) {
                        if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
                            // add a new match
                            addMatch( leftTuple,
                                      rightTuple,
                                      childLeftTuple, 
                                      null,
                                      workingMemory,
                                      memory,
                                      accctx,
                                      true );
                        } else {
                            // we must re-add this to ensure deterministic iteration
                            LeftTuple temp = childLeftTuple.getLeftParentNext();
                            childLeftTuple.reAddRight();
                            childLeftTuple = temp;
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
                        LeftTuple temp = childLeftTuple.getLeftParentNext();
                        // remove the match
                        removeMatch( rightTuple,
                                     childLeftTuple,
                                     workingMemory,
                                     memory,
                                     accctx,
                                     false );
                        childLeftTuple = temp;
                        // the next line means that when a match is removed from the current leftTuple
                        // and the accumulate does not support the reverse operation, then the whole
                        // result is dirty (since removeMatch above is not recalculating the total)
                        // and we need to do this later
                        isDirty = !accumulate.supportsReverse();
                    }
                    // else do nothing, was false before and false now.
                }
                if ( isDirty ) {
                    reaccumulateForLeftTuple( leftTuple,
                                              workingMemory,
                                              memory,
                                              accctx );
                }
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );
        evaluateResultConstraints( ActivitySource.LEFT,
                                   leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   accctx,
                                   true );
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        memory.betaMemory.getRightTupleMemory().remove( rightTuple );
        memory.betaMemory.getRightTupleMemory().add( rightTuple );
        
        if ( memory.betaMemory.getLeftTupleMemory() == null || memory.betaMemory.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }        

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
                        final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  workingMemory,
                                  memory,
                                  accctx,
                                  true );
                        evaluateResultConstraints( ActivitySource.RIGHT,
                                                   leftTuple,
                                                   context,
                                                   workingMemory,
                                                   memory,
                                                   accctx,
                                                   true );
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) leftTuple.getNext() ) {
                    if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                                leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                        LeftTuple temp = null;
                        if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
                            temp = childLeftTuple.getRightParentNext();
                            // we must re-add this to ensure deterministic iteration
                            childLeftTuple.reAddLeft();
                            removeMatch( rightTuple,
                                         childLeftTuple,
                                         workingMemory,
                                         memory,
                                         accctx,
                                         true );
                            childLeftTuple = childLeftTuple.getRightParentNext();
                        }
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  null,
                                  childLeftTuple,
                                  workingMemory,
                                  memory,
                                  accctx,
                                  true );
                        if( temp != null ) {
                            childLeftTuple = temp;
                        }
                        evaluateResultConstraints( ActivitySource.RIGHT,
                                                   leftTuple,
                                                   context,
                                                   workingMemory,
                                                   memory,
                                                   accctx,
                                                   true );
                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {

                        LeftTuple temp = childLeftTuple.getRightParentNext();
                        final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                        // remove the match
                        removeMatch( rightTuple,
                                     childLeftTuple,
                                     workingMemory,
                                     memory,
                                     accctx,
                                     true );
                        evaluateResultConstraints( ActivitySource.RIGHT,
                                                   leftTuple,
                                                   context,
                                                   workingMemory,
                                                   memory,
                                                   accctx,
                                                   true );

                        childLeftTuple = temp;
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }

        this.constraints.resetFactHandle( memory.betaMemory.getContext() );
    }

    /**
     * Evaluate result constraints and propagate assert in case they are true
     * 
     * @param leftTuple
     * @param context
     * @param workingMemory
     * @param memory
     * @param accresult
     * @param handle
     */
    private void evaluateResultConstraints(final ActivitySource source,
                                           final LeftTuple leftTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory,
                                           final AccumulateMemory memory,
                                           final AccumulateContext accctx,
                                           final boolean useLeftMemory ) {

        // get the actual result
        final Object result = this.accumulate.getResult( memory.workingMemoryContext,
                                                         accctx.context,
                                                         leftTuple,
                                                         workingMemory );

        if ( result == null ) {
            throw new RuntimeDroolsException( "Accumulate must not return a null value." );
        }

        if ( accctx.result == null ) {
            final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                                                  workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                                                        result ),
                                                                                                  workingMemory ); // so far, result is not an event

            accctx.result = new RightTuple( handle,
                                            this );
        } else {
            accctx.result.getFactHandle().setObject( result );
        }

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( accctx.result.getFactHandle(),
                                                       workingMemory,
                                                       memory.alphaContexts[i] ) ) {
                isAllowed = false;
                break;
            }
        }
        if ( isAllowed ) {
            this.resultBinder.updateFromTuple( memory.resultsContext,
                                               workingMemory,
                                               leftTuple );
            if ( !this.resultBinder.isAllowedCachedLeft( memory.resultsContext,
                                                         accctx.result.getFactHandle() ) ) {
                isAllowed = false;
            }
            this.resultBinder.resetTuple( memory.resultsContext );
        }

        if ( accctx.propagated == true ) {
            // temporarily break the linked list to avoid wrong interactions
            LeftTuple[] matchings = splitList( leftTuple,
                                               accctx,
                                               false );
            if ( isAllowed ) {
                // modify 
                if ( ActivitySource.LEFT.equals( source ) ) {
                    this.sink.propagateModifyChildLeftTuple( leftTuple.firstChild,
                                                             leftTuple,
                                                             context,
                                                             workingMemory,
                                                             useLeftMemory );
                } else {
                    this.sink.propagateModifyChildLeftTuple( leftTuple.firstChild,
                                                             accctx.result,
                                                             context,
                                                             workingMemory,
                                                             useLeftMemory );
                }
            } else {
                // retract
                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory );
                accctx.propagated = false;
            }
            // restore the matchings list
            restoreList( leftTuple,
                         matchings );
        } else if ( isAllowed ) {
            // temporarily break the linked list to avoid wrong interactions
            LeftTuple[] matchings = splitList( leftTuple,
                                               accctx,
                                               false );
            // assert
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                accctx.result,
                                                null, 
                                                null,
                                                context,
                                                workingMemory,
                                                useLeftMemory );
            accctx.propagated = true;
            // restore the matchings list
            restoreList( leftTuple,
                         matchings );
        }

    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
            if ( accctx.propagated ) {
                // temporarily break the linked list to avoid wrong interactions
                LeftTuple[] matchings = splitList( leftTuple,
                                                   accctx,
                                                   true );
                sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                     accctx.result,
                                                     null,
                                                     null,
                                                     sink,
                                                     true ),
                                      context,
                                      workingMemory );
                restoreList( leftTuple,
                             matchings );
            }
        }
    }

    protected void doRemove(final InternalWorkingMemory workingMemory,
                            final AccumulateMemory memory) {
        Iterator it = memory.betaMemory.getCreatedHandles().iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            AccumulateContext ctx = (AccumulateContext) entry.getValue();
            workingMemory.getFactHandleFactory().destroyFactHandle( ctx.result.getFactHandle() );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#hashCode()
     */
    public int hashCode() {
        return this.leftInput.hashCode() ^ this.rightInput.hashCode() ^ this.accumulate.hashCode() ^ this.resultBinder.hashCode() ^ ArrayUtils.hashCode( this.resultConstraints );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof AccumulateNode) ) {
            return false;
        }

        final AccumulateNode other = (AccumulateNode) object;

        if ( this.getClass() != other.getClass() || (!this.leftInput.equals( other.leftInput )) || (!this.rightInput.equals( other.rightInput )) || (!this.constraints.equals( other.constraints )) ) {
            return false;
        }

        return this.accumulate.equals( other.accumulate ) && resultBinder.equals( other.resultBinder ) && Arrays.equals( this.resultConstraints,
                                                                                                                         other.resultConstraints );
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        AccumulateMemory memory = new AccumulateMemory();
        memory.betaMemory = this.constraints.createBetaMemory( config );
        memory.workingMemoryContext = this.accumulate.createWorkingMemoryContext();
        memory.resultsContext = this.resultBinder.createContext();
        memory.alphaContexts = new ContextEntry[this.resultConstraints.length];
        for ( int i = 0; i < this.resultConstraints.length; i++ ) {
            memory.alphaContexts[i] = this.resultConstraints[i].createContextEntry();
        }
        memory.betaMemory.setBehaviorContext( this.behavior.createBehaviorContext() );
        return memory;
    }

    public short getType() {
        return NodeTypeEnums.AccumulateNode;
    }

    private void addMatch(final LeftTuple leftTuple,
                          final RightTuple rightTuple,
                          final LeftTuple currentLeftChild,
                          final LeftTuple currentRightChild,
                          final InternalWorkingMemory workingMemory,
                          final AccumulateMemory memory,
                          final AccumulateContext accresult,
                          final boolean useLeftMemory ) {
        LeftTuple tuple = leftTuple;
        InternalFactHandle handle = rightTuple.getFactHandle();
        if ( this.unwrapRightObject ) {
            // if there is a subnetwork, handle must be unwrapped
            tuple = (LeftTuple) handle.getObject();
            handle = tuple.getLastHandle();
        }
        this.accumulate.accumulate( memory.workingMemoryContext,
                                    accresult.context,
                                    tuple,
                                    handle,
                                    workingMemory );

        // in sequential mode, we don't need to keep record of matched tuples
        if ( useLeftMemory ) {
            // linking left and right by creating a new left tuple
            new LeftTuple( leftTuple,
                           rightTuple,
                           currentLeftChild,
                           currentRightChild,
                           this,
                           true );
        }
    }

    /**
     * Removes a match between left and right tuple
     *
     * @param rightTuple
     * @param match
     * @param result
     */
    private void removeMatch(final RightTuple rightTuple,
                             final LeftTuple match,
                             final InternalWorkingMemory workingMemory,
                             final AccumulateMemory memory,
                             final AccumulateContext accctx,
                             final boolean reaccumulate) {
        // save the matching tuple
        LeftTuple leftTuple = match.getLeftParent();

        if ( match != null ) {
            // removing link between left and right
            match.unlinkFromLeftParent();
            match.unlinkFromRightParent();
        }

        // if there is a subnetwork, we need to unwrap the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        LeftTuple tuple = leftTuple;
        if ( this.unwrapRightObject ) {
            tuple = (LeftTuple) handle.getObject();
            handle = tuple.getLastHandle();
        }

        if ( this.accumulate.supportsReverse() ) {
            // just reverse this single match
            this.accumulate.reverse( memory.workingMemoryContext,
                                     accctx.context,
                                     tuple,
                                     handle,
                                     workingMemory );
        } else {
            // otherwise need to recalculate all matches for the given leftTuple
            if ( reaccumulate ) {
                reaccumulateForLeftTuple( leftTuple,
                                          workingMemory,
                                          memory,
                                          accctx );

            }
        }
    }

    private void reaccumulateForLeftTuple(final LeftTuple leftTuple,
                                          final InternalWorkingMemory workingMemory,
                                          final AccumulateMemory memory,
                                          final AccumulateContext accctx) {
        this.accumulate.init( memory.workingMemoryContext,
                              accctx.context,
                              leftTuple,
                              workingMemory );
        for ( LeftTuple childMatch = getFirstMatch( leftTuple,
                                                    accctx,
                                                    false ); childMatch != null; childMatch = childMatch.getLeftParentNext() ) {
            InternalFactHandle childHandle = childMatch.getRightParent().getFactHandle();
            LeftTuple tuple = leftTuple;
            if ( this.unwrapRightObject ) {
                tuple = (LeftTuple) childHandle.getObject();
                childHandle = tuple.getLastHandle();
            }
            this.accumulate.accumulate( memory.workingMemoryContext,
                                        accctx.context,
                                        tuple,
                                        childHandle,
                                        workingMemory );
        }
    }

    private void removePreviousMatchesForLeftTuple(final LeftTuple leftTuple,
                                                   final InternalWorkingMemory workingMemory,
                                                   final AccumulateMemory memory,
                                                   final AccumulateContext accctx) {
        // so we just split the list keeping the head 
        LeftTuple[] matchings = splitList( leftTuple,
                                           accctx,
                                           false );
        for ( LeftTuple match = matchings[0]; match != null; match = match.getLeftParentNext() ) {
            // can't unlink from the left parent as it was already unlinked during the splitList call above
            match.unlinkFromRightParent();
        }
        // since there are no more matches, the following call will just re-initialize the accumulation
        this.accumulate.init( memory.workingMemoryContext,
                              accctx.context,
                              leftTuple,
                              workingMemory );
    }

    private void removePreviousMatchesForRightTuple(final RightTuple rightTuple,
                                                    final PropagationContext context,
                                                    final InternalWorkingMemory workingMemory,
                                                    final AccumulateMemory memory,
                                                    final LeftTuple firstChild) {
        for ( LeftTuple match = firstChild; match != null; ) {
            final LeftTuple tmp = match.getRightParentNext();
            final LeftTuple parent = match.getLeftParent();
            final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( parent );
            removeMatch( rightTuple,
                         match,
                         workingMemory,
                         memory,
                         accctx,
                         true );
            evaluateResultConstraints( ActivitySource.RIGHT,
                                       parent,
                                       context,
                                       workingMemory,
                                       memory,
                                       accctx,
                                       true );
            match = tmp;
        }
    }

    protected LeftTuple[] splitList(final LeftTuple parent,
                                    final AccumulateContext accctx,
                                    final boolean isUpdatingSink) {
        LeftTuple[] matchings = new LeftTuple[2];

        // save the matchings list
        matchings[0] = getFirstMatch( parent,
                                      accctx,
                                      isUpdatingSink );
        matchings[1] = matchings[0] != null ? parent.lastChild : null;

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

    /**
     * Skips the propagated tuple handles and return the first handle
     * in the list that correspond to a match
     * 
     * @param leftTuple
     * @param accctx
     * @return
     */
    private LeftTuple getFirstMatch(final LeftTuple leftTuple,
                                    final AccumulateContext accctx,
                                    final boolean isUpdatingSink) {
        // unlink all right matches 
        LeftTuple child = leftTuple.firstChild;

        if ( accctx.propagated ) {
            // To do that, we need to skip the first N children that are in fact
            // the propagated tuples
            int target = isUpdatingSink ? this.sink.size() - 1 : this.sink.size();
            for ( int i = 0; i < target; i++ ) {
                child = child.getLeftParentNext();
            }
        }
        return child;
    }

    public static class AccumulateMemory
        implements
        Externalizable {
        private static final long serialVersionUID = 400L;

        public Object             workingMemoryContext;
        public BetaMemory         betaMemory;
        public ContextEntry[]     resultsContext;
        public ContextEntry[]     alphaContexts;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            workingMemoryContext = in.readObject();
            betaMemory = (BetaMemory) in.readObject();
            resultsContext = (ContextEntry[]) in.readObject();
            alphaContexts = (ContextEntry[]) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( workingMemoryContext );
            out.writeObject( betaMemory );
            out.writeObject( resultsContext );
            out.writeObject( alphaContexts );
        }

    }

    public static class AccumulateContext
        implements
        Externalizable {
        public Serializable context;
        public RightTuple   result;
        public boolean      propagated;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = (Serializable) in.readObject();
            result = (RightTuple) in.readObject();
            propagated = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
            out.writeObject( result );
            out.writeBoolean( propagated );
        }

    }

    private static enum ActivitySource {
        LEFT, RIGHT
    }

}
