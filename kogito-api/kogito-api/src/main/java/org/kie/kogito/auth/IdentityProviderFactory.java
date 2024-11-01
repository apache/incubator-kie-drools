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
 * Factory that resolves the {@link IdentityProvider}
 */
public interface IdentityProviderFactory {

    /**
     * Enables (true) using the application security context when resolving current User Identity. Defaults to false.
     */
    String KOGITO_SECURITY_AUTH_ENABLED = "kogito.security.auth.enabled";

    /**
     * Comma-separated list of roles that allow identity impersonation when resolving the actual User Identity.
     */
    String KOGITO_SECURITY_AUTH_IMPERSONATION_ALLOWED_FOR_ROLES = "kogito.security.auth.impersonation.allowed-for-roles";

    IdentityProvider getOrImpersonateIdentity(String user, Collection<String> roles);
}
