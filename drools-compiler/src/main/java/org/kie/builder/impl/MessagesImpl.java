package org.kie.builder.impl;

import org.kie.builder.Message;
import org.kie.builder.Messages;

import java.util.Collections;
import java.util.List;

public class MessagesImpl implements Messages {

    private List<Message> insertedMessages;
    private List<Message> deletedMessages;

    public MessagesImpl() { }

    public MessagesImpl(List<Message> insertedMessages, List<Message> deleteMessages) {
        this.insertedMessages = ( insertedMessages == null )  ? Collections.<Message>emptyList() : insertedMessages;
        this.deletedMessages = ( deleteMessages == null )  ? Collections.<Message>emptyList() : deleteMessages;
    }

    public List<Message> getInsertedMessages() {
        return insertedMessages;
    }

    public List<Message> getDeletedMessages() {
        return deletedMessages;
    }
    
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "Inserted Messages:\n");
        for ( Message msg : insertedMessages ) {
            sBuilder.append(  msg.toString() );
            sBuilder.append( "\n" );
        }
        
        sBuilder.append( "---\n" );
        sBuilder.append( "Deleted Messages:\n");
        for ( Message msg : deletedMessages ) {
            sBuilder.append(  msg.toString() );
            sBuilder.append( "\n" );
        }        
        return sBuilder.toString();
    }
}
