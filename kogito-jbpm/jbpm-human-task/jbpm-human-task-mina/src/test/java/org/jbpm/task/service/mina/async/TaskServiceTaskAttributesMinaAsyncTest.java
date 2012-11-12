package org.jbpm.task.service.mina.async;

import org.jbpm.task.service.base.async.TaskServiceTaskAttributesBaseAsyncTest;
import org.jbpm.task.service.mina.AsyncMinaTaskClient;
import org.jbpm.task.service.mina.MinaTaskServer;

public class TaskServiceTaskAttributesMinaAsyncTest extends TaskServiceTaskAttributesBaseAsyncTest {

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
        client = new AsyncMinaTaskClient();
        client.connect("127.0.0.1", 9123);
    }

}
