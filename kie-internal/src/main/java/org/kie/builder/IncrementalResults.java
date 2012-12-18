package org.kie.builder;

import java.util.List;

public interface IncrementalResults {
    List<Message> getAddedMessages();
    List<Message> getRemovedMessages();
}
