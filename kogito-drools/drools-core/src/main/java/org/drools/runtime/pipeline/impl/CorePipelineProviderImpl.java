package org.drools.runtime.pipeline.impl;

import org.drools.runtime.dataloader.impl.EntryPointReceiverAdapter;
import org.drools.runtime.dataloader.impl.StatelessKnowledgeSessionReceiverAdapter;
import org.drools.runtime.pipeline.Adapter;
import org.drools.runtime.pipeline.CorePipelineProvider;
import org.drools.runtime.pipeline.Expression;
import org.drools.runtime.pipeline.Splitter;

public class CorePipelineProviderImpl implements CorePipelineProvider {
    public Expression newMvelExpression(String expression) {
        return new MvelExpression(expression);
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
}
