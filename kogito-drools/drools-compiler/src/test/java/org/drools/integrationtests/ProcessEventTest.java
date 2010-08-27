package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.rule.Package;
import org.drools.runtime.process.ProcessInstance;

public class ProcessEventTest extends TestCase {
    
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
        ProcessInstance processInstance =
            session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    public void testProcessInstanceSignalEvent() throws Exception {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        ProcessInstance processInstance = session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        session = SerializationHelper.getSerialisedStatefulSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        processInstance.signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    public void testExternalEventCorrelation() throws Exception {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        ProcessInstance processInstance = session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals("SomeText", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
        
        session = SerializationHelper.getSerialisedStatefulSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }

    public void testInternalEventCorrelation() throws Exception {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        ProcessInstance processInstance = session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals("SomeText", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));

        session = SerializationHelper.getSerialisedStatefulSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
    }
    
    public void testInternalNodeSignalCompositeEvent() {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance =
            workingMemory.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance)
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    public void testProcessInstanceSignalCompositeEvent() throws Exception {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        ProcessInstance processInstance = session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        session = SerializationHelper.getSerialisedStatefulSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        processInstance.signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    public void testExternalCompositeEventCorrelation() throws Exception {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        ProcessInstance processInstance = session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        session = SerializationHelper.getSerialisedStatefulSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("MyValue", ((VariableScopeInstance) 
    		((org.drools.process.instance.ProcessInstance) processInstance).getContextInstance(
				VariableScope.VARIABLE_SCOPE)).getVariable("MyVar"));
    }
    
    public void testInternalCompositeEventCorrelation() throws Exception {
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
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        ProcessInstance processInstance = session.startProcess("org.drools.event");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());

        session = SerializationHelper.getSerialisedStatefulSession(session);
        processInstance = session.getProcessInstance(processInstance.getId());
        ((InternalWorkingMemory) session).getProcessRuntime().signalEvent("MyEvent", "MyValue");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
    }
    
}
