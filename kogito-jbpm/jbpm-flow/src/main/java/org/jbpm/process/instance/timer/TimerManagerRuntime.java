package org.jbpm.process.instance.timer;

import java.util.Map;

import org.jbpm.process.instance.event.SignalManager;
import org.kie.api.time.SessionClock;

public interface TimerManagerRuntime {

    void startOperation();

    void endOperation();

    SessionClock getSessionClock();

    SignalManager getSignalManager();

    TimerManager getTimerManager();

    boolean isActive();

    void startProcess(String processId, Map<String, Object> paramaeters, String timer);
}
