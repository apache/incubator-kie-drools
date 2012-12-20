package org.droolsjbpm.services.api;

import java.util.Map;

import org.kie.runtime.process.WorkItemHandler;

public interface WorkItemHandlerProducer {

    Map<String, WorkItemHandler> getWorkItemHandlers(String location, Map<String, Object> params);
}
