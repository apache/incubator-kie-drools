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

package org.jbpm.process.workitem.bpmn2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.test.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import static org.junit.Assert.*;

public class ServiceTaskTest extends AbstractBaseTest {

    @Test
    public void testServiceTaskWithClassInKjar() throws Exception {

        String javaSrc = "package org.jbpm.workitems; " +
                " public class HelloService { " +
                "     public String hello(String name) { " +
                "         return \"Hello \" + name + \"!\"; " +
                "     } " +
                " }";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(ks.newKieModuleModel().toXML())
                .write("src/main/resources/BPMN2-ServiceProcess.bpmn2",
                       IOUtils.toString(this.getClass().getResourceAsStream("/BPMN2-ServiceProcess.bpmn2")))
                .write("src/main/java/org/jbpm/workitems/HelloService.java",
                       javaSrc);

        ks.newKieBuilder(kfs).buildAll();
        KieContainer kcontainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());

        KieSession ksession = createSession(kcontainer.getKieBase());
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new ServiceTaskHandler(ksession,
                                                                                     kcontainer.getClassLoader()));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess",
                                                                                                  params);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john!",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    private static KieSession createSession(KieBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory",
                       "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory",
                       "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newKieSession(config,
                                   EnvironmentFactory.newEnvironment());
    }
}

