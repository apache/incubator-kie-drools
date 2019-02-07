package org.jbpm.process.instance;

import java.util.Map;

import org.jbpm.process.instance.event.SignalManager;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.process.instance.timer.TimerManagerRuntime;
import org.kie.api.time.SessionClock;

class LightTimerManagerRuntime implements TimerManagerRuntime {

    private LightProcessRuntime lightProcessRuntime;

    public LightTimerManagerRuntime(LightProcessRuntime lightProcessRuntime) {
        this.lightProcessRuntime = lightProcessRuntime;
    }

    @Override
    public void startOperation() {

    }

    @Override
    public void endOperation() {

    }

    @Override
    public SessionClock getSessionClock() {
        return null;
    }

    @Override
    public SignalManager getSignalManager() {
        return lightProcessRuntime.getSignalManager();
    }

    @Override
    public TimerManager getTimerManager() {
        return lightProcessRuntime.getTimerManager();
    }

    @Override
    public boolean isActive() {
        return lightProcessRuntime.isActive();
    }

    @Override
    public void startProcess(String processId, Map<String, Object> paramaeters, String timer) {
        lightProcessRuntime.startProcess(processId, paramaeters, timer);
    }

    ;
}
