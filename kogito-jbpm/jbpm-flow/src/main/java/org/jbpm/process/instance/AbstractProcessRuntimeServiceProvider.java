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

import java.util.Optional;

import org.jbpm.process.instance.impl.DefaultProcessInstanceManager;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.services.signal.LightSignalManager;
import org.kie.services.signal.SignalManager;
import org.kie.services.time.TimerService;
import org.kie.submarine.process.WorkItemHandlerConfig;

public class AbstractProcessRuntimeServiceProvider implements ProcessRuntimeServiceProvider {

    private final TimerService timerService;
    private final ProcessInstanceManager processInstanceManager;
    private final SignalManager signalManager;
    private final WorkItemManager workItemManager;

    public AbstractProcessRuntimeServiceProvider(
            TimerService timerService,
            WorkItemHandlerConfig workItemHandlerProvider) {
        processInstanceManager = new DefaultProcessInstanceManager();
        signalManager = new LightSignalManager(
                id -> Optional.ofNullable(
                        processInstanceManager.getProcessInstance(id)));
        this.timerService = timerService;
        this.workItemManager = new LightWorkItemManager(processInstanceManager, signalManager);

        for (String workItem : workItemHandlerProvider.names()) {
            workItemManager.registerWorkItemHandler(
                    workItem, workItemHandlerProvider.forName(workItem));
        }
    }

    @Override
    public TimerService getTimerService() {
        return timerService;
    }

    @Override
    public ProcessInstanceManager getProcessInstanceManager() {
        return processInstanceManager;
    }

    @Override
    public SignalManager getSignalManager() {
        return signalManager;
    }

    @Override
    public WorkItemManager getWorkItemManager() {
        return workItemManager;
    }
}
