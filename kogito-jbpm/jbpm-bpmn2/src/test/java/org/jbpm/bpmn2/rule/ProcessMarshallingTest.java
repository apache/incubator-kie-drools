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
package org.jbpm.bpmn2.rule;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.integrationtests.test.Person;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessMarshallingTest extends AbstractBaseTest {

    @Test
    public void testMarshallingProcessInstancesAndGlobals() {
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
                        "    <ruleSet id=\"2\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\"/>\n" +
                        "    <connection from=\"2\" to=\"3\"/>\n" +
                        "  </connections>\n" +
                        "</process>";

        builder.add(ResourceFactory.newReaderResource(new StringReader(rule)), ResourceType.DRL);
        builder.add(ResourceFactory.newReaderResource(new StringReader(process)), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        kruntime.getKieRuntime().getEnvironment().set("org.jbpm.rule.task.waitstate", true);

        List<Object> list = new ArrayList<Object>();
        kruntime.getKieSession().setGlobal("list", list);

        Person p = new Person("bobba fet", 32);
        kruntime.getKieSession().insert(p);
        kruntime.startProcess("org.test.ruleflow");

        assertThat(kruntime.getKieSession().getProcessInstances()).hasSize(1);

        kruntime.getKieSession().fireAllRules();

        assertThat(((List<Object>) kruntime.getKieSession().getGlobal("list"))).hasSize(1);
        assertThat(((List<Object>) kruntime.getKieSession().getGlobal("list")).get(0)).isEqualTo(p);
        assertThat(kruntime.getKieSession().getProcessInstances()).isEmpty();
    }

}
