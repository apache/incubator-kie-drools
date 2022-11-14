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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.io.ByteArrayResource;
import org.drools.io.ReaderResource;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.jbpm.compiler.xml.compiler.SemanticKnowledgeBuilderConfigurationImpl;
import org.jbpm.integrationtests.handler.TestWorkItemHandler;
import org.jbpm.integrationtests.test.Person;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.instance.WorkflowProcessInstanceUpgrader;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessUpgradeTest extends AbstractBaseTest {

    @Test
    public void testDefaultUpgrade() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.integrationtests.test.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        builder = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder(new SemanticKnowledgeBuilderConfigurationImpl());
        builder.add(new ReaderResource(new StringReader(rule)), ResourceType.DRL);

        String process =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <workItem id=\"2\" name=\"Hello\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
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

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(builder.getKnowledgePackages());
        KogitoProcessRuntime kruntime = InternalProcessRuntime.asKogitoProcessRuntime(kbase.newKieSession());

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);

        Person p = new Person("bobba fet", 32);
        kruntime.getKieSession().insert(p);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.test.ruleflow");

        assertThat(kruntime.getKieSession().getProcessInstances()).hasSize(1);

        String process2 =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow2\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <workItem id=\"2\" name=\"Hello\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "      </work>\n" +
                        "    </workItem>\n" +
                        "    <actionNode id=\"4\" name=\"Action\" >" +
                        "      <action type=\"expression\" dialect=\"java\">System.out.println();\n" +
                        "list.add(\"Executed\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\"/>\n" +
                        "    <connection from=\"2\" to=\"4\"/>\n" +
                        "    <connection from=\"4\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";
        builder = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder(new SemanticKnowledgeBuilderConfigurationImpl());
        builder.add(new ReaderResource(new StringReader(process2)), ResourceType.DRF);
        kbase.addPackages(builder.getKnowledgePackages());
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
                kruntime, processInstance.getStringId(), "org.test.ruleflow2", new HashMap<>());
        assertThat(processInstance.getProcessId()).isEqualTo("org.test.ruleflow2");

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        assertThat(list).hasSize(1);
    }

    @Test
    public void testMappingUpgrade() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.integrationtests.test.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        builder = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder(new SemanticKnowledgeBuilderConfigurationImpl());
        builder.add(new ReaderResource(new StringReader(rule)), ResourceType.DRL);

        String process =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <workItem id=\"2\" name=\"Hello\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
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

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(builder.getKnowledgePackages());
        KogitoProcessRuntime kruntime = InternalProcessRuntime.asKogitoProcessRuntime(kbase.newKieSession());

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);

        Person p = new Person("bobba fet", 32);
        kruntime.getKieSession().insert(p);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.test.ruleflow");

        assertThat(kruntime.getKogitoProcessInstances()).hasSize(1);

        String process2 =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow2\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <workItem id=\"102\" name=\"Hello\" >\n" +
                        "      <work name=\"Human Task\" >\n" +
                        "      </work>\n" +
                        "    </workItem>\n" +
                        "    <actionNode id=\"4\" name=\"Action\" >" +
                        "      <action type=\"expression\" dialect=\"java\">System.out.println();\n" +
                        "list.add(\"Executed\");</action>\n" +
                        "    </actionNode>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"102\"/>\n" +
                        "    <connection from=\"102\" to=\"4\"/>\n" +
                        "    <connection from=\"4\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";
        builder = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder(new SemanticKnowledgeBuilderConfigurationImpl());
        builder.add(new ReaderResource(new StringReader(process2)), ResourceType.DRF);
        kbase.addPackages(builder.getKnowledgePackages());
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2", 102L);

        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
                kruntime, processInstance.getStringId(), "org.test.ruleflow2", mapping);
        assertThat(processInstance.getProcessId()).isEqualTo("org.test.ruleflow2");

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertThat(list).hasSize(1);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testCompositeMappingUpgrade() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.integrationtests.test.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        builder = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder(new SemanticKnowledgeBuilderConfigurationImpl());
        builder.add(new ByteArrayResource(rule.getBytes()), ResourceType.DRL);

        String process =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <composite id=\"2\" name=\"Composite\" >\n" +
                        "      <nodes>\n" +
                        "        <workItem id=\"1\" name=\"Hello\" >\n" +
                        "          <work name=\"Human Task\" >\n" +
                        "          </work>\n" +
                        "        </workItem>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </composite>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\"/>\n" +
                        "    <connection from=\"2\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";
        builder.add(new ReaderResource(new StringReader(process)), ResourceType.DRF);

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(builder.getKnowledgePackages());
        KogitoProcessRuntime kruntime = InternalProcessRuntime.asKogitoProcessRuntime(kbase.newKieSession());

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);

        Person p = new Person("bobba fet", 32);
        kruntime.getKieSession().insert(p);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.test.ruleflow");

        assertThat(kruntime.getKieSession().getProcessInstances()).hasSize(1);

        String process2 =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow2\" package-name=\"org.test\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "  </header>\n" +
                        "  <nodes>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <composite id=\"2\" name=\"Composite\" >\n" +
                        "      <nodes>\n" +
                        "        <workItem id=\"101\" name=\"Hello\" >\n" +
                        "          <work name=\"Human Task\" >\n" +
                        "          </work>\n" +
                        "        </workItem>\n" +
                        "        <actionNode id=\"2\" name=\"Action\" >" +
                        "          <action type=\"expression\" dialect=\"java\">System.out.println();\n" +
                        "list.add(\"Executed\");</action>\n" +
                        "        </actionNode>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "        <connection from=\"101\" to=\"2\"/>\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"101\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"2\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </composite>\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\"/>\n" +
                        "    <connection from=\"2\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";
        builder.add(new ReaderResource(new StringReader(process2)), ResourceType.DRF);
        kbase.addPackages(builder.getKnowledgePackages());
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2:1", 101L);

        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
                kruntime, processInstance.getStringId(), "org.test.ruleflow2", mapping);
        assertThat(processInstance.getProcessId()).isEqualTo("org.test.ruleflow2");

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertThat(list).hasSize(1);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

}
