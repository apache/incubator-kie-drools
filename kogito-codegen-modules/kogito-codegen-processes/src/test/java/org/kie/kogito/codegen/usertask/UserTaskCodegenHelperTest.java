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

package org.kie.kogito.codegen.usertask;

import java.nio.file.Path;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.impl.WorkImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTaskCodegenHelperTest {
    static final String PROCESS_ID = "approvals";
    static final String TASK_ID = "taskId";
    static final String PACKAGE = "org.kie.kogito.usertask";
    static final Path PACKAGE_PATH = Path.of("org/kie/kogito/usertask");

    private Work work;

    @BeforeEach
    void setUp() {
        work = new WorkImpl();
        work.setParameter("ProcessId", PROCESS_ID);
        work.setParameter(Work.PARAMETER_UNIQUE_TASK_ID, TASK_ID);
        work.setParameter("PackageName", PACKAGE);
    }

    @Test
    void testGetWorkProcessId() {
        assertThat(UserTaskCodegenHelper.processId(work)).isEqualTo("Approvals");
    }

    @Test
    void testGetWorkClassName() {
        assertThat(UserTaskCodegenHelper.className(work)).isEqualTo("Approvals_TaskId");
    }

    @Test
    void testGetWorkPackagePath() {
        assertThat(UserTaskCodegenHelper.path(work))
                .isNotNull()
                .isEqualTo(PACKAGE_PATH);

    }

    @Test
    void testGetPath() {
        assertThat(UserTaskCodegenHelper.path(PACKAGE))
                .isNotNull()
                .isEqualTo(PACKAGE_PATH);

        assertThat(UserTaskCodegenHelper.path("test"))
                .isNotNull()
                .isEqualTo(Path.of("test"));
    }
}
