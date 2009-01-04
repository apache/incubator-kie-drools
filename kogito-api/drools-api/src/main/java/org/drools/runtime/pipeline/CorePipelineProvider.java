package org.drools.runtime.pipeline;

import java.util.List;

public interface CorePipelineProvider {
    public Expression newMvelExpression(String expression);
    
    public Action newMvelAction(String action);

    public Splitter newIterateSplitter();

    public Adapter newEntryPointReceiverAdapter();

    public Adapter newStatelessKnowledgeSessionReceiverAdapter();
    
    public ListAdapter newListAdapter(List<Object> list, boolean syncAccessors); 
    
    public Callable newCallable();
}
