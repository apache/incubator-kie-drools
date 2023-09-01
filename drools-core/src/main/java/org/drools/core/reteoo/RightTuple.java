package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.PropagationContext;

public interface RightTuple extends Tuple {

    LeftTuple getBlocked();
    void setBlocked( LeftTuple leftTuple );
    void addBlocked( LeftTuple leftTuple );
    void removeBlocked( LeftTuple leftTuple );

    LeftTuple getTempBlocked();
    void setTempBlocked( LeftTuple tempBlocked );

    RightTuple getTempNextRightTuple();
    void setTempNextRightTuple( RightTuple tempNextRightTuple );

    InternalFactHandle getFactHandleForEvaluation();

    void retractTuple( PropagationContext context, ReteEvaluator reteEvaluator );

    void setExpired( ReteEvaluator reteEvaluator, PropagationContext pctx );
}