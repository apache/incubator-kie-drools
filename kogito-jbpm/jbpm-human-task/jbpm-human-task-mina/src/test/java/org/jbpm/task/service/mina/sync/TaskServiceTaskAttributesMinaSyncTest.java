package org.jbpm.task.service.mina.sync;

import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.base.sync.TaskServiceTaskAttributesBaseSyncTest;
import org.jbpm.task.service.mina.AsyncMinaTaskClient;
import org.jbpm.task.service.mina.MinaTaskServer;

public class TaskServiceTaskAttributesMinaSyncTest extends TaskServiceTaskAttributesBaseSyncTest {

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer( taskService );
        System.out.println("Waiting for the MinaTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }
        client = new SyncTaskServiceWrapper( new AsyncMinaTaskClient() );
        client.connect("127.0.0.1", 9123);
    }

}
