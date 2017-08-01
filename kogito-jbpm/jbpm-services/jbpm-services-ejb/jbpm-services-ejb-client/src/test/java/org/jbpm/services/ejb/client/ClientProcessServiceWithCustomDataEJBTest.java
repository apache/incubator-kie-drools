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

package org.jbpm.services.ejb.client;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;
import org.jbpm.services.ejb.client.helper.DeploymentServiceWrapper;
import org.jbpm.services.ejb.remote.api.RemoteMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class ClientProcessServiceWithCustomDataEJBTest extends AbstractKieServicesBaseTest {
	
	private static final String application = "sample-war-ejb-app";
	
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private static final String ARTIFACT_ID = "custom-data-project";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0";
    
    private ClassLoader customClassLoader;
    private ClassLoader originClassLoader;
    
    private Long processInstanceId;
    
    @Before
    public void prepare() throws MalformedURLException {
    	originClassLoader = Thread.currentThread().getContextClassLoader();
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        File kjar = new File("src/test/resources/kjar/custom-data-project-1.0.jar");
        File pom = new File("src/test/resources/kjar/pom.xml");
		KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
        
        URL[] urls = new URL[]{kjar.toURI().toURL()};
        customClassLoader = new URLClassLoader(urls, this.getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(customClassLoader);
    }
    
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
	    	// let's abort process instance to leave the system in clear state
	    	processService.abortProcessInstance(processInstanceId);
    	}
    	Thread.currentThread().setContextClassLoader(originClassLoader);
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

	@Override
	protected void close() {
		// do nothing
	}

	@Override
	protected void configureServices() {
		try {
			ClientServiceFactory factory = ServiceFactoryProvider.getProvider("JBoss");
			DeploymentServiceEJBRemote deploymentService = factory.getService(application, DeploymentServiceEJBRemote.class);
			ProcessServiceEJBRemote processService = factory.getService(application, ProcessServiceEJBRemote.class);
			RuntimeDataServiceEJBRemote runtimeDataService = factory.getService(application, RuntimeDataServiceEJBRemote.class);
			DefinitionServiceEJBRemote definitionService = factory.getService(application, DefinitionServiceEJBRemote.class);
			UserTaskServiceEJBRemote userTaskService = factory.getService(application, UserTaskServiceEJBRemote.class);
			
			setBpmn2Service(definitionService);
			setProcessService(processService);
			setRuntimeDataService(runtimeDataService);
			setUserTaskService(userTaskService);
			setDeploymentService(new DeploymentServiceWrapper(deploymentService));
		} catch (Exception e) {
			throw new RuntimeException("Unable to configure services", e);
		}
	}
	
	@Test
	public void testStartProcessWithCustomData() {

        assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        Map<String, Object> parameters = new RemoteMap();
        Object person = getInstance("org.jbpm.test.Person", new Object[]{"john", 25, true});
        parameters.put("person", person);
        
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "custom-data-project.work-on-custom-data", parameters);
        assertNotNull(processInstanceId);
        
        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter(0, 10));
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());
        
        List<Long> tasks = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        Long taskId = tasks.get(0);
        
        userTaskService.start(taskId, "john");
        
        Map<String, Object> data = userTaskService.getTaskInputContentByTaskId(taskId);
        assertNotNull(data);
        
        Object fromTaskPerson = data.get("_person");
        assertNotNull(fromTaskPerson);
        assertEquals("john", getFieldValue(fromTaskPerson, "name"));
        
        setFieldValue(fromTaskPerson, "name", "John Doe");
        
        RemoteMap outcome = new RemoteMap();
        outcome.put("person_", fromTaskPerson);
        
        userTaskService.complete(taskId, "john", outcome);
        
        
        ProcessInstanceDesc desc = runtimeDataService.getProcessInstanceById(processInstanceId);
        assertNotNull(desc);
        assertEquals(2, (int)desc.getState());
        processInstanceId = null;
	}
	
	protected Object getInstance(String className, Object[] params) {
		try {
			Class<?> clazz = Class.forName(className, true, customClassLoader);
			
			if (params == null || params.length == 0) {
				return clazz.newInstance();
			}
			int i = 0;
			Class<?>[] parameterTypes = new Class[params.length];
			for (Object o : params) {
				parameterTypes[i] = o.getClass();
				i++;
			}
			Constructor<?> c = clazz.getConstructor(parameterTypes);
			
			return c.newInstance(params);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create instance of " + className, e);
		}
	}
	
	protected Object getFieldValue(Object object, String fieldName) {
		try {
			Field f = object.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(object);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get value for filed of " + fieldName, e);
		}
	}
	
	protected void setFieldValue(Object object, String fieldName, Object value) {
		try {
			Field f = object.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(object, value);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get value for filed of " + fieldName, e);
		}
	}
}
