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
import java.util.Map;

import org.drools.core.process.instance.WorkItemManager;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class RetrySyncWorkItemByMemoryTest extends JbpmTestCase {

    private static final String RETRY_WORKITEM_MEMORY_PROCESS_ID = "org.jbpm.test.retry-workitem-memory";

    public RetrySyncWorkItemByMemoryTest() {
        super( false, false );
    }

    @Test
    public void workItemRecoveryTestByMemory() {
        manager = createRuntimeManager( "org/jbpm/test/functional/workitem/retry-workitem-memory.bpmn2" );
        RuntimeEngine runtimeEngine = getRuntimeEngine( EmptyContext.get() );
        KieSession kieSession = runtimeEngine.getKieSession();
        WorkItemManager workItemManager = (org.drools.core.process.instance.WorkItemManager) kieSession.getWorkItemManager();

        workItemManager.registerWorkItemHandler( "ExceptionWorkitem", new ExceptionWorkItemHandler() );
        workItemManager.registerWorkItemHandler( "NoExceptionWorkitem", new NoExceptionWorkItemHandler() );
        ProcessInstance pi = kieSession.startProcess( RETRY_WORKITEM_MEMORY_PROCESS_ID );
        org.junit.Assert.assertEquals( pi.getState(), 1 );

        org.kie.api.runtime.process.WorkflowProcessInstance p = (org.kie.api.runtime.process.WorkflowProcessInstance) kieSession.getProcessInstance( pi.getId() );
        Collection<NodeInstance> nis = ((org.kie.api.runtime.process.WorkflowProcessInstance) p).getNodeInstances();
        retryWorkItem( workItemManager, nis );
        org.junit.Assert.assertEquals( p.getState(), 2 );

    }

    private void retryWorkItem( WorkItemManager workItemManager, Collection<NodeInstance> nis ) {
        for ( NodeInstance di : nis ) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put( "exception", "no" );
            workItemManager.retryWorkItem( di.getId(), map );
        }
    }
}