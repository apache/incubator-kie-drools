package org.jbpm.task.service.test;

import static org.jbpm.task.service.test.impl.TestServerUtil.*;

import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServiceTaskAttributesBaseTest;
import org.jbpm.task.service.test.impl.TestTaskServer;

public class TaskServiceTaskAttributesTest extends TaskServiceTaskAttributesBaseTest {

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
