/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.droolsjbpm.services.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.shared.services.api.Domain;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.RulesNotificationService;
import org.jbpm.shared.services.api.ServicesSessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.droolsjbpm.services.impl.example.NotificationWorkItemHandler;
import org.droolsjbpm.services.impl.example.TriggerTestsWorkItemHandler;
import org.droolsjbpm.services.impl.model.RuleNotificationInstanceDesc;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.QueryResults;
import org.kie.runtime.rule.QueryResultsRow;

public abstract class DomainKnowledgeServiceWithRulesBaseTest {

    @Inject
    protected transient TaskServiceEntryPoint taskService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    @Inject
    private FileService fs;
    @Inject
    private ServicesSessionManager sessionManager;
    @Inject
    private KnowledgeDomainService domainService;
    @Inject
    private TriggerTestsWorkItemHandler triggerTestsWorkItemHandler;
    @Inject
    private NotificationWorkItemHandler notificationWorkItemHandler;
    @Inject
    private transient RulesNotificationService rulesNotificationService;

    
    @Test @Ignore // FIX java.lang.ClassCastException: org.drools.common.DefaultFactHandle cannot be cast to org.drools.common.EventFactHandle
                  // in CDIRuleAwareProcessEventListener line 40
    public void testReleaseProcessWithRules() throws FileException, InterruptedException {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);


        
        sessionManager.buildSession("myKsession", "processes/release/", true);

        sessionManager.addKsessionHandler("myKsession", "MoveToStagingArea", new DoNothingWorkItemHandler());
        sessionManager.addKsessionHandler("myKsession", "MoveToTest", new DoNothingWorkItemHandler());
        sessionManager.addKsessionHandler("myKsession", "TriggerTests", triggerTestsWorkItemHandler);
        sessionManager.addKsessionHandler("myKsession", "MoveBackToStaging", new DoNothingWorkItemHandler());
        sessionManager.addKsessionHandler("myKsession", "MoveToProduction", new DoNothingWorkItemHandler());

        sessionManager.addKsessionHandler("myKsession", "Email", notificationWorkItemHandler);

        sessionManager.registerHandlersForSession("myKsession", 1);

        sessionManager.registerRuleListenerForSession("myKsession", 1);

        sessionManager.getKsessionsByName("myKsession").get(1).setGlobal("rulesNotificationService", rulesNotificationService);

        sessionManager.getKsessionsByName("myKsession").get(1).setGlobal("taskService", taskService);

        // Let's start a couple of processes
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("release_name", "first release");
        params.put("release_path", "/releasePath/");

        ProcessInstance firstPI = sessionManager.getKsessionsByName("myKsession").get(1).startProcess("org.jbpm.release.process", params);

        params = new HashMap<String, Object>();
        params.put("release_name", "second release");
        params.put("release_path", "/releasePath2/");



        ProcessInstance secondPI = sessionManager.getKsessionsByName("myKsession").get(1).startProcess("org.jbpm.release.process", params);

        QueryResults queryResults = sessionManager.getKsessionsByName("myKsession").get(1).getQueryResults("getProcessInstances", new Object[]{});

        for(QueryResultsRow r : queryResults){
          WorkflowProcessInstanceImpl pi = (WorkflowProcessInstanceImpl)r.get("$w");
          System.out.println("PI "+pi);
        }
        
        assertEquals(2, queryResults.size());

        params = new HashMap<String, Object>();
        params.put("release_name", "third release");
        params.put("release_path", "/releasePath/");


        // This process must be automatically aborted because it's using the same release path than the first process.
        ProcessInstance thirdPI = sessionManager.getKsessionsByName("myKsession").get(1).startProcess("org.jbpm.release.process", params);

        assertEquals(ProcessInstance.STATE_ABORTED, thirdPI.getState());

        

        Collection<RuleNotificationInstanceDesc> allNotificationInstance = rulesNotificationService.getAllNotificationInstance();
        assertEquals(1, allNotificationInstance.size());
        
        Collection<RuleNotificationInstanceDesc> notificationsBySessionId = rulesNotificationService.getAllNotificationInstanceBySessionId(1);
        assertEquals(1, notificationsBySessionId.size());



    }

    private class DoNothingWorkItemHandler implements WorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            for (String k : wi.getParameters().keySet()) {
                System.out.println("Key = " + k + " - value = " + wi.getParameter(k));
            }

            wim.completeWorkItem(wi.getId(), null);
        }

        @Override
        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
        }
    }

    private class MockTestWorkItemHandler implements WorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
            for (String k : wi.getParameters().keySet()) {
                System.out.println("Key = " + k + " - value = " + wi.getParameter(k));
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("out_test_successful", "true");
            params.put("out_test_report", "All Test were SUCCESSFULY executed!");
            wim.completeWorkItem(wi.getId(), params);
        }

        @Override
        public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
        }
    }
}
