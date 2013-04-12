package org.jbpm.runtime.manager.api;

import java.util.Map;

import org.kie.api.runtime.process.WorkItemHandler;

public interface WorkItemHandlerProducer {

    Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params);
}
