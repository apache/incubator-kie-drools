package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.Callable;
import org.drools.runtime.pipeline.PipelineContext;

public class CallableImpl<E> extends BaseEmitter
    implements
    Callable {
    public static final String key = "__CallableReturnValue__";

    public CallableImpl() {
        super();
    }

    public Object call(Object object,
                       PipelineContext context) {
        emit( object,
              context );
        return context.getProperties().remove( key );
    }

    public void receive(Object object,
                       PipelineContext context) {
        context.getProperties().put( key,
                                     object );

    }

}
