/*
 * Copyright 2013 JBoss Inc
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

package org.kie.api.runtime.manager.audit;

import java.util.Date;

/**
 * Audit view of process instance
 *
 */
public interface ProcessInstanceLog {

	/**
	 * Process instance identifier
     * @return the {@link Long} value
	 */
	Long getProcessInstanceId();

	/**
	 * Process id of the definition
     * @return the {@link String} value
	 */
	String getProcessId();

	/**
	 * Start date of this process instance
     * @return the {@link Date} value
	 */
	Date getStart();

	/**
	 * End date of this process instance, null if process instance is still active
     * @return the {@link Date} value
	 */
	Date getEnd();

	/**
	 * Status of the process instance and might be one of:
	 * <ul>
	 * 	<li>ProcessInstance.STATE_ACTIVE</li>
	 * 	<li>ProcessInstance.STATE_COMPLETED</li>
	 * 	<li>ProcessInstance.STATE_ABORTED</li>
	 * 	<li>ProcessInstance.STATE_SUSPENDED</li>
	 * </ul>
     * @return the {@link Integer} value
	 */
	Integer getStatus();

	/**
	 * Parent process instance id, will be null for top level process instance
     * @return the {@link Long} value
	 */
	Long getParentProcessInstanceId();

	/**
	 * Outcome of the process instance that is providing error information in case process 
	 * instance completed with an error
     * @return the {@link String} value
	 */
	String getOutcome();

	/**
	 * Amount of time (in milliseconds) that process instance took to complete.
	 * Available only when process instance is completed.
     * @return the {@link String} value
	 */
	Long getDuration();

	/**
	 * Identifier of a entity (user) who initiated this process instance.
     * @return the {@link String} value
	 */
	String getIdentity();

	/**
	 * External (optional) identifier associated with this process instance
     * @return the {@link String} value
	 */
	String getExternalId();

	/**
	 * Version of the process definition
     * @return the {@link String} value
	 */
	String getProcessVersion();

	/**
	 * Name of the process definition
     * @return the {@link String} value
	 */
	String getProcessName();
	
	/**
     * Description of the process instance
     * @return the {@link String} value
     */
    String getProcessInstanceDescription();

}
