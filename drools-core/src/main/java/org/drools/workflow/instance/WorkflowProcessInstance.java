package org.drools.workflow.instance;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.drools.Agenda;
import org.drools.WorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItemListener;
import org.drools.workflow.core.WorkflowProcess;

/**
 * A process instance for a RuleFlow process.
 * Contains a reference to all its node instances, and the agenda that
 * is controlling the RuleFlow process.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkflowProcessInstance extends ProcessInstance, NodeInstanceContainer {

    WorkflowProcess getWorkflowProcess();

    WorkingMemory getWorkingMemory();

    Agenda getAgenda();
    
    void addWorkItemListener(WorkItemListener listener);

    void removeWorkItemListener(WorkItemListener listener);

}