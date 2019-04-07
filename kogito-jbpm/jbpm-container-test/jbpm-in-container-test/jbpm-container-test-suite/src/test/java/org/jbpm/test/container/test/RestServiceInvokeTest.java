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
import org.jbpm.test.container.archive.RegisterRestService;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.test.container.groups.EWS;
import org.jboss.shrinkwrap.api.Archive;
import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

@Category({EAP.class, EWS.class, WAS.class, WLS.class})
public class RestServiceInvokeTest extends JbpmContainerTest {

    private static RegisterRestService rrs;

    @Deployment(name = "RegisterService", testable = false)
    @TargetsContainer(REMOTE_CONTAINER)
    public static Archive<?> deployRestService() {
        rrs = new RegisterRestService();
        Archive <?> war = rrs.buildArchive();       //in case of WebSphere (WAS) it is an ear, see implementation of buildArchive()
        System.out.println("### Deploying war '" + war + "'");
        return war;
    }

    @Test
    @RunAsClient
    public void testGet() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(rrs.getResource(RegisterRestService.BPMN_CALL_REST_SERVICE_SIMPLE));
        ksession.getWorkItemManager().registerWorkItemHandler("REST", new RESTWorkItemHandler());

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("Url", RegisterRestService.SERVICE_URL + "PersonList/ping");
        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                RegisterRestService.PROCESS_CALL_REST_SERVICE_SIMPLE, arguments);

        Assertions.assertThat(pi.getVariable("Result")).as("REST call failed - Wrong response.").isEqualTo("pong");
        Assertions.assertThat(pi.getVariable("Status")).as("REST call failed - Wrong status code.").isEqualTo(200);
        Assertions.assertThat(pi.getState()).as("Process did not finish")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @RunAsClient
    public void testSecuredGet() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(rrs.getResource(RegisterRestService.BPMN_CALL_REST_SERVICE_SIMPLE));
        ksession.getWorkItemManager().registerWorkItemHandler("REST", new RESTWorkItemHandler("ibek", "ibek1234;"));

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("Url", RegisterRestService.SERVICE_URL + "PersonList/securedPing");
        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                RegisterRestService.PROCESS_CALL_REST_SERVICE_SIMPLE, arguments);

        Assertions.assertThat(pi.getVariable("Result")).as("REST call failed - Wrong response.")
                .isEqualTo("securedPong");
        Assertions.assertThat(pi.getVariable("Status")).as("REST call failed - Wrong status code.").isEqualTo(200);
        Assertions.assertThat(pi.getState()).as("Process did not finish")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @RunAsClient
    public void testSecuredFail() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(rrs.getResource(RegisterRestService.BPMN_CALL_REST_SERVICE_SIMPLE));
        ksession.getWorkItemManager().registerWorkItemHandler("REST", new RESTWorkItemHandler("undefined", "user"));

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("Url", RegisterRestService.SERVICE_URL + "PersonList/securedPing");
        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                RegisterRestService.PROCESS_CALL_REST_SERVICE_SIMPLE, arguments);

        Assertions.assertThat(pi.getVariable("Status")).as("REST call failed").isEqualTo((Object) 401);
        Assertions.assertThat(pi.getState()).as("Process did not finish")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @RunAsClient
    public void testPost() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(rrs.getResource(RegisterRestService.BPMN_CALL_REST_SERVICE));
        ksession.getWorkItemManager().registerWorkItemHandler("REST", new RESTWorkItemHandler());

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("Url", RegisterRestService.SERVICE_URL + "PersonList/add");
        arguments.put("Method", "POST");
        arguments.put("ContentType", "application/json");
        arguments.put("Content", "{ \"name\": \"Marek\",  \"middlename\": \"-\", \"surname\": \"Baluch\" }");
        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                RegisterRestService.PROCESS_CALL_REST_SERVICE, arguments);

        Assertions.assertThat(pi.getVariable("Result")).as("REST call failed - Wrong response.").isEqualTo("Ok");
        Assertions.assertThat(pi.getVariable("Status")).as("REST call failed - Wrong status code.").isEqualTo(200);
        Assertions.assertThat(pi.getState()).as("Process did not finish")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Ignore(value = "Not implemented - Very little added value")
    @Test
    @RunAsClient
    public void testPut() throws Exception {
        // TBD: someday
    }

    @Test
    @RunAsClient
    public void testDelete() throws Exception {
        System.out.println("### Running proccess ...");

        KieSession ksession = getSession(rrs.getResource(RegisterRestService.BPMN_CALL_REST_SERVICE));
        ksession.getWorkItemManager().registerWorkItemHandler("REST", new RESTWorkItemHandler());

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("Url", RegisterRestService.SERVICE_URL + "PersonList/delete?name=Don&middlename=Non&surname=Existent");
        arguments.put("Method", "DELETE");
        arguments.put("ContentType", "text/plain");
        WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.startProcess(
                RegisterRestService.PROCESS_CALL_REST_SERVICE, arguments);

        Assertions.assertThat(pi.getVariable("Result")).as("REST call failed - Wrong response.").isEqualTo("Fail");
        Assertions.assertThat(pi.getVariable("Status")).as("REST call failed - Wrong status code.").isEqualTo(200);
        Assertions.assertThat(pi.getState()).as("Process did not finish")
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

}
