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

package org.jbpm.test.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.assertj.core.api.Assertions;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;
import org.junit.After;
import org.junit.Before;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.query.QueryContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public abstract class AbstractRuntimeEJBServicesTest extends AbstractEJBServicesTest {

    private static DeploymentService staticDeploymentService;

    @EJB
    protected ProcessServiceEJBLocal processService;

    @EJB
    protected UserTaskServiceEJBLocal userTaskService;

    protected static String kieJar;

    @Before
    public void testRuntimeEJBs() {
        Assertions.assertThat(processService).isNotNull();
        Assertions.assertThat(userTaskService).isNotNull();

        archive.setProcessService(processService);
    }

    @Before
    public void saveDeploymentService() {
        staticDeploymentService = deploymentService;
    }

    @Before
    public void deployKieJar() {
        kieJar = archive.deployBasicKieJar().getIdentifier();
    }

    @After
    @Override
    public void cleanup() {
        
        List<Long> pids = archive.getPids();
        List<Long> all = (List<Long>) ((ArrayList<Long>) pids).clone();
        for (Long pid : all) {
            ProcessInstanceDesc pi = runtimeDataService.getProcessInstanceById(pid);
            if (pi == null || pi.getState() != ProcessInstance.STATE_ACTIVE) {
                pids.remove(pid);
            }
        }
        if (!pids.isEmpty()) {
            processService.abortProcessInstances(pids);
        }
        pids.clear();


        cleanupSingletonSessionId();
        List<DeploymentUnit> units = archive.getUnits();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                //clear audit logs
                RuntimeManager manager = deploymentService.getRuntimeManager(unit.getIdentifier());
                RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                engine.getAuditService().clear();
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        kieJar = null;
    }

    public Long startProcessInstance(String processId) {
        return startProcessInstance(processId, new HashMap<String, Object>());
    }

    public Long startProcessInstance(String processId, Map<String, Object> params) {
        return archive.startProcess(kieJar, processId, params);
    }

    public void abortProcessInstance(Long processInstanceId) {
        processService.abortProcessInstance(processInstanceId);
    }

    public boolean hasNodeLeft(Long processInstanceId, String nodeName) {
        List<NodeInstanceDesc> processInstanceHistory = getProcessInstanceHistory(processInstanceId);

        for (NodeInstanceDesc node : processInstanceHistory) {
            if (node.getName() != null && node.getName().equals(nodeName)) {
                // The history contains also records of a node when it was not
                // yet
                // completed.
                if (node.isCompleted()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasProcessInstanceCompleted(Long processInstanceId) {
        ProcessInstance processInstance = processService.getProcessInstance(processInstanceId);
        if (processInstance != null) {
            return processInstance.getState() == ProcessInstance.STATE_COMPLETED;
        }
        return true;
    }

    public boolean hasTaskCompleted(Long taskId) {
        return runtimeDataService.getTaskById(taskId).getStatus().equals(org.kie.api.task.model.Status.Completed);
    }

    public List<NodeInstanceDesc> getProcessInstanceHistory(Long processInstanceId) {
        return (List<NodeInstanceDesc>) runtimeDataService.getProcessInstanceFullHistory(processInstanceId, new QueryContext(0, 40));
    }
}
