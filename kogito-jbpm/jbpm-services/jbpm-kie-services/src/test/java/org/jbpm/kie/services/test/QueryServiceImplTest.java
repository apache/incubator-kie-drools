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

import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_PROCESSID;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_PROCESSINSTANCEID;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_PROCESSNAME;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_START;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_STATUS;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_TASK_VAR_NAME;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_TASK_VAR_VALUE;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_VAR_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceWithCustomVarsQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceWithVarsQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.RawListQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.TaskSummaryQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithCustomVarsQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithVarsQueryMapper;
import org.jbpm.kie.services.test.objects.TestQueryParamBuilderFactory;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceWithVarsDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;
import org.jbpm.services.api.query.NamedQueryMapper;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import org.jbpm.services.api.query.model.QueryParam;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.AdvancedQueryContext;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryServiceImplTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);
    private static final String PO_TASK_QUERY = "select ti.activationTime, ti.actualOwner, ti.createdBy, ti.createdOn, ti.deploymentId, " + "ti.description, ti.dueDate, ti.name, ti.parentId, ti.priority, ti.processId, ti.processInstanceId, " + "ti.processSessionId, ti.status, ti.taskId, ti.workItemId, oe.id, eo.entity_id " + "from AuditTaskImpl ti " + "left join PeopleAssignments_PotOwners po on ti.taskId = po.task_id " + "left join OrganizationalEntity oe on po.entity_id = oe.id " + " left join PeopleAssignments_ExclOwners eo on ti.taskId = eo.task_id ";

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected String correctUser = "testUser";
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
    public void testGetProcessInstances() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Collection<ProcessInstanceDesc> instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(1, (int) instances.iterator().next().getState());

        // search using named mapper to refer to query mappers by name
        instances = queryService.query(query.getName(), new NamedQueryMapper<Collection<ProcessInstanceDesc>>("ProcessInstances"), new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(1, (int) instances.iterator().next().getState());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(COLUMN_PROCESSNAME, false));
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(3, (int) instances.iterator().next().getState());
    }

    @Test
    public void testGetProcessInstancesByState() {
        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        Collection<ProcessInstanceDesc> instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        // search for aborted only
        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_STATUS, 3));
        assertNotNull(instances);
        assertEquals(0, instances.size());
        // aborted and active
        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_STATUS, 3, 1));
        assertNotNull(instances);
        assertEquals(1, instances.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        // aborted only
        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_STATUS, 3));
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(3, (int) instances.iterator().next().getState());
    }

    @Test
    public void testGetProcessInstancesByProcessId() {
        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        Collection<ProcessInstanceDesc> instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(), QueryParam.likeTo(COLUMN_PROCESSID, true, "org.jbpm%"));
        assertNotNull(instances);
        assertEquals(1, instances.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesWithVariables() {

        query = new SqlQueryDefinition("getAllProcessInstancesWithVariables", dataSourceJNDIname);
        query.setExpression("select pil.*, v.variableId, v.value " + "from ProcessInstanceLog pil " + "inner join (select vil.processInstanceId ,vil.variableId, MAX(vil.ID) maxvilid  FROM VariableInstanceLog vil " + "GROUP BY vil.processInstanceId, vil.variableId ORDER BY vil.processInstanceId)  x " + "ON (v.variableId = x.variableId  AND v.id = x.maxvilid )" + "INNER JOIN VariableInstanceLog v " + "ON (v.processInstanceId = pil.processInstanceId)");

        queryService.registerQuery(query);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        params.put("approval_translatedDocument", "translated content");
        params.put("approval_reviewComment", "reviewed content");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<ProcessInstanceWithVarsDesc> processInstanceLogs = queryService.query(query.getName(), ProcessInstanceWithVarsQueryMapper.get(), new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        ProcessInstanceWithVarsDesc instance = processInstanceLogs.get(0);
        assertEquals(3, instance.getVariables().size());

        processInstanceLogs = queryService.query(query.getName(), ProcessInstanceWithVarsQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_VAR_NAME, "approval_document"));
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        instance = processInstanceLogs.get(0);
        assertEquals(1, instance.getVariables().size());

        processInstanceLogs = queryService.query(query.getName(), ProcessInstanceWithVarsQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_VAR_NAME, "not existing"));
        assertNotNull(processInstanceLogs);
        assertEquals(0, processInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }

    @Test
    public void testGetTaskInstances() {

        query = new SqlQueryDefinition("getAllTaskInstances", dataSourceJNDIname);
        query.setExpression("select ti.* from AuditTaskImpl ti ");

        queryService.registerQuery(query);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTaskInstancesWithVariables() {

        query = new SqlQueryDefinition("getAllTaskInputInstancesWithVariables", dataSourceJNDIname);
        query.setExpression("select ti.*, tv.name tvname, tv.value tvvalue from AuditTaskImpl ti " + "inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv " + "on (tv.taskId = ti.taskId)");

        queryService.registerQuery(query);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        params.put("approval_translatedDocument", "translated content");
        params.put("approval_reviewComment", "reviewed content");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<UserTaskInstanceWithVarsDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithVarsQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        UserTaskInstanceWithVarsDesc instance = taskInstanceLogs.get(0);
        assertEquals(3, instance.getVariables().size());

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithVarsQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_TASK_VAR_NAME, "Comment"), QueryParam.equalsTo(COLUMN_TASK_VAR_VALUE, "Write a Document"));
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        instance = taskInstanceLogs.get(0);
        assertEquals(1, instance.getVariables().size());

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithVarsQueryMapper.get(), new QueryContext(), QueryParam.equalsTo(COLUMN_TASK_VAR_NAME, "Comment"), QueryParam.equalsTo(COLUMN_TASK_VAR_VALUE, "Wrong Comment"));
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTaskInstancesAsPotOwners() {

        query = new SqlQueryDefinition("getMyTaskInstances", dataSourceJNDIname, Target.PO_TASK);
        query.setExpression(PO_TASK_QUERY);

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        identityProvider.setName("notvalid");

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        identityProvider.setName("salaboy");

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        List<TaskSummary> taskSummaries = queryService.query(query.getName(), TaskSummaryQueryMapper.get(), new QueryContext());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTaskInstancesAsExcludedOwner() {
        final String potentialOwner = "maciej";
        final String excludedOwner = "kris";
        final List<String> roles = Arrays.asList("admins");

        query = new SqlQueryDefinition("getMyTaskInstances", dataSourceJNDIname, Target.PO_TASK);
        query.setExpression(PO_TASK_QUERY);

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.ExcludedOwner", new HashMap<String, Object>());
        assertNotNull(processInstanceId);

        identityProvider.setName(potentialOwner);
        identityProvider.setRoles(roles);

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        identityProvider.setName(excludedOwner);
        identityProvider.setRoles(roles);

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTaskInstancesAsBA() {

        query = new SqlQueryDefinition("getBATaskInstances", dataSourceJNDIname, Target.BA_TASK);
        query.setExpression("select ti.activationTime, ti.actualOwner, ti.createdBy, ti.createdOn, ti.deploymentId, "
                                    + "ti.description, ti.dueDate, ti.name, ti.parentId, ti.priority, ti.processId, ti.processInstanceId, "
                                    + "ti.processSessionId, ti.status, ti.taskId, ti.workItemId, oe.id "
                                    + "from AuditTaskImpl ti," + "PeopleAssignments_BAs bas, "
                                    + "OrganizationalEntity oe "
                                    + "where ti.taskId = bas.task_id and bas.entity_id = oe.id ");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        identityProvider.setName("Administrator");

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        identityProvider.setName("salaboy");
        identityProvider.setRoles(Arrays.asList("Administrators"));

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTaskInstancesWithCustomVariables() throws Exception {

        deploymentService.deploy(deploymentUnitJPA);
        units.add(deploymentUnitJPA);

        query = new SqlQueryDefinition("getAllTaskInstancesWithCustomVariables", dataSourceJNDIname);
        query.setExpression("select ti.*,  c.firstname, c.lastname, c.age, c.customerId from AuditTaskImpl ti " + "inner join (select mv.map_var_id, mv.taskid from MappedVariable mv) mv " + "on (mv.taskid = ti.taskId) " + "inner join Customer c " + "on (c.id = mv.map_var_id)");

        queryService.registerQuery(query);

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnitJPA.getIdentifier());
        assertNotNull(manager);

        Class<?> clazz = Class.forName("org.jbpm.test.Customer", true, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        Object cinstance = clazz.newInstance();
        // set fields
        setFieldValue(cinstance, "firstName", "john");
        setFieldValue(cinstance, "lastName", "doe");
        setFieldValue(cinstance, "age", new Integer(45));
        setFieldValue(cinstance, "customerId", new Long(1234));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", cinstance);

        processInstanceId = processService.startProcess(deploymentUnitJPA.getIdentifier(), "persistence-test.customer-evaluation", params);
        assertNotNull(processInstanceId);

        Map<String, String> variableMap = new HashMap<String, String>();
        variableMap.put("FIRSTNAME", "string");
        variableMap.put("LASTNAME", "string");
        variableMap.put("AGE", "integer");
        variableMap.put("CUSTOMERID", "long");

        List<UserTaskInstanceWithVarsDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithCustomVarsQueryMapper.get(variableMap), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        UserTaskInstanceWithVarsDesc instance = taskInstanceLogs.get(0);
        assertEquals(4, instance.getVariables().size());

        assertTrue(instance.getVariables().containsKey("FIRSTNAME"));
        assertTrue(instance.getVariables().containsKey("LASTNAME"));
        assertTrue(instance.getVariables().containsKey("AGE"));
        assertTrue(instance.getVariables().containsKey("CUSTOMERID"));

        assertEquals("john", instance.getVariables().get("FIRSTNAME"));
        assertEquals("doe", instance.getVariables().get("LASTNAME"));
        assertEquals(45, instance.getVariables().get("AGE"));
        assertEquals(1234l, instance.getVariables().get("CUSTOMERID"));

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesWithQueryParamBuilder() {
        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        Collection<ProcessInstanceDesc> instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        QueryParamBuilderFactory qbFactory = new TestQueryParamBuilderFactory();

        assertTrue(qbFactory.accept("test"));

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("min", processInstanceId);
        parameters.put("max", processInstanceId + 2);
        QueryParamBuilder<?> paramBuilder = qbFactory.newInstance(parameters);

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(), paramBuilder);
        assertNotNull(instances);
        assertEquals(1, instances.size());

        parameters = new HashMap<String, Object>();
        parameters.put("min", processInstanceId + 2);
        parameters.put("max", 0l);
        paramBuilder = qbFactory.newInstance(parameters);

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(), paramBuilder);
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesWithCustomVariables() throws Exception {

        deploymentService.deploy(deploymentUnitJPA);
        units.add(deploymentUnitJPA);

        query = new SqlQueryDefinition("getAllProcessInstancesWithCustomVariables", dataSourceJNDIname);
        query.setExpression("select pi.*,  c.firstname, c.lastname, c.age, c.customerId from ProcessInstanceLog pi " + "inner join (select mv.map_var_id, mv.processInstanceId from MappedVariable mv) mv " + "on (mv.processInstanceId = pi.processinstanceId) " + "inner join Customer c " + "on (c.id = mv.map_var_id)");

        queryService.registerQuery(query);

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnitJPA.getIdentifier());
        assertNotNull(manager);

        Class<?> clazz = Class.forName("org.jbpm.test.Customer", true, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        Object cinstance = clazz.newInstance();
        // set fields
        setFieldValue(cinstance, "firstName", "john");
        setFieldValue(cinstance, "lastName", "doe");
        setFieldValue(cinstance, "age", new Integer(45));
        setFieldValue(cinstance, "customerId", new Long(1234));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", cinstance);

        processInstanceId = processService.startProcess(deploymentUnitJPA.getIdentifier(), "persistence-test.customer-evaluation", params);
        assertNotNull(processInstanceId);

        Map<String, String> variableMap = new HashMap<String, String>();
        variableMap.put("FIRSTNAME", "string");
        variableMap.put("LASTNAME", "string");
        variableMap.put("AGE", "integer");
        variableMap.put("CUSTOMERID", "long");

        List<ProcessInstanceWithVarsDesc> processInstanceLogs = queryService.query(query.getName(), ProcessInstanceWithCustomVarsQueryMapper.get(variableMap), new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        ProcessInstanceWithVarsDesc instance = processInstanceLogs.get(0);
        assertEquals(4, instance.getVariables().size());

        assertTrue(instance.getVariables().containsKey("FIRSTNAME"));
        assertTrue(instance.getVariables().containsKey("LASTNAME"));
        assertTrue(instance.getVariables().containsKey("AGE"));
        assertTrue(instance.getVariables().containsKey("CUSTOMERID"));

        assertEquals("john", instance.getVariables().get("FIRSTNAME"));
        assertEquals("doe", instance.getVariables().get("LASTNAME"));
        assertEquals(45, instance.getVariables().get("AGE"));
        assertEquals(1234l, instance.getVariables().get("CUSTOMERID"));

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesWithRawMapper() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        List<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());

        List<Object> firstRow = instances.get(0);
        assertNotNull(firstRow);

        assertEquals(18, firstRow.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesWithRawMapperMultipleRows() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        List<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        Long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId2);

        instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(2, instances.size());

        List<Object> firstRow = instances.get(0);
        assertNotNull(firstRow);

        assertEquals(18, firstRow.size());

        List<Object> secondRow = instances.get(1);
        assertNotNull(secondRow);

        assertEquals(18, secondRow.size());

        Assert.assertNotEquals(firstRow, secondRow);

        processService.abortProcessInstance(processInstanceId2);
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesCount() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        Collection<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), QueryParam.count(COLUMN_PROCESSINSTANCEID));
        assertNotNull(instances);
        assertEquals(1, instances.size());

        List<Object> result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(result.get(0) instanceof Number);
        assertEquals(0, ((Number) result.get(0)).intValue());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), QueryParam.count(COLUMN_PROCESSINSTANCEID));
        assertNotNull(instances);
        assertEquals(1, instances.size());

        result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(result.get(0) instanceof Number);
        assertEquals(1, ((Number) result.get(0)).intValue());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }

    @Test
    public void testGetProcessInstancesCountAndGroup() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryParam[] parameters = QueryParam.getBuilder().append(QueryParam.groupBy(COLUMN_PROCESSNAME)).append(QueryParam.count(COLUMN_PROCESSINSTANCEID)).get();

        Collection<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jboss.qa.bpms.HumanTask");

        instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(2, instances.size());
        // write document process
        List<Object> result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.get(1) instanceof Number);
        assertEquals(1, ((Number) result.get(1)).intValue());

        // org.jboss.qa.bpms.HumanTask task process
        result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.get(1) instanceof Number);
        assertEquals(1, ((Number) result.get(1)).intValue());

        processService.abortProcessInstance(processInstanceId2);
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }

    @Test
    public void testGetFilteredProcessInstances() {

        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname, Target.FILTERED_PROCESS);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Collection<ProcessInstanceDesc> instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(1, (int) instances.iterator().next().getState());

        // let's now change the roles so user should not see instances
        roles.clear();
        roles.add("employees");
        identityProvider.setRoles(roles);
        identityProvider.setName("anotherUser2");

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());
    }

    @Test
    public void testGetFilteredTaskInstancesAsPotOwners() {

        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);

        query = new SqlQueryDefinition("getMyTaskInstances", dataSourceJNDIname, Target.FILTERED_PO_TASK);
        query.setExpression(PO_TASK_QUERY);

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        identityProvider.setName("salaboy");

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        List<TaskSummary> taskSummaries = queryService.query(query.getName(), TaskSummaryQueryMapper.get(), new QueryContext());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        // let's now change the roles so user should not see instances
        roles.clear();
        roles.add("employees");
        identityProvider.setRoles(roles);
        identityProvider.setName("anotherUser");

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        taskSummaries = queryService.query(query.getName(), TaskSummaryQueryMapper.get(), new QueryContext());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetFilteredTaskInstancesAsBA() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);

        query = new SqlQueryDefinition("getTaskInstancesAdmin", dataSourceJNDIname, Target.FILTERED_BA_TASK);
        query.setExpression("select ti.activationTime, ti.actualOwner, ti.createdBy, ti.createdOn, ti.deploymentId, "
                                    + "ti.description, ti.dueDate, ti.name, ti.parentId, ti.priority, ti.processId, ti.processInstanceId, "
                                    + "ti.processSessionId, ti.status, ti.taskId, ti.workItemId "
                                    + "from  AuditTaskImpl ti");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        identityProvider.setName("Administrator");
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    /*
     * JBPM-5468 adding support for full ORDER BY clause with QueryService.  This test
     * selects 3 processes and verifies sorting criteria specified by the ORDER BY clause.
     */
    @Test
    public void testGetProcessInstancesWithOrderByClause() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        List<ProcessInstanceDesc> instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new AdvancedQueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        Long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId2);
        Long processInstanceId3 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jboss.qa.bpms.HumanTask");
        assertNotNull(processInstanceId3);

        String orderByClause = "processId asc, processInstanceId desc";

        instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new AdvancedQueryContext(orderByClause));
        assertNotNull(instances);
        assertEquals(3, instances.size());

        // Check that sorting criteria for processInstanceId is descending
        assertTrue("Testing ORDER BY clause with descending sort order on processInstanceId failed", instances.get(2).getId() < instances.get(1).getId());

        processService.abortProcessInstance(processInstanceId2);
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }
    
    @Test
    public void testRegisterInvalidQuery() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("this is an invalid query");
        
        try {
            queryService.registerQuery(query);
            fail("Should fail as the query is invalid");
        } catch (Exception e) {
            // expected
        }

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(0, queries.size());
    }
    
    @Test
    public void testGetProcessInstancesGroupWithInterval() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryParam[] parameters = QueryParam.getBuilder().append(QueryParam.groupBy(COLUMN_START, QueryParam.DAY, 30)).get();

        Collection<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jboss.qa.bpms.HumanTask");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // expected date is day when processes started
        String expectedDate = sdf.format(new Date());

        instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        // write document process
        List<Object> result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(1, result.size());
        
        assertEquals(expectedDate, result.get(0));

        processService.abortProcessInstance(processInstanceId2);
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }

    
    @Test
    public void testGetProcessInstancesGroupWithoutInterval() {

        query = new SqlQueryDefinition("getAllProcessInstances", dataSourceJNDIname);
        query.setExpression("select * from processinstancelog");

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryParam[] parameters = QueryParam.getBuilder().append(QueryParam.groupBy(COLUMN_START)).get();

        Collection<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jboss.qa.bpms.HumanTask");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        // expected date is day when processes started
        String expectedDate = sdf.format(new Date());

        instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        // write document process
        List<Object> result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(1, result.size());
        
        assertEquals(expectedDate, result.get(0));

        processService.abortProcessInstance(processInstanceId2);
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }
    
    protected void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {

        }
    }
}
