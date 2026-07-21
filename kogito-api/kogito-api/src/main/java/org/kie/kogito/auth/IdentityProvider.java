/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.auth;

import java.util.Collection;

/**
 * Delivers security information about given identity that includes name and assigned roles.
 *
 */
public interface IdentityProvider {

    /**
     * Returns name assigned to the current context, usually refers to the username
     * 
     * @return assigned name taken from security context
     */
    String getName();

    /**
     * Returns roles assigned to the current context if any
     * 
     * @return Collection of assigned roles or empty one
     */
    Collection<String> getRoles();

    /**
     * Checks if given role is assigned to current context
     * 
     * @param role role to be checked
     * @return true if the role is found otherwise null
     */
    boolean hasRole(String role);
}
