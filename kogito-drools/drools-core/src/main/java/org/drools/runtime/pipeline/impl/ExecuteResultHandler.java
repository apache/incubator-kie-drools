package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;

public class ExecuteResultHandler extends BaseEmitter
    implements
    Action {

    public void handleResult(PipelineContext context, Object object) {
        context.getResultHandler().handleResult(context.getResult());
        try {
            context.getResultHandler().handleResult(context.getResult());
        } catch (Exception e) {
            handleException(this, object, e);
        }
    }

    public void receive(Object object,
                        PipelineContext context) {
        handleResult(context, object);
        emit( object,
              context );
    }

}
