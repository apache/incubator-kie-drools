package org.kie.internal.builder;

import org.kie.builder.Message;

import java.util.List;

public interface IncrementalResults {
    List<Message> getAddedMessages();
    List<Message> getRemovedMessages();
}
