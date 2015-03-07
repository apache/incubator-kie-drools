package org.jbpm.examples.evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;

public class EvaluationExample {

    public static final void main(String[] args) {
        try {
            RuntimeManager manager = getRuntimeManager("evaluation/Evaluation.bpmn");        
            RuntimeEngine runtime = manager.getRuntimeEngine(null);
            KieSession ksession = runtime.getKieSession();

            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("employee", "krisv");
            params.put("reason", "Yearly performance evaluation");
    		ksession.startProcess("com.sample.evaluation", params);

    		// complete Self Evaluation
            TaskService taskService = runtime.getTaskService();
    		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
    		TaskSummary task = tasks.get(0);
    		System.out.println("'krisv' completing task " + task.getName() + ": " + task.getDescription());
    		taskService.start(task.getId(), "krisv");
    		Map<String, Object> results = new HashMap<String, Object>();
    		results.put("performance", "exceeding");
    		taskService.complete(task.getId(), "krisv", results);
    		
    		// john from HR
    		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
    		task = tasks.get(0);
    		System.out.println("'john' completing task " + task.getName() + ": " + task.getDescription());
    		taskService.start(task.getId(), "john");
    		results = new HashMap<String, Object>();
    		results.put("performance", "acceptable");
    		taskService.complete(task.getId(), "john", results);
    		
    		// mary from PM
    		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
    		task = tasks.get(0);
    		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
    		taskService.start(task.getId(), "mary");
    		results = new HashMap<String, Object>();
    		results.put("performance", "outstanding");
    		taskService.complete(task.getId(), "mary", results);
    		
    		System.out.println("Process instance completed");
    		
    		manager.disposeRuntimeEngine(runtime);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(0);
    }

    private static RuntimeManager getRuntimeManager(String process) {
        // load up the knowledge base
    	JBPMHelper.startH2Server();
    	JBPMHelper.setupDataSource();
    	Properties properties= new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("john", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
            .userGroupCallback(userGroupCallback)
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource(process), ResourceType.BPMN2)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }
    
}
