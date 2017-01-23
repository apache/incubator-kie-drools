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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;

public class AbstractAssignmentTests extends HumanTaskServicesBaseTest {

    
    protected void assertPotentialOwners(Task task, int expectedSize, String...expectedNames) {
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        
        assertEquals("Expected size of potential owners do not match", expectedSize, potentialOwners.size());
        
        if (expectedNames.length > 0) {
            List<String> names = potentialOwners.stream().map(po -> po.getId()).collect(toList());
            
            assertTrue("No match for expected potential owner names", names.containsAll(Arrays.asList(expectedNames)));
        }
    }
    
    protected void assertActualOwner(Task task, String actualOwner) {
        assertNotNull("No actual owner when expected", task.getTaskData().getActualOwner());
        assertEquals("Not matching actual owner", actualOwner, task.getTaskData().getActualOwner().getId());
    }
    
    protected void assertNoActualOwner(Task task) {
        assertNull("Actual owner present when not expected", task.getTaskData().getActualOwner());        
    }
   
    protected void createAndAssertTask(String taskExpression, String actualOwner, int expectedPotOwners, String... expectedPotOwnerNames) {
        Task task = TaskFactory.evalTask(new StringReader(taskExpression));
        assertPotentialOwners(task, expectedPotOwners);
       
        taskService.addTask(task, new HashMap<String, Object>());
        assertPotentialOwners(task, expectedPotOwners, expectedPotOwnerNames);
        assertActualOwner(task, actualOwner);
    }
}
