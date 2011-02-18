/*
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LeftTupleList;
import org.drools.core.util.LinkedList;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;

public class FromNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    NodeMemory {
    /**
     *
     */
    private static final long          serialVersionUID = 510l;

    private DataProvider               dataProvider;
    private LeftTupleSource            tupleSource;
    private AlphaNodeFieldConstraint[] alphaConstraints;
    private BetaConstraints            betaConstraints;

    private LeftTupleSinkNode          previousTupleSinkNode;
    private LeftTupleSinkNode          nextTupleSinkNode;

    protected boolean                  tupleMemoryEnabled;

    public FromNode() {
    }

    public FromNode(final int id,
                    final DataProvider dataProvider,
                    final LeftTupleSource tupleSource,
                    final AlphaNodeFieldConstraint[] constraints,
                    final BetaConstraints binder,
                    final boolean tupleMemoryEnabled,
                    final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.dataProvider = dataProvider;
        this.tupleSource = tupleSource;
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        dataProvider = (DataProvider) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        alphaConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        betaConstraints = (BetaConstraints) in.readObject();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( dataProvider );
        out.writeObject( tupleSource );
        out.writeObject( alphaConstraints );
        out.writeObject( betaConstraints );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
        out.writeBoolean( tupleMemoryEnabled );
    }

    /**
     * @inheritDoc
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        Map<Object, RightTuple> matches = null;
        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
            matches = new LinkedHashMap<Object, RightTuple>();
            memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                       matches );
        }

        this.betaConstraints.updateFromTuple( memory.betaMemory.getContext(),
                                              workingMemory,
                                              leftTuple );

        for ( final java.util.Iterator< ? > it = this.dataProvider.getResults( leftTuple,
                                                                               workingMemory,
                                                                               context,
                                                                               memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();

            final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object,
                                                                                                  null, // set this to null, otherwise it uses the driver fact's entrypoint
                                                                                                  workingMemory,
                                                                                                  null );

            RightTuple rightTuple = new RightTuple( handle,
                                                    null );

            checkConstraintsAndPropagate( leftTuple,
                                          rightTuple,
                                          context,
                                          workingMemory,
                                          memory );
            if ( this.tupleMemoryEnabled ) {
                addToCreatedHandlesMap( matches,
                                        rightTuple );
            }
        }

        this.betaConstraints.resetTuple( memory.betaMemory.getContext() );
    }

    private void addToCreatedHandlesMap(final Map<Object, RightTuple> matches,
                                        final RightTuple rightTuple) {
        if ( rightTuple.getFactHandle().isValid() ) {
            Object object = rightTuple.getFactHandle().getObject();
            // keeping a list of matches
            RightTuple existingMatch = matches.get( object );
            if ( existingMatch != null ) {
                // this is for the obscene case where two or more objects returned by "from"
                // have the same hash code and evaluate equals() to true, so we need to preserve
                // all of them to avoid leaks
                rightTuple.setNext( existingMatch );
            }
            matches.put( object,
                         rightTuple );
        }
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
        if ( leftTuple != null ) {
            leftTuple.reAdd(); //
            // LeftTuple previously existed, so continue as modify
            modifyLeftTuple( leftTuple,
                             context,
                             workingMemory );
        } else {
            // LeftTuple does not exist, so create and continue as assert
            assertLeftTuple( new LeftTuple( factHandle,
                                            this,
                                            true ),
                             context,
                             workingMemory );
        }
    }

    @SuppressWarnings("unchecked")
    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        memory.betaMemory.getLeftTupleMemory().add( leftTuple );

        final Map<Object, RightTuple> previousMatches = (Map<Object, RightTuple>) memory.betaMemory.getCreatedHandles().remove( leftTuple );
        final Map<Object, RightTuple> newMatches = new LinkedHashMap<Object, RightTuple>();
        memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                   newMatches );

        this.betaConstraints.updateFromTuple( memory.betaMemory.getContext(),
                                              workingMemory,
                                              leftTuple );

        FastIterator rightIt = LinkedList.fastIterator;
        for ( final java.util.Iterator< ? > it = this.dataProvider.getResults( leftTuple,
                                                                               workingMemory,
                                                                               context,
                                                                               memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();
            RightTuple rightTuple = previousMatches.remove( object );

            if ( rightTuple == null ) {
                // new match, propagate assert
                final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object,
                                                                                                      null, // set this to null, otherwise it uses the driver fact's entrypoint
                                                                                                      workingMemory,
                                                                                                      null );
                rightTuple = new RightTuple( handle,
                                             null );
            } else {
                // previous match, so reevaluate and propagate modify
                if ( rightIt.next( rightTuple ) != null ) {
                    // handle the odd case where more than one object has the same hashcode/equals value
                    previousMatches.put( object,
                                         (RightTuple) rightIt.next( rightTuple ) );
                    rightTuple.setNext( null );
                }
            }

            checkConstraintsAndPropagate( leftTuple,
                                          rightTuple,
                                          context,
                                          workingMemory,
                                          memory );
            addToCreatedHandlesMap( newMatches,
                                    rightTuple );
        }

        this.betaConstraints.resetTuple( memory.betaMemory.getContext() );

        for ( RightTuple rightTuple : previousMatches.values() ) {
            for ( RightTuple current = rightTuple; current != null; current = (RightTuple) rightIt.next( current ) ) {
                retractMatchAndDestroyHandle( leftTuple,
                                              current,
                                              context,
                                              workingMemory );
            }
        }
    }

    private void checkConstraintsAndPropagate(final LeftTuple leftTuple,
                                              final RightTuple rightTuple,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory,
                                              final FromMemory memory) {
        boolean isAllowed = true;
        if ( this.alphaConstraints != null ) {
            // First alpha node filters
            for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                if ( !this.alphaConstraints[i].isAllowed( rightTuple.getFactHandle(),
                                                          workingMemory,
                                                          memory.alphaContexts[i] ) ) {
                    // next iteration
                    isAllowed = false;
                    break;
                }
            }
        }

        if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                                    rightTuple.getFactHandle() ) ) {

            if ( rightTuple.firstChild == null ) {
                // this is a new match, so propagate as assert
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    null,
                                                    null,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            } else {
                // this is an existing match, so propagate as a modify
                this.sink.propagateModifyChildLeftTuple( rightTuple.firstChild,
                                                         leftTuple,
                                                         context,
                                                         workingMemory,
                                                         this.tupleMemoryEnabled );
            }
        } else {
            retractMatchAndDestroyHandle( leftTuple,
                                          rightTuple,
                                          context,
                                          workingMemory );
        }
    }

    private void retractMatchAndDestroyHandle(final LeftTuple leftTuple,
                                              final RightTuple rightTuple,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
        if ( rightTuple.firstChild != null ) {
            // there was a previous match, so need to retract
            this.sink.propagateRetractChildLeftTuple( rightTuple.firstChild,
                                                      leftTuple,
                                                      context,
                                                      workingMemory );

        }
        workingMemory.getFactHandleFactory().destroyFactHandle( rightTuple.getFactHandle() );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        this.sink.propagateRetractLeftTuple( leftTuple,
                                             context,
                                             workingMemory );
        destroyCreatedHandles( workingMemory,
                               memory,
                               leftTuple );
    }

    @SuppressWarnings("unchecked")
    private void destroyCreatedHandles(final InternalWorkingMemory workingMemory,
                                       final FromMemory memory,
                                       final LeftTuple leftTuple) {
        Map<Object, RightTuple> matches = (Map<Object, RightTuple>) memory.betaMemory.getCreatedHandles().remove( leftTuple );
        FastIterator rightIt = LinkedList.fastIterator;
        for ( RightTuple rightTuple : matches.values() ) {
            for ( RightTuple current = rightTuple; current != null; ) {
                RightTuple next = (RightTuple) rightIt.next( current );
                workingMemory.getFactHandleFactory().destroyFactHandle( current.getFactHandle() );
                current.unlinkFromRightParent();
                current = next;
            }
        }
    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated() {
        this.tupleSource.networkUpdated();
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {

        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }

        if ( !this.isInUse() ) {
            for ( InternalWorkingMemory workingMemory : workingMemories ) {
                FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
                Iterator it = memory.betaMemory.getLeftTupleMemory().iterator();
                for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                    destroyCreatedHandles( workingMemory,
                                           memory,
                                           leftTuple );
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                }
                workingMemory.clearNodeMemory( this );
            }
        }

        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
    }

    @SuppressWarnings("unchecked")
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        FastIterator rightIter = LinkedList.fastIterator;
        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            Map<Object, RightTuple> matches = (Map<Object, RightTuple>) memory.betaMemory.getCreatedHandles().get( leftTuple );
            for ( RightTuple rightTuples : matches.values() ) {
                for ( RightTuple rightTuple = rightTuples; rightTuple != null; rightTuple = (RightTuple) rightIter.next( rightTuples ) ) {
                    sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                         rightTuple,
                                                         null,
                                                         null,
                                                         sink,
                                                         this.tupleMemoryEnabled ),
                                          context,
                                          workingMemory );
                }
            }
        }
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        BetaMemory beta = new BetaMemory( new LeftTupleList(),
                                          null,
                                          this.betaConstraints.createContext() );
        return new FromMemory( beta,
                               this.dataProvider.createContext(),
                               this.alphaConstraints );
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.FromNode;
    }

    public static class FromMemory
        implements
        Serializable {
        private static final long serialVersionUID = 510l;

        public BetaMemory         betaMemory;
        public Object             providerContext;
        public ContextEntry[]     alphaContexts;

        public FromMemory(BetaMemory betaMemory,
                          Object providerContext,
                          AlphaNodeFieldConstraint[] constraints) {
            this.betaMemory = betaMemory;
            this.providerContext = providerContext;
            this.alphaContexts = new ContextEntry[constraints.length];
            for ( int i = 0; i < constraints.length; i++ ) {
                this.alphaContexts[i] = constraints[i].createContextEntry();
            }
        }
    }

}
