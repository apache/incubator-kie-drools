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

package org.jbpm.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.jbpm.JbpmBaseTest;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.junit.Test;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.runtime.process.ProcessInstance;

public class ProcessEventListenerTest extends JbpmBaseTest {

    @Test
	public void testInternalNodeSignalEvent() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(process);
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();

        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();

        final ProcessEventListener listener = createProcessEventListener(processEventList);

        ((InternalWorkingMemory)session).getProcessRuntime().addEventListener(listener);
        ProcessInstance processInstance =
        	((InternalWorkingMemory)session).getProcessRuntime().startProcess("org.kie.event");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance)
                                    ((org.jbpm.process.instance.ProcessInstance) processInstance)
                                        .getContextInstance(VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
        assertEquals( 28, processEventList.size() );
        for (ProcessEvent e: processEventList) {
        	System.out.println(e);
        }
        assertEquals( "org.kie.event", ((ProcessStartedEvent) processEventList.get(2)).getProcessInstance().getProcessId());

    }
    
    private ProcessEventListener createProcessEventListener(final List<ProcessEvent> processEventList) {  
        return new ProcessEventListener() {

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

            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                processEventList.add(event);
            }
        };
    }
    
    private static final String process = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.kie.event\" package-name=\"org.kie\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <variables>\n" +
            "      <variable name=\"MyVar\" >\n" +
            "        <type name=\"org.kie.process.core.datatype.impl.type.StringDataType\" />\n" +
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
            "</process>";
            
}
