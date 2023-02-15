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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.io.ReaderResource;
import org.jbpm.integrationtests.handler.TestWorkItemHandler;
import org.jbpm.integrationtests.test.Person;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessMarshallingTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessMarshallingTest.class);

    @Test
    public void testMarshallingProcessInstanceWithWorkItem() {
        String process =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"myVariable\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        <value>OldValue</value>\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <workItem id=\"2\" name=\"Email\" >\n" +
                        "      <work name=\"Email\" >\n" +
                        "        <parameter name=\"Subject\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Mail</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Text\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>This is an email</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"To\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>you@mail.com</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"From\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>me@mail.com</value>\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "    </workItem>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\"/>\n" +
                        "    <connection from=\"2\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";
        builder.add(new ReaderResource(new StringReader(process)), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email", handler);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("myVariable", "ThisIsMyValue");
        kruntime.startProcess("org.test.ruleflow", variables);

        assertThat(kruntime.getKogitoProcessInstances()).hasSize(1);
        assertThat(handler.getWorkItem()).isNotNull();

        assertThat(kruntime.getKogitoProcessInstances()).hasSize(1);
        VariableScopeInstance variableScopeInstance =
                (VariableScopeInstance) ((ProcessInstance) kruntime.getKogitoProcessInstances().iterator().next()).getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertThat(variableScopeInstance.getVariable("myVariable")).isEqualTo("ThisIsMyValue");

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertThat(kruntime.getKogitoProcessInstances()).isEmpty();
    }

    @Test
    public void testMarshallingWithMultipleHumanTasks() {
        String process =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "  xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
                        "\n" +
                        "    <header>\n" +
                        "      <variables>\n" +
                        "        <variable name=\"list\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
                        "        </variable>\n" +
                        "      </variables>\n" +
                        "    </header>\n" +
                        "\n" +
                        "    <nodes>\n" +
                        "      <forEach id=\"4\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"list\" >\n" +
                        "        <nodes>\n" +
                        "          <humanTask id=\"1\" name=\"Human Task\" >\n" +
                        "            <work name=\"Human Task\" >\n" +
                        "              <parameter name=\"Comment\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "              </parameter>\n" +
                        "              <parameter name=\"ActorId\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "              </parameter>\n" +
                        "              <parameter name=\"Priority\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "              </parameter>\n" +
                        "              <parameter name=\"TaskName\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "                <value>Do something: #{item}</value>\n" +
                        "              </parameter>\n" +
                        "            </work>\n" +
                        "          </humanTask>\n" +
                        "          <humanTask id=\"2\" name=\"Human Task Again\" >\n" +
                        "            <work name=\"Human Task\" >\n" +
                        "              <parameter name=\"Comment\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "              </parameter>\n" +
                        "              <parameter name=\"ActorId\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "              </parameter>\n" +
                        "              <parameter name=\"Priority\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "              </parameter>\n" +
                        "              <parameter name=\"TaskName\" >\n" +
                        "                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "                <value>Do something else: #{item}</value>\n" +
                        "              </parameter>\n" +
                        "            </work>\n" +
                        "          </humanTask>\n" +
                        "        </nodes>\n" +
                        "        <connections>\n" +
                        "          <connection from=\"1\" to=\"2\" />\n" +
                        "        </connections>\n" +
                        "        <in-ports>\n" +
                        "          <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "        </in-ports>\n" +
                        "        <out-ports>\n" +
                        "          <out-port type=\"DROOLS_DEFAULT\" nodeId=\"2\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "        </out-ports>\n" +
                        "      </forEach>\n" +
                        "      <start id=\"1\" name=\"Start\" />\n" +
                        "      <end id=\"3\" name=\"End\" />\n" +
                        "    </nodes>\n" +
                        "\n" +
                        "    <connections>\n" +
                        "      <connection from=\"1\" to=\"4\" />\n" +
                        "      <connection from=\"4\" to=\"3\" />\n" +
                        "    </connections>\n" +
                        "\n" +
                        "</process>\n";
        builder.add(new ReaderResource(new StringReader(process)), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        TestListWorkItemHandler handler = new TestListWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        list.add("three");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("list", list);
        KogitoProcessInstance kogitoProcessInstance = kruntime.startProcess("com.sample.ruleflow", parameters);

        assertThat(kruntime.getKogitoProcessInstances()).hasSize(1);
        //complete all user tasks instances
        for (int i = 0; i < list.size() * 2; i++) {
            completeWorkItems(kruntime, handler);
        }
        assertThat(kruntime.getKogitoProcessInstances()).isEmpty();
        assertThat(kogitoProcessInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    public void completeWorkItems(KogitoProcessRuntime kruntime, TestListWorkItemHandler handler) {
        KogitoWorkItem workItem = handler.getWorkItems().get(0);
        handler.reset();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
    }

    private static class TestListWorkItemHandler implements KogitoWorkItemHandler {
        private List<KogitoWorkItem> workItems = new ArrayList<>();

        public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
            logger.debug("Executing workItem {}", workItem.getParameter("TaskName"));
            workItems.add(workItem);
        }

        public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
            workItems.remove(workItem);
        }

        public List<KogitoWorkItem> getWorkItems() {
            return workItems;
        }

        public void reset() {
            workItems.clear();
        }
    }

    @Test
    public void testVariablePersistenceMarshallingStrategies() {
        String process =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"myVariable\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        <value>OldValue</value>\n" +
                        "      </variable>\n" +
                        "      <variable name=\"myPerson\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"org.jbpm.integrationtests.test.Person\"/>\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <workItem id=\"2\" name=\"Email\" >\n" +
                        "      <work name=\"Report\" >\n" +
                        "        <parameter name=\"Subject\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Mail</value>\n" +
                        "        </parameter>\n" +
                        "        <parameter name=\"Subject\" >\n" +
                        "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "          <value>Mail</value>\n" +
                        "        </parameter>\n" +
                        "      </work>\n" +
                        "    </workItem>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\"/>\n" +
                        "    <connection from=\"2\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";
        builder.add(new ReaderResource(new StringReader(process)), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Report", handler);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("myVariable", "ThisIsMyValue");
        Person myPerson = new Person("Nikola Tesla", 156);
        variables.put("myPerson", myPerson);
        kruntime.startProcess("org.test.ruleflow", variables);

        assertThat(kruntime.getKogitoProcessInstances()).hasSize(1);
        assertThat(handler.getWorkItem()).isNotNull();

        assertThat(kruntime.getKogitoProcessInstances()).hasSize(1);
        VariableScopeInstance variableScopeInstance =
                (VariableScopeInstance) ((ProcessInstance) kruntime.getKogitoProcessInstances().iterator().next()).getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertThat(variableScopeInstance.getVariable("myVariable")).isEqualTo("ThisIsMyValue");
        assertThat(variableScopeInstance.getVariable("myPerson")).isEqualTo(myPerson);

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertThat(kruntime.getKogitoProcessInstances()).isEmpty();
    }
}
