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

package org.jbpm.services.cdi;

/**
 * This class is a "backup" version of the IdentityProvider, for 
 * situations in which the IdentityProvider CDI proxy is not available. 
 * </p>
 * The "normal" implementation of the IdentityProvider expects a 
 * HTTP request context object to be available, but the code may be used
 * in situations where there is not an HTTP request or session present.
 * </p>
 * In those situations, a request scoped implementation of this interface
 * may be used in order to provide the appropriate information about the 
 * user.
 */
public interface RequestScopedBackupIdentityProvider {
	
	public static final String UNKNOWN = "unknown";

    String getName();
    
}
