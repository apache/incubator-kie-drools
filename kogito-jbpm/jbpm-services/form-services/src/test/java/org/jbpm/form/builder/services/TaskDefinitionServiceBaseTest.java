package org.jbpm.form.builder.services;

import java.io.File;
import java.util.List;
import javax.inject.Inject;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.jbpm.form.builder.services.impl.fs.FSTaskDefinitionService;
import org.jbpm.form.builder.services.tasks.TaskRef;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public abstract class TaskDefinitionServiceBaseTest{
    @Inject
    private FSTaskDefinitionService service;
    
    private String baseUrl = "/tmp";
    private String fileSeparator = System.getProperty("file.separator");
    
    @Before
    public void setUp(){
        service.setBaseUrl(baseUrl);
    }
    
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(baseUrl+fileSeparator+"somePackage"));
    }
    @Test
    public void testGetProcessTasks() throws Exception {
        
        String bpmn2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        List<TaskRef> tasks = service.getProcessTasks(bpmn2Content, "GuvnorGetProcessTasksTest.bpmn2");
        assertNotNull("tasks shouldn't be null", tasks);
        assertTrue("tasks should contain 6 elements", tasks.size() == 6);
    }
    @Test
    public void testQueryOK() throws Exception {
        
        
        // Contains 5 Human Tasks + 1 for initialization
        FileUtils.copyFileToDirectory(new File(getClass().getResource("GuvnorGetProcessTasksTest.bpmn2").getPath()), new File(baseUrl+fileSeparator+"somePackage"), true);
        // Contains 1 Human Task and it doesn't required any task for initialization
        FileUtils.copyFileToDirectory(new File(getClass().getResource("GuvnorGetProcessTasksTest2.bpmn2").getPath()), new File(baseUrl+fileSeparator+"somePackage"), true);
        

        List<TaskRef> tasks = service.query("somePackage", "");
        
        assertNotNull("tasks shouldn't be null", tasks);
        assertFalse("tasks shouldn't be empty", tasks.isEmpty());
        assertEquals(7, tasks.size());
        for (TaskRef sampleTask : tasks) {
            System.out.println("Sample Task: "+sampleTask);
            assertNotNull("sampleTask shouldn't be null", sampleTask);
            assertNotNull("processId shouldn't be null", sampleTask.getProcessId());
            assertFalse("processId shouldn't be empty", "".equals(sampleTask.getProcessId()));
            assertNotNull("taskId shouldn't be null", sampleTask.getTaskId());
            assertFalse("taskId shouldn't be empty", "".equals(sampleTask.getTaskId()));
        }
    }
    
//    public void testQueryOKEmptyProcess() throws Exception {
//        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
//        HttpClient client = EasyMock.createMock(HttpClient.class);
//        Map<String, String> responses = new HashMap<String, String>();
//        StringBuilder props = new StringBuilder();
//        props.append("sampleProcess1.bpmn2=AAAAA\n");
//        props.append("anotherThing.txt=AAAAA\n");
//        props.append("sampleProcess2.bpmn2=AAAAA\n");
//        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
//        String process2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest2.bpmn2"));
//        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess1.bpmn2", "");
//        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess2.bpmn2", process2Content);
//        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
//            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(3);
//        service.getHelper().setClient(client);
//        
//        EasyMock.replay(client);
//        List<TaskRef> tasks = service.query("somePackage", "");
//        EasyMock.verify(client);
//        assertNotNull("tasks shouldn't be null", tasks);
//        assertFalse("tasks shouldn't be empty", tasks.isEmpty());
//        for (TaskRef sampleTask : tasks) {
//            assertNotNull("sampleTask shouldn't be null", sampleTask);
//            assertNotNull("processId shouldn't be null", sampleTask.getProcessId());
//            assertFalse("processId shouldn't be empty", "".equals(sampleTask.getProcessId()));
//            assertNotNull("taskId shouldn't be null", sampleTask.getTaskId());
//            assertFalse("taskId shouldn't be empty", "".equals(sampleTask.getTaskId()));
//        }
//    }
//    
    @Test
    public void testQueryOKWithFilter() throws Exception {
        
        
        // Contains 5 Human Tasks + 1 for initialization
        FileUtils.copyFileToDirectory(new File(getClass().getResource("GuvnorGetProcessTasksTest.bpmn2").getPath()), new File(baseUrl+fileSeparator+"somePackage"), true);
        // Contains 1 Human Task and it doesn't required any task for initialization
        FileUtils.copyFileToDirectory(new File(getClass().getResource("GuvnorGetProcessTasksTest2.bpmn2").getPath()), new File(baseUrl+fileSeparator+"somePackage"), true);
        
        String filter = "Review";
        List<TaskRef> tasks = service.query("somePackage", filter);
        
        assertNotNull("tasks shouldn't be null", tasks);
        assertFalse("tasks shouldn't be empty", tasks.isEmpty());
        assertEquals(1, tasks.size());
        for (TaskRef sampleTask : tasks) {
            assertNotNull("sampleTask shouldn't be null", sampleTask);
            assertNotNull("processId shouldn't be null", sampleTask.getProcessId());
            assertFalse("processId shouldn't be empty", "".equals(sampleTask.getProcessId()));
            assertNotNull("taskId shouldn't be null", sampleTask.getTaskId());
            assertFalse("taskId shouldn't be empty", "".equals(sampleTask.getTaskId()));
            assertTrue("taskId or processId should contain filter", sampleTask.getTaskId().contains(filter) || sampleTask.getProcessId().contains(filter));
        }
    }

    @Test
    public void testGetTasksByNameOK() throws Exception {
        
       
        // Contains 5 Human Tasks + 1 for initialization
        FileUtils.copyFileToDirectory(new File(getClass().getResource("GuvnorGetProcessTasksTest.bpmn2").getPath()), new File(baseUrl+fileSeparator+"somePackage"), true);
        // Contains 1 Human Task and it doesn't required any task for initialization
        FileUtils.copyFileToDirectory(new File(getClass().getResource("GuvnorGetProcessTasksTest2.bpmn2").getPath()), new File(baseUrl+fileSeparator+"somePackage"), true);
        
        String taskId = "Review";
        String processId = "com.sample.humantask";
        
        
        List<TaskRef> tasks = service.getTasksByName("somePackage", processId, taskId);
        
        
        assertNotNull("tasks shouldn't be null", tasks);
        assertEquals("tasks should have one item", tasks.size(), 1);
        TaskRef task = tasks.iterator().next();
        assertNotNull("sampleTask shouldn't be null", task);
        assertNotNull("processId shouldn't be null", task.getProcessId());
        assertFalse("processId shouldn't be empty", "".equals(task.getProcessId()));
        assertNotNull("taskId shouldn't be null", task.getTaskId());
        assertFalse("taskId shouldn't be empty", "".equals(task.getTaskId()));
        assertEquals("taskId should be the same as task.taskId", task.getTaskId(), taskId);
        assertEquals("processId should be the same as task.processId", task.getProcessId(), processId);
    }


    @Test
    public void testGetBPMN2TaskOK() throws Exception {
        
        
        
        String process1Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        String processName = "testProcess.bpmn2";
        String taskName = "Review";
        
        TaskRef task = service.getBPMN2Task(process1Content, processName, taskName);
        assertNotNull("task shouldn't be null", task);
        assertNotNull("task.taskId shouldn't be null", task.getTaskId());
        assertNotNull("task.processId shouldn't be null", task.getProcessId());
        assertEquals("task.taskName should be the same as taskName", taskName, task.getTaskName());
    }

     
}
