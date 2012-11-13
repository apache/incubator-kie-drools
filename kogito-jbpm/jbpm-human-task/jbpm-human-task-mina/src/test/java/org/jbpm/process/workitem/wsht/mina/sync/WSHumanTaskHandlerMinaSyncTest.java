///**
// * Copyright 2010 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.process.workitem.wsht.mina.sync;
//
//import org.kie.SystemEventListenerFactory;
//import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
//import org.jbpm.process.workitem.wsht.sync.WSHumanTaskHandlerBaseSyncTest;
//import org.jbpm.task.service.AsyncTaskServiceWrapper;
//import org.jbpm.task.service.TaskClient;
//import org.jbpm.task.service.TaskServer;
//import org.jbpm.task.service.mina.MinaTaskClientConnector;
//import org.jbpm.task.service.mina.MinaTaskClientHandler;
//import org.jbpm.task.service.mina.MinaTaskServer;
//
//public class WSHumanTaskHandlerMinaSyncTest extends WSHumanTaskHandlerBaseSyncTest {
//
//    private TaskServer server;
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        server = new MinaTaskServer(taskService);
//        Thread thread = new Thread(server);
//        thread.start();
//        System.out.println("Waiting for the Mina Server to come up");
//        while (!server.isRunning()) {
//            System.out.print(".");
//            Thread.sleep(50);
//        }
//        setClient(new AsyncTaskServiceWrapper(new TaskClient(new MinaTaskClientConnector("client 1",
//                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())))));
//        
//        setHandler(new SyncWSHumanTaskHandler(getClient(), ksession));
//    }
//
//    protected void tearDown() throws Exception {
//        ((SyncWSHumanTaskHandler) getHandler()).dispose();
//        getClient().disconnect();
//        server.stop();
//        super.tearDown();
//    }
//}
