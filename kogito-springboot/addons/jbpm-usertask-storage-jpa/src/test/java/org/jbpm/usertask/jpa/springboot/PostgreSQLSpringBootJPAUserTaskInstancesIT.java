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

import org.jbpm.usertask.jpa.repository.AttachmentRepository;
import org.jbpm.usertask.jpa.repository.CommentRepository;
import org.jbpm.usertask.jpa.springboot.repository.SpringBootUserTaskInstanceRepository;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for UserTask Storage with PostgreSQL database.
 * Tests CRUD operations, identity-based queries, attachments, and comments.
 */
@SpringBootTest(classes = TestApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
@ActiveProfiles("test-postgresql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class PostgreSQLSpringBootJPAUserTaskInstancesIT extends BaseSpringBootJPAUserTaskInstancesIT {

    @Autowired
    public PostgreSQLSpringBootJPAUserTaskInstancesIT(SpringBootJPAUserTaskInstances userTaskInstances,
            SpringBootUserTaskInstanceRepository userTaskInstanceRepository,
            AttachmentRepository attachmentRepository,
            CommentRepository commentRepository) {
        super(userTaskInstances, userTaskInstanceRepository, attachmentRepository, commentRepository);
    }
}
