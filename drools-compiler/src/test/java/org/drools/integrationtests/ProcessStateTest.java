package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryConsoleLogger;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;

public class ProcessStateTest extends TestCase {
    
    public void testOnEntryExit() {
        PackageBuilder builder = new PackageBuilder();
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
            "    <state id=\"2\" name=\"State\" />\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processInstance.signalEvent("signal", null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }


    public void testNewStateNode() {
        PackageBuilder builder = new PackageBuilder();
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "       <constraint name=\"one\" type=\"rule\" >" +
            "           eval(true)" +
            "       </constraint>"+
             "       <constraint name=\"two\" type=\"rule\" >" +
            "           eval(false)" +
            "       </constraint>"+
            "    </state>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
             "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" fromType=\"one\" />\n" +
            "    <connection from=\"2\" to=\"4\" fromType=\"two\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        PackageBuilderErrors errors = builder.getErrors();

        for(DroolsError error: errors.getErrors()){
            System.out.println("Error: "+error);
        }
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        new WorkingMemoryConsoleLogger(workingMemory);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.state");
        //If the any constraint evaluates to true you can also signal the state node to continue
       // assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
      //  assertEquals(2, ((RuleFlowProcessInstance)processInstance).getNodeInstances().iterator().next().getNodeId());
      //  processInstance.signalEvent("signal", null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    
}
