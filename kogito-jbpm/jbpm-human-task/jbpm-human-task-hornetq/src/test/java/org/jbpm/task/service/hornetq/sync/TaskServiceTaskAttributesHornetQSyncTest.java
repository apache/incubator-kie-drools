package org.jbpm.task.service.hornetq.sync;

import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.base.sync.TaskServiceTaskAttributesBaseSyncTest;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;

public class TaskServiceTaskAttributesHornetQSyncTest extends TaskServiceTaskAttributesBaseSyncTest {

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

        client = new SyncTaskServiceWrapper(new AsyncHornetQTaskClient());
        client.connect("127.0.0.1", 5445);
    }

}
