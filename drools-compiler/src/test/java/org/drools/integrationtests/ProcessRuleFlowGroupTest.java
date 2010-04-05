package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;

public class ProcessRuleFlowGroupTest extends TestCase {
    
    public void testRuleSetProcessContext() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.ruleset\" package-name=\"org.drools\" version=\"1\" >\n" +
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
            "package org.drools;\n" +
            "\n" +
            "import org.drools.Person;\n" +
            "import org.drools.runtime.process.ProcessContext;\n" +
            "\n" +
            "rule MyRule ruleflow-group \"MyGroup\"\n" +
            "  when\n" +
            "    Person( age > 25 )\n" +
            "  then\n" +
            "    System.out.println(drools.getContext(ProcessContext.class).getProcessInstance().getProcessName());\n" +
            "end");
        builder.addRuleFlow(source);
        builder.addPackageFromDrl(source2);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
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
