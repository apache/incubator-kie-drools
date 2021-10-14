/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.process;

import java.util.Map;

import org.kie.api.runtime.process.ProcessInstance;

/**
 * Classes that implement this interface will provide additional capabilities
 * based on correlation. Most important to allow users to define custom
 * correlation keys as an alternative to considered internal process instance id.
 */
public interface CorrelationAwareProcessRuntime {

    /**
     * Start a new process instance.  The process (definition) that should
     * be used is referenced by the given process id.  Parameters can be passed
     * to the process instance (as name-value pairs), and these will be set
     * as variables of the process instance.
     *
     * @param processId  the id of the process that should be started
     * @param correlationKey custom correlation key that can be used to identify process instance
     * @param parameters  the process variables that should be set when starting the process instance
     * @return the <code>ProcessInstance</code> that represents the instance of the process that was started
     */
    ProcessInstance startProcess(String processId, CorrelationKey correlationKey,
                                 Map<String, Object> parameters);

    /**
     * Creates a new process instance (but does not yet start it).  The process
     * (definition) that should be used is referenced by the given process id.
     * Parameters can be passed to the process instance (as name-value pairs),
     * and these will be set as variables of the process instance.  You should only
     * use this method if you need a reference to the process instance before actually
     * starting it.  Otherwise, use startProcess.
     *
     * @param processId  the id of the process that should be started
     * @param correlationKey custom correlation key that can be used to identify process instance
     * @param parameters  the process variables that should be set when creating the process instance
     * @return the <code>ProcessInstance</code> that represents the instance of the process that was created (but not yet started)
     */
    ProcessInstance createProcessInstance(String processId, CorrelationKey correlationKey,
                                          Map<String, Object> parameters);

    /**
     * Starts a process with the values supplied from the current nodes
     * 
     * @param processId The process's identifier
     * @param correlationKey correlation key to be assigned to process instance - must be unique 
     * @param params process variables
     * @param nodeIds list of unique node ids that would be triggered once the process is created
     * @return process instance identifier
     * @throws RuntimeException in case of encountered errors
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist
     * @throws DeploymentNotActiveException in case deployment with given deployment id is not active
     */
    ProcessInstance startProcessFromNodeIds(String processId, CorrelationKey key, Map<String, Object> params, String... nodeIds);

    /**
     * Returns the process instance with the given correlationKey.  Note that only active process instances
     * will be returned.  If a process instance has been completed already, this method will return
     * <code>null</code>.
     *
     * @param correlationKey the custom correlation key assigned when process instance was created
     * @return the process instance with the given id or <code>null</code> if it cannot be found
     */
    ProcessInstance getProcessInstance(CorrelationKey correlationKey);
}
