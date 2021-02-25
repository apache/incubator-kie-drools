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
import java.util.ArrayList;
import java.util.List;

import org.drools.core.io.impl.ReaderResource;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ProcessExceptionHandlerTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessExceptionHandlerTest.class);

    @Test
    public void testFaultWithoutHandler() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <fault id=\"2\" name=\"Fault\" faultName=\"myFault\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);
        KnowledgeBuilderErrors errors = builder.getErrors();
        if (errors != null && !errors.isEmpty()) {
            for (KnowledgeBuilderError error : errors) {
                logger.error(error.toString());
            }
            fail("Package could not be compiled");
        }
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.exception");
        assertEquals(KogitoProcessInstance.STATE_ABORTED, processInstance.getState());
    }

    @Test
    public void testProcessExceptionHandlerAction() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"SomeVar\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        <value>SomeValue</value>\n" +
                        "      </variable>\n" +
                        "      <variable name=\"faultVar\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "    <exceptionHandlers>\n" +
                        "      <exceptionHandler faultName=\"myFault\" type=\"action\" faultVariable=\"faultVar\" >\n" +
                        "        <action type=\"expression\" name=\"Print\" dialect=\"java\" >list.add(context.getVariable(\"faultVar\"));</action>\n" +
                        "      </exceptionHandler>\n" +
                        "    </exceptionHandlers>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <fault id=\"2\" name=\"Fault\" faultName=\"myFault\" faultVariable=\"SomeVar\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.exception");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("SomeValue", list.get(0));
    }

    @Test
    public void testProcessExceptionHandlerTriggerNode() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <exceptionHandlers>\n" +
                        "      <exceptionHandler faultName=\"myFault\" type=\"action\"  >\n" +
                        "        <action type=\"expression\" name=\"Complete\" dialect=\"java\" >((org.jbpm.process.instance.ProcessInstance) context.getProcessInstance()).setState(org.jbpm.process.instance.ProcessInstance.STATE_COMPLETED);</action>\n"
                        + "      </exceptionHandler>\n" +
                        "    </exceptionHandlers>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <fault id=\"2\" name=\"Fault\" faultName=\"myFault\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.exception");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testCompositeNodeExceptionHandlerTriggerNode() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <composite id=\"2\" name=\"Composite\" >\n" +
                        "      <variables>\n" +
                        "        <variable name=\"SomeVar\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>SomeValue</value>\n" +
                        "        </variable>\n" +
                        "        <variable name=\"FaultVariable\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </variable>\n" +
                        "      </variables>\n" +
                        "      <exceptionHandlers>\n" +
                        "        <exceptionHandler faultName=\"MyFault\" type=\"action\" faultVariable=\"FaultVariable\" >\n" +
                        "          <action type=\"expression\" name=\"Trigger\" dialect=\"java\" >context.getProcessInstance().signalEvent(\"MyEvent\", null);</action>\n" +
                        "        </exceptionHandler>\n" +
                        "      </exceptionHandlers>\n" +
                        "      <nodes>\n" +
                        "        <fault id=\"1\" name=\"Fault\" faultName=\"MyFault\" faultVariable=\"SomeVar\" />\n" +
                        "        <eventNode id=\"2\" name=\"Event\" >\n" +
                        "          <eventFilters>\n" +
                        "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
                        "          </eventFilters>\n" +
                        "        </eventNode>\n" +
                        "        <actionNode id=\"3\" name=\"Action\" >\n" +
                        "          <action type=\"expression\" dialect=\"java\" >list.add(context.getVariable(\"FaultVariable\"));</action>\n" +
                        "        </actionNode>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "        <connection from=\"2\" to=\"3\" />\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </composite>\n" +
                        "    <end id=\"3\" name=\"End\" />" +
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
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.exception");
        assertEquals(1, list.size());
        assertEquals("SomeValue", list.get(0));
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testNestedExceptionHandler() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <exceptionHandlers>\n" +
                        "      <exceptionHandler faultName=\"otherFault\" type=\"action\" >\n" +
                        "        <action type=\"expression\" name=\"Print\" dialect=\"java\" >list.add(\"Triggered global exception scope\");</action>\n" +
                        "      </exceptionHandler>\n" +
                        "    </exceptionHandlers>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <composite id=\"2\" name=\"Composite\" >\n" +
                        "	   <completionCondition>autocomplete</completionCondition>\n" +
                        "      <variables>\n" +
                        "        <variable name=\"SomeVar\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>SomeValue</value>\n" +
                        "        </variable>\n" +
                        "        <variable name=\"FaultVariable\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        </variable>\n" +
                        "      </variables>\n" +
                        "      <exceptionHandlers>\n" +
                        "        <exceptionHandler faultName=\"MyFault\" type=\"action\" faultVariable=\"FaultVariable\" >\n" +
                        "          <action type=\"expression\" name=\"Trigger\" dialect=\"java\" >((org.jbpm.workflow.instance.node.CompositeNodeInstance) context.getNodeInstance()).signalEvent(\"MyEvent\", null);</action>\n"
                        +
                        "        </exceptionHandler>\n" +
                        "      </exceptionHandlers>\n" +
                        "      <nodes>\n" +
                        "        <fault id=\"1\" name=\"Fault\" faultName=\"MyFault\" faultVariable=\"SomeVar\" />\n" +
                        "        <eventNode id=\"2\" name=\"Event\" >\n" +
                        "          <eventFilters>\n" +
                        "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
                        "          </eventFilters>\n" +
                        "        </eventNode>\n" +
                        "        <fault id=\"3\" name=\"Fault\" faultName=\"otherFault\" />\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "        <connection from=\"2\" to=\"3\" />\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "    </composite>\n" +
                        "    <end id=\"3\" name=\"End\" />" +
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

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.exception");
        assertEquals(1, list.size());
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
