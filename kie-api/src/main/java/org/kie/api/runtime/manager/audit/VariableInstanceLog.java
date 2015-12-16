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

package org.kie.api.runtime.manager.audit;

import java.util.Date;

/**
 * Audit view of variables that keeps track of all changes of variable identified by given name/id.
 * This means that variable log will contain both current and previous (if exists) value of the variable.
 *
 */
public interface VariableInstanceLog {

	/**
	 * Process instance identifier
	 * @return
	 */
	Long getProcessInstanceId();

	/**
	 * Process id of the definition
	 * @return
	 */
	String getProcessId();

	/**
	 * Additional information in case variable is defined on composite node level to be able to distinguish 
	 * it between top level and embedded level variables
	 * @return
	 */
	String getVariableInstanceId();

	/**
	 * Identifier of the variable aka variable name
	 * @return
	 */
	String getVariableId();

	/**
	 * Current value of the variable
	 * @return
	 */
	String getValue();

	/**
	 * Previous value of the variable (if any)
	 * @return
	 */
	String getOldValue();

	/**
	 * Date when the variable was set (to current value)
	 * @return
	 */
	Date getDate();

	/**
	 * External (optional) identifier associated with this process instance
	 * @return
	 */
	String getExternalId();
}
