package org.kie.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.commons.jci.problems.CompilationProblem;
import org.kie.builder.KnowledgeBuilderResult;
import org.kie.builder.Message;
import org.kie.builder.Message.Level;

public class Messages {
    private List<Message>        messages = new ArrayList<Message>();

    private long                 idGenerator = 1L;

    public List<Message> getMessages() {
        return messages;
    }
    
    public void addMessage(CompilationProblem problem) {
        messages.add( new MessageImpl( idGenerator++, problem) );
    }
    
    public void addMessage(KnowledgeBuilderResult result) {
        messages.add( new MessageImpl( idGenerator++, result) );
    }        

    public void addMessage(Level level, String path, String text) {
        messages.add( new MessageImpl( idGenerator++, level, path, text)  );
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public long getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(long idGenerator) {
        this.idGenerator = idGenerator;
    }

    public List<Message> filterMessages(Level... levels) {
        return MessageImpl.filterMessages(messages, levels);
    }
}