package org.drools.compiler.kie.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class IncrementalResultsImpl implements IncrementalResults {

    private long          idGenerator = 1L;

    private List<Message> addedMessages = new ArrayList<>();
    private List<Message> removedMessages = new ArrayList<>();

    @Override
    public List<Message> getAddedMessages() {
        return addedMessages;
    }

    @Override
    public List<Message> getRemovedMessages() {
        return removedMessages;
    }

    public void addMessage(KnowledgeBuilderResult result, String kieBaseName ) {
        addedMessages.add(result.asMessage(idGenerator++).setKieBaseName(kieBaseName));
    }

    public void removeMessage(KnowledgeBuilderResult result, String kieBaseName) {
        removedMessages.add(result.asMessage(idGenerator++).setKieBaseName(kieBaseName));
    }
}
