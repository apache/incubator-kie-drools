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

package org.jbpm.services.ejb.client;

import javax.naming.NamingException;

/**
 * Generic service factory used for remote look ups that are usually container specific.
 *
 */
public interface ClientServiceFactory {
	
	/**
	 * Returns unique name of given factory implementation
	 * @return
	 */
	String getName();

	/**
	 * Returns remote view of given service interface from selected application
	 * @param application application identifier on the container
	 * @param serviceInterface remote service interface to be found
	 * @return
	 * @throws NamingException
	 */
	<T> T getService(String application, Class<T> serviceInterface) throws NamingException;
}
