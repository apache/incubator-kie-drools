package org.drools.runtime.pipeline.impl;

import java.util.List;

import org.drools.runtime.pipeline.ListAdapter;
import org.drools.runtime.pipeline.PipelineContext;

public class ListAdapterImpl extends BaseStage
    implements
    ListAdapter {
    private List<Object> list;
    private boolean      syncAccessor;

    public ListAdapterImpl(List<Object> list,
                           boolean syncAccessor) {
        super();
        this.list = list;
        this.syncAccessor = syncAccessor;
    }

    public List<Object> getList() {
        if ( this.syncAccessor ) {
            synchronized ( this ) {
                return list;
            }
        } else {
            return list;
        }
    }

    public void setList(List<Object> list) {
        if ( this.syncAccessor ) {
            synchronized ( this ) {
                this.list = list;
            }
        } else {
            this.list = list;
        }
    }

    public void receive(Object object,
                       PipelineContext context) {
        this.list.add( object );
    }

}
