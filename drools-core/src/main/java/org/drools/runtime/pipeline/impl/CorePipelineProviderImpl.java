package org.drools.runtime.pipeline.impl;

import java.util.List;

import org.drools.runtime.dataloader.impl.EntryPointReceiverAdapter;
import org.drools.runtime.dataloader.impl.StatelessKnowledgeSessionReceiverAdapter;
import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.Adapter;
import org.drools.runtime.pipeline.Callable;
import org.drools.runtime.pipeline.CorePipelineProvider;
import org.drools.runtime.pipeline.Expression;
import org.drools.runtime.pipeline.Splitter;
import org.drools.runtime.pipeline.ListAdapter;

public class CorePipelineProviderImpl
    implements
    CorePipelineProvider {
    public Expression newMvelExpression(String expression) {
        return new MvelExpression( expression );
    }
    
    public Action newMvelAction(String action) {
        return new MvelAction( action );
    }    

    public Splitter newIterateSplitter() {
        return new IterateSplitter();
    }

    public Adapter newEntryPointReceiverAdapter() {
        return new EntryPointReceiverAdapter();
    }

    public Adapter newStatelessKnowledgeSessionReceiverAdapter() {
        return new StatelessKnowledgeSessionReceiverAdapter();
    }

    public ListAdapter newListAdapter(List<Object> list,
                                      boolean syncAccessors) {
        return new ListAdapterImpl( list,
                                    syncAccessors );
    }
    
    public Callable newCallable() {
        return new CallableImpl();
    }    
}
