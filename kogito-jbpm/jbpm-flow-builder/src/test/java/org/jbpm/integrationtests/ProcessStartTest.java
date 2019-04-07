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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.DroolsError;
import org.drools.core.common.InternalWorkingMemory;
import org.jbpm.integrationtests.test.Message;
import org.jbpm.integrationtests.test.Person;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProcessStartTest extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessStartTest.class);
    
    @Test
	public void testStartConstraintTrigger() throws Exception {
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.start\" package-name=\"org.jbpm\" version=\"1\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"SomeVar\" >\n" +
            "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "      <variable name=\"SomeOtherVar\" >\n" +
            "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
			    logger.error(error.toString());
			}
			fail("Could not build process");
		}
		
        KieSession session = createKieSession(builder.getPackages());
        
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
	
    @Test
	public void testStartEventTrigger() throws Exception {
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
            "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "      <variable name=\"SomeOtherVar\" >\n" +
            "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
			    logger.error(error.toString());
			}
			fail("Could not build process");
		}
		
        KieSession session = createKieSession(builder.getPackages());
        
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
