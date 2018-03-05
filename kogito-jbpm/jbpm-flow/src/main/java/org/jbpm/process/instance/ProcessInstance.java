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

package org.jbpm.process.instance;

import java.util.Date;
import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.kie.api.definition.process.Process;

/**
 * A process instance is the representation of a process during its execution.
 * It contains all the runtime status information about the running process.
 * A process can have multiple instances.
 * 
 */
public interface ProcessInstance extends org.kie.api.runtime.process.ProcessInstance, ContextInstanceContainer, ContextableInstance {

    void setId(long id);

    void setProcess(Process process);

    Process getProcess();   

    void setState(int state);
    
    void setState(int state, String outcome);
    
    void setState(int state, String outcome, Object faultData);
    
    void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime);
    
    InternalKnowledgeRuntime getKnowledgeRuntime();

    void start();
    
    void start(String tigger);
    
    String getOutcome();
    
    void setParentProcessInstanceId(long parentId);
    
    Map<String, Object> getMetaData();

	Object getFaultData();
	
	void setSignalCompletion(boolean signalCompletion);
	
	boolean isSignalCompletion();
	
	String getDeploymentId();
	
	void setDeploymentId(String deploymentId);
	
	Date getStartDate();

	int getSlaCompliance();
	
	Date getSlaDueDate();
	
	void configureSLA();
    
}
