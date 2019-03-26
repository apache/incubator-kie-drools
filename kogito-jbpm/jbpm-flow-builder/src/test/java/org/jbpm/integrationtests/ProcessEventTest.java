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

import org.drools.core.common.InternalWorkingMemory;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import static org.junit.Assert.assertEquals;

public class ProcessEventTest extends AbstractBaseTest {
  
    @Test
    public void testInternalNodeSignalEvent() {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
        KieSession session = createKieSession(builder.getPackages());
        
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    @Test
    public void testProcessInstanceSignalEvent() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
            "    <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"3\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        KieSession session = createKieSession(builder.getPackages());
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        processInstance.signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    @Test
    public void testExternalEventCorrelation() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <eventNode id=\"2\" name=\"Event\" variableName=\"MyVar\" scope=\"external\" >\n" +
            "      <eventFilters>\n" +
            "        <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "      </eventFilters>\n" +
            "    </eventNode>\n" +
            "    <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"3\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        KieSession session = createKieSession(builder.getPackages());
                
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals("SomeText", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
        
        session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }

    @Test
    public void testInternalEventCorrelation() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
            "    <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"3\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        KieSession session = createKieSession(builder.getPackages());
        
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals("SomeText", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));

        session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
    }
    
    @Test
    public void testInternalNodeSignalCompositeEvent() {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"CompositeNode\" >\n" +
            "      <nodes>\n" +
            "        <actionNode id=\"1\" name=\"Signal Event\" >\n" +
            "          <action type=\"expression\" dialect=\"java\" >context.getProcessInstance().signalEvent(\"MyEvent\", \"MyValue\");</action>\n" +
            "        </actionNode>\n" +
            "        <eventNode id=\"2\" name=\"Event\" variableName=\"MyVar\" >\n" +
            "          <eventFilters>\n" +
            "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "          </eventFilters>\n" +
            "        </eventNode>\n" +
            "        <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"1\" to=\"3\" />\n" +
            "        <connection from=\"2\" to=\"3\" />\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
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
        KieSession ksession = createKieSession(builder.getPackages());
        
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.core.event");
        assertEquals("Process did not complete!", ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance)
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    @Test
    public void testProcessInstanceSignalCompositeEvent() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"CompositeNode\" >\n" +
            "      <nodes>\n" +
            "        <eventNode id=\"2\" name=\"Event\" variableName=\"MyVar\" >\n" +
            "          <eventFilters>\n" +
            "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "          </eventFilters>\n" +
            "        </eventNode>\n" +
            "        <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"2\" to=\"3\" />\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
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
        KieSession session = createKieSession(builder.getPackages());
        
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        processInstance.signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    @Test
    public void testExternalCompositeEventCorrelation() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"CompositeNode\" >\n" +
            "      <nodes>\n" +
            "        <eventNode id=\"2\" name=\"Event\" variableName=\"MyVar\" scope=\"external\" >\n" +
            "          <eventFilters>\n" +
            "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "          </eventFilters>\n" +
            "        </eventNode>\n" +
            "        <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"2\" to=\"3\" />\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
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
        KieSession session = createKieSession(builder.getPackages());
        
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.jbpm.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    @Test
    public void testInternalCompositeEventCorrelation() throws Exception {
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.core.event\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"MyVar\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"CompositeNode\" >\n" +
            "      <nodes>\n" +
            "        <eventNode id=\"2\" name=\"Event\" variableName=\"MyVar\" >\n" +
            "          <eventFilters>\n" +
            "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "          </eventFilters>\n" +
            "        </eventNode>\n" +
            "        <join id=\"3\" name=\"Join\" type=\"1\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"2\" to=\"3\" />\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
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
        KieSession session = createKieSession(builder.getPackages());
        ProcessInstance processInstance = session.startProcess("org.drools.core.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        session = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
    }
    
}
