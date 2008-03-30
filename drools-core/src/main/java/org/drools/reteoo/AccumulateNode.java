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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Externalizable;

import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.CollectNode.CollectMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Accumulate;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.ArrayUtils;
import org.drools.util.Entry;
import org.drools.util.FactEntry;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;

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
                          final Accumulate accumulate,
                          final boolean unwrapRightObject,
                          final BuildContext context) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
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

        AccumulateResult accresult = new AccumulateResult();

        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
            memory.resultMap.put( leftTuple,
                                      accresult,
                                      false );
        }

        final Object accContext = this.accumulate.createContext();

        accresult.context = accContext;
        this.accumulate.init( memory.workingMemoryContext,
                              accContext,
                              leftTuple,
                              workingMemory );

        this.constraints.updateFromTuple( memory.betaMemory.getContext(),
                                          workingMemory,
                                          leftTuple );

        for ( RightTuple rightTuple = memory.betaMemory.getRightTupleMemory().getFirst( leftTuple ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
            InternalFactHandle handle = rightTuple.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                       handle ) ) {
                if ( this.unwrapRightObject ) {
                    // if there is a subnetwork, handle must be unwrapped
                    LeftTuple tuple = (LeftTuple) handle.getObject();
                    handle = tuple.getLastHandle();
                    this.accumulate.accumulate( memory.workingMemoryContext,
                                                accContext,
                                                tuple,
                                                handle,
                                                workingMemory );
                } else {
                    this.accumulate.accumulate( memory.workingMemoryContext,
                                                accContext,
                                                leftTuple,
                                                handle,
                                                workingMemory );
                }
            }
        }

        this.constraints.resetTuple( memory.betaMemory.getContext() );

        final Object result = this.accumulate.getResult( memory.workingMemoryContext,
                                                         accContext,
                                                         leftTuple,
                                                         workingMemory );

        if ( result == null ) {
            throw new RuntimeDroolsException( "Accumulate must not return a null value." );
        }

        // First alpha node filters
        boolean isAllowed = true;
        final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                                              false,
                                                                                              workingMemory ); // so far, result is not an event
        
        final RightTuple resultTuple = new RightTuple( handle,
                                                       this );        

        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( handle,
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
                                                        handle ) ) {
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    resultTuple,
                                                    context,
                                                    workingMemory );
            } else {
                workingMemory.getFactHandleFactory().destroyFactHandle( handle );
            }
        } else {
            workingMemory.getFactHandleFactory().destroyFactHandle( handle );
        }

    }

    /**
     * @inheritDoc
     *
     * As the accumulate node will always propagate the tuple,
     * it must always also retreat it.
     *
     */
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );
        
        memory.resultMap.remove( leftTuple );

        this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                              context,
                                                              workingMemory );
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
                                 factHandle,
                                 context,
                                 workingMemory );
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
        memory.betaMemory.getRightTupleMemory().remove( rightTuple );
        
        InternalFactHandle handle = rightTuple.getFactHandle(); 

        this.constraints.updateFromFactHandle( memory.betaMemory.getContext(),
                                               workingMemory,
                                               handle );
        // need to clone the tuples to avoid concurrent modification exceptions
        Entry[] tuples = memory.betaMemory.getLeftTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            LeftTuple tuple = (LeftTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( memory.betaMemory.getContext(),
                                                        tuple ) ) {
                if ( this.accumulate.supportsReverse() ) {
                    this.modifyTuple( false,
                                      tuple,
                                      handle,
                                      context,
                                      workingMemory );
                } else {
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

    public void modifyTuple(final boolean isAssert,
                            final LeftTuple leftTuple,
                            InternalFactHandle handle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );
        AccumulateResult accresult = (AccumulateResult) memory.resultMap.get( leftTuple );

        // if tuple was propagated
        this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                              context,
                                                              workingMemory );

        LeftTuple tuple = leftTuple;
        if ( this.unwrapRightObject ) {
            // if there is a subnetwork, handle must be unwrapped
            tuple = (LeftTuple) handle.getObject();
            handle = tuple.getLastHandle();
        }

        if ( context.getType() == PropagationContext.ASSERTION ) {
            // assertion
            if ( accresult.context == null ) {
                final Object accContext = this.accumulate.createContext();

                this.accumulate.init( memory.workingMemoryContext,
                                      accContext,
                                      leftTuple,
                                      workingMemory );

                accresult.context = accContext;
            }

            this.accumulate.accumulate( memory.workingMemoryContext,
                                        accresult.context,
                                        tuple,
                                        handle,
                                        workingMemory );
        } else if ( context.getType() == PropagationContext.MODIFICATION || context.getType() == PropagationContext.RULE_ADDITION || context.getType() == PropagationContext.RULE_REMOVAL ) {
            // modification
            if ( isAssert ) {
                this.accumulate.accumulate( memory.workingMemoryContext,
                                            accresult.context,
                                            tuple,
                                            handle,
                                            workingMemory );
            } else {
                this.accumulate.reverse( memory.workingMemoryContext,
                                         accresult.context,
                                         tuple,
                                         handle,
                                         workingMemory );
            }
        } else {
            // retraction
            this.accumulate.reverse( memory.workingMemoryContext,
                                     accresult.context,
                                     tuple,
                                     handle,
                                     workingMemory );
        }

        final Object result = this.accumulate.getResult( memory.workingMemoryContext,
                                                         accresult.context,
                                                         leftTuple,
                                                         workingMemory );

        if ( result == null ) {
            throw new RuntimeDroolsException( "Accumulate must not return a null value." );
        }

        // First alpha node filters
        boolean isAllowed = true;
        final InternalFactHandle createdHandle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                                                     false,
                                                                                                     workingMemory ); // so far, result is not an event
        
        final RightTuple resultTuple = new RightTuple( handle,
                                                       this );
        
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( createdHandle,
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
                                                        createdHandle ) ) {
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    resultTuple,
                                                    context,
                                                    workingMemory );
            } else {
                workingMemory.getFactHandleFactory().destroyFactHandle( createdHandle );
            }

            this.resultBinder.resetTuple( memory.resultsContext );
        } else {
            workingMemory.getFactHandleFactory().destroyFactHandle( createdHandle );
        }
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            LeftTuple childLeftTuple = leftTuple.getBetaChildren();
            if ( childLeftTuple != null ) {
                RightTuple rightTuple = childLeftTuple.getRightParent();
                sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                     rightTuple,
                                                     sink ),
                                      context,
                                      workingMemory );
            }
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

    public String toString() {
        return "[ " + this.getClass().getName() + "(" + this.id + ") ]";
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        AccumulateMemory memory = new AccumulateMemory();
        memory.betaMemory = this.constraints.createBetaMemory( config );
        memory.resultMap = new ObjectHashMap();
        memory.workingMemoryContext = this.accumulate.createWorkingMemoryContext();
        memory.resultsContext = this.resultBinder.createContext();
        memory.alphaContexts = new ContextEntry[this.resultConstraints.length];
        for ( int i = 0; i < this.resultConstraints.length; i++ ) {
            memory.alphaContexts[i] = this.resultConstraints[i].createContextEntry();
        }
        return memory;
    }

    public static class AccumulateMemory
        implements
        Externalizable {
        private static final long serialVersionUID = 400L;

        public Object             workingMemoryContext;
        public BetaMemory         betaMemory;
        public ObjectHashMap      resultMap;
        public ContextEntry[]     resultsContext;
        public ContextEntry[]     alphaContexts;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            workingMemoryContext = in.readObject();
            betaMemory = (BetaMemory) in.readObject();
            resultMap = (ObjectHashMap) in.readObject();
            resultsContext = (ContextEntry[]) in.readObject();
            alphaContexts = (ContextEntry[]) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( workingMemoryContext );
            out.writeObject( betaMemory );
            out.writeObject( resultMap );
            out.writeObject( resultsContext );
            out.writeObject( alphaContexts );
        }

    }

    public static class AccumulateResult
        implements
        Externalizable {
        public Object             context;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }

    }
}
