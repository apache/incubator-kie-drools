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

package org.kie.kogito.index.quarkus.service.auth;

import org.kie.kogito.index.service.auth.DataIndexAuthTokenReader;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusDataIndexAuthTokenReader implements DataIndexAuthTokenReader {

    private SecurityIdentity identity;

    @Inject
    public QuarkusDataIndexAuthTokenReader(Instance<SecurityIdentity> identity) {
        if (identity.isResolvable()) {
            this.identity = identity.get();
        }
    }

    @Override
    public String readToken() {
        if (identity == null || identity.getCredential(TokenCredential.class) == null) {
            return null;
        }
        return "Bearer " + identity.getCredential(TokenCredential.class).getToken();
    }
}
