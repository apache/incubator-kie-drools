package org.kie.builder;

import java.util.List;

public interface Messages {
    List<Message> getInsertedMessages();
    List<Message> getDeletedMessages();
}
