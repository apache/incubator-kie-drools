package org.jbpm.formbuilder.server.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.tika.io.IOUtils;
import org.easymock.EasyMock;
import org.jbpm.formbuilder.server.GuvnorHelper;
import org.jbpm.formbuilder.server.mock.MockAnswer;
import org.jbpm.formbuilder.server.mock.MockDeleteMethod;
import org.jbpm.formbuilder.server.mock.MockGetMethod;
import org.jbpm.formbuilder.server.mock.MockPostMethod;
import org.jbpm.formbuilder.server.mock.MockPutMethod;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.jbpm.formbuilder.shared.task.TaskServiceException;

public class GuvnorTaskDefinitionServiceTest extends TestCase {

	private String baseUrl = "http://www.redhat.com";
	private GuvnorHelper helper = new GuvnorHelper(baseUrl, "", "");
	
    public void testGetProcessTasks() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        String bpmn2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        List<TaskRef> tasks = service.getProcessTasks(bpmn2Content, "GuvnorGetProcessTasksTest.bpmn2");
        assertNotNull("tasks shouldn't be null", tasks);
        assertTrue("tasks should contain 6 elements", tasks.size() == 6);
    }

    public void testQueryOK() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        StringBuilder props = new StringBuilder();
        props.append("sampleProcess1.bpmn2=AAAAA\n");
        props.append("anotherThing.txt=AAAAA\n");
        props.append("sampleProcess2.bpmn2=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        String process1Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        String process2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest2.bpmn2"));
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess1.bpmn2", process1Content);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess2.bpmn2", process2Content);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(3);
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<TaskRef> tasks = service.query("somePackage", "");
        EasyMock.verify(client);
        assertNotNull("tasks shouldn't be null", tasks);
        assertFalse("tasks shouldn't be empty", tasks.isEmpty());
        for (TaskRef sampleTask : tasks) {
            assertNotNull("sampleTask shouldn't be null", sampleTask);
            assertNotNull("processId shouldn't be null", sampleTask.getProcessId());
            assertFalse("processId shouldn't be empty", "".equals(sampleTask.getProcessId()));
            assertNotNull("taskId shouldn't be null", sampleTask.getTaskId());
            assertFalse("taskId shouldn't be empty", "".equals(sampleTask.getTaskId()));
        }
    }
    
    public void testQueryOKEmptyProcess() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        StringBuilder props = new StringBuilder();
        props.append("sampleProcess1.bpmn2=AAAAA\n");
        props.append("anotherThing.txt=AAAAA\n");
        props.append("sampleProcess2.bpmn2=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        String process2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest2.bpmn2"));
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess1.bpmn2", "");
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess2.bpmn2", process2Content);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(3);
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        List<TaskRef> tasks = service.query("somePackage", "");
        EasyMock.verify(client);
        assertNotNull("tasks shouldn't be null", tasks);
        assertFalse("tasks shouldn't be empty", tasks.isEmpty());
        for (TaskRef sampleTask : tasks) {
            assertNotNull("sampleTask shouldn't be null", sampleTask);
            assertNotNull("processId shouldn't be null", sampleTask.getProcessId());
            assertFalse("processId shouldn't be empty", "".equals(sampleTask.getProcessId()));
            assertNotNull("taskId shouldn't be null", sampleTask.getTaskId());
            assertFalse("taskId shouldn't be empty", "".equals(sampleTask.getTaskId()));
        }
    }
    
    public void testQueryOKWithFilter() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        StringBuilder props = new StringBuilder();
        props.append("sampleProcess1.bpmn2=AAAAA\n");
        props.append("anotherThing.txt=AAAAA\n");
        props.append("sampleProcess2.bpmn2=AAAAA\n");
        responses.put("GET " + helper.getApiSearchUrl("somePackage"), props.toString());
        String process1Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        String process2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest2.bpmn2"));
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess1.bpmn2", process1Content);
        responses.put("GET " + helper.getApiSearchUrl("somePackage") + "sampleProcess2.bpmn2", process2Content);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(3);
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        String filter = "Review";
        List<TaskRef> tasks = service.query("somePackage", filter);
        EasyMock.verify(client);
        assertNotNull("tasks shouldn't be null", tasks);
        assertFalse("tasks shouldn't be empty", tasks.isEmpty());
        for (TaskRef sampleTask : tasks) {
            assertNotNull("sampleTask shouldn't be null", sampleTask);
            assertNotNull("processId shouldn't be null", sampleTask.getProcessId());
            assertFalse("processId shouldn't be empty", "".equals(sampleTask.getProcessId()));
            assertNotNull("taskId shouldn't be null", sampleTask.getTaskId());
            assertFalse("taskId shouldn't be empty", "".equals(sampleTask.getTaskId()));
            assertTrue("taskId or processId should contain filter", sampleTask.getTaskId().contains(filter) || sampleTask.getProcessId().contains(filter));
        }
    }
    
    public void testQueryIOProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        IOException exception = new IOException("mock io error");
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(exception).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.query("somePackage", "");
            fail ("query(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testQueryUnknownProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.query("somePackage", "");
            fail ("query(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetTasksByNameOK() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String xml1 = "<packages><package><title>somePackage</title>" +
        		"<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1</assets>" +
        		"<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2</assets>" +
        		"</package></packages>";
        String xml2 = "<asset><sourceLink>" +
        		helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1/source" +
        		"</sourceLink><metadata><format>bpmn2</format></metadata></asset>";
        String xml3 = "<asset><sourceLink>" +
        		helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2/source" +
                "</sourceLink><metadata><format>bpmn2</format></metadata></asset>";
        String process1Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        String process2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest2.bpmn2"));
        responses.put("GET " + helper.getRestBaseUrl(), xml1);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1", xml2);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2", xml3);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1/source", process1Content);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2/source", process2Content);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(5);
        service.getHelper().setClient(client);
        String taskId = "Review";
        String processId = "com.sample.humantask";
        
        EasyMock.replay(client);
        List<TaskRef> tasks = service.getTasksByName("somePackage", processId, taskId);
        EasyMock.verify(client);
        
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
    
    public void testGetTasksByNameJAXBProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String xml1 = "<packages><package><title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1</assetsBROKEN_XML>";
        responses.put("GET " + helper.getRestBaseUrl(), xml1);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        String taskId = "Review";
        String processId = "com.sample.humantask";
        
        EasyMock.replay(client);
        try {
            service.getTasksByName("somePackage", processId, taskId);
            fail("getTasksByName(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type JAXBException", cause instanceof JAXBException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetTasksByNameIOProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        String taskId = "Review";
        String processId = "com.sample.humantask";
        
        EasyMock.replay(client);
        try {
            service.getTasksByName("somePackage", processId, taskId);
            fail("getTasksByName(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetTasksByNameUnknownProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        String taskId = "Review";
        String processId = "com.sample.humantask";
        
        EasyMock.replay(client);
        try {
            service.getTasksByName("somePackage", processId, taskId);
            fail("getTasksByName(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetContainingPackageOK() throws Exception {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String xml1 = "<packages><package><title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1</assets>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2</assets>" +
                "<metadata><uuid>" + uuid1 + "</uuid></metadata>" +
                "</package></packages>";
        String xml2 = "<asset><sourceLink>" +
        		helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1/source" +
                "</sourceLink><metadata><format>bpmn2</format><uuid>somethingelse</uuid></metadata></asset>";
        String xml3 = "<asset><sourceLink>" +
        		helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2/source" +
                "</sourceLink><metadata><format>bpmn2</format><uuid>" + uuid2 + "</uuid></metadata></asset>";
        responses.put("GET " + helper.getRestBaseUrl(), xml1);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1", xml2);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2", xml3);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(3);
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        String packageName = service.getContainingPackage(uuid2);
        EasyMock.verify(client);
        
        assertNotNull("packageName shouldn't be null", packageName);
        assertEquals("packageName should be somePackage", packageName, "somePackage");
    }
    
    public void testGetContainingPackageJAXBProblem() throws Exception {
        String uuid1 = UUID.randomUUID().toString();
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String xml1 = "<packages><package><title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1</assetsBROKEN_XML>";
        responses.put("GET " + helper.getRestBaseUrl(), xml1);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getContainingPackage(uuid1);
            fail("getContainingPackage(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type JAXBException", cause instanceof JAXBException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetContainingPackageIOProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getContainingPackage(UUID.randomUUID().toString());
            fail("getContainingPackage(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetContainingPackageUnkownProblem() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getContainingPackage(UUID.randomUUID().toString());
            fail("getContainingPackage(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetTaskByUUIDOK() throws Exception {
        String uuid1 = UUID.randomUUID().toString();
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String xml1 = "<packages><package><title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1</assets>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2</assets>" +
                "</package></packages>";
        String xml2 = "<asset><sourceLink>" +
                helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1/source" +
                "</sourceLink><metadata><format>bpmn2</format><uuid>somethingelse</uuid></metadata></asset>";
        String xml3 = "<asset><sourceLink>" +
                helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2/source" +
                "</sourceLink><metadata><format>bpmn2</format><uuid>" + uuid1 + "</uuid></metadata></asset>";
        String process2Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        responses.put("GET " + helper.getRestBaseUrl(), xml1);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1", xml2);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2", xml3);
        responses.put("GET " + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2/source", process2Content);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).times(4);
        service.getHelper().setClient(client);
        String taskId = "Review";
        
        EasyMock.replay(client);
        TaskRef task = service.getTaskByUUID("somePackage", taskId, uuid1);
        EasyMock.verify(client);
        
        assertNotNull("task shouldn't be null", task);
        assertNotNull("processId shouldn't be null", task.getProcessId());
        assertFalse("processId shouldn't be empty", "".equals(task.getProcessId()));
        assertNotNull("taskId shouldn't be null", task.getTaskId());
        assertFalse("taskId shouldn't be empty", "".equals(task.getTaskId()));
        assertEquals("taskId should be the same as task.taskId", task.getTaskId(), taskId);
    }
    
    public void testGetTaskByUUIDJAXBProblem() throws Exception {
        String uuid1 = UUID.randomUUID().toString();
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Map<String, String> responses = new HashMap<String, String>();
        String xml1 = "<packages><package><title>somePackage</title>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess1</assets>" +
                "<assets>" + helper.getRestBaseUrl() + "somePackage/assets/sampleProcess2</assetsBROKEN_XML>";
        responses.put("GET " + helper.getRestBaseUrl(), xml1);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
            andAnswer(new MockAnswer(responses, new IllegalArgumentException("unexpected call"))).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getTaskByUUID("somePackage", "Review", uuid1);
            fail("getTaskByUUID(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type JAXBException", cause instanceof JAXBException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetTaskByUUIDIOProblem() throws Exception {
        String uuid1 = UUID.randomUUID().toString();
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new IOException("mock io error")).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getTaskByUUID("somePackage", "Review", uuid1);
            fail("getTaskByUUID(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type IOException", cause instanceof IOException);
        }
        EasyMock.verify(client);
    }

    public void testGetTaskByUUIDUnknownProblem() throws Exception {
        String uuid1 = UUID.randomUUID().toString();
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        HttpClient client = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).andThrow(new NullPointerException()).once();
        service.getHelper().setClient(client);
        
        EasyMock.replay(client);
        try {
            service.getTaskByUUID("somePackage", "Review", uuid1);
            fail("getTaskByUUID(...) should not succeed");
        } catch (TaskServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be of type NullPointerException", cause instanceof NullPointerException);
        }
        EasyMock.verify(client);
    }
    
    public void testGetBPMN2TaskOK() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        String process1Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest.bpmn2"));
        String processName = "testProcess.bpmn2";
        String taskName = "Review";
        
        TaskRef task = service.getBPMN2Task(process1Content, processName, taskName);
        assertNotNull("task shouldn't be null", task);
        assertNotNull("task.taskId shouldn't be null", task.getTaskId());
        assertNotNull("task.processId shouldn't be null", task.getProcessId());
        assertEquals("task.taskName should be the same as taskName", taskName, task.getTaskName());
    }
    
    public void testGetBPMN2TaskInvalidProcess() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        String process1Content = "";
        String processId = "AFileThatDoesntBelongHere.txt";
        String taskId = "Review";
        
        TaskRef task = service.getBPMN2Task(process1Content, processId, taskId);
        assertNull("task should be null", task);
    }
    
    public void testGetBPMN2TaskNoTasks() throws Exception {
        GuvnorTaskDefinitionService service = createService(baseUrl, "", "");
        String process1Content = IOUtils.toString(getClass().getResourceAsStream("GuvnorGetProcessTasksTest2.bpmn2"));
        String processId = "testProcess.bpmn2";
        String taskId = "ATaskThatDoesntExist";
        
        TaskRef task = service.getBPMN2Task(process1Content, processId, taskId);
        assertNull("task should be null", task);
    }
    
    private GuvnorTaskDefinitionService createService(String baseUrl, String user, String password) {
        GuvnorTaskDefinitionService service = new GuvnorTaskDefinitionService();
        service.setHelper(new GuvnorHelper(baseUrl, user, password) {
            @Override
            public GetMethod createGetMethod(String url) {
                return new MockGetMethod(url);
            }
            @Override
            public PostMethod createPostMethod(String url) {
                return new MockPostMethod(url);
            }
            @Override
            public DeleteMethod createDeleteMethod(String url) {
                return new MockDeleteMethod(url);
            }
            @Override
            public PutMethod createPutMethod(String url) {
                return new MockPutMethod(url);
            }
        });
        return service;
    }
}
