/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.internals.lifecycle;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.Operation;

public class MVELLifeCycleManagerTest {

	/**
	 * Tests that a user who is in the ExcludedOwners list of the {@link Task task's) {@link PeopleAssignments peopleAssignment's) object is
	 * not allowed to execute operations on the given task. We expect to get a {@link PermissionDeniedException}.
	 */
	@Test(expected = PermissionDeniedException.class)
	public void testClaimIsAllowedExcludedOwner() {

		User testUser = new UserImpl("BB8");

		List<String> testGroupIds = new ArrayList<>();
		testGroupIds.add("testGroup1");

		// Create the task.
		String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Created } ), ";
		str += "peopleAssignments = (with ( new PeopleAssignments() ) { excludedOwners = [new User('BB8')], potentialOwners = [new Group('testGroup1')]}),";
		str += "name =  'This is my task name' })";
		InternalTask task = (InternalTask) TaskFactory.evalTask(new StringReader(str));

		// Test whether we can claim the task. This should not be possible.
		Operation operation = Operation.Claim;
		List<OperationCommand> operationCommands = new ArrayList<>();
		OperationCommand operationCommand = new OperationCommand();
		// Set the list of user-types (e.g. PotentialOwners, BusinessAdministrators, etc.) that are allowed to execute this operation.
		List<Allowed> allowed = new ArrayList<>();
		// We should only allow PotentialOwner in this test (we're claiming a task).
		allowed.add(Allowed.PotentialOwner);
		operationCommand.setAllowed(allowed);

		// Set the status that is required to be able to execute this operation.
		List<Status> status = new ArrayList<>();
		// Before we claim a task, the status is "Created".
		status.add(Status.Created);
		operationCommand.setStatus(status);
		operationCommands.add(operationCommand);

		// We don't need "targetEntity" and "entities" for this test.
		MVELLifeCycleManager taskLcManager = new MVELLifeCycleManager();
		taskLcManager.evalCommand(operation, operationCommands, task, testUser, null, testGroupIds, null);
	}

	/**
	 * Tests that a user who is in the ExcludedOwners list of the {@link Task task's) {@link PeopleAssignments peopleAssignment's) object IS
	 * allowed to execute operations on the given task IF the person is also a Business Administrator.
	 */
	@Test
	public void testDelegateIsAllowedExcludedOwnerBusinessAdministrator() {

		User testUser = new UserImpl("BB8");

		List<String> testGroupIds = new ArrayList<>();
		testGroupIds.add("testGroup1");

		// Create the task.
		String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
		str += "peopleAssignments = (with ( new PeopleAssignments() ) { excludedOwners = [new User('BB8')], potentialOwners = [new Group('testGroup1')], businessAdministrators = [new User('BB8')]}),";
		str += "name =  'This is my task name' })";
		InternalTask task = (InternalTask) TaskFactory.evalTask(new StringReader(str));

		/*
		 * Test whether we can delegate the task. Because the user is a BusinessAdministrator, this should be possible, even if the owner is
		 * in the ExcludedOwners list.
		 */
		Operation operation = Operation.Delegate;
		List<OperationCommand> operationCommands = new ArrayList<>();
		OperationCommand operationCommand = new OperationCommand();
		// Set the list of user-types (e.g. PotentialOwners, BusinessAdministrators, etc.) that are allowed to execute this operation.
		List<Allowed> allowed = new ArrayList<>();
		// We should only allow PotentialOwner in this test (we're claiming a task).
		allowed.add(Allowed.PotentialOwner);
		allowed.add(Allowed.BusinessAdministrator);
		operationCommand.setAllowed(allowed);

		// Set the status that is required to be able to execute this operation.
		List<Status> status = new ArrayList<>();
		// Before we claim a task, the status is "Created".
		status.add(Status.Ready);
		operationCommand.setStatus(status);
		operationCommands.add(operationCommand);

		// We don't need "targetEntity" and "entities" for this test.
		MVELLifeCycleManager taskLcManager = new MVELLifeCycleManager();
		taskLcManager.evalCommand(operation, operationCommands, task, testUser, null, testGroupIds, null);
	}
}
