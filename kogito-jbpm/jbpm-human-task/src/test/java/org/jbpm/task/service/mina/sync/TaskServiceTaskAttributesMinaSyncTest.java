package org.jbpm.task.service.mina.sync;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.service.AsyncTaskServiceWrapper;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.base.sync.TaskServiceTaskAttributesBaseSyncTest;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.mina.MinaTaskServer;

public class TaskServiceTaskAttributesMinaSyncTest extends TaskServiceTaskAttributesBaseSyncTest {

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer( taskService );
        Thread thread = new Thread( server );
        thread.start();
        System.out.println("Waiting for the MinaTask Server to come up");
        while (!server.isRunning()) {
        	System.out.print(".");
        	Thread.sleep( 50 );
        }
        client = new AsyncTaskServiceWrapper( new TaskClient(new MinaTaskClientConnector("client 1",
                                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener()))));
        client.connect("127.0.0.1", 9123);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }
}
