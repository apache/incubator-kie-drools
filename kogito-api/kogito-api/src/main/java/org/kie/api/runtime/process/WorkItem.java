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

package org.kie.api.runtime.process;

import java.util.Date;
import java.util.Map;

import org.kie.kogito.process.workitem.Policy;

/**
 * Represents one unit of work that needs to be executed.  It contains
 * all the information that it necessary to execute this unit of work
 * as parameters, and (possibly) results related to its execution.
 *
 * WorkItems represent a unit of work in an abstract, high-level and
 * implementation-independent manner.  They are created by the engine
 * whenever an external task needs to be performed.  The engine will
 * delegate the work item to the appropriate <code>WorkItemHandler</code>
 * for execution.  Whenever a work item is completed (or whenever the work
 * item cannot be executed and should be aborted), the work item manager
 * should be notified.
 *
 * For example, a work item could be created whenever an email needs to
 * be sent.  This work item would have a name that represents the type of
 * work that needs to be executed (e.g. "Email") and parameters related to
 * its execution (e.g. "From" = "me@mail.com", "To" = ..., "Body" = ..., ...).
 * Result parameters can contain results related to the execution of this
 * work item (e.g. "Success" = true).
 *
 * @see org.kie.api.runtime.process.WorkItemHandler
 * @see org.kie.api.runtime.process.WorkItemManager
 */
public interface WorkItem {

    int PENDING   = 0;
    int ACTIVE    = 1;
    int COMPLETED = 2;
    int ABORTED   = 3;

    /**
     * The unique id of this work item
     * @return the id of this work item
     */
    String getId();

    /**
     * The name of the work item.  This represents the type
     * of work that should be executed.
     * @return the name of the work item
     */
    String getName();

    /**
     * The state of the work item.
     * @return the state of the work item
     */
    int getState();

    /**
     * Returns the value of the parameter with the given name.  Parameters
     * can be used to pass information necessary for the execution of this
     * work item.  Returns <code>null</code> if the parameter cannot be found.
     *
     * @param name the name of the parameter
     * @return the value of the parameter
     */
    Object getParameter(String name);

    /**
     * Returns the map of parameters of this work item.  Parameters
     * can be used to pass information necessary for the execution
     * of this work item.
     *
     * @return the map of parameters of this work item
     */
    Map<String, Object> getParameters();

    /**
     * Returns the value of the result parameter with the given name.  Result parameters
     * can be used to pass information related the result of the execution of this
     * work item.  Returns <code>null</code> if the result cannot be found.
     *
     * @param name the name of the result parameter
     * @return the value of the result parameter
     */
    Object getResult(String name);

    /**
     * Returns the map of result parameters of this work item.  Result parameters
     * can be used to pass information related the result of the execution of this
     * work item.
     *
     * @return the map of results of this work item
     */
    Map<String, Object> getResults();

    /**
     * The id of the process instance that requested the execution of this
     * work item
     *
     * @return the id of the related process instance
     */
    String getProcessInstanceId();
    
    /**
     * Returns optional life cycle phase id associated with this work item
     * @return optional life cycle phase id
     */
    String getPhaseId();
    
    /**
     * Returns optional life cycle phase status associated with this work item 
     * @return optional life cycle phase status
     */
    String getPhaseStatus();
    
    /**
     * Returns timestamp indicating the start date of this work item
     * @return start date
     */
    Date getStartDate();
    
    /**
     * Returns timestamp indicating the completion date of this work item
     * @return completion date
     */
    Date getCompleteDate(); 
    
    /**
     * The node instance that is associated with this
     * work item
     *
     * @return the related node instance
     */
    NodeInstance getNodeInstance();

    /**
     * The process instance that requested the execution of this
     * work item
     *
     * @return the related process instance
     */
    ProcessInstance getProcessInstance();
    
    /**
     * Enforces given policies on this work item. It must false in case of any policy 
     * violations. 
     * @param policies optional policies to be enforced
     * @return return true if this work item can enforce all policies otherwise false
     */
    default boolean enforce(Policy<?>...policies) {
        return true;
    }

}
