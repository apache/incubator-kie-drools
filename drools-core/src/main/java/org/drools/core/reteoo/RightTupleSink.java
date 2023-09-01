package org.drools.core.reteoo;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.PropagationContext;

public interface RightTupleSink extends Sink {

    void retractRightTuple(final RightTuple rightTuple,
                           final PropagationContext context,
                           final ReteEvaluator reteEvaluator);
    
    void modifyRightTuple(final RightTuple rightTuple,
                          final PropagationContext context,
                          final ReteEvaluator reteEvaluator);

    ObjectTypeNode.Id getRightInputOtnId();
}
