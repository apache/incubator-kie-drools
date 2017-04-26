/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.services.task.assignment;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;

import static org.assertj.core.api.Assertions.*;

public class AbstractAssignmentTest extends HumanTaskServicesBaseTest {

    protected void assertPotentialOwners(Task task, int expectedSize, String... expectedNames) {
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();

        assertThat(potentialOwners).hasSize(expectedSize);

        if (expectedNames.length > 0) {
            assertThat(potentialOwners).as("No match for expected potential owner name")
                    .extracting(OrganizationalEntity::getId)
                    .contains(expectedNames);
        }
    }

    protected void assertActualOwner(Task task, String expectedActualOwner) {
        User actualOwner = task.getTaskData().getActualOwner();
        assertThat(actualOwner).as("No actual owner when expected").isNotNull();
        assertThat(actualOwner.getId()).as("Not matching actual owner").isEqualTo(expectedActualOwner);
    }

    protected void assertNoActualOwner(Task task) {
        assertThat(task.getTaskData().getActualOwner()).as("Actual owner present when not expected").isNull();
    }

    protected long createAndAssertTask(String taskExpression, String actualOwner, int expectedPotentialOwners,
                                       String... expectedPotentialOwnerNames) {
        Task task = TaskFactory.evalTask(new StringReader(taskExpression));
        assertPotentialOwners(task, expectedPotentialOwners);

        long taskId = taskService.addTask(task, Collections.emptyMap());

        task = taskService.getTaskById(taskId);
        assertPotentialOwners(task, expectedPotentialOwners, expectedPotentialOwnerNames);
        assertActualOwner(task, actualOwner);

        return taskId;
    }
}
