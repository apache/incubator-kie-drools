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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jbpm.integrationtests.test.Person;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessStateTest extends AbstractBaseTest {

    @Test
    public void testManualSignalState() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" name=\"StateA\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"toB\" />\n" +
                        "       <constraint toNodeId=\"4\" name=\"toC\" />\n" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <state id=\"3\" name=\"StateB\" />\n" +
                        "    <state id=\"4\" name=\"StateC\" />\n" +
                        "    <end id=\"5\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"2\" to=\"4\" />\n" +
                        "    <connection from=\"3\" to=\"2\" />\n" +
                        "    <connection from=\"4\" to=\"5\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        // start process
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kruntime.startProcess("org.drools.state");
        // should be in state A
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        StateNodeInstance stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("StateA");
        // signal "toB" so we move to state B
        processInstance.signalEvent("signal", "toB");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("StateB");
        // if no constraint specified for a connection,
        // we default to the name of the target node
        // signal "StateA", so we move back to state A
        processInstance.signalEvent("signal", "StateA");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("StateA");
        // signal "toC" so we move to state C
        processInstance.signalEvent("signal", "toC");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("StateC");
        // signal something completely wrong, this should simply be ignored
        processInstance.signalEvent("signal", "Invalid");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("StateC");
        // signal "End", so we move to the end
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).isEmpty();
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testImmediateStateConstraint1() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"one\" >\n" +
                        "            eval(true)" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"4\" name=\"two\" >\n" +
                        "           eval(false)" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("1");
    }

    @Test
    public void testImmediateStateConstraintPriorities1() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"one\" priority=\"1\" >\n" +
                        "            eval(true)" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"5\" name=\"two\" priority=\"2\" >\n" +
                        "           eval(true)" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("1");
    }

    @Test
    public void testImmediateStateConstraintPriorities2() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"one\" priority=\"2\" >\n" +
                        "            eval(true)" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"5\" name=\"two\" priority=\"1\" >\n" +
                        "           eval(true)" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("2");
    }

    @Test
    public void testDelayedStateConstraint() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.jbpm\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <imports>\n" +
                        "      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
                        "    </imports>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"one\" >\n" +
                        "            Person( age &gt; 21 )" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"4\" name=\"two\" >\n" +
                        "           Person( age &lt;= 21 )" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        assertThat(list).isEmpty();
        Person person = new Person("John Doe", 30);
        kruntime.getKieSession().insert(person);
        kruntime.getKieSession().fireAllRules();
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("1");
    }

    @Test
    public void testDelayedStateConstraint2() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.jbpm\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <imports>\n" +
                        "      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
                        "    </imports>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"age &gt; 21\" >\n" +
                        "            Person( age &gt; 21 )" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"5\" name=\"age &lt;=21 \" >\n" +
                        "           Person( age &lt;= 21 )" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        assertThat(list).isEmpty();
        Person person = new Person("John Doe", 20);
        kruntime.getKieSession().insert(person);
        kruntime.getKieSession().fireAllRules();
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("2");
    }

    @Test
    @Disabled("Needs fix")
    public void FIXMEtestDelayedStateConstraintPriorities1() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <imports>\n" +
                        "      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
                        "    </imports>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"one\" priority=\"1\" >\n" +
                        "            Person( )" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"5\" name=\"two\" priority=\"2\" >\n" +
                        "           Person( )" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        assertThat(list).isEmpty();
        Person person = new Person("John Doe", 30);
        kruntime.getKieSession().insert(person);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("1");
    }

    @Test
    @Disabled("Needs fix")
    public void FIXMEtestDelayedStateConstraintPriorities2() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <imports>\n" +
                        "      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
                        "    </imports>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" >\n" +
                        "      <constraints>\n" +
                        "        <constraint toNodeId=\"3\" name=\"one\" priority=\"2\" >\n" +
                        "            Person( )" +
                        "        </constraint>" +
                        "       <constraint toNodeId=\"5\" name=\"two\" priority=\"1\" >\n" +
                        "           Person( )" +
                        "        </constraint>" +
                        "      </constraints>\n" +
                        "    </state>\n" +
                        "    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"4\" name=\"End\" />\n" +
                        "    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
                        "      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"6\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "    <connection from=\"3\" to=\"4\" />\n" +
                        "    <connection from=\"2\" to=\"5\" />\n" +
                        "    <connection from=\"5\" to=\"6\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.state");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        assertThat(list).isEmpty();
        Person person = new Person("John Doe", 30);
        kruntime.getKieSession().insert(person);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo("2");
    }

    @Test
    public void testActionState() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"s\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        <value>a</value>\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" name=\"State\" >\n" +
                        "      <onEntry>" +
                        "        <action type=\"expression\" dialect=\"mvel\" >list.add(\"Action1\" + s);</action>\n" +
                        "        <action type=\"expression\" dialect=\"java\" >list.add(\"Action2\" + s);</action>\n" +
                        "      </onEntry>\n" +
                        "      <onExit>\n" +
                        "        <action type=\"expression\" dialect=\"mvel\" >list.add(\"Action3\" + s);</action>\n" +
                        "        <action type=\"expression\" dialect=\"java\" >list.add(\"Action4\" + s);</action>\n" +
                        "      </onExit>\n" +
                        "    </state>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        // start process
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kruntime.startProcess("org.drools.state");
        // should be in state A
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        StateNodeInstance stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("State");
        assertThat(list).hasSize(2).contains("Action1a", "Action2a");

        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).isEmpty();
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(4).contains("Action3a", "Action4a");
    }

    @Test
    public void testTimerState() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
                        "\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"s\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "        <value>a</value>\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <state id=\"2\" name=\"State\" >\n" +
                        "      <timers>\n" +
                        "        <timer id=\"1\" delay=\"1s\" period=\"2s\" >\n" +
                        "          <action type=\"expression\" dialect=\"mvel\" >list.add(\"Timer1\" + s);</action>\n" +
                        "        </timer>\n" +
                        "        <timer id=\"2\" delay=\"1s\" period=\"2s\" >\n" +
                        "          <action type=\"expression\" dialect=\"mvel\" >list.add(\"Timer2\" + s);</action>\n" +
                        "        </timer>\n" +
                        "      </timers>\n" +
                        "    </state>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "\n" +
                        "</process>");
        builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);
        new Thread(() -> kruntime.getKieSession().fireUntilHalt()).start();
        // start process
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kruntime.startProcess("org.drools.state");
        // should be in state A
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).hasSize(1);
        StateNodeInstance stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertThat(stateInstance.getNodeName()).isEqualTo("State");
        assertThat(list).isEmpty();
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
        }
        assertThat(list).hasSize(4).contains("Timer1a", "Timer2a");

        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        assertThat(nodeInstances).isEmpty();
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(4);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
        }
        assertThat(list).hasSize(4);
        kruntime.getKieSession().halt();
    }

}
