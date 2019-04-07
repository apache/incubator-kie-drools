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

package org.jbpm.services.cdi.test;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.kie.services.test.ProcessServiceImplTest;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProcessServiceCDIImplTest extends ProcessServiceImplTest {
	
	@Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "domain-services.jar")                
                .addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.commands") // This should not be required here
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")
                .addPackage("org.jbpm.services.task.audit.service")

                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.context")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi")                               
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.jbpm.shared.services.impl.tx")
                
                .addPackage("org.jbpm.kie.services.api")
                .addPackage("org.jbpm.kie.services.impl")                
                .addPackage("org.jbpm.kie.services.api.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.event.listeners")
                .addPackage("org.jbpm.kie.services.impl.audit")
                .addPackage("org.jbpm.kie.services.impl.form")
                .addPackage("org.jbpm.kie.services.impl.form.provider")
                .addPackage("org.jbpm.kie.services.impl.query")  
                .addPackage("org.jbpm.kie.services.impl.query.mapper")  
                .addPackage("org.jbpm.kie.services.impl.query.persistence")  
                .addPackage("org.jbpm.kie.services.impl.query.preprocessor")  
                
                .addPackage("org.jbpm.services.cdi")
                .addPackage("org.jbpm.services.cdi.impl")
                .addPackage("org.jbpm.services.cdi.impl.form")
                .addPackage("org.jbpm.services.cdi.impl.manager")
                .addPackage("org.jbpm.services.cdi.producer")
                .addPackage("org.jbpm.services.cdi.impl.security")
                .addPackage("org.jbpm.services.cdi.impl.query")
                
                .addPackage("org.jbpm.test.util")
                .addPackage("org.jbpm.kie.services.test")
                .addPackage("org.jbpm.services.cdi.test") // Identity Provider Test Impl here
                .addClass("org.jbpm.services.cdi.test.util.CDITestHelperNoTaskService")
                .addClass("org.jbpm.services.cdi.test.util.CountDownDeploymentListenerCDIImpl")
                .addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener")
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    
    @Override
	protected void close() {
		// do nothing here and let CDI close resources
	}

	@Override
	protected void configureServices() {
		// do nothing here and let CDI configure services 
	}

	@Inject	
	@Override
	public void setDeploymentService(DeploymentService deploymentService) {
		
		super.setDeploymentService(deploymentService);
	}

	@Inject
	@Override
	public void setBpmn2Service(DefinitionService bpmn2Service) {
		
		super.setBpmn2Service(bpmn2Service);
	}

	@Inject
	@Override
	public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
		
		super.setRuntimeDataService(runtimeDataService);
	}

	@Inject
	@Override
	public void setProcessService(ProcessService processService) {
		
		super.setProcessService(processService);
	}

	@Inject
	@Override
	public void setUserTaskService(UserTaskService userTaskService) {
		
		super.setUserTaskService(userTaskService);
	}
	
}
