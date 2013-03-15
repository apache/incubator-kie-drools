package org.drools.compiler.kie.builder.impl;

import org.kie.builder.IncrementalResults;
import org.kie.builder.KnowledgeBuilderResult;
import org.kie.builder.Message;

import java.util.ArrayList;
import java.util.List;

public class IncrementalResultsImpl implements IncrementalResults {

    private long          idGenerator = 1L;

    private List<Message> addedMessages = new ArrayList<Message>();
    private List<Message> removedMessages = new ArrayList<Message>();

    @Override
    public List<Message> getAddedMessages() {
        return addedMessages;
    }

    @Override
    public List<Message> getRemovedMessages() {
        return removedMessages;
    }

    public void addMessage(KnowledgeBuilderResult result) {
        addedMessages.add( new MessageImpl( idGenerator++, result ) );
    }

    public void removeMessage(KnowledgeBuilderResult result) {
        removedMessages.add( new MessageImpl( idGenerator++, result ) );
    }
}
