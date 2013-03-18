package org.kie.internal.builder;

import org.kie.api.builder.Message;

import java.util.List;

public interface IncrementalResults {
    List<Message> getAddedMessages();
    List<Message> getRemovedMessages();
}
