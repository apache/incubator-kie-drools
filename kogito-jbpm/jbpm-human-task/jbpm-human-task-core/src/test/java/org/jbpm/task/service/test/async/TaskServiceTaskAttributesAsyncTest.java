package org.jbpm.task.service.test.async;

import static org.jbpm.task.service.test.impl.TestServerUtil.*;

import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.base.async.TaskServiceTaskAttributesBaseAsyncTest;
import org.jbpm.task.service.test.impl.TestTaskServer;

public class TaskServiceTaskAttributesAsyncTest extends TaskServiceTaskAttributesBaseAsyncTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        
        server = startAsyncServer(taskService);

        client = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client.connect();
	}


}
