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

import org.drools.FactHandle;
import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.ArrayUtils;
import org.drools.core.util.Entry;
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

        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
            memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                       accresult,
                                                       false );
        }

        accresult.context = this.accumulate.createContext();

        this.accumulate.init( memory.workingMemoryContext,
                              accresult.context,
                              leftTuple,
                              workingMemory );

        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );

        for ( RightTuple rightTuple = memory.betaMemory.getRightTupleMemory().getFirst( leftTuple ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
            InternalFactHandle handle = rightTuple.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                       handle ) ) {
                LeftTuple tuple = leftTuple;
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

        final Object result = this.accumulate.getResult( memory.workingMemoryContext,
                                                         accresult.context,
                                                         leftTuple,
                                                         workingMemory );

        if ( result == null ) {
            throw new RuntimeDroolsException( "Accumulate must not return a null value." );
        }

        final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                                              workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                                                    result ),
                                                                                              workingMemory ); // so far, result is not an event

        accresult.result = new RightTuple( handle,
                                           this );

        evaluateResultConstraints( leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   accresult );

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

        LeftTuple child = getFirstMatch( leftTuple,
                                         accctx );

        // Now, unlink the matches 
        while ( child != null ) {
            LeftTuple tmp = child.getLeftParentNext();
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = tmp;
        }

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

        if ( !this.tupleMemoryEnabled ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               factHandle );

        // need to clone the tuples to avoid concurrent modification exceptions
        Entry[] tuples = memory.betaMemory.getLeftTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            LeftTuple tuple = (LeftTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        tuple ) ) {
                if ( this.accumulate.supportsReverse() || context.getType() == PropagationContext.ASSERTION ) {
                    modifyTuple( true,
                                 tuple,
                                 rightTuple,
                                 context,
                                 workingMemory,
                                 memory );
                } else {
                    // context is MODIFICATION and does not supports reverse
                    this.retractLeftTuple( tuple,
                                           context,
                                           workingMemory );
                    this.assertLeftTuple( tuple,
                                          context,
                                          workingMemory );
                }
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
        if( context.getType() == PropagationContext.EXPIRATION ) {
            ((PropagationContextImpl)context).setFactHandle( null );
        }

        behavior.retractRightTuple( memory.betaMemory.getBehaviorContext(),
                                    rightTuple,
                                    workingMemory );
        memory.betaMemory.getRightTupleMemory().remove( rightTuple );

        for ( LeftTuple childTuple = rightTuple.getBetaChildren(); childTuple != null; ) {
            LeftTuple tmp = childTuple.getRightParentNext();
            if ( this.accumulate.supportsReverse() ) {
                this.modifyTuple( false,
                                  childTuple.getParent(),
                                  rightTuple,
                                  context,
                                  workingMemory,
                                  memory );
            } else {
                // does not support reverse, so needs to be fully retracted and reasserted
                LeftTuple match = childTuple.getParent();

                // but first, needs to remove the matching child
                childTuple.unlinkFromLeftParent();
                childTuple.unlinkFromRightParent();

                this.retractLeftTuple( match,
                                       context,
                                       workingMemory );
                this.assertLeftTuple( match,
                                      context,
                                      workingMemory );
            }
            childTuple = tmp;
        }
        
        if( context.getType() == PropagationContext.EXPIRATION ) {
            ((PropagationContextImpl)context).setFactHandle( origin );
        }

    }

    /**
     * @param rightTuple
     * @param leftTuple
     */
    private void removeMatchingChild(final LeftTuple leftTuple,
                                     final RightTuple rightTuple) {
        if ( leftTuple.getBetaChildren() != null ) {
            // removing link between left and right
            LeftTuple match = leftTuple.getBetaChildren();
            while ( match.getRightParent() != rightTuple ) {
                match = match.getLeftParentNext();
            }
            match.unlinkFromLeftParent();
            match.unlinkFromRightParent();
        }
    }

    public void modifyTuple(final boolean isAssert,
                            final LeftTuple leftTuple,
                            final RightTuple rightTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory,
                            final AccumulateMemory memory) {

        final AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );

        // if tuple was propagated
        if ( accctx.propagated ) {
            LeftTuple firstMatch = getFirstMatch( leftTuple,
                                                  accctx );

            // we may have no matches yet
            if ( firstMatch != null ) {
                // temporarily break the linked list to avoid wrong retracts
                firstMatch.getLeftParentPrevious().setLeftParentNext( null );
                firstMatch.setLeftParentPrevious( null );
            }
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
            // now set the beta children to the first match
            leftTuple.setBetaChildren( firstMatch );
            accctx.propagated = false;
        }

        if ( isAssert ) {
            // linking left and right by creating a new left tuple
            new LeftTuple( leftTuple,
                           rightTuple,
                           this,
                           this.tupleMemoryEnabled );
        } else {
            removeMatchingChild( leftTuple,
                                 rightTuple );
        }

        // if there is a subnetwork, we need to unwrapp the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        LeftTuple tuple = leftTuple;
        if ( this.unwrapRightObject ) {
            tuple = ((LeftTuple) handle.getObject());
            handle = tuple.getLastHandle();
        }

        if ( context.getType() == PropagationContext.ASSERTION ) {
            // assertion
            this.accumulate.accumulate( memory.workingMemoryContext,
                                        accctx.context,
                                        tuple,
                                        handle,
                                        workingMemory );
        } else if ( context.getType() == PropagationContext.MODIFICATION || context.getType() == PropagationContext.RULE_ADDITION || context.getType() == PropagationContext.RULE_REMOVAL ) {
            // modification
            if ( isAssert ) {
                this.accumulate.accumulate( memory.workingMemoryContext,
                                            accctx.context,
                                            tuple,
                                            handle,
                                            workingMemory );
            } else {
                this.accumulate.reverse( memory.workingMemoryContext,
                                         accctx.context,
                                         tuple,
                                         handle,
                                         workingMemory );
            }
        } else {
            // retraction and expiration
            this.accumulate.reverse( memory.workingMemoryContext,
                                     accctx.context,
                                     tuple,
                                     handle,
                                     workingMemory );
        }

        final Object result = this.accumulate.getResult( memory.workingMemoryContext,
                                                         accctx.context,
                                                         leftTuple,
                                                         workingMemory );

        if ( result == null ) {
            throw new RuntimeDroolsException( "Accumulate must not return a null value." );
        }

        // update result object 
        accctx.result.getFactHandle().setObject( result );
        workingMemory.getFactHandleFactory().increaseFactHandleRecency( accctx.result.getFactHandle() );

        evaluateResultConstraints( leftTuple,
                                   context,
                                   workingMemory,
                                   memory,
                                   accctx );

    }

//    public static class AccumulatePropagationCallBack {
//        private Object            workingMemoryContext;
//        private AccumulateContext accctx;
//        private LeftTuple         leftTuple;
//
//    }

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
    private void evaluateResultConstraints(final LeftTuple leftTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory,
                                           final AccumulateMemory memory,
                                           final AccumulateContext accctx) {
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
            if ( this.resultBinder.isAllowedCachedLeft( memory.resultsContext,
                                                        accctx.result.getFactHandle() ) ) {
                accctx.propagated = true;
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    accctx.result,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            }
            this.resultBinder.resetTuple( memory.resultsContext );
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
                                    final AccumulateContext colctx) {
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
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
            if ( accctx.propagated ) {
                sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                     accctx.result,
                                                     sink,
                                                     this.tupleMemoryEnabled ),
                                      context,
                                      workingMemory );
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
}
