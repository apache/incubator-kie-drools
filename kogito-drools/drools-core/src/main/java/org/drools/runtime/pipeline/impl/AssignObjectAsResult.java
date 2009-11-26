package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;

public class AssignObjectAsResult extends BaseEmitter
    implements
    Action {

    public AssignObjectAsResult() {
        super();
    }

    public void assignResult(PipelineContext context, Object object) {
        context.setResult(object);
    }

    public void receive(Object object,
                        PipelineContext context) {
        assignResult( context , object);

        emit( object,
              context );
    }

}
