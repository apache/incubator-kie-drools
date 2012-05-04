//package org.jbpm.task.service.hornetq;
//
//import org.jbpm.task.service.TaskServiceTaskAttributesBaseTest;
//
//public class TaskServiceTaskAttributesHornetQTest extends TaskServiceTaskAttributesBaseTest {
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		server = new HornetQTaskServer(taskService, 5446);
//		Thread thread = new Thread(server);
//		thread.start();
//		System.out.println("Waiting for the HornetQTask Server to come up");
//        while (!server.isRunning()) {
//        	System.out.print(".");
//        	Thread.sleep( 50 );
//        }
//
//		client = new AsyncHornetQTaskClient();
//		client.connect("127.0.0.1", 5446);
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		client.disconnect();
//		server.stop();
//	}
//
//}
