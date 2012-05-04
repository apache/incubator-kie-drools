package org.jbpm.task.service.test;

import static org.jbpm.task.service.test.impl.TestServerUtil.*;

import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServiceTaskAttributesBaseUserGroupCallbackTest;
import org.jbpm.task.service.test.impl.TestTaskServer;

public class TaskServiceTaskAttributesUserGroupCallbackTest extends TaskServiceTaskAttributesBaseUserGroupCallbackTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        server = startServer(taskService);

        client = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client.connect();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }
}
