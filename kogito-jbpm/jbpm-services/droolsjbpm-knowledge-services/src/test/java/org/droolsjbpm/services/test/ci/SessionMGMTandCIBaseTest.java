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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import org.apache.commons.io.IOUtils;
import org.drools.core.util.FileManager;
import org.drools.impl.EnvironmentFactory;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.wih.CDIHTWorkItemHandler;
import org.junit.Test;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.scanner.MavenRepository;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.kie.KieBase;
import org.kie.builder.KieScanner;
import org.kie.builder.impl.InternalKieModule;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.process.ProcessInstance;
import static org.kie.scanner.MavenRepository.getMavenRepository;

/**
 *
 * @author salaboy
 */
public abstract class SessionMGMTandCIBaseTest extends AbstractKieCiTest {

  protected FileManager fileManager;
  private File kPom;
  @Inject
  protected TaskServiceEntryPoint taskService;
  @Inject
  private CDIHTWorkItemHandler handler;
  @Inject
  protected KnowledgeAdminDataService adminDataService;
  @Inject
  private EntityManager em;
  @Inject
  private EntityManagerFactory emf;

  public SessionMGMTandCIBaseTest() {
  }

  protected void resetFileManager() {
    this.fileManager.tearDown();
    this.fileManager = new FileManager();
    this.fileManager.setUp();
  }

  @Test @Ignore
  public void simpleCITest() throws IOException {
    KieServices ks = KieServices.Factory.get();
    ReleaseId releaseId = ks.newReleaseId("org.jbpm", "myprocesses", "1.0-SNAPSHOT");
    kPom = createKPom(releaseId);


    InternalKieModule kJar1 = createKieJar(ks, releaseId, "support",
            IOUtils.toString(new FileReader("src/test/resources/repo/examples/support/support.bpmn")));
    KieContainer kieContainer = ks.newKieContainer(releaseId);


    MavenRepository repository = getMavenRepository();
    repository.deployArtifact(releaseId, kJar1, kPom);

    KieScanner scanner = ks.newKieScanner(kieContainer);

    scanner.scanNow();

    Environment env = EnvironmentFactory.newEnvironment();
    UserTransaction ut = setupEnvironment(env);
    
    KieBase kieBase = kieContainer.getKieBase("KBase1");
    KieSession ksession1 = JPAKnowledgeService.newStatefulKnowledgeSession(kieBase, null, env);
    completeOperation(ut, null);
//    KieSession ksession1 = kieContainer.newKieSession("KSession1");
    handler.addSession(ksession1);
    ksession1.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("customer", "salaboy");
    
    

    ProcessInstance startProcess = ksession1.startProcess("support.process", params);

    assertNotNull(startProcess);

    // Configure Release
    List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

    assertEquals(1, tasksAssignedToSalaboy.size());
    assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());


  }

  private File createKPom(ReleaseId releaseId) throws IOException {
    File pomFile = fileManager.newFile("pom.xml");
    fileManager.write(pomFile, getPom(releaseId));
    return pomFile;
  }

  
  /*
   * following are supporting methods to allow execution on application startup
   * as at that time RequestScoped entity manager cannot be used so instead
   * use EntityMnagerFactory and manage transaction manually
   */
  protected EntityManager getEntityManager() {
      try {
          this.em.toString();          
          return this.em;
      } catch (ContextNotActiveException e) {
          EntityManager em = this.emf.createEntityManager();
          return em;
      }
  }
  
  protected UserTransaction setupEnvironment(Environment environment) {
      UserTransaction ut = null;
      try {
          this.em.toString();
          environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.em.getEntityManagerFactory());
      } catch (ContextNotActiveException e) {
          try {
              ut = InitialContext.doLookup("java:comp/UserTransaction");
          } catch (Exception ex) {
              try {
                  ut = InitialContext.doLookup(System.getProperty("jbpm.ut.jndi.lookup", "java:jboss/UserTransaction"));
                  
              } catch (Exception e1) {
                  throw new RuntimeException("Cannot find UserTransaction", e1);
              }
          }
          try {
              ut.begin();
              environment.set(EnvironmentName.TRANSACTION, ut);
          } catch (Exception ex) {
              
          }
          environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.emf);
      }
      
      return ut;
  }
  protected void completeOperation(UserTransaction ut, EntityManager entityManager) {
      if (ut != null) {
          try {
              ut.commit();
              if (entityManager != null) {
                  entityManager.clear();
                  entityManager.close();
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }

}
