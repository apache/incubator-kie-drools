package org.kie.internal.builder;

import java.util.List;

import org.kie.api.builder.Message;

public interface IncrementalResults {
    List<Message> getAddedMessages();
    List<Message> getRemovedMessages();
}
