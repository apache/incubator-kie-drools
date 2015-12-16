/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.conf;

/**
 * Handler responsible for controlling access to writable properties.
 * It's main responsibility is to accept or reject given value depending 
 * on the underlying implementation e.g. disallow null values
 *
 * @see DeploymentDescriptorBuilder
 */
public interface BuilderHandler {
	
	/**
	 * Verifies if given <code>value</code> is acceptable to be written via builder
	 * @param value
	 * @return
	 */
	boolean accepted(Object value);
}