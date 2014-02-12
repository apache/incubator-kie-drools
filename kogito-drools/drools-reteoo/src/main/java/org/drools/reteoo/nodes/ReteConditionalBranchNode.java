package org.drools.reteoo.nodes;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleIterator;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchEvaluator.ConditionalExecution;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.LeftTupleSourceUtils;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Iterator;

public class ReteConditionalBranchNode extends ConditionalBranchNode {

    public ReteConditionalBranchNode() {
    }

    public ReteConditionalBranchNode(int id, LeftTupleSource tupleSource, ConditionalBranchEvaluator branchEvaluator, BuildContext context) {
        super(id, tupleSource, branchEvaluator, context);
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               (LeftTupleSink) this, getLeftInputOtnId(), getLeftInferredMask());
    }

    public void attach( BuildContext context ) {
        super.attach( context );
        if (context == null) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION,
                                                                                               null, null, null);
            getLeftTupleSource().updateSink(this, propagationContext, workingMemory);
        }
    }


    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final ConditionalBranchMemory memory = (ConditionalBranchMemory) workingMemory.getNodeMemory( this );

        boolean breaking = false;
        ConditionalExecution conditionalExecution = branchEvaluator.evaluate( leftTuple, workingMemory, memory.context );

        if ( conditionalExecution != null ) {
            boolean useLeftMemory = true;
            if ( !this.tupleMemoryEnabled ) {
                // This is a hack, to not add closed DroolsQuery objects
                Object object = leftTuple.get( 0 ).getObject();
                if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                    useLeftMemory = false;
                }
            }

            conditionalExecution.getSink().propagateAssertLeftTuple( leftTuple,
                                                                     context,
                                                                     workingMemory,
                                                                     useLeftMemory );
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

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        Iterator<LeftTuple> it = LeftTupleIterator.iterator(workingMemory, this);

        for ( LeftTuple leftTuple =  it.next(); leftTuple != null; leftTuple = it.next() ) {
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
                            final InternalWorkingMemory[] workingMemories) {
        if ( !this.isInUse() ) {
            for( InternalWorkingMemory workingMemory : workingMemories ) {
                workingMemory.clearNodeMemory( this );
            }
            getLeftTupleSource().removeTupleSink(this);
        } else {
            throw new RuntimeException("ConditionalBranchNode cannot be shared");
        }
    }

}
