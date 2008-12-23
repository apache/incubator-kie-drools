package org.drools.runtime.pipeline.impl;

import org.drools.definition.pipeline.Splitter;
import org.drools.runtime.pipeline.PipelineContext;

public class IterateSplitter extends BaseEmitter
    implements
    Splitter {

    public void signal(Object object,
                       PipelineContext context) {
        if ( object instanceof Iterable ) {
            for ( Object result : ((Iterable) object) ) {
                emit( result,
                      context );
            }
        } else {
            emit( object,
                  context );
        }
    }

}
