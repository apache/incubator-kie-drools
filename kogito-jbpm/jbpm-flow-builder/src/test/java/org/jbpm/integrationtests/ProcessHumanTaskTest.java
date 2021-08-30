/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.io.impl.ReaderResource;
import org.jbpm.integrationtests.handler.TestWorkItemHandler;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessHumanTaskTest extends AbstractBaseTest {

    @Test
    public void testHumanTask() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <humanTask id=\"2\" name=\"HumanTask\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "        <parameter name=\"ActorId\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>John Doe</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"TaskName\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Do something</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Priority\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Comment\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "    </humanTask>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.humantask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        KogitoWorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testSwimlane() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <swimlanes>\n" +
                        "      <swimlane name=\"actor1\" />\n" +
                        "    </swimlanes>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <humanTask id=\"2\" name=\"HumanTask\" swimlane=\"actor1\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "        <parameter name=\"ActorId\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>John Doe</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"TaskName\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Do something</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Priority\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Comment\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "      <mapping type=\"out\" from=\"ActorId\" to=\"ActorId\" />" +
                        "    </humanTask>\n" +
                        "    <humanTask id=\"3\" name=\"HumanTask\" swimlane=\"actor1\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "        <parameter name=\"ActorId\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"TaskName\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Do something else</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Priority\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Comment\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "      <mapping type=\"out\" from=\"ActorId\" to=\"ActorId\" />" +
                        "    </humanTask>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.humantask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        KogitoWorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Do something", workItem.getParameter("TaskName"));
        assertEquals("John Doe", workItem.getParameter("ActorId"));
        Map<String, Object> results = new HashMap<String, Object>();
        ((HumanTaskWorkItemImpl) workItem).setActualOwner("Jane Doe");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), results);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Do something else", workItem.getParameter("TaskName"));
        assertEquals("Jane Doe", workItem.getParameter("SwimlaneActorId"));
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testHumanTaskCancel() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <humanTask id=\"2\" name=\"HumanTask\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "        <parameter name=\"ActorId\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>John Doe</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"TaskName\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Do something</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Priority\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Comment\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "    </humanTask>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = (ProcessInstance) kruntime.startProcess("org.drools.humantask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        KogitoWorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        processInstance.setState(KogitoProcessInstance.STATE_ABORTED);
        assertTrue(handler.isAborted());
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testHumanTaskCancel2() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <humanTask id=\"2\" name=\"HumanTask\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "        <parameter name=\"ActorId\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>John Doe</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"TaskName\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Do something</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Priority\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Comment\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "      <onExit>\n" +
                        "        <action type=\"expression\" name=\"Cancel\" dialect=\"java\" >((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).cancel();</action>\n" +
                        "      </onExit>\n" +
                        "    </humanTask>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.humantask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        KogitoWorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertFalse(handler.isAborted());
    }

}
