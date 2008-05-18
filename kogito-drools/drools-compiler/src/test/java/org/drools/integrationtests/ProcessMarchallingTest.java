package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulSession;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.Package;

public class ProcessMarchallingTest extends TestCase {

    public void test1() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ));
        
        String process = 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
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
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        session.insert( p );
        session.startProcess("org.test.ruleflow");
        
        assertEquals(1, session.getProcessInstances().size());
        
        session = getSerialisedStatefulSession( session );
        assertEquals(1, session.getProcessInstances().size());
        
        session.fireAllRules();

        assertEquals( 1, ((List) session.getGlobal("list")).size());
        assertEquals( p, ((List) session.getGlobal("list")).get(0));
        assertEquals(0, session.getProcessInstances().size());
    }
    
    public void test2() throws Exception {
        String process = 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
    		"    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
    		"  <header>\n" +
    		"  </header>\n" +
    		"  <nodes>\n" +
    		"    <start id=\"1\" name=\"Start\" />\n" +
    		"    <workItem id=\"2\" name=\"Email\" >\n" +
    		"      <work name=\"Email\" >\n" +
    		"        <parameter name=\"Subject\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" >Mail</parameter>\n" +
    		"        <parameter name=\"Text\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" >This is an email</parameter>\n" +
    		"        <parameter name=\"To\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" >you@mail.com</parameter>\n" +
    		"        <parameter name=\"From\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" >me@mail.com</parameter>\n" +
    		"      </work>\n" +
    		"    </workItem>\n" +
    		"    <end id=\"3\" name=\"End\" />\n" +
    		"  </nodes>\n" +
    		"  <connections>\n" +
    		"    <connection from=\"1\" to=\"2\"/>\n" +
			"    <connection from=\"2\" to=\"3\"/>\n" +
			"  </connections>\n" +
			"</process>";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Email", handler);
        session.startProcess("org.test.ruleflow");

        assertEquals(1, session.getProcessInstances().size());
        assertTrue(handler.getWorkItemId() != -1);
        
        session = getSerialisedStatefulSession( session );
        assertEquals(1, session.getProcessInstances().size());
        
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
        session.getWorkItemManager().completeWorkItem(handler.getWorkItemId(), null);
        logger.writeToDisk();

        assertEquals(0, session.getProcessInstances().size());
    }
    
    private static class TestWorkItemHandler implements WorkItemHandler {
    	private long workItemId = -1;
    	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			workItemId = workItem.getId();
		}
		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		}
		public long getWorkItemId() {
			return workItemId;
		}
    }
    
}