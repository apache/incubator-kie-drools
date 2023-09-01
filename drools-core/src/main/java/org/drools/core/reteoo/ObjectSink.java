package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.PropagationContext;

/**
 * Receiver of propagated <code>FactHandleImpl</code>s from a
 * <code>ObjectSource</code>.
 * 
 * @see ObjectSource
 */
public interface ObjectSink
    extends
    Sink {

    void assertObject(InternalFactHandle factHandle,
                      PropagationContext propagationContext,
                      ReteEvaluator reteEvaluator);

    void modifyObject(InternalFactHandle factHandle,
                      ModifyPreviousTuples modifyPreviousTuples,
                      PropagationContext context,
                      ReteEvaluator reteEvaluator);
    
    void byPassModifyToBetaNode (InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator);
}
