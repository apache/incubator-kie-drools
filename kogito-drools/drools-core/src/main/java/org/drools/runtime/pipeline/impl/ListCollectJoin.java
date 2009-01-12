package org.drools.runtime.pipeline.impl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.pipeline.Join;
import org.drools.runtime.pipeline.PipelineContext;

public class ListCollectJoin extends BaseEmitter implements Join {
    private Map<PipelineContext, List> lists;
    
    public ListCollectJoin() {
        lists = new IdentityHashMap<PipelineContext, List>();
    }
    
    public void receive(Object object,
                        PipelineContext context) {
        List list = lists.get( context );
        if ( list == null ) {
            list = new ArrayList();
            lists.put( context, list );
        }
        list.add( object );
    }
    
    public void completed(PipelineContext context) {
        emit( lists.remove( context ), context);
    }    
}
