package org.jbpm.runtime.manager.api;

import java.util.List;
import java.util.Map;

public interface EventListenerProducer<T> {

    List<T> getEventListeners(String identifier, Map<String, Object>  params);
}
