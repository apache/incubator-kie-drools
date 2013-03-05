/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.test.ci;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.SessionManagerImpl;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

import static org.junit.Assert.*;
import org.kie.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
public abstract class SessionMGMTandCIBaseTest extends AbstractKieCiTest {

  @Inject
  protected TaskServiceEntryPoint taskService;
  @Inject
  private BPMN2DataService bpmn2Service;
  @Inject
  protected KnowledgeAdminDataService adminDataService;
  @Inject
  private SessionManagerImpl kieSessionManager;

  public SessionMGMTandCIBaseTest() {
  }

//  @Test @Ignore
//  public void simpleCITest() throws IOException {
//    KieServices ks = KieServices.Factory.get();
//    ReleaseId releaseId = ks.newReleaseId("org.jbpm", "myprocesses", "1.0-SNAPSHOT");
//    kPom = createKPom(releaseId);
//
//
//    InternalKieModule kJar1 = createKieJar(ks, releaseId, "support",
//            IOUtils.toString(new FileReader("src/test/resources/repo/processes/support/support.bpmn")));
//
//    MavenRepository repository = getMavenRepository();
//    repository.deployArtifact(releaseId, kJar1, kPom);
//
//    KieContainer kieContainer = ks.newKieContainer(releaseId);
//    KieScanner scanner = ks.newKieScanner(kieContainer);
//
//    scanner.scanNow();
//
//    Environment env = EnvironmentFactory.newEnvironment();
//    UserTransaction ut = setupEnvironment(env);
//    
//    KieBase kieBase = kieContainer.getKieBase("KBase1");
//    KieSession ksession1 = JPAKnowledgeService.newStatefulKnowledgeSession(kieBase, null, env);
//
//
//    handler.addSession(ksession1);
//    ksession1.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
//    Map<String, Object> params = new HashMap<String, Object>();
//    params.put("customer", "salaboy");
//    
//    
//
//    ProcessInstance startProcess = ksession1.startProcess("support.process", params);
//
//    assertNotNull(startProcess);
//
//    // Configure Release
//    List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
//
//    assertEquals(1, tasksAssignedToSalaboy.size());
//    assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());
//
//
//  }
  @Test
  public void simpleSessionMGMTCITest() throws IOException {

    kieSessionManager.setDomain(new SimpleDomainImpl("myDomain"));

    int ksessionId = kieSessionManager.buildSession("support", "processes/support", true);
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("customer", "salaboy");
    ProcessInstance startProcess = kieSessionManager.getKsessionById(ksessionId).startProcess("support.process", params);

    assertNotNull(startProcess);

    // Configure Release
    List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

    assertEquals(1, tasksAssignedToSalaboy.size());
    assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());

    TaskSummary createSupportTask = tasksAssignedToSalaboy.get(0);

    taskService.start(createSupportTask.getId(), "salaboy");

    Map<String, Object> taskContent = taskService.getTaskContent(createSupportTask.getId());

    assertEquals("salaboy", taskContent.get("input_customer"));

    Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings("support.process", createSupportTask.getName());

    assertEquals(1, taskOutputMappings.size());
    assertEquals("output_customer", taskOutputMappings.values().iterator().next());

    Map<String, Object> output = new HashMap<String, Object>();

    output.put("output_customer", "salaboy@redhat");
    taskService.complete(createSupportTask.getId(), "salaboy", output);

    tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
    assertEquals(1, tasksAssignedToSalaboy.size());

    assertEquals("Resolve Support", tasksAssignedToSalaboy.get(0).getName());
    
    

  }
}
