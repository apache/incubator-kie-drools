/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.runtime.process;

import org.drools.definition.process.Process;

/**
 * A process instance represents one specific instance of a process
 * that is currently executing.  Whenever a process is started, a
 * process instance is created that represents that specific instance
 * that was started.  It contains all runtime information related to
 * that instance.  Multiple process instances of the same process
 * can be executed simultaneously.
 * 
 * For example, consider a process definition that describes how to
 * process a purchase order.  Whenever a new purchase order comes in,
 * a new process instance will be created for that purchase order.
 * Multiple process instances (one for each purchase order) can coexist.
 * 
 *  A process instance is uniquely identified by an id.
 *  
 *  This class can be extended to represent one specific type of process,
 *  e.g. <code>WorkflowProcessInstance</code> when using a <code>WorkflowProcess</code>
 *  where the process logic is expressed as a flow chart.
 *  
 *  @see org.drools.core.runtime.process.WorkflowProcessInstance
 */
public interface ProcessInstance
    extends
    EventListener {

    int STATE_PENDING   = 0;
    int STATE_ACTIVE    = 1;
    int STATE_COMPLETED = 2;
    int STATE_ABORTED   = 3;
    int STATE_SUSPENDED = 4;

    /**
     * The id of the process definition that is related to this process instance.
     * @return the id of the process definition that is related to this process instance
     */
    String getProcessId();
    
    Process getProcess();

    /**
     * The unique id of this process instance.
     * @return the unique id of this process instance
     */
    long getId();

    /**
     * The name of the process definition that is related to this process instance.
     * @return the name of the process definition that is related to this process instance
     */
    String getProcessName();

    /**
     * The state of the process instance.
     * @return the state of the process instance
     */
    int getState();
    
}
