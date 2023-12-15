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
package org.kie.kogito.svg.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass({ SecurityContextHolder.class })
public class SpringBootAuthHelper {

    private List<PrincipalAuthTokenReader> authTokenReaders;

    public SpringBootAuthHelper(@Autowired List<PrincipalAuthTokenReader> authTokenReaders) {
        this.authTokenReaders = authTokenReaders;
    }

    public Optional<String> getAuthToken() {
        return Optional.ofNullable(getToken());
    }

    private String getToken() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext == null || securityContext.getAuthentication() == null) {
            return null;
        }

        Object principal = securityContext.getAuthentication().getPrincipal();

        return this.authTokenReaders.stream().filter(reader -> reader.acceptsPrincipal(principal)).findFirst()
                .map(reader -> "Bearer " + reader.readToken(principal)).orElse(null);
    }
}
