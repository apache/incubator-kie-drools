/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.test.functional.workitem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.process.instance.WorkItemManager;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

import static org.junit.Assert.*;

public class RetrySyncWorkItemByJPATest extends JbpmTestCase {

    private static final String RETRY_WORKITEM_JPA_PROCESS_ID = "org.jbpm.test.retryWorkitem-jpa";

    public RetrySyncWorkItemByJPATest() {
        super( true, true );
    }

    @Test
    public void workItemRecoveryTestByJPA() {
        manager = createRuntimeManager( "org/jbpm/test/functional/workitem/retry-workitem-jpa.bpmn2" );
        RuntimeEngine runtimeEngine = getRuntimeEngine( EmptyContext.get() );
        KieSession kieSession = runtimeEngine.getKieSession();
        WorkItemManager workItemManager = (org.drools.core.process.instance.WorkItemManager) kieSession.getWorkItemManager();

        workItemManager.registerWorkItemHandler( "ExceptionWorkitem", new ExceptionWorkItemHandler() );
        ProcessInstance pi = kieSession.startProcess( RETRY_WORKITEM_JPA_PROCESS_ID );
        TaskService taskService = runtimeEngine.getTaskService();

        assertProcessInstanceActive( pi.getId() );
        assertNodeTriggered( pi.getId(), "lockingNode" );
        assertNodeActive( pi.getId(), kieSession, "lockingNode" );

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner( "john", "en-UK" );
        assertEquals( 1, tasks.size() );
        TaskSummary taskSummary = tasks.get( 0 );

        taskService.start( taskSummary.getId(), "john" );
        taskService.complete( taskSummary.getId(), "john", null );

        ProcessInstance processInstance = (org.kie.api.runtime.process.WorkflowProcessInstance) kieSession.getProcessInstance( pi.getId() );
        Collection<NodeInstance> nis = ((org.kie.api.runtime.process.WorkflowProcessInstance) processInstance).getNodeInstances();
        retryWorkItem( workItemManager, nis );
        assertProcessInstanceCompleted( processInstance.getId() );
    }

    private void retryWorkItem( WorkItemManager workItemManager, Collection<NodeInstance> nis ) {
        for ( NodeInstance di : nis ) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put( "exception", "no" );
            workItemManager.retryWorkItem( di.getId(), map );
        }
    }
}