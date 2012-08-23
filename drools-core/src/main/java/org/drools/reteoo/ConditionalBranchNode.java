package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.LeftTupleIterator;
import org.drools.common.Memory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.UpdateContext;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.ConditionalBranchEvaluator.ConditionalExecution;
import org.drools.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Node which allows to follow different paths in the Rete-OO network,
 * based on the result of a boolean <code>Test</code>.
 */
public class ConditionalBranchNode extends LeftTupleSource implements LeftTupleSinkNode, NodeMemory {

    private LeftTupleSource tupleSource;

    private ConditionalBranchEvaluator branchEvaluator;

    protected boolean tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    public ConditionalBranchNode() { }

    public ConditionalBranchNode( int id,
                                  LeftTupleSource tupleSource,
                                  ConditionalBranchEvaluator branchEvaluator,
                                  BuildContext context ) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation());
        this.tupleSource = tupleSource;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        this.branchEvaluator = branchEvaluator;

        initMasks(context, tupleSource);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        tupleSource = (LeftTupleSource) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        branchEvaluator = (ConditionalBranchEvaluator) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(tupleSource);
        out.writeBoolean(tupleMemoryEnabled);
        out.writeObject(branchEvaluator);
    }

    public void attach( BuildContext context ) {
        this.tupleSource.addTupleSink(this, context);
        if (context == null) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
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

    public void networkUpdated(UpdateContext updateContext) {
        this.tupleSource.networkUpdated(updateContext);
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final ConditionalBranchMemory memory = (ConditionalBranchMemory) workingMemory.getNodeMemory( this );

        boolean breaking = false;
        ConditionalExecution conditionalExecution = branchEvaluator.evaluate( leftTuple, workingMemory, memory.context );

        if ( conditionalExecution != null ) {
            conditionalExecution.getSink().propagateAssertLeftTuple( leftTuple,
                                                                     context,
                                                                     workingMemory,
                                                                     this.tupleMemoryEnabled );
            breaking = conditionalExecution.isBreaking();
        }

        if ( !breaking ) {
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                this.tupleMemoryEnabled );
        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        if ( leftTuple.getFirstChild() != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final ConditionalBranchMemory memory = (ConditionalBranchMemory) workingMemory.getNodeMemory( this );
        boolean wasPropagated = leftTuple.getFirstChild() != null;

        ConditionalExecution conditionalExecution = branchEvaluator.evaluate( leftTuple, workingMemory, memory.context );

        if ( wasPropagated ) {
            LeftTupleSink mainSink = this.sink.getSinks()[0];
            LeftTupleSink oldSink = leftTuple.getFirstChild().getSink();

            if ( conditionalExecution != null ) {
                LeftTupleSink newSink = conditionalExecution.getSink().getSinks()[0];
                if ( oldSink.equals(newSink) ) {
                    // old and new propagation on the same branch sink -> modify
                    conditionalExecution.getSink().propagateModifyChildLeftTuple( leftTuple,
                                                                                  context,
                                                                                  workingMemory,
                                                                                  this.tupleMemoryEnabled );
                    if ( !conditionalExecution.isBreaking() ) {
                        this.sink.propagateAssertLeftTuple( leftTuple,
                                                            context,
                                                            workingMemory,
                                                            this.tupleMemoryEnabled );
                    }
                } else {
                    if ( oldSink.equals(mainSink) ) {
                        // old propagation on main sink
                        if ( conditionalExecution.isBreaking() ) {
                            // condition is breaking -> retract on main
                            this.sink.propagateRetractLeftTuple( leftTuple,
                                                                 context,
                                                                 workingMemory );
                        } else {
                            // condition not breaking -> also modify main
                            this.sink.propagateModifyChildLeftTuple( leftTuple,
                                                                     context,
                                                                     workingMemory,
                                                                     this.tupleMemoryEnabled );
                        }
                    } else {
                        // old propagation on branch sink -> retract
                        conditionalExecution.getSink().propagateRetractLeftTuple( leftTuple,
                                                                                  context,
                                                                                  workingMemory );
                   }

                    // new propagation on different branch sink -> assert
                    conditionalExecution.getSink().propagateAssertLeftTuple( leftTuple,
                                                                             context,
                                                                             workingMemory,
                                                                             this.tupleMemoryEnabled );
                    if ( !conditionalExecution.isBreaking() && !oldSink.equals(mainSink) ) {
                        this.sink.propagateAssertLeftTuple( leftTuple,
                                                            context,
                                                            workingMemory,
                                                            this.tupleMemoryEnabled );
                    }
                }
            } else {
                if ( oldSink.equals(mainSink) ) {
                    // old and new propagation on main sink -> modify
                    this.sink.propagateModifyChildLeftTuple( leftTuple,
                                                             context,
                                                             workingMemory,
                                                             this.tupleMemoryEnabled );
                } else {
                    // old propagation on branch sink -> retract
                    this.sink.propagateRetractLeftTuple( leftTuple,
                                                         context,
                                                         workingMemory );
                    // new propagation on main sink -> assert
                    this.sink.propagateAssertLeftTuple( leftTuple,
                                                        context,
                                                        workingMemory,
                                                        this.tupleMemoryEnabled );
                }
            }
        } else {
            // not propagated -> assert
            boolean breaking = false;
            if ( conditionalExecution != null ) {
                conditionalExecution.getSink().propagateAssertLeftTuple( leftTuple,
                                                                         context,
                                                                         workingMemory,
                                                                         this.tupleMemoryEnabled );
                breaking = conditionalExecution.isBreaking();
            }
            if ( !breaking ) {
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            }
        }
    }

    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[ConditionalBranchNode: cond=" + this.branchEvaluator + "]";
    }

    public int hashCode() {
        return this.tupleSource.hashCode() ^ this.branchEvaluator.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || object.getClass() != EvalConditionNode.class ) {
            return false;
        }

        final ConditionalBranchNode other = (ConditionalBranchNode) object;

        return this.tupleSource.equals( other.tupleSource ) && this.branchEvaluator.equals( other.branchEvaluator );
    }

    public Memory createMemory(final RuleBaseConfiguration config) {
        return new ConditionalBranchMemory( branchEvaluator.createContext() );
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        LeftTupleIterator it = LeftTupleIterator.iterator( workingMemory, this );

        for ( LeftTuple leftTuple =  ( LeftTuple ) it.next(); leftTuple != null; leftTuple =  ( LeftTuple ) it.next() ) {
            LeftTuple childLeftTuple = leftTuple.getFirstChild();
            while ( childLeftTuple != null ) {
                RightTuple rightParent = childLeftTuple.getRightParent();
                sink.assertLeftTuple( sink.createLeftTuple( leftTuple, rightParent, childLeftTuple, null, sink, true ),
                                      context,
                                      workingMemory );

                while ( childLeftTuple != null && childLeftTuple.getRightParent() == rightParent ) {
                    // skip to the next child that has a different right parent
                    childLeftTuple = childLeftTuple.getLeftParentNext();
                }
            }
        }
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }

        if ( !this.isInUse() ) {
            for( InternalWorkingMemory workingMemory : workingMemories ) {
                workingMemory.clearNodeMemory( this );
            }
        } else {
            throw new RuntimeException("ConditionalBranchNode cannot be shared");
        }

        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
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
        return NodeTypeEnums.ConditionalBranchNode;
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple,sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }

    public static class ConditionalBranchMemory
            implements
            Externalizable,
            Memory {

        private static final long serialVersionUID = 510l;

        public Object             context;

        public ConditionalBranchMemory() {

        }

        public ConditionalBranchMemory(final Object context) {
            this.context = context;
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            context = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }

        public short getNodeType() {
            return NodeTypeEnums.EvalConditionNode;
        }
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return tupleSource.getObjectTypeNode();
    }
}
