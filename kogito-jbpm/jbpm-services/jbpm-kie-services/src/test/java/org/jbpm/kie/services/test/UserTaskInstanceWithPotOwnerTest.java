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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.kie.services.impl.query.builder.UserTaskPotOwnerQueryBuilderFactory;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithModifVarsQueryMapper;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.model.QueryParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.services.api.query.QueryResultMapper.*;
import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithPotOwnerQueryMapper;


public class UserTaskInstanceWithPotOwnerTest extends AbstractKieServicesBaseTest {
    
    
    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected String correctUser = "salaboy";
    protected String wrongUser = "wrongUser";

    protected Long processInstanceId = null;
    protected KModuleDeploymentUnit deploymentUnit = null;
    protected KModuleDeploymentUnit deploymentUnitJPA = null;

    protected QueryDefinition query;

    protected String dataSourceJNDIname;

    @Before
    public void prepare() {
        System.setProperty("org.jbpm.ht.callback", "custom");
        System.setProperty("org.jbpm.ht.custom.callback", "org.jbpm.kie.services.test.objects.TestUserGroupCallbackImpl");

        this.dataSourceJNDIname = getDataSourceJNDI();
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/EmptyHumanTask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/BPMN2-UserTask.bpmn2");
        processes.add("repo/processes/general/SimpleHTProcess.bpmn2");
        processes.add("repo/processes/general/AdHocSubProcess.bpmn2");
        processes.add("repo/processes/general/ExcludedOwner.bpmn2");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder().addRequiredRole("view:managers");

        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        assertNotNull(deploymentService);

        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        prepareJPAModule(ks, repository);

        assertNotNull(processService);
    }

    protected void prepareJPAModule(KieServices ks, KieMavenRepository repository) {
        // jpa module
        ReleaseId releaseIdJPA = ks.newReleaseId("org.jbpm.test", "persistence-test", "1.0.0");
        File kjarJPA = new File("src/test/resources/kjar-jpa/persistence-test.jar");
        File pomJPA = new File("src/test/resources/kjar-jpa/pom.xml");

        repository.installArtifact(releaseIdJPA, kjarJPA, pomJPA);

        deploymentUnitJPA = new KModuleDeploymentUnit("org.jbpm.test", "persistence-test", "1.0.0");
    }

    protected String getDataSourceJNDI() {
        return "jdbc/testDS1";
    }

    @After
    public void cleanup() {
        System.clearProperty("org.jbpm.ht.callback");
        System.clearProperty("org.jbpm.ht.custom.callback");

        if (query != null) {
            try {
                queryService.unregisterQuery(query.getName());
            } catch (QueryNotFoundException e) {

            }
        }

        if (processInstanceId != null) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);

                ProcessInstance pi = processService.getProcessInstance(processInstanceId);
                assertNull(pi);
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it was already completed/aborted
            }
        }
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }
        close();
    }
    
    @Test
    public void testSearchTaskByPotOwnerMapper() {
        query = new SqlQueryDefinition("jbpmHumanTasksPO", dataSourceJNDIname);
        query.setExpression("select t.actualowner_id as actualowner, t.CREATEDBY_ID as createdby, t.CREATEDON as CREATEDON, t.EXPIRATIONTIME as expirationDate, " +
                "t.id as TASKID, t.name as NAME, t.priority as PRIORITY, t.PROCESSINSTANCEID as PROCESSINSTANCEID, t.PROCESSID as PROCESSID, t.STATUS as STATUS,  " +
                "po.entity_id as POTOWNER, t.FORMNAME AS FORMNAME, p.processinstancedescription as PROCESSINSTANCEDESCRIPTION, t.subject as SUBJECT, t.deploymentid as DEPLOYMENTID " +
                "from TASK t " +
                "inner join PEOPLEASSIGNMENTS_POTOWNERS po on t.id=po.task_id " +
                "inner join PROCESSINSTANCELOG p on t.processinstanceid = p.processinstanceid");

        queryService.registerQuery(query);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<UserTaskInstanceWithPotOwnerDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithPotOwnerQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithPotOwnerQueryMapper.get(), new QueryContext(),QueryParam.equalsTo(COLUMN_POTOWNER, wrongUser));
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithPotOwnerQueryMapper.get(), new QueryContext(),QueryParam.equalsTo(COLUMN_POTOWNER, correctUser));
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());


        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        
    }
    
    @Test
    public void testSearchTaskByPotOwnerQueryParamBuilder() {
        query = new SqlQueryDefinition("jbpmHumanTasksPO", dataSourceJNDIname);
        query.setExpression("select t.actualowner_id as actualowner, t.CREATEDBY_ID as createdby, t.CREATEDON as CREATEDON, t.EXPIRATIONTIME as expirationDate, " +
                "t.id as TASKID, t.name as NAME, t.priority as PRIORITY, t.PROCESSINSTANCEID as PROCESSINSTANCEID, t.PROCESSID as PROCESSID, t.STATUS as STATUS,  " +
                "po.entity_id as POTOWNER, t.FORMNAME AS FORMNAME, p.processinstancedescription as PROCESSINSTANCEDESCRIPTION, t.subject as SUBJECT, t.deploymentid as DEPLOYMENTID " +
                "from TASK t " +
                "inner join PEOPLEASSIGNMENTS_POTOWNERS po on t.id=po.task_id " +
                "inner join PROCESSINSTANCELOG p on t.processinstanceid = p.processinstanceid");

        queryService.registerQuery(query);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        
        List<UserTaskInstanceWithPotOwnerDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithPotOwnerQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());
        assertNotNull(taskInstanceLogs.get(0).getProcessInstanceDescription());

        QueryParamBuilderFactory qbFactory = new UserTaskPotOwnerQueryBuilderFactory();

        assertTrue(qbFactory.accept("potOwnerBuilder"));

        List<String> potOwners = new ArrayList<String>();
        potOwners.add("salaboy");
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("potOwner", potOwners);
        QueryParamBuilder<?> paramBuilder = qbFactory.newInstance(parameters);

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithPotOwnerQueryMapper.get(), new QueryContext(), paramBuilder);
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());
        
        potOwners = new ArrayList<String>();
        potOwners.add("wrongPotOwner");
        parameters = new HashMap<String, Object>();
        parameters.put("potOwner", potOwners);
        paramBuilder = qbFactory.newInstance(parameters);

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithPotOwnerQueryMapper.get(), new QueryContext(), paramBuilder);
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        
    }
    
    @Test
    public void testSearchTaskWithModifVarsMapper() {
        query = new SqlQueryDefinition("jbpmGetTaskWithPO", dataSourceJNDIname);
        query.setExpression("select t.id as TASKID, t.name as NAME,  t.FORMNAME AS FORMNAME, t.subject as SUBJECT, " +
                "t.actualowner_id as ACTUALOWNER, po.entity_id as POTOWNER, p.processinstancedescription as PROCESSINSTANCEDESCRIPTION, t.CREATEDON as CREATEDON, " +
                "t.CREATEDBY_ID as CREATEDBY, t.EXPIRATIONTIME as EXPIRATIONTIME, " +
                "(select max(logtime) from taskevent where processinstanceid = t.processinstanceid and taskid = t.id) as lastmodificationdate, " +
                "(select userid from taskevent where logtime = (select max(logtime) from taskevent where processinstanceid = t.processinstanceid and taskid = t.id)) as lastmodificationuser, " +
                "t.priority as PRIORITY, t.STATUS as STATUS, t.PROCESSINSTANCEID as PROCESSINSTANCEID, t.PROCESSID as PROCESSID, " +
                "t.deploymentid as DEPLOYMENTID, d.name as TVNAME, d.type as TVTYPE, d.value as TVVALUE " +
                "from TASK t " +
                "inner join PEOPLEASSIGNMENTS_POTOWNERS po on t.id=po.task_id " +
                "inner join PROCESSINSTANCELOG p on t.processinstanceid = p.processinstanceid " +
                "inner join TASKVARIABLEIMPL d on t.id=d.taskid");

        queryService.registerQuery(query);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<UserTaskInstanceWithPotOwnerDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithModifVarsQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithModifVarsQueryMapper.get(), new QueryContext(),QueryParam.equalsTo(COLUMN_POTOWNER, wrongUser));
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithModifVarsQueryMapper.get(), new QueryContext(),QueryParam.equalsTo(COLUMN_POTOWNER, correctUser));
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithModifVarsQueryMapper.get(), new QueryContext(),QueryParam.equalsTo(COLUMN_POTOWNER, correctUser));
        assertNotNull(taskInstanceLogs.get(0).getLastModificationUser());
        assertNotNull(taskInstanceLogs.get(0).getProcessInstanceDescription());
        
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        
    }
}
