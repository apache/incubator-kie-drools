package org.kie.builder;

import java.util.List;

public interface Results {
    List<Message> getInsertedMessages();
    List<Message> getDeletedMessages();
}
