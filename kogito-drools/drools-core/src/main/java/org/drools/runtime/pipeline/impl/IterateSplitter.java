package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.Join;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Splitter;

public class IterateSplitter extends BaseEmitter
    implements
    Splitter {
    
    private Join join;

    public void receive(Object object,
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
        
        if ( this.join != null ) {
            this.join.completed( context);
        }
    }

    public void setJoin(Join join) {
        this.join = join;
    }

}
