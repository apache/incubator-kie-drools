package org.jbpm.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;


public class ProcessKModuleMain {

   public static void main(String[] args) {

       RuntimeManager manager = createRuntimeManager();
       RuntimeEngine engine = manager.getRuntimeEngine(null);
       KieSession ksession = engine.getKieSession();
       TaskService taskService = engine.getTaskService();

       ksession.startProcess("com.sample.bpmn.hello");

       // let john execute Task 1
       List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
       TaskSummary task = list.get(0);
       System.out.println("John is executing task " + task.getName());
       taskService.start(task.getId(), "john");
       taskService.complete(task.getId(), "john", null);

       // let mary execute Task 2
       list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
       task = list.get(0);
       System.out.println("Mary is executing task " + task.getName());
       taskService.start(task.getId(), "mary");
       taskService.complete(task.getId(), "mary", null);

       manager.disposeRuntimeEngine(engine);
       System.exit(0);
   }

   private static RuntimeManager createRuntimeManager() {
	   cleanupSingletonSessionId();
       JBPMHelper.startH2Server();
       JBPMHelper.setupDataSource();
       EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
       RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
           .newClasspathKmoduleDefaultBuilder()
           .entityManagerFactory(emf)
           .userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroups.properties"));
       return RuntimeManagerFactory.Factory.get()
           .newSingletonRuntimeManager(builder.get(), "com.sample:example:1.0");
   }
   
   private static void cleanupSingletonSessionId() {
       File tempDir = new File(System.getProperty("java.io.tmpdir"));
       if (tempDir.exists()) {
           
           String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
               
               @Override
               public boolean accept(File dir, String name) {
                   
                   return name.endsWith("-jbpmSessionId.ser");
               }
           });
           for (String file : jbpmSerFiles) {
               
               new File(tempDir, file).delete();
           }
       }
   }

}
