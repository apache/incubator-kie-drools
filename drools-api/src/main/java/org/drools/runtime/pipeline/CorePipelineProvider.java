package org.drools.runtime.pipeline;

public interface CorePipelineProvider {
    public Expression newMvelExpression(String expression);

    public Splitter newIterateSplitter();

    public Adapter newEntryPointReceiverAdapter();

    public Adapter newStatelessKnowledgeSessionReceiverAdapter();
}
