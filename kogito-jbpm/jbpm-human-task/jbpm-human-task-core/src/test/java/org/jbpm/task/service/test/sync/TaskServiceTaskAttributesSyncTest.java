package org.jbpm.task.service.test.sync;

import static org.jbpm.task.service.test.impl.TestServerUtil.createTestTaskClientConnector;
import static org.jbpm.task.service.test.impl.TestServerUtil.startServer;

import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.base.sync.TaskServiceTaskAttributesBaseSyncTest;
import org.jbpm.task.service.test.impl.TestTaskServer;

public class TaskServiceTaskAttributesSyncTest extends TaskServiceTaskAttributesBaseSyncTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        server = startServer(taskService);

        TaskClient taskClient = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client = new SyncTaskServiceWrapper(taskClient);
        client.connect();
    }

}
