/*
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.rule.Package;
import org.drools.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
public class ProcessEventListenerTest extends TestCase{
    public void testInternalNodeSignalEvent() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <eventNode id=\"2\" name=\"Event\" variableName=\"MyVar\" >\n" +
            "      <eventFilters>\n" +
            "        <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "      </eventFilters>\n" +
            "    </eventNode>\n" +
            "    <actionNode id=\"3\" name=\"Signal Event\" >\n" +
            "      <action type=\"expression\" dialect=\"java\" >context.getProcessInstance().signalEvent(\"MyEvent\", \"MyValue\");</action>\n" +
            "    </actionNode>\n" +
            "    <join id=\"4\" name=\"Join\" type=\"1\" />\n" +
            "    <end id=\"5\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"3\" />\n" +
            "    <connection from=\"2\" to=\"4\" />\n" +
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

        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();

        final ProcessEventListener listener = new ProcessEventListener() {

			public void afterNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
			}

			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
			}

			public void afterProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
			}

			public void afterProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
			}

			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                processEventList.add(event);
			}

			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                processEventList.add(event);
			}

			public void beforeProcessCompleted(ProcessCompletedEvent event) {
                processEventList.add(event);
			}

			public void beforeProcessStarted(ProcessStartedEvent event) {
                processEventList.add(event);
			}

        };

        ((InternalWorkingMemory)session).getProcessRuntime().addEventListener(listener);
        ProcessInstance processInstance =
        	((InternalWorkingMemory)session).getProcessRuntime().startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance)
                                    ((org.drools.process.instance.ProcessInstance) processInstance)
                                        .getContextInstance(VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
        assertEquals( 24, processEventList.size() );
        assertEquals( "org.drools.event", ((ProcessStartedEvent) processEventList.get(0)).getProcessInstance().getProcessId());

    }
}
