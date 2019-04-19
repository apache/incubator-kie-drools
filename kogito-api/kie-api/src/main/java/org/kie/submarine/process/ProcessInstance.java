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

package org.kie.submarine.process;

public interface ProcessInstance<T> {
    
    /**
     * Returns process definition associated with this process instance
     * @return process definition of this process instance
     */
    Process<T> process();
    
    /**
     * Starts process instance
     */
    void start();
    
    /**
     * Sends given signal into this process instance
     * @param signal signal to be processed
     */
    <S> void send(Signal<S> signal);
    
    /**
     * Aborts this process instance
     */
    void abort();
    
    /**
     * Returns process variables of this process instance
     * @return variables of the process instance
     */
    T variables();
    
    /**
     * Returns current status of this process instance
     * @return
     */
    int status();
    
    /**
     * Completes work item belonging to this process instance with given variables
     * @param id id of the work item to complete
     * @param variables optional variables
     */
    void completeWorkItem(long id, T variables);
    
    /**
     * Returns identifier of this process instance
     * @return id of the process instance
     */
    long id();
}
