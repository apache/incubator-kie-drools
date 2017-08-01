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

package org.jbpm.kie.services.impl;

/**
 * Provides additional hook to process variables before they will be used by services.
 * Common use case might be to unwrap variables from some transport related "cover"
 *  
 */
public interface VariablesAware {

	/**
	 * Generic processing method that might be simply returning same instance of variables
	 * if no processing is required.
	 * @param variables object that holds varialble(s)
	 * @param cl classloader that shall be used to operate on the variables e.g. project class loader
	 * @return
	 */
	<T> T  process(T variables, ClassLoader cl);
}
