package org.kie.internal.fluent;

import java.util.Map;

public interface RequestContext {
    long getRequestId();

    long getConversationId();

    Map<String, Object> getOut();
}
