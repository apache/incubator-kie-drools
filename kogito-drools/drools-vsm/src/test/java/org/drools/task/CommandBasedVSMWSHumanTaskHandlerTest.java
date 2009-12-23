package org.drools.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.SystemEventListenerFactory;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.ContentData;
import org.drools.task.service.HumanTaskServiceImpl;
import org.drools.task.service.PermissionDeniedException;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.GenericMessageHandlerImpl;
import org.drools.vsm.HumanTaskServiceProvider;
import org.drools.vsm.ServiceManagerData;
import org.drools.vsm.mina.MinaAcceptor;
import org.drools.vsm.mina.MinaConnector;
import org.drools.vsm.mina.MinaIoHandler;
import org.drools.vsm.remote.ServiceManagerRemoteClient;
import org.drools.vsm.task.CommandBasedVSMWSHumanTaskHandler;
import org.drools.vsm.task.TaskServerMessageHandlerImpl;
import org.drools.vsm.task.responseHandlers.BlockingGetContentMessageResponseHandler;
import org.drools.vsm.task.responseHandlers.BlockingGetTaskMessageResponseHandler;
import org.drools.vsm.task.responseHandlers.BlockingTaskOperationMessageResponseHandler;
import org.drools.vsm.task.responseHandlers.BlockingTaskSummaryMessageResponseHandler;

public class CommandBasedVSMWSHumanTaskHandlerTest extends BaseTest {
	
    private static final int DEFAULT_WAIT_TIME = 5000;
    private static final int MANAGER_COMPLETION_WAIT_TIME = DEFAULT_WAIT_TIME;
    private static final int MANAGER_ABORT_WAIT_TIME = DEFAULT_WAIT_TIME;

    private MinaAcceptor server;
	private MinaAcceptor humanTaskServer;
	private ServiceManagerRemoteClient client;
	private CommandBasedVSMWSHumanTaskHandler handler;
	private HumanTaskServiceImpl humanTaskClient;
	
	public void setUp() throws Exception {
		super.setUp();
		SocketAddress address = new InetSocketAddress("127.0.0.1", 9123);
    	ServiceManagerData serverData = new ServiceManagerData();
    	// setup Server
    	SocketAcceptor acceptor = new NioSocketAcceptor();
    	acceptor.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener(),
    			new GenericMessageHandlerImpl(serverData,
    					SystemEventListenerFactory.getSystemEventListener())));
    	this.server = new MinaAcceptor(acceptor, address);
    	this.server.start();
    	// End Execution Server
    	
    	// Human task Server configuration
    	SocketAddress htAddress = new InetSocketAddress("127.0.0.1", 9124);
    	SocketAcceptor htAcceptor = new NioSocketAcceptor();
    	
    	htAcceptor.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener(),
    			new TaskServerMessageHandlerImpl(taskService,
    					SystemEventListenerFactory.getSystemEventListener())));
    	this.humanTaskServer = new MinaAcceptor(htAcceptor, htAddress);
    	this.humanTaskServer.start();
    	
    	// End Human task Server configuration
    	
    	// setup the ht client
    	NioSocketConnector htclientConnector = new NioSocketConnector();
    	htclientConnector.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener()));
    	GenericConnector htMinaClient = new MinaConnector("client ht",
    			htclientConnector,
    			htAddress,
    			SystemEventListenerFactory.getSystemEventListener() );
    	
    	// setup the SM client
    	NioSocketConnector clientConnector = new NioSocketConnector();
    	clientConnector.setHandler(new MinaIoHandler(SystemEventListenerFactory.getSystemEventListener()));
    	GenericConnector minaClient = new MinaConnector("client SM",
    			clientConnector,
    			address,
    			SystemEventListenerFactory.getSystemEventListener());
    	
    	// Service Manager client, that contains a list of service beside the execution Server, in this case the HumanTaskService
    	List<GenericConnector> services = new ArrayList<GenericConnector>();
    	services.add(htMinaClient);
    	this.client = new ServiceManagerRemoteClient("client SM", minaClient, services);
    	
    	((ServiceManagerRemoteClient) client).connect();
    	
    	HumanTaskServiceProvider humanTaskServiceFactory = this.client.getHumanTaskService();
    	this.humanTaskClient = (HumanTaskServiceImpl) humanTaskServiceFactory.newHumanTaskServiceClient();
    	
    	KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
    	KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();
    	
    	this.handler = new CommandBasedVSMWSHumanTaskHandler(kbase.newStatefulKnowledgeSession());
		
	}
	
    protected void tearDown() throws Exception {
    	this.handler.dispose();
    	this.client.disconnect();
    	this.server.stop();
    	this.humanTaskServer.stop();
        super.tearDown();
    }

    public void testTask() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(15000);
        System.out.println("Completed task " + task.getId());
        assertTrue(manager.waitTillCompleted(DEFAULT_WAIT_TIME));
    }

    public void testTaskMultipleActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader, Dalai Lama");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Ready, task.getStatus());

        System.out.println("Claiming task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.claim(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Claimed task " + task.getId());

        System.out.println("Starting task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testTaskGroupActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        humanTaskClient.getTasksAssignedAsPotentialOwner(null, groupIds, "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Ready, taskSummary.getStatus());

        System.out.println("Claiming task " + taskSummary.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.claim(taskSummary.getId(), "Darth Vader", operationResponseHandler);
        PermissionDeniedException denied = null;
        System.out.println("1");
        try {
            operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
            System.out.println("2");
        } catch (PermissionDeniedException e) {
        	System.out.println("EXCEPTION: " + e);
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);
        System.out.println("Claimed task " + taskSummary.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskMessageResponseHandler getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(Status.Ready, task.getTaskData().getStatus());
    }

    public void testTaskSingleAndGroupActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task One");
        workItem.setParameter("TaskName", "TaskNameOne");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        workItem = new WorkItemImpl();
        workItem.setName("Human Task Two");
        workItem.setParameter("TaskName", "TaskNameTwo");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", groupIds, "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(2, tasks.size());
    }

    public void testTaskFail() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Failing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.fail(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Failed task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskSkip() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Skipping task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.skip(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Skipped task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskAbortSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        handler.abortWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(0, tasks.size());
    }

    public void testTaskAbortNotSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Skippable", "false");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());

        handler.abortWorkItem(workItem, manager);

        Thread.sleep(500);

        responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
    }

    public void testTaskData() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Content", "This is the content");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());

        BlockingGetTaskMessageResponseHandler getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        BlockingGetContentMessageResponseHandler getContentResponseHandler = new BlockingGetContentMessageResponseHandler();
        humanTaskClient.getContent(contentId, getContentResponseHandler);
        ByteArrayInputStream bis = new ByteArrayInputStream(getContentResponseHandler.getContent().getContent());
        ObjectInputStream in = new ObjectInputStream(bis);
        Object data = in.readObject();
        in.close();
        assertEquals("This is the content", data);

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.lang.String");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject("This is the result");
        out.close();
        result.setContent(bos.toByteArray());
        humanTaskClient.complete(task.getId(), "Darth Vader", result, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        Map<String, Object> results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }

    public void testOnAllSubTasksEndParentEndStrategy() throws Exception {

        TestWorkItemManager manager = new TestWorkItemManager();
        //Create the parent task
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameParent");
        workItem.setParameter("Comment", "CommentParent");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        //Set the subtask policy
        workItem.setParameter("SubTaskStrategies", "OnAllSubTasksEndParentEnd");
        handler.executeWorkItem(workItem, manager);


        Thread.sleep(500);

        //Test if the task is succesfully created
        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskNameParent", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("CommentParent", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        //Create the child task
        workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameChild1");
        workItem.setParameter("Comment", "CommentChild1");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskMessageResponseHandler getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(task.getId(), getTaskResponseHandler);
        Task parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> subTasks = responseHandler.getResults();
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(subTaskSummary1.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(subTaskSummary2.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        Task subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        Task subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the child task 1
        System.out.println("Completing sub task " + subTask1.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.complete(subTask1.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed sub task " + subTask1.getId());

        // Complete the child task 2
        System.out.println("Completing sub task " + subTask2.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.complete(subTask2.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed sub task " + subTask2.getId());

        //Check if the child task 1 is Completed

        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTask1.getId(), getTaskResponseHandler);
        subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed

        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTask2.getId(), getTaskResponseHandler);
        subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Check is the parent task is Complete
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(parentTask.getId(), getTaskResponseHandler);
        parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testOnParentAbortAllSubTasksEndStrategy() throws Exception {

        TestWorkItemManager manager = new TestWorkItemManager();
        //Create the parent task
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameParent");
        workItem.setParameter("Comment", "CommentParent");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        //Set the subtask policy
        workItem.setParameter("SubTaskStrategies", "OnParentAbortAllSubTasksEnd");
        handler.executeWorkItem(workItem, manager);


        Thread.sleep(500);

        //Test if the task is succesfully created
        BlockingTaskSummaryMessageResponseHandler responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskNameParent", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("CommentParent", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        //Create the child task
        workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameChild1");
        workItem.setParameter("Comment", "CommentChild1");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationMessageResponseHandler operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskMessageResponseHandler getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(task.getId(), getTaskResponseHandler);
        Task parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        responseHandler = new BlockingTaskSummaryMessageResponseHandler();
        humanTaskClient.getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> subTasks = responseHandler.getResults();
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(subTaskSummary1.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.start(subTaskSummary2.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        Task subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        Task subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the parent task
        System.out.println("Completing parent task " + parentTask.getId());
        operationResponseHandler = new BlockingTaskOperationMessageResponseHandler();
        humanTaskClient.skip(parentTask.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed parent task " + parentTask.getId());

        //Check if the child task 1 is Completed
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed
        getTaskResponseHandler = new BlockingGetTaskMessageResponseHandler();
        humanTaskClient.getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    private class TestWorkItemManager implements WorkItemManager {

        private volatile boolean completed;
        private volatile boolean aborted;
        private volatile Map<String, Object> results;

        public synchronized boolean waitTillCompleted(long time) {
            if (!isCompleted()) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of completed
                }
            }

            return isCompleted();
        }

        public synchronized boolean waitTillAborted(long time) {
            if (!isAborted()) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of aborted
                }
            }

            return isAborted();
        }

        public void abortWorkItem(long id) {
            setAborted(true);
        }

        public synchronized boolean isAborted() {
            return aborted;
        }

        private synchronized void setAborted(boolean aborted) {
            this.aborted = aborted;
            notifyAll();
        }

        public void completeWorkItem(long id, Map<String, Object> results) {
            this.results = results;
            setCompleted(true);
        }

        private synchronized void setCompleted(boolean completed) {
            this.completed = completed;
            notifyAll();
        }

        public synchronized boolean isCompleted() {
            return completed;
        }

        public WorkItem getWorkItem(long id) {
            return null;
        }

        public Set<WorkItem> getWorkItems() {
            return null;
        }

        public Map<String, Object> getResults() {
            return results;
        }

        public void internalAbortWorkItem(long id) {
        }

        public void internalAddWorkItem(WorkItem workItem) {
        }

        public void internalExecuteWorkItem(WorkItem workItem) {
        }

        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        }

    }
}
