package org.drools.compiler.kie.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.builder.Message;
import org.kie.builder.Message.Level;
import org.kie.builder.Results;

public class ResultsImpl
    implements
    Results {
    private List<Message> messages    = new ArrayList<Message>();

    private long          idGenerator = 1L;

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean hasMessages(Level... levels) {
        return !filterMessages( levels ).isEmpty();
    }

    @Override
    public List<Message> getMessages(Level... levels) {
        return filterMessages( levels );
    }

    public void addMessage(CompilationProblem problem) {
        messages.add( new MessageImpl( idGenerator++,
                                       problem ) );
    }

    public void addMessage(KnowledgeBuilderResult result) {
        messages.add( new MessageImpl( idGenerator++,
                                       result ) );
    }

    public void addMessage(Level level,
                           String path,
                           String text) {
        messages.add( new MessageImpl( idGenerator++,
                                       level,
                                       path,
                                       text ) );
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
        return MessageImpl.filterMessages( messages,
                                           levels );
    }

    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "Error Messages:\n" );
        for ( Message msg : filterMessages( Level.ERROR ) ) {
            sBuilder.append( msg.toString() );
            sBuilder.append( "\n" );
        }

        sBuilder.append( "---\n" );
        sBuilder.append( "Warning Messages:\n" );
        for ( Message msg : filterMessages( Level.WARNING ) ) {
            sBuilder.append( msg.toString() );
            sBuilder.append( "\n" );
        }
        
        sBuilder.append( "---\n" );
        sBuilder.append( "Info Messages:\n" );
        for ( Message msg : filterMessages( Level.INFO ) ) {
            sBuilder.append( msg.toString() );
            sBuilder.append( "\n" );
        }        
        return sBuilder.toString();
    }

}
