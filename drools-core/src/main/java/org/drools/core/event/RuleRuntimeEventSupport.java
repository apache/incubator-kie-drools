package org.drools.core.event;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.event.rule.impl.ObjectDeletedEventImpl;
import org.drools.core.event.rule.impl.ObjectInsertedEventImpl;
import org.drools.core.event.rule.impl.ObjectUpdatedEventImpl;
import org.drools.core.common.PropagationContext;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;

public class RuleRuntimeEventSupport extends AbstractEventSupport<RuleRuntimeEventListener> {

    public void fireObjectInserted(final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object object,
                                   final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ObjectInsertedEventImpl event = new ObjectInsertedEventImpl( asKieRuntime(reteEvaluator), propagationContext, handle, object );
            notifyAllListeners( event, ( l, e ) -> l.objectInserted( e ) );
        }
    }

    public void fireObjectUpdated(final PropagationContext propagationContext,
                                  final FactHandle handle,
                                  final Object oldObject,
                                  final Object object,
                                  final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ObjectUpdatedEventImpl event = new ObjectUpdatedEventImpl( asKieRuntime(reteEvaluator), propagationContext, handle, oldObject, object );
            notifyAllListeners( event, ( l, e ) -> l.objectUpdated( e ) );
        }
    }

    public void fireObjectRetracted(final PropagationContext propagationContext,
                                    final FactHandle handle,
                                    final Object oldObject,
                                    final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ObjectDeletedEventImpl event = new ObjectDeletedEventImpl( asKieRuntime(reteEvaluator), propagationContext, handle, oldObject );
            notifyAllListeners( event, ( l, e ) -> l.objectDeleted( e ) );
        }
    }

    private KieRuntime asKieRuntime(ReteEvaluator reteEvaluator) {
        return reteEvaluator instanceof InternalWorkingMemory ? ((InternalWorkingMemory) reteEvaluator).getKnowledgeRuntime() : null;
    }
}
