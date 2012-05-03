//package org.jbpm.task.service.mina;
//
//import org.jbpm.task.service.TaskServiceTaskAttributesBaseTest;
//
//public class TaskServiceTaskAttributesMinaTest extends TaskServiceTaskAttributesBaseTest {
//
//	@Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        server = new MinaTaskServer( taskService );
//        Thread thread = new Thread( server );
//        thread.start();
//        System.out.println("Waiting for the MinaTask Server to come up");
//        while (!server.isRunning()) {
//        	System.out.print(".");
//        	Thread.sleep( 50 );
//        }
//        client = new AsyncMinaTaskClient();
//        client.connect("127.0.0.1", 9123);
//    }
//
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        client.disconnect();
//        server.stop();
//    }
//}
