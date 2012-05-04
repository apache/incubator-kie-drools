package org.jbpm.task.service.test.spring;

import static org.jbpm.task.service.TaskServiceBaseTest.*;

import org.junit.Test;

public class TaskServiceSpringTest extends BaseSpringTest {

    @Test
    public void testTasksOwnedQueryWithI18N() throws Exception {    
        runTestTasksOwnedQueryWithI18N(client, users, groups);
    }
    
    @Test
    public void testPotentialOwnerQueries() throws Exception { 
        runTestPotentialOwnerQueries(client, users, groups);
    }
    
    @Test
    public void testPeopleAssignmentQueries() throws Exception { 
        runTestPeopleAssignmentQueries(client, taskSession, users, groups);
    }
}
