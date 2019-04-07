/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.archive.ejbtransactions;

import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.task.TaskService;


public abstract class TaskProcessScenario extends ProcessScenario {
    protected TaskService taskService;

    /**
     * Constructor with mandatory parameters:
     * 
     * @param kbase
     *            KnowledgeBase instance containing the process definition
     * @param processId
     *            ID of process to be tested
     * @param params
     *            Parameters which will be passed to process instance.
     */
    public TaskProcessScenario(TaskService ts, KieBase kbase, String processId, Map<String, Object> params) {
        super(kbase, processId, params);
        this.taskService = ts;
    }

    public TaskProcessScenario(TaskService ts, KieBase kbase, String processId, Map<String, Object> params,
            ProcessEventListener l) {
        super(kbase, processId, params, l);
        this.taskService = ts;
    }

}
