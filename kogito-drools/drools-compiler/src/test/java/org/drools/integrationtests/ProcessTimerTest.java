package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulSession;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Message;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;

public class ProcessTimerTest extends TestCase {
	
	public void testSimpleProcess() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.timer\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Message\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <end id=\"2\" name=\"End\" />\n" +
			"    <timer id=\"3\" name=\"Timer\" delay=\"800\"  period=\"200\" />\n" +
			"    <actionNode id=\"4\" name=\"Action\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >System.out.println(\"Triggered\");\n" +
			"myList.add( new Message() );\n" +
			"insert( new Message() );\n" +
			"</action>\n" +
			"    </actionNode>/n" + 
			"    <milestone id=\"5\" name=\"Wait\" >Number( intValue &gt;= 5 ) from accumulate ( m: Message( ), count( m ) )</milestone>\n" +
			"  </nodes>\n" +
			"\n" +
			"  <connections>\n" +
			"    <connection from=\"5\" to=\"2\" />\n" +
			"    <connection from=\"1\" to=\"3\" />\n" +
			"    <connection from=\"3\" to=\"4\" />\n" +
			"    <connection from=\"4\" to=\"5\" />\n" +
			"  </connections>\n" +
			"\n" +
			"</process>");
		builder.addRuleFlow(source);
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		StatefulSession session = ruleBase.newStatefulSession();
		List<Message> myList = new ArrayList<Message>();
		session.setGlobal("myList", myList);
        ProcessInstance processInstance =
        	session.startProcess("org.drools.timer");
        assertEquals(0, myList.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, session.getTimerManager().getTimers().size());
        
        session = getSerialisedStatefulSession( session );
        assertEquals(1, session.getTimerManager().getTimers().size());

        // test that the delay works
        System.out.println("Sleep1");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // do nothing
        }
        assertEquals(0, myList.size());
        
        // test that the period works
        System.out.println("Sleep2");
        try {
        	Thread.sleep(1300);
        } catch (InterruptedException e) {
        	// do nothing
        }
        session.fireAllRules();
        assertEquals(5, myList.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
	}

}
