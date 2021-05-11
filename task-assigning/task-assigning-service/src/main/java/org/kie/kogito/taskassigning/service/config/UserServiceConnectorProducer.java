/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service.config;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.kogito.taskassigning.user.service.UserServiceConnector;

import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.USER_SERVICE_CONNECTOR;

@ApplicationScoped
public class UserServiceConnectorProducer {

    @Inject
    TaskAssigningConfig config;

    @Inject
    @Any
    Instance<UserServiceConnector> userServiceConnectorInstance;

    private UserServiceConnector instance;

    @PostConstruct
    void init() {
        instance = userServiceConnectorInstance.select(new UserServiceConnectorQualifierImpl(config.getUserServiceConnector())).get();
        if (instance == null) {
            throw new IllegalArgumentException("No user service connector was found for the configured value " +
                    USER_SERVICE_CONNECTOR + " = " + config.getUserServiceConnector());
        }
    }

    @Produces
    public UserServiceConnector userServiceConnector() {
        return instance;
    }
}
