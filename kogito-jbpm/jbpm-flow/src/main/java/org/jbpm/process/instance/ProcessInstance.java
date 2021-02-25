/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance;

import java.util.Date;
import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.workflow.instance.NodeInstance;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

/**
 * A process instance is the representation of a process during its execution.
 * It contains all the runtime status information about the running process.
 * A process can have multiple instances.
 */
public interface ProcessInstance extends KogitoProcessInstance,
        ContextInstanceContainer,
        ContextableInstance {

    void setId(String id);

    Process getProcess();

    void setProcess(Process process);

    void setState(int state);

    void setState(int state, String outcome);

    void setState(int state, String outcome, Object faultData);

    void setErrorState(NodeInstance nodeInstanceInError, Exception e);

    InternalKnowledgeRuntime getKnowledgeRuntime();

    void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime);

    void start();

    void start(String trigger);

    String getOutcome();

    void setParentProcessInstanceId(String parentId);

    void setRootProcessInstanceId(String parentId);

    void setRootProcessId(String processId);

    Map<String, Object> getMetaData();

    void setMetaData(String name, Object data);

    Object getFaultData();

    boolean isSignalCompletion();

    void setSignalCompletion(boolean signalCompletion);

    String getDeploymentId();

    void setDeploymentId(String deploymentId);

    Date getStartDate();

    void setStartDate(Date date);

    int getSlaCompliance();

    Date getSlaDueDate();

    void configureSLA();

    void setReferenceId(String referenceId);

    void disconnect();

    void reconnect();

    AgendaFilter getAgendaFilter();

    void setAgendaFilter(AgendaFilter agendaFilter);
}
