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

package org.jbpm.test.container.test;

import java.util.HashMap;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jbpm.test.container.JbpmContainerTest;
import org.jbpm.test.container.archive.HelloWebService;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

@Category({EAP.class, WAS.class, WLS.class})
public class WebServiceInvokeTest extends JbpmContainerTest {
    private static HelloWebService hws;

    @Deployment(name = "HelloWebService", testable = false)
    @TargetsContainer(REMOTE_CONTAINER)
    public static Archive<?> deployWebService() {
        hws = new HelloWebService();
        WebArchive war = hws.buildArchive();
        System.out.println("### Deploying war '" + war + "'");
        return war;
    }

    @Test
    @RunAsClient
    public void testWebServiceSync() throws Exception {
        System.out.println("### Running proccess ...");
        
        KieSession ksession = getSession(hws.getResource(HelloWebService.BPMN_CALL_WEB_SERVICE_NO_INTERFACE));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new WebServiceWorkItemHandler(ksession));
        
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("parameter", "Fredy");
        arguments.put("mode", "SYNC");

        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(HelloWebService.PROCESS_CALL_WEB_SERVICE, arguments);
        Assertions.assertThat(pi.getVariable("result")).as("WebService call failed.").isEqualTo("Hello Fredy");
        Assertions.assertThat(pi.getState()).as("Process did not finish.").isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @RunAsClient
    public void testWebServiceAsync() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(hws.getResource(HelloWebService.BPMN_CALL_WEB_SERVICE_NO_INTERFACE));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new WebServiceWorkItemHandler(ksession));

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("parameter", "Fredy");
        arguments.put("mode", "ASYNC");

        /*
         * WebService has been called in mode ASYNC so ksession should return
         * control to our program.
         */
        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(HelloWebService.PROCESS_CALL_WEB_SERVICE, arguments);
        Assertions.assertThat(pi.getVariable("result")).as("Result variable should not yet be set!").isNull();
        Assertions.assertThat(pi.getState()).as("Process finished prematurely.").isEqualTo(ProcessInstance.STATE_ACTIVE);

        /*
         * Wait for the process to complete
         */
        Thread.sleep(4000);

        /*
         * Make sure we got the response back.
         */
        Assertions.assertThat(pi.getVariable("result")).as("WebService call failed.").isEqualTo("Hello Fredy");
        Assertions.assertThat(pi.getState()).as("Process did not finish").isEqualTo(ProcessInstance.STATE_COMPLETED);

    }

    @Test
    @RunAsClient
    public void testWebServiceOneWay() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(hws.getResource(HelloWebService.BPMN_CALL_WEB_SERVICE_NO_INTERFACE));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new WebServiceWorkItemHandler(ksession));

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("parameter", "Fredy");
        arguments.put("mode", "ONEWAY");

        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(HelloWebService.PROCESS_CALL_WEB_SERVICE, arguments);
        Assertions.assertThat(pi.getVariable("result")).as("WebService call failed.").isEqualTo(null);
        Assertions.assertThat(pi.getState()).as("Process did not finish").isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @RunAsClient
    public void testMultipleImports() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(hws.getResource(HelloWebService.BPMN_CALL_WEB_SERVICE_MULTI_IMPORTS));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new WebServiceWorkItemHandler(ksession));

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("parameter", "Many Imports");
        arguments.put("mode", "SYNC");

        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                HelloWebService.PROCESS_CALL_WEB_SERVICE, arguments);
        Assertions.assertThat(pi.getVariable("result")).as("WebService call failed.").isEqualTo("Hello Many Imports");
        Assertions.assertThat(pi.getState()).as("Process did not finish.")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @RunAsClient
    public void testInterfaceNamePassedViaAssignemnt() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(hws.getResource(HelloWebService.BPMN_CALL_WEB_SERVICE_NO_INTERFACE));
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new WebServiceWorkItemHandler(ksession));

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("parameter", "No Name");
        arguments.put("mode", "SYNC");

        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                HelloWebService.PROCESS_CALL_WEB_SERVICE, arguments);
        Assertions.assertThat(pi.getVariable("result")).as("WebService call failed.").isEqualTo("Hello No Name");
        Assertions.assertThat(pi.getState()).as("Process did not finish.")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
