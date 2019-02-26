package org.kie.services.signal;

import java.util.Optional;

import org.kie.api.runtime.process.EventListener;

public interface EventListenerResolver {
    Optional<EventListener> find(long id);
}
