package org.kie.services.signal;

import java.util.Optional;

public interface SignalableResolver {
    Optional<Signalable> find(long id);
}
