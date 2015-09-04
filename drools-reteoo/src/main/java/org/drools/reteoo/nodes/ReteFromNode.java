/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.reteoo.nodes;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.LeftTupleSourceUtils;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.From;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReteFromNode extends FromNode {

    public ReteFromNode() {
    }

    public ReteFromNode(int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] constraints,
                        BetaConstraints binder, boolean tupleMemoryEnabled, BuildContext context, From from) {
        super(id, dataProvider, tupleSource, constraints, binder, tupleMemoryEnabled, context, from);
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               this, getLeftInputOtnId(), getLeftInferredMask());
    }

    /**
     * @inheritDoc
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        Map<Object, RightTuple> matches = null;
        boolean useLeftMemory = true;
        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = leftTuple.get( 0 ).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        if ( useLeftMemory ) {
            memory.getBetaMemory().getLeftTupleMemory().add( leftTuple );
            matches = new LinkedHashMap<Object, RightTuple>();
            leftTuple.setObject( matches );
        }

        this.betaConstraints.updateFromTuple( memory.getBetaMemory().getContext(),
                                              workingMemory,
                                              leftTuple );

        for ( final java.util.Iterator< ? > it = this.dataProvider.getResults( leftTuple,
                                                                               workingMemory,
                                                                               context,
                                                                               memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();
            if ( (object == null) || !resultClass.isAssignableFrom( object.getClass() ) ) {
                continue; // skip anything if it not assignable
            }

            RightTuple rightTuple = createRightTuple( leftTuple,
                                                      context,
                                                      workingMemory,
                                                      object );

            checkConstraintsAndPropagate( leftTuple,
                                          rightTuple,
                                          context,
                                          workingMemory,
                                          memory,
                                          useLeftMemory );
            if ( useLeftMemory ) {
                addToCreatedHandlesMap( matches,
                                        rightTuple );
            }
        }

        this.betaConstraints.resetTuple( memory.getBetaMemory().getContext() );
    }

    @SuppressWarnings("unchecked")
    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        memory.getBetaMemory().getLeftTupleMemory().removeAdd( leftTuple );

        final Map<Object, RightTuple> previousMatches = (Map<Object, RightTuple>) leftTuple.getObject();
        final Map<Object, RightTuple> newMatches = new HashMap<Object, RightTuple>();
        leftTuple.setObject( newMatches );

        this.betaConstraints.updateFromTuple( memory.getBetaMemory().getContext(),
                                              workingMemory,
                                              leftTuple );

        FastIterator rightIt = LinkedList.fastIterator;
        for ( final java.util.Iterator< ? > it = this.dataProvider.getResults( leftTuple,
                                                                               workingMemory,
                                                                               context,
                                                                               memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();
            if ( !resultClass.isAssignableFrom( object.getClass() ) ) {
                continue; // skip anything if it not assignable
            }

            RightTuple rightTuple = previousMatches.remove( object );

            if ( rightTuple == null ) {
                // new match, propagate assert
                rightTuple = createRightTuple( leftTuple,
                                               context,
                                               workingMemory,
                                               object );
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
                                          memory,
                                          true );
            addToCreatedHandlesMap( newMatches,
                                    rightTuple );
        }

        this.betaConstraints.resetTuple( memory.getBetaMemory().getContext() );

        for ( RightTuple rightTuple : previousMatches.values() ) {
            for ( RightTuple current = rightTuple; current != null; current = (RightTuple) rightIt.next( current ) ) {
                retractMatch( leftTuple,
                              current,
                              context,
                              workingMemory );
            }
        }
    }

    protected void checkConstraintsAndPropagate( final LeftTuple leftTuple,
                                                 final RightTuple rightTuple,
                                                 final PropagationContext context,
                                                 final InternalWorkingMemory workingMemory,
                                                 final FromMemory memory,
                                                 final boolean useLeftMemory ) {
        boolean isAllowed = true;
        if ( this.alphaConstraints != null ) {
            // First alpha node filters
            for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                if ( !this.alphaConstraints[i].isAllowed( rightTuple.getFactHandle(),
                                                          workingMemory,
                                                          memory.getAlphaContexts()[i] ) ) {
                    // next iteration
                    isAllowed = false;
                    break;
                }
            }
        }

        if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( memory.getBetaMemory().getContext(),
                                                                    rightTuple.getFactHandle() ) ) {

            if ( rightTuple.firstChild == null ) {
                // this is a new match, so propagate as assert
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    null,
                                                    null,
                                                    context,
                                                    workingMemory,
                                                    useLeftMemory );
            } else {
                // this is an existing match, so propagate as a modify
                this.sink.propagateModifyChildLeftTuple( rightTuple.firstChild,
                                                         leftTuple,
                                                         context,
                                                         workingMemory,
                                                         useLeftMemory );
            }
        } else {
            retractMatch( leftTuple,
                          rightTuple,
                          context,
                          workingMemory );
        }
    }

    protected void retractMatch(final LeftTuple leftTuple,
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
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
        memory.getBetaMemory().getLeftTupleMemory().remove( leftTuple );
        this.sink.propagateRetractLeftTuple( leftTuple,
                                             context,
                                             workingMemory );
        unlinkCreatedHandles( workingMemory,
                              memory,
                              leftTuple );
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {

        if ( !this.isInUse() ) {
            for ( InternalWorkingMemory workingMemory : workingMemories ) {
                FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
                Iterator it = memory.getBetaMemory().getLeftTupleMemory().iterator();
                for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                    unlinkCreatedHandles( workingMemory,
                                          memory,
                                          leftTuple );
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                }
                workingMemory.clearNodeMemory( this );
            }
            getLeftTupleSource().removeTupleSink( this );
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void unlinkCreatedHandles(final InternalWorkingMemory workingMemory,
                                      final FromMemory memory,
                                      final LeftTuple leftTuple) {
        Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
        FastIterator rightIt = LinkedList.fastIterator;
        for ( RightTuple rightTuple : matches.values() ) {
            for ( RightTuple current = rightTuple; current != null; ) {
                RightTuple next = (RightTuple) rightIt.next( current );
                current.unlinkFromRightParent();
                current = next;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        FastIterator rightIter = LinkedList.fastIterator;
        final Iterator tupleIter = memory.getBetaMemory().getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {

            this.betaConstraints.updateFromTuple( memory.getBetaMemory().getContext(),
                                                  workingMemory,
                                                  leftTuple );

            Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
            for ( RightTuple rightTuples : matches.values() ) {
                for ( RightTuple rightTuple = rightTuples; rightTuple != null; rightTuple = (RightTuple) rightIter.next( rightTuple ) ) {
                    boolean isAllowed = true;
                    if ( this.alphaConstraints != null ) {
                        // First alpha node filters
                        for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                            if ( !this.alphaConstraints[i].isAllowed( rightTuple.getFactHandle(),
                                                                      workingMemory,
                                                                      memory.getAlphaContexts()[i] ) ) {
                                // next iteration
                                isAllowed = false;
                                break;
                            }
                        }
                    }

                    if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( memory.getBetaMemory().getContext(),
                                                                                rightTuple.getFactHandle() ) ) {
                        sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
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

            this.betaConstraints.resetTuple( memory.getBetaMemory().getContext() );
        }
    }

    public void attach( BuildContext context ) {
        super.attach( context );
        if (context == null ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
            this.leftInput.updateSink( this,
                                       propagationContext,
                                       workingMemory );
        }
    }
}
