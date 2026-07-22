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

package org.jbpm.usertask.jpa.quarkus;

import org.jbpm.usertask.jpa.AbstractJPAUserTaskInstancesIT;
import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.jbpm.usertask.jpa.repository.AttachmentRepository;
import org.jbpm.usertask.jpa.repository.CommentRepository;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(value = PostgreSqlQuarkusTestResource.class, restrictToAnnotatedClass = true)
@TestProfile(PostgreSQLQuarkusTestProfile.class)
@TestTransaction
public class PostgreSQLQuarkusJPAUserTaskInstancesIT extends AbstractJPAUserTaskInstancesIT {

    @Inject
    public PostgreSQLQuarkusJPAUserTaskInstancesIT(JPAUserTaskInstances userTaskInstances,
            UserTaskInstanceRepository userTaskInstanceRepository,
            AttachmentRepository attachmentRepository,
            CommentRepository commentRepository) {
        super(userTaskInstances, userTaskInstanceRepository, attachmentRepository, commentRepository);
    }
}
