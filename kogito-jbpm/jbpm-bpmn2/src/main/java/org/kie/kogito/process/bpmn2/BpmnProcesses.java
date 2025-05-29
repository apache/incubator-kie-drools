/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.process.bpmn2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcess;

public class BpmnProcesses implements Processes {

    private Map<String, Process<? extends Model>> mappedProcesses = new HashMap<>();

    private ProcessInstancesFactory processInstancesFactory;

    public void setProcessInstancesFactory(ProcessInstancesFactory processInstancesFactory) {
        this.processInstancesFactory = processInstancesFactory;
    }

    public BpmnProcesses addProcess(Process<? extends Model> process) {
        mappedProcesses.put(process.id(), process);
        ((AbstractProcess) process).setProcessInstancesFactory(processInstancesFactory);
        process.activate();
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

    @Override
    public int hashCode() {
        return Objects.hash(mappedProcesses.keySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BpmnProcesses other = (BpmnProcesses) obj;
        return Objects.equals(mappedProcesses, other.mappedProcesses);
    }

}
