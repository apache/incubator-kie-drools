package org.drools.runtime.pipeline;

import java.util.List;

public interface CorePipelineProvider {
    public Expression newMvelExpression(String expression);

    public Splitter newIterateSplitter();

    public Adapter newEntryPointReceiverAdapter();

    public Adapter newStatelessKnowledgeSessionReceiverAdapter();
    
    public ListAdapter newListAdapter(List<Object> list, boolean syncAccessors); 
    
    public Callable newCallable();
}
