package org.jbpm.task.service.hornetq.async;

import org.jbpm.task.service.base.async.TaskServiceTaskAttributesBaseUserGroupCallbackAsyncTest;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;


public class TaskServiceTaskAttributesHornetQUserGroupCallbackAsyncTest extends TaskServiceTaskAttributesBaseUserGroupCallbackAsyncTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new HornetQTaskServer(taskService, 5445);
        System.out.println("Waiting for the HornetQTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }

        client = new AsyncHornetQTaskClient();
        client.connect("127.0.0.1", 5445);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }
}
