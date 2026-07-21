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
package org.kie.kogito.codegen.process;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTaskSpecialCharsIT extends AbstractCodegenIT {

    @Test
    public void testUserTaskNamesWithSpecialCharactersCompileSuccessfully() throws Exception {
        // Generate code from BPMN with user tasks containing special characters:
        Application app = generateCodeProcessesOnly("usertask/UserTasksWithSpecialChars.bpmn2");

        assertThat(app).isNotNull();

        // Verify the process was generated and can be retrieved
        Process<? extends Model> process = app.get(Processes.class).processById("UserTasksWithSpecialChars");
        assertThat(process)
                .as("Process should be generated and accessible")
                .isNotNull();

        assertThat(process.id())
                .as("Process ID should match")
                .isEqualTo("UserTasksWithSpecialChars");
    }

    @Test
    public void testProcessWithSpecialCharTaskNamesCanBeInstantiated() throws Exception {
        Application app = generateCodeProcessesOnly("usertask/UserTasksWithSpecialChars.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> process = app.get(Processes.class).processById("UserTasksWithSpecialChars");

        // Create a process instance
        Model model = process.createModel();
        ProcessInstance<?> processInstance = process.createInstance(model);

        assertThat(processInstance)
                .as("Process instance should be created successfully")
                .isNotNull();

        // Start the process
        processInstance.start();

        assertThat(processInstance.status())
                .as("Process should be active after start")
                .isEqualTo(ProcessInstance.STATE_ACTIVE);

        // Verify that user tasks are accessible
        assertThat(processInstance.workItems())
                .as("Process should have user tasks waiting")
                .isNotEmpty();

        WorkItem workItem = processInstance.workItems().iterator().next();
        assertThat(workItem)
                .as("Work item should be accessible")
                .isNotNull();

        // The work item name should be one of our tasks with special characters
        assertThat(workItem.getName())
                .as("Work item should have a valid name")
                .isIn("HR-Interview", "Technical-Review", "Final.Approval", "Manager@Approval");
    }

    @Test
    public void testUserTasksWithSpecialCharsCanBeCompleted() throws Exception {
        Application app = generateCodeProcessesOnly("usertask/UserTasksWithSpecialChars.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> process = app.get(Processes.class).processById("UserTasksWithSpecialChars");
        Model model = process.createModel();
        ProcessInstance<?> processInstance = process.createInstance(model);
        processInstance.start();

        // Get the first user task
        WorkItem workItem = processInstance.workItems().iterator().next();
        String workItemId = workItem.getId();

        // Complete the work item - this internally uses the generated signal methods
        processInstance.completeWorkItem(workItemId, Collections.emptyMap());

        // Verify the work item was completed successfully
        assertThat(processInstance.status())
                .as("Process should still be active or completed")
                .isIn(ProcessInstance.STATE_ACTIVE, ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testGeneratedProcessClassHasSanitizedMethodNames() throws Exception {
        // Generate application
        Application app = generateCodeProcessesOnly("usertask/UserTasksWithSpecialChars.bpmn2");
        assertThat(app).isNotNull();

        // Load the main Process class which contains all the methods for the 4 user tasks
        ClassLoader classLoader = testClassLoader();
        Class<?> processClass = classLoader.loadClass("org.kie.kogito.test.UserTasksWithSpecialCharsProcess");

        assertThat(processClass)
                .as("Process class should be generated")
                .isNotNull();

        // Get ALL method names from the Process class
        java.util.List<String> methodNames = java.util.Arrays.stream(processClass.getDeclaredMethods())
                .map(java.lang.reflect.Method::getName)
                .collect(java.util.stream.Collectors.toList());

        assertThat(methodNames)
                .as("Process class should have methods")
                .isNotEmpty();

        // Verify that ALL method names are valid Java identifiers (no special characters)
        for (String methodName : methodNames) {
            assertThat(methodName)
                    .as("Method name '%s' should not contain hyphens", methodName)
                    .doesNotContain("-");

            assertThat(methodName)
                    .as("Method name '%s' should not contain dots", methodName)
                    .doesNotContain(".");

            assertThat(methodName)
                    .as("Method name '%s' should not contain @ symbols", methodName)
                    .doesNotContain("@");

            assertThat(methodName)
                    .as("Method name '%s' should not contain spaces", methodName)
                    .doesNotContain(" ");
        }

    }

}
