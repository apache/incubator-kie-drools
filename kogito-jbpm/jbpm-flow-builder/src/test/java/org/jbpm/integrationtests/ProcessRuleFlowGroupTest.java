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

package org.jbpm.integrationtests;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;

import org.jbpm.integrationtests.test.Person;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class ProcessRuleFlowGroupTest extends AbstractBaseTest {
    
    @Test
    public void testRuleSetProcessContext() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.ruleset\" package-name=\"org.jbpm\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <ruleSet id=\"2\" name=\"RuleSet\" ruleFlowGroup=\"MyGroup\" >\n" +
            "    </ruleSet>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        Reader source2 = new StringReader(
            "package org.jbpm;\n" +
            "\n" +
            "import org.jbpm.integrationtests.test.Person;\n" +
            "import org.kie.api.runtime.process.ProcessContext;\n" +
            "\n" +
            "rule MyRule ruleflow-group \"MyGroup\" dialect \"mvel\" \n" +
            "  when\n" +
            "    Person( age > 25 )\n" +
            "  then\n" +
            "    System.out.println(drools.getContext(ProcessContext).getProcessInstance().getProcessName());\n" +
            "end");
        builder.addRuleFlow(source);
        builder.addPackageFromDrl(source2);

        KieSession workingMemory = createKieSession(builder.getPackages());
        workingMemory.getEnvironment().set("org.jbpm.rule.task.waitstate", "true");
        
        Person person = new Person();
        person.setAge(30);
        workingMemory.insert(person);
        // start process
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance)
            workingMemory.startProcess("org.drools.ruleset");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workingMemory.fireAllRules();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
