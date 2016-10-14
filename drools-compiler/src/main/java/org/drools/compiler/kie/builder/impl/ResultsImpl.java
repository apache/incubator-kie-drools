/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.List;

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

    public MessageImpl addMessage(KnowledgeBuilderResult result) {
        MessageImpl message = new MessageImpl( idGenerator++, result );
        messages.add( message );
        return message;
    }

    public MessageImpl addMessage(Level level, String path, String text) {
        MessageImpl message = new MessageImpl( idGenerator++, level, path, text );
        messages.add( message );
        return message;
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
