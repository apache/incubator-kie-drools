package org.kie.builder.impl;

import org.kie.builder.Message;
import org.kie.builder.Messages;

import java.util.List;

public class MessagesImpl implements Messages {

    private List<Message> insertedMessages;
    private List<Message> deletedMessages;

    public MessagesImpl() { }

    public MessagesImpl(List<Message> insertedMessages) {
        this.insertedMessages = insertedMessages;
    }

    public List<Message> getInsertedMessages() {
        return insertedMessages;
    }

    public List<Message> getDeletedMessages() {
        return deletedMessages;
    }
}
