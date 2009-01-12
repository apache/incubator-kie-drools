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

    public void receive(Object object,
                        PipelineContext context) {
        context.setResult( object );

        emit( object,
              context );
    }

}
