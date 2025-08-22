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

package org.jbpm.usertask.jpa.springboot;

import java.util.List;
import java.util.Optional;

import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.jbpm.usertask.jpa.mapper.UserTaskInstanceEntityMapper;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class SpringBootJPAUserTaskInstances extends JPAUserTaskInstances {

    @Autowired
    public SpringBootJPAUserTaskInstances(UserTaskInstanceRepository userTaskInstanceRepository, UserTaskInstanceEntityMapper userTaskInstanceEntityMapper) {
        super(userTaskInstanceRepository, userTaskInstanceEntityMapper);
    }

    @Override
    public Optional<UserTaskInstance> findById(String userTaskInstanceId) {
        return super.findById(userTaskInstanceId);
    }

    @Override
    public List<UserTaskInstance> findByIdentity(IdentityProvider identityProvider) {
        return super.findByIdentity(identityProvider);
    }

    @Override
    public boolean exists(String userTaskInstanceId) {
        return super.exists(userTaskInstanceId);
    }

    @Override
    public UserTaskInstance create(UserTaskInstance userTaskInstance) {
        return super.create(userTaskInstance);
    }

    @Override
    public UserTaskInstance update(UserTaskInstance userTaskInstance) {
        return super.update(userTaskInstance);
    }

    @Override
    public UserTaskInstance remove(UserTaskInstance userTaskInstance) {
        return super.remove(userTaskInstance);
    }
}
