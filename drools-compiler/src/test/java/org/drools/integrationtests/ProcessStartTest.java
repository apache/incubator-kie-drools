package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Message;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class ProcessStartTest extends TestCase {
    
	public void testStartConstraintTrigger() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.start\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"SomeVar\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "      <variable name=\"SomeOtherVar\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" >\n" +
			"      <triggers>" +
			"        <trigger type=\"constraint\" >\n" +
			"          <constraint type=\"rule\" dialect=\"mvel\" >p:Person()</constraint>\n" +
			"          <mapping type=\"in\" from=\"p.getName()\" to=\"SomeVar\" />\n" +
			"          <mapping type=\"in\" from=\"&quot;SomeString&quot;\" to=\"SomeOtherVar\" />\n" +
			"        </trigger>\n " +
			"      </triggers>\n" +
			"    </start>\n" +
			"    <actionNode id=\"2\" name=\"Action\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >myList.add(context.getVariable(\"SomeVar\"));\n" +
			"myList.add(context.getVariable(\"SomeOtherVar\"));</action>\n" +
			"    </actionNode>\n" + 
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
		if (!builder.getErrors().isEmpty()) {
			for (DroolsError error: builder.getErrors().getErrors()) {
				System.err.println(error);
			}
			fail("Could not build process");
		}
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		StatefulSession session = ruleBase.newStatefulSession();
		List<Message> myList = new ArrayList<Message>();
		session.setGlobal("myList", myList);

		assertEquals(0, myList.size());
        
		Person jack = new Person();
        jack.setName("Jack");
        session.insert(jack);
        session.fireAllRules();
        assertEquals(2, myList.size());
        assertEquals("Jack", myList.get(0));
        assertEquals("SomeString", myList.get(1));
	}
	
	public void testStartEventTrigger() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.start\" package-name=\"org.drools\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"SomeVar\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "      <variable name=\"SomeOtherVar\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" >\n" +
			"      <triggers>" +
			"        <trigger type=\"event\" >\n" +
			"          <eventFilters>" +
			"            <eventFilter type=\"eventType\" eventType=\"myEvent\" />\n" +
			"          </eventFilters>" +
			"          <mapping type=\"in\" from=\"event\" to=\"SomeVar\" />\n" +
			"          <mapping type=\"in\" from=\"SomeString\" to=\"SomeOtherVar\" />\n" +
			"        </trigger>\n " +
			"      </triggers>\n" +
			"    </start>\n" +
			"    <actionNode id=\"2\" name=\"Action\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >myList.add(context.getVariable(\"SomeVar\"));\n" +
			"myList.add(context.getVariable(\"SomeOtherVar\"));</action>\n" +
			"    </actionNode>\n" + 
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
		if (!builder.getErrors().isEmpty()) {
			for (DroolsError error: builder.getErrors().getErrors()) {
				System.err.println(error);
			}
			fail("Could not build process");
		}
		
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		StatefulSession session = ruleBase.newStatefulSession();
		List<Message> myList = new ArrayList<Message>();
		session.setGlobal("myList", myList);

		assertEquals(0, myList.size());
        
		((InternalWorkingMemory) session).getProcessRuntime().signalEvent("myEvent", "Jack");
        session.fireAllRules();
        assertEquals(2, myList.size());
        assertEquals("Jack", myList.get(0));
        assertEquals("SomeString", myList.get(1));
	}
	
}
