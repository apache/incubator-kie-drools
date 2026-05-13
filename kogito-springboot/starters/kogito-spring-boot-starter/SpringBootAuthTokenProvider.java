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
package org.kogito.addons.springboot.common.rest.workitem;

import java.util.Optional;

import org.kie.addons.springboot.auth.SpringBootAuthTokenHelper;
import org.kie.kogito.auth.AuthTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass({ SecurityContextHolder.class })
public class SpringBootAuthTokenProvider implements AuthTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootAuthTokenProvider.class);

    private final SpringBootAuthTokenHelper authTokenHelper;

    @Autowired
    public SpringBootAuthTokenProvider(SpringBootAuthTokenHelper authTokenHelper) {
        this.authTokenHelper = authTokenHelper;
    }

    @Override
    public Optional<String> getAuthToken() {
        Optional<String> token = authTokenHelper.getAuthToken();
        if (token.isPresent()) {
            logger.debug("Token retrieved from Spring Boot SecurityContext via SpringBootAuthTokenHelper");
        } else {
            logger.debug("No token available from Spring Boot SecurityContext");
        }
        return token;
    }
}

