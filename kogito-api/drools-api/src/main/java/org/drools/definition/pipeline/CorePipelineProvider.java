package org.drools.definition.pipeline;

public interface CorePipelineProvider {
    public Expression newMvelExpression(String expression);

    public Splitter newIterateSplitter();

    public Adapter newStatefulKnowledgeSessionReceiverAdapter();

    public Adapter newStatelessKnowledgeSessionReceiverAdapter();
}
