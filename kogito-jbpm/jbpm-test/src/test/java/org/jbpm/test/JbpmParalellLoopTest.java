/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

/**
 * This is a sample file to launch a process.
 */
public class JbpmParalellLoopTest extends JbpmJUnitBaseTestCase {

  TaskService taskService;

  @Test
  public void testProcessMaryReject() {

    RuntimeManager manager = createRuntimeManager("JbpmParalellLoopTest.bpmn");
    RuntimeEngine engine = getRuntimeEngine(null);
    KieSession ksession = engine.getKieSession();

    taskService = engine.getTaskService();

    ProcessInstance processInstance = ksession.startProcess("hu.tsm.ParalellLoopTest");

    assertProcessInstanceActive(processInstance.getId());

    for (int i = 0; i < 20; i++) {

      System.out.println(">>> Loop: " + i);

      assertNodeTriggered(processInstance.getId(), "ApproveMary");
      assertNodeTriggered(processInstance.getId(), "ApproveJohn");

      String user = "mary";
      complete(user, "Reject");

    }

    // assertNodeTriggered(processInstance.getId(), "ApproveMary");
    // assertNodeTriggered(processInstance.getId(), "ApproveJohn");

    // A Jóváhagyás folyamatnak vége
    complete("john", "Approve");
    complete("mary", "Approve");

    assertProcessInstanceCompleted(processInstance.getId());

    manager.disposeRuntimeEngine(engine);
    manager.close();
  }

  @Test
  public void testProcessJohnReject() {

    RuntimeManager manager = createRuntimeManager("JbpmParalellLoopTest.bpmn");
    RuntimeEngine engine = getRuntimeEngine(null);
    KieSession ksession = engine.getKieSession();

    // ksession.addEventListener(new TestProcessEventListener());

    taskService = engine.getTaskService();

    ProcessInstance processInstance = ksession.startProcess("hu.tsm.ParalellLoopTest");

    assertProcessInstanceActive(processInstance.getId());

    for (int i = 0; i < 20; i++) {

      System.out.println(">>> Loop: " + i);


      assertNodeTriggered(processInstance.getId(), "ApproveMary");
      assertNodeTriggered(processInstance.getId(), "ApproveJohn");

      String user = "john";
      complete(user, "Reject");

    }

    // assertNodeTriggered(processInstance.getId(), "ApproveMary");
    // assertNodeTriggered(processInstance.getId(), "ApproveJohn");

    // A Jóváhagyás folyamatnak vége
    complete("john", "Approve");
    complete("mary", "Approve");

    assertProcessInstanceCompleted(processInstance.getId());

    manager.disposeRuntimeEngine(engine);
    manager.close();
  }

  @Test
  public void testProcessMaryApproveJohnReject() {

    RuntimeManager manager = createRuntimeManager("JbpmParalellLoopTest.bpmn");
    RuntimeEngine engine = getRuntimeEngine(null);
    KieSession ksession = engine.getKieSession();

    // ksession.addEventListener(new TestProcessEventListener());

    taskService = engine.getTaskService();

    ProcessInstance processInstance = ksession.startProcess("hu.tsm.ParalellLoopTest");

    assertProcessInstanceActive(processInstance.getId());

    for (int i = 0; i < 20; i++) {

      System.out.println(">>> Loop: " + i);

      complete("mary", "Approve");
      complete("john", "Reject");

    }

    // A Jóváhagyás folyamatnak vége
    complete("john", "Approve");
    complete("mary", "Approve");

    assertProcessInstanceCompleted(processInstance.getId());

    manager.disposeRuntimeEngine(engine);
    manager.close();
  }

  @Test
  public void testProcessJohnApproveMaryReject() {

    RuntimeManager manager = createRuntimeManager("JbpmParalellLoopTest.bpmn");
    RuntimeEngine engine = getRuntimeEngine(null);
    KieSession ksession = engine.getKieSession();

    // ksession.addEventListener(new TestProcessEventListener());

    taskService = engine.getTaskService();

    ProcessInstance processInstance = ksession.startProcess("hu.tsm.ParalellLoopTest");

    assertProcessInstanceActive(processInstance.getId());

    for (int i = 0; i < 20; i++) {

      System.out.println(">>> Loop: " + i);

      complete("john", "Approve");
      complete("mary", "Reject");

    }

    // A Jóváhagyás folyamatnak vége
    complete("john", "Approve");
    complete("mary", "Approve");

    assertProcessInstanceCompleted(processInstance.getId());

    manager.disposeRuntimeEngine(engine);
    manager.close();
  }

  @Test
  public void testProcessAlternateReject() {

    RuntimeManager manager = createRuntimeManager("JbpmParalellLoopTest.bpmn");
    RuntimeEngine engine = getRuntimeEngine(null);
    KieSession ksession = engine.getKieSession();

    // ksession.addEventListener(new TestProcessEventListener());

    taskService = engine.getTaskService();

    ProcessInstance processInstance = ksession.startProcess("hu.tsm.ParalellLoopTest");

    assertProcessInstanceActive(processInstance.getId());

    for (int i = 0; i < 20; i++) {

      System.out.println(">>> Loop: " + i);

      assertNodeTriggered(processInstance.getId(), "ApproveMary");
      assertNodeTriggered(processInstance.getId(), "ApproveJohn");

      String user = "mary";
      boolean alternate = true;
      if (alternate && i % 2 == 1) {
        user = "john";
      }

      complete(user, "Reject");

    }
    
    complete("john", "Approve");
    complete("mary", "Approve");

    assertProcessInstanceCompleted(processInstance.getId());

    manager.disposeRuntimeEngine(engine);
    manager.close();
  }

  @Test
  public void testProcessMaryApproveJohnApprove() {

    RuntimeManager manager = createRuntimeManager("JbpmParalellLoopTest.bpmn");
    RuntimeEngine engine = getRuntimeEngine(null);
    KieSession ksession = engine.getKieSession();

    // ksession.addEventListener(new TestProcessEventListener());

    taskService = engine.getTaskService();

    ProcessInstance processInstance = ksession.startProcess("hu.tsm.ParalellLoopTest");

    assertProcessInstanceActive(processInstance.getId());

    complete("john", "Approve");
    complete("mary", "Approve");

    assertProcessInstanceCompleted(processInstance.getId());

    manager.disposeRuntimeEngine(engine);
    manager.close();
  }


  private void complete(String user, String outcome) {

    List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");
    TaskSummary task = list.get(0);
    System.out.println("complete task");
    System.out.println("- " + user + " is executing task " + task.getName());
    taskService.start(task.getId(), user);

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("OUTCOME", outcome);
    taskService.complete(task.getId(), user, map);
    System.out.println("- " + user + " executed task " + task.getName());
  }

  public JbpmParalellLoopTest() {
    super(true, true);
  }

}