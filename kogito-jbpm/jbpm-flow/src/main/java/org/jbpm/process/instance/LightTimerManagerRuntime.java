/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance;

import java.util.Map;

import org.kie.services.signal.SignalManager;
import org.kie.services.time.manager.TimerManager;
import org.kie.services.time.manager.TimerManagerRuntime;
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

    @Override
    public long getIdentifier() {
        return 0;
    }

    ;
}
