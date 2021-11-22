/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.process.bpmn2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;

public class BpmnProcesses implements Processes {

    private Map<String, Process<? extends Model>> mappedProcesses = new HashMap<>();

    public BpmnProcesses addProcess(Process<? extends Model> process) {
        mappedProcesses.put(process.id(), process);
        return this;
    }

    @Override
    public Process<? extends Model> processById(String processId) {
        return mappedProcesses.get(processId);
    }

    @Override
    public Collection<String> processIds() {
        return mappedProcesses.keySet();
    }
}
