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
//package org.jbpm.task;
//
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.EntityManager;
//
//import org.kie.SystemEventListenerFactory;
//import org.jbpm.task.impl.factories.TaskFactory;
//import org.jbpm.task.service.MockEscalatedDeadlineHandler;
//
//
//public abstract class TaskServiceEscalationBaseTest extends BaseTest {
//
// 
//
//    
//
//    public void testUnescalatedDeadlines() throws Exception {
//        
//
////        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
//        //taskService.setEscalatedDeadlineHandler(handler);
//
//        //Reader reader;
//        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.UnescalatedDeadlines));
//        List<Task> tasks = (List<Task>) TaskFactory.evalTask(reader);
//        long now = new Date().getTime();
//
//        for (Task task : tasks) {
//            taskService.addTask(task, new HashMap<String,Object>());
//        }
//
////        testDeadlines(now, handler); 
//    }
//
//    public void testUnescalatedDeadlinesOnStartup() throws Exception {
//      
//
//        //Reader reader;
//        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.UnescalatedDeadlines));
//        List<Task> tasks = (List<Task>) eval(reader,
//                vars);
//        long now = ((Date) vars.get("now")).getTime();
//
//        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();
//        for (Task task : tasks) {
//            // for this one we put the task in directly;
//            em.persist(task);
//        }
//        em.getTransaction().commit();
//
//        // now create a new service, to see if it initiates from the DB correctly
//        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
//        org.jbpm.task.service.TaskService local = new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener(), handler);
//
//        testDeadlines(now, handler);
//    }
//
//}
