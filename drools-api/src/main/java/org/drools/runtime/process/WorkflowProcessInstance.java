/**
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

/**
 * A workflow process instance represents one specific instance of a
 * workflow process that is currently executing.  It is an extension
 * of a <code>ProcessInstance</code> and contains all runtime state
 * related to the execution of workflow processes. 
 *   
 * @see org.drools.runtime.process.ProcessInstance
 */
public interface WorkflowProcessInstance
    extends
    ProcessInstance,
    NodeInstanceContainer {

	/**
	 * Returns the value of the variable with the given name.  Note
	 * that only variables in the process-level scope will be searched.
	 * Returns <code>null</code> if the value of the variable is null
	 * or if the variable cannot be found.
	 *  
	 * @param name the name of the variable
	 * @return the value of the variable, or <code>null</code> if it cannot be found
	 */
	Object getVariable(String name);
	
	void setVariable(String name, Object value);

}