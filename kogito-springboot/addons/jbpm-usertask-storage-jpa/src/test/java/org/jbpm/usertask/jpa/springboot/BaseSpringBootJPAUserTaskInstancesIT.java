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

import java.net.URISyntaxException;

import org.jbpm.usertask.jpa.AbstractJPAUserTaskInstancesIT;
import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.jbpm.usertask.jpa.repository.AttachmentRepository;
import org.jbpm.usertask.jpa.repository.CommentRepository;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseSpringBootJPAUserTaskInstancesIT extends AbstractJPAUserTaskInstancesIT {

    public BaseSpringBootJPAUserTaskInstancesIT(JPAUserTaskInstances userTaskInstances,
            UserTaskInstanceRepository userTaskInstanceRepository,
            AttachmentRepository attachmentRepository,
            CommentRepository commentRepository) {
        super(userTaskInstances, userTaskInstanceRepository, attachmentRepository, commentRepository);
    }

    /**
     * Override test methods that access lazy collections and wrap them in TransactionTemplate
     * to ensure the Hibernate session remains open for lazy loading.
     */

    @Test
    @Transactional
    @Override
    public void testEditTaskInputOutputs() {
        super.testEditTaskInputOutputs();
    }

    @Test
    @Transactional
    @Override
    public void testCreateUserTask() {
        super.testCreateUserTask();
    }

    @Test
    @Transactional
    @Override
    public void testAttachments() throws URISyntaxException {
        super.testAttachments();
    }

    @Test
    @Transactional
    @Override
    public void testComments() {
        super.testComments();
    }

    @Test
    @Transactional
    @Override
    public void testFindByIdentityByAdminGroups() {
        super.testFindByIdentityByAdminGroups();
    }
}
