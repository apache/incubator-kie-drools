/*
 * Copyright 2010 JBoss Inc
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

import org.drools.runtime.KnowledgeContext;

/**
 * Represents the context when executing a process.
 */
public interface ProcessContext extends KnowledgeContext {

    /**
     * Returns the process instance that is currently being
     * executed in this context.
     *
     * @return the process instance that is currently being
     * executed in this context
     */
    ProcessInstance getProcessInstance();

    /**
     * Returns the node instance that is currently being
     * executed in this context, or <code>null</node> if no
     * node instance is currently being executed.
     *
     * @return the node instance that is currently being
     * executed in this context
     */
    NodeInstance getNodeInstance();

    /**
     * Returns the value of the variable with the given name.
     * Based on the current node instance, it will try to resolve
     * the given variable, taking nested variable scopes into
     * account.  Returns <code>null</code> if the variable could
     * not be found.
     * 
     * @param variableName the name of the variable
     * @return the value of the variable
     */
    Object getVariable(String variableName);

    /**
     * Sets the value of the variable with the given name.
     * Based on the current node instance, it will try to resolve
     * the given variable, taking nested variable scopes into
     * account.  
     * 
     * If the variable cannot be resolved, it will set the value as
     * a process-level variable.  It is however recommended to only
     * use this with caution, as it is always recommended to define
     * the variables that are used inside a process.
     * 
     * @param variableName the name of the variable
     * @param value the value of the variable
     */
    void setVariable(String variableName,
                     Object value);

}
