package org.kie.services.time.manager;

import java.util.Map;

import org.kie.api.time.SessionClock;
import org.kie.kogito.signal.SignalManager;

/**
 * A smaller API surface than StatefulSession/KieRuntime
 */
public interface TimerManagerRuntime {

    void startOperation();

    void endOperation();

    SessionClock getSessionClock();

    SignalManager getSignalManager();

    TimerManager getTimerManager();

    boolean isActive();

    void startProcess(String processId, Map<String, Object> paramaeters, String timer);

    long getIdentifier();
}
