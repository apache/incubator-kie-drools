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
package org.kie.kogito.svg.auth.impl;

import org.kie.kogito.svg.auth.PrincipalAuthTokenReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass({ OidcUser.class })
public class OIDCPrincipalAuthTokenReader implements PrincipalAuthTokenReader<OidcUser> {

    @Override
    public boolean acceptsPrincipal(Object principal) {
        return principal instanceof OidcUser;
    }

    @Override
    public String readToken(OidcUser principal) {
        return principal.getIdToken().getTokenValue();
    }
}
