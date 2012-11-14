/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.admin;

import org.jbpm.task.identity.UserGroupCallbackManager;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.kie.SystemEventListenerFactory;
import org.jbpm.task.*;


import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.*;
import org.jbpm.task.service.TaskService;

import org.jbpm.task.service.local.LocalTaskService;
import org.junit.*;
import static org.junit.Assert.*;

/**
 */
public class TasksAdminTest {

    private EntityManagerFactory emf;
    private TaskServiceSession taskSession;
    private TaskService taskService;
    
    private Map<String, User> users = new HashMap<String, User>();
    private Map<String, Group> groups = new HashMap<String, Group>();

    public TasksAdminTest() {
    }

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());

        taskSession = taskService.createSession();

        addUsersAndGroups(taskSession, users, groups);

        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);
        
        UserGroupCallbackManager.getInstance().setCallback(null);
    }

    @After
    public void tearDown() {
        taskSession.dispose();
        emf.close();
    }

    @Test
    public void completedTasksTest() {
        runCompletedTasksTest(users, taskService);
    }
    
    public static void runCompletedTasksTest(Map<String, User> users, TaskService taskService) { 
        LocalTaskService localTaskService = new LocalTaskService(taskService);

        Task task = createSimpleTask(users.get("salaboy"), users.get("administrator"));

        localTaskService.addTask(task, new ContentData());

        Task simpleTask = localTaskService.getTaskByWorkItemId(1);

        localTaskService.start(simpleTask.getId(), "salaboy");

        TasksAdmin admin = taskService.createTaskAdmin();
        List<TaskSummary> completedTasks = admin.getCompletedTasks();
        assertEquals(0, completedTasks.size());

        localTaskService.complete(simpleTask.getId(), "salaboy", null);

        completedTasks = admin.getCompletedTasks();
        assertEquals(1, completedTasks.size());
    }

    @Test
    public void completedSinceTasksTest() {
        runCompletedSinceTasksTest(users, taskService);
    }
    
    public static void runCompletedSinceTasksTest(Map<String, User> users, TaskService taskService) { 
        LocalTaskService localTaskService = new LocalTaskService(taskService);

        Task task = createSimpleTask(users.get("salaboy"), users.get("administrator"));

        localTaskService.addTask(task, new ContentData());
        List<TaskSummary> salaboysTasks = localTaskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        TaskSummary simpleTask = salaboysTasks.get(0);

        localTaskService.start(simpleTask.getId(), "salaboy");

        TasksAdmin admin = taskService.createTaskAdmin();
        List<TaskSummary> completedTasks = admin.getCompletedTasks(new Date());
        assertEquals(0, completedTasks.size());

        localTaskService.complete(simpleTask.getId(), "salaboy", null);

        completedTasks = admin.getCompletedTasks(new Date());
        assertEquals(1, completedTasks.size());
    }

    @Test 
    public void archiveTasksTest() {
        runArchiveTasksTest(users, taskService, emf);
    }
    
    public static void runArchiveTasksTest(Map<String, User> users, TaskService taskService, EntityManagerFactory emf) { 
        LocalTaskService localTaskService = new LocalTaskService(taskService);

        Task task = createSimpleTask(users.get("salaboy"), users.get("administrator"));

        localTaskService.addTask(task, new ContentData());
        List<TaskSummary> salaboysTasks = localTaskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        
        TaskSummary simpleTask = salaboysTasks.get(0);
        assertEquals("My Simple Task", simpleTask.getName());
        
        localTaskService.start(simpleTask.getId(), "salaboy");
            
        TasksAdmin admin = taskService.createTaskAdmin();
        int archived = admin.archiveTasks(admin.getActiveTasks());
        assertEquals(1, archived);
        List<TaskSummary> archivedTasks = admin.getArchivedTasks();
        assertEquals(1, archivedTasks.size());
       
        //@TODO: FIX THIS ISSUE .. the localTaskService is not getting the updates
        System.out.println(">>> Archived? "+localTaskService.getTask(archivedTasks.get(0).getId()).isArchived());
        EntityManager em = emf.createEntityManager();
        System.out.println(">>> Archived? "+em.find(Task.class, archivedTasks.get(0).getId()).isArchived());
    }
    
    @Test
    public void removeTasksTest() {
        runRemoveTasksTest(users, taskService, emf);
    }

    public static void runRemoveTasksTest(Map<String, User> users, TaskService taskService, EntityManagerFactory emf) {
        LocalTaskService localTaskService = new LocalTaskService(taskService);

        Task task = createSimpleTask(users.get("salaboy"), users.get("administrator"));

        localTaskService.addTask(task, new ContentData());
        List<TaskSummary> salaboysTasks = localTaskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        TaskSummary simpleTask = salaboysTasks.get(0);
        assertEquals("My Simple Task", simpleTask.getName());
        
        localTaskService.start(simpleTask.getId(), "salaboy");
            
        TasksAdmin admin = taskService.createTaskAdmin();
        List<TaskSummary> activeTasks = admin.getActiveTasks();
        Task activeTask = localTaskService.getTask(activeTasks.get(0).getId());
        int removed = admin.removeTasks(activeTasks);
        assertEquals(0, removed);
        // We need to archive the tasks first 
        int archived = admin.archiveTasks(activeTasks);
        assertEquals(1, archived);
        
        removed = admin.removeTasks(activeTasks);
        assertEquals(1, removed);
        
        EntityManager em = emf.createEntityManager();
        assertNull(em.find(Task.class, activeTask.getId()));
    }
    
    public static void addUsersAndGroups(TaskServiceSession taskSession, Map<String, User> users, Map<String, Group> groups) {
        User user = new User("salaboy");
        taskSession.addUser(user);
        
        User administrator = new User("Administrator");
        taskSession.addUser(administrator);
        
        users.put("salaboy", user);
        users.put("administrator", administrator);
        
        Group myGroup = new Group("group1");
        taskSession.addGroup(myGroup);
        
        groups.put("group1", myGroup);

    }

    private static Task createSimpleTask(User user, User administrator) {
        Task task = new Task();
        task.setPriority(1);
        
        PeopleAssignments peopleAssignments = new PeopleAssignments();
        List<OrganizationalEntity> adminsEntities = new ArrayList<OrganizationalEntity>();
        adminsEntities.add(administrator);
        peopleAssignments.setBusinessAdministrators(adminsEntities);
        List<OrganizationalEntity> usersEntities = new ArrayList<OrganizationalEntity>();
        usersEntities.add(user);
        peopleAssignments.setPotentialOwners(usersEntities);
        peopleAssignments.setTaskInitiator(user);
        task.setPeopleAssignments(peopleAssignments);
        
        List<I18NText> names = new ArrayList<I18NText>();
        names.add(new I18NText("en-UK", "My Simple Task"));
        task.setNames(names);
        
        TaskData data = new TaskData();
        data.setActualOwner(user);
        data.setCreatedBy(user);
        data.setWorkItemId(1);
        data.setProcessInstanceId(1);
        data.setProcessSessionId(1);
        task.setTaskData(data);
        return task;
    }
}
