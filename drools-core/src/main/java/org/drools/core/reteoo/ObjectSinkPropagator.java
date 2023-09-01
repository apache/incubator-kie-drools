package org.drools.core.reteoo;

import java.io.Externalizable;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.PropagationContext;

public interface ObjectSinkPropagator
    extends
    Externalizable {

    ObjectSinkPropagator addObjectSink(ObjectSink sink, int alphaNodeHashingThreshold, int alphaNodeRangeIndexThreshold);
    ObjectSinkPropagator removeObjectSink(ObjectSink sink);

    default void changeSinkPartition( ObjectSink sink, RuleBasePartitionId oldPartition, RuleBasePartitionId newPartition, int alphaNodeHashingThreshold, int alphaNodeRangeIndexThreshold ) { }

    void propagateAssertObject(InternalFactHandle factHandle,
                               PropagationContext context,
                               ReteEvaluator reteEvaluator);

    BaseNode getMatchingNode(BaseNode candidate);

    ObjectSink[] getSinks();

    int size();
    boolean isEmpty();

    void propagateModifyObject(InternalFactHandle factHandle,
                               ModifyPreviousTuples modifyPreviousTuples,
                               PropagationContext context,
                               ReteEvaluator reteEvaluator);
    
    void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                 final ModifyPreviousTuples modifyPreviousTuples,
                                 final PropagationContext context,
                                 final ReteEvaluator reteEvaluator);
    
    void doLinkRiaNode(ReteEvaluator reteEvaluator);

    void doUnlinkRiaNode(ReteEvaluator reteEvaluator);

}
