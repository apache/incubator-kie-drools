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

package org.jbpm.services.ejb.test;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceWithVarsQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithVarsQueryMapper;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceWithVarsDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;
import org.jbpm.services.api.query.NamedQueryMapper;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import org.jbpm.services.api.query.model.QueryParam;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.query.QueryServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.services.ejb.test.identity.TestIdentityProvider;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.services.api.query.QueryResultMapper.*;
import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class QueryServiceEJBIntegrationTest extends AbstractTestSupport {

private static final Logger logger = LoggerFactory.getLogger(QueryServiceEJBIntegrationTest.class);

    @Deployment
    public static WebArchive createDeployment() {
        File archive = new File("target/sample-war-ejb-app.war");
        if (!archive.exists()) {
            throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
        }
        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
        war.addPackage("org.jbpm.services.ejb.test"); // test cases
        war.addPackage("org.jbpm.services.ejb.test.identity"); // test identity provider
    
        // deploy test kjar
        deployKjar();
        
        return war;
    }

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected String correctUser = "testUser";
    protected String wrongUser = "wrongUser";

    private Long processInstanceId = null;
    private static KModuleDeploymentUnit deploymentUnit = null;
    
    private QueryDefinition query;
    
    @Before
    public void prepare() {
        assertNotNull(deploymentService);
        
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);
    }
    
    protected static void deployKjar() {
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/EmptyHumanTask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/BPMN2-UserTask.bpmn2");
        processes.add("repo/processes/general/SimpleHTProcess.bpmn2");
        processes.add("repo/processes/general/AdHocSubProcess.bpmn2");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);
   }
    

    @After
    public void cleanup() {
        if (query != null) {
            try {
                queryService.unregisterQuery(query.getName());
            } catch (QueryNotFoundException e) {
                
            }
        }
        if (processInstanceId != null) {
            // let's abort process instance to leave the system in clear state
            processService.abortProcessInstance(processInstanceId);
            
            ProcessInstance pi = processService.getProcessInstance(processInstanceId);      
            assertNull(pi);
        }
        int deleted = 0;
        deleted += commandService.execute(new UpdateStringCommand("delete from  NodeInstanceLog nid"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  ProcessInstanceLog pid"));        
        deleted += commandService.execute(new UpdateStringCommand("delete from  VariableInstanceLog vsd"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  AuditTaskImpl vsd"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  TaskVariableImpl vsd"));
        System.out.println("Deleted " + deleted);
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }
    
    @EJB
    private DeploymentServiceEJBLocal deploymentService;
    
    @EJB
    private ProcessServiceEJBLocal processService;
    
    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;
    
    @EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
    private TransactionalCommandService commandService;
    
    @Inject
    private TestIdentityProvider identityProvider;
    
    @EJB
    private QueryServiceEJBLocal queryService;


    @Test
    public void testGetProcessInstances() {
        
        query = new SqlQueryDefinition("getAllProcessInstances", "java:jboss/datasources/ExampleDS");
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
    	assertEquals(1, (int)instances.iterator().next().getState());
    	
    	// search using named mapper to refer to query mappers by name
    	instances = queryService.query(query.getName(), new NamedQueryMapper<Collection<ProcessInstanceDesc>>("ProcessInstances"), new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(1, (int)instances.iterator().next().getState());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = queryService.query(query.getName(), ProcessInstanceQueryMapper.get(), new QueryContext(COLUMN_PROCESSNAME, false));
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }

    @Test
    public void testGetProcessInstancesByState() {
        query = new SqlQueryDefinition("getAllProcessInstances", "java:jboss/datasources/ExampleDS");
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
    	assertEquals(3, (int)instances.iterator().next().getState());
    }
    
    @Test
    public void testGetProcessInstancesByProcessId() {
        query = new SqlQueryDefinition("getAllProcessInstances", "java:jboss/datasources/ExampleDS");
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
        
        query = new SqlQueryDefinition("getAllProcessInstancesWithVariables", "java:jboss/datasources/ExampleDS");
        query.setExpression("select pil.*, v.variableId, v.value " +
                            "from ProcessInstanceLog pil " +
                            "inner join (select vil.processInstanceId ,vil.variableId, MAX(vil.ID) maxvilid  FROM VariableInstanceLog vil " +
                            "GROUP BY vil.processInstanceId, vil.variableId ORDER BY vil.processInstanceId)  x " +
                            "ON (v.variableId = x.variableId  AND v.id = x.maxvilid )" +
                            "INNER JOIN VariableInstanceLog v " +        
                            "ON (v.processInstanceId = pil.processInstanceId)");
        
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
        
        query = new SqlQueryDefinition("getAllTaskInstances", "java:jboss/datasources/ExampleDS");
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
        
        query = new SqlQueryDefinition("getAllTaskInputInstancesWithVariables", "java:jboss/datasources/ExampleDS");
        query.setExpression("select ti.*, tv.name tvname, tv.value tvvalue from AuditTaskImpl ti " +
                            "inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv "+
                            "on (tv.taskId = ti.taskId)");
        
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
        assertEquals(5, instance.getVariables().size());
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithVarsQueryMapper.get(), new QueryContext(), 
                                                            QueryParam.equalsTo(COLUMN_TASK_VAR_NAME, "Comment"), 
                                                            QueryParam.equalsTo(COLUMN_TASK_VAR_VALUE, "Write a Document"));
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        instance = taskInstanceLogs.get(0); 
        assertEquals(1, instance.getVariables().size());
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceWithVarsQueryMapper.get(), new QueryContext(), 
                QueryParam.equalsTo(COLUMN_TASK_VAR_NAME, "Comment"), 
                QueryParam.equalsTo(COLUMN_TASK_VAR_VALUE, "Wrong Comment"));
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }
    
    @Test
    public void testGetTaskInstancesAsPotOwners() {
        
        query = new SqlQueryDefinition("getMyTaskInstances", "java:jboss/datasources/ExampleDS", Target.PO_TASK);
        query.setExpression("select ti.*, oe.id OEID from AuditTaskImpl ti,"
                        + "PeopleAssignments_PotOwners po, "
                        + "OrganizationalEntity oe "
                        + "where ti.taskId = po.task_id and po.entity_id = oe.id ");
        
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
        
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }
    
    @Test
    public void testGetTaskInstancesAsBA() {
        
        query = new SqlQueryDefinition("getBATaskInstances", "java:jboss/datasources/ExampleDS", Target.BA_TASK);
        query.setExpression("select ti.*, oe.id OEID from AuditTaskImpl ti,"
                        + "PeopleAssignments_BAs bas, "
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
    
  
    
    protected void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);            
            f.setAccessible(true);            
            f.set(instance, value);
        } catch (Exception e) {
            
        }
    }
}
