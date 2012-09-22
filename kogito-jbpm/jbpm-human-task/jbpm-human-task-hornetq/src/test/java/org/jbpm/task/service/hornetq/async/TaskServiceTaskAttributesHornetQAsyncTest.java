package org.jbpm.task.service.hornetq.async;

import org.jbpm.task.service.base.async.TaskServiceTaskAttributesBaseAsyncTest;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;

public class TaskServiceTaskAttributesHornetQAsyncTest extends TaskServiceTaskAttributesBaseAsyncTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		server = new HornetQTaskServer(taskService, 5445);
		Thread thread = new Thread(server);
		thread.start();
		System.out.println("Waiting for the HornetQTask Server to come up");
        while (!server.isRunning()) {
        	System.out.print(".");
        	Thread.sleep( 50 );
        }

		client = new AsyncHornetQTaskClient();
		client.connect("127.0.0.1", 5445);
	}

}
