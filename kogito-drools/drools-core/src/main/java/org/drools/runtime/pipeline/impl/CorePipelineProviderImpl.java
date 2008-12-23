package org.drools.runtime.pipeline.impl;

import org.drools.definition.pipeline.Adapter;
import org.drools.definition.pipeline.CorePipelineProvider;
import org.drools.definition.pipeline.Expression;
import org.drools.definition.pipeline.Splitter;
import org.drools.runtime.dataloader.impl.StatefulKnowledgeSessionReceiverAdapter;
import org.drools.runtime.dataloader.impl.StatelessKnowledgeSessionReceiverAdapter;

public class CorePipelineProviderImpl implements CorePipelineProvider {
    public Expression newMvelExpression(String expression) {
        return new MvelExpression(expression);
    }

    public Splitter newIterateSplitter() {
        return new IterateSplitter();
    }

    public Adapter newStatefulKnowledgeSessionReceiverAdapter() {
        return new StatefulKnowledgeSessionReceiverAdapter();
    }

    public Adapter newStatelessKnowledgeSessionReceiverAdapter() {
        return new StatelessKnowledgeSessionReceiverAdapter();
    }
}
