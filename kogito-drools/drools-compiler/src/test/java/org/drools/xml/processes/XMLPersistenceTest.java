package org.drools.xml.processes;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.process.core.impl.WorkImpl;
import org.drools.process.core.timer.Timer;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.XmlProcessReader;
import org.drools.xml.XmlRuleFlowProcessDumper;

public class XMLPersistenceTest extends TestCase {
    
    public void testPersistenceOfEmptyNodes() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess() {
            private static final long serialVersionUID = 400L;
            int id = 0;
            public void addNode(Node node) {
                node.setId(++id);
                super.addNode(node);
            }
        };
        process.addNode(new StartNode());
        process.addNode(new EndNode());
        process.addNode(new ActionNode());
        process.addNode(new Split());
        process.addNode(new Join());
        process.addNode(new MilestoneNode());
        process.addNode(new RuleSetNode());
        process.addNode(new SubProcessNode());
        process.addNode(new WorkItemNode());
        process.addNode(new TimerNode());
        
        String xml = XmlRuleFlowProcessDumper.INSTANCE.dump(process, false);
        if (xml == null) {
            throw new IllegalArgumentException("Failed to persist empty nodes!");
        }
        
        System.out.println(xml);
        System.out.println("-------------------");
        
        XmlProcessReader reader = new XmlProcessReader(
            new PackageBuilderConfiguration().getSemanticModules());
        process = (RuleFlowProcess) reader.read(new StringReader(xml));
        if (process == null) {
            throw new IllegalArgumentException("Failed to reload process!");
        }
        
        assertEquals(10, process.getNodes().length);
        
//        System.out.println("************************************");
        
        String xml2 = XmlRuleFlowProcessDumper.INSTANCE.dump(process, false);
        if (xml2 == null) {
            throw new IllegalArgumentException("Failed to persist empty nodes!");
        }
        
        System.out.println(xml2);
        
//        assertEquals(xml, xml2);
    }

    public void testPersistenceOfFullNodes() throws Exception {
        RuleFlowProcess process = new RuleFlowProcess() {
            private static final long serialVersionUID = 400L;
            int id = 0;
            public void addNode(Node node) {
                node.setId(++id);
                super.addNode(node);
            }
        };
        process.setMetaData("routerLayout", 1);
        
        List<String> imports = new ArrayList<String>();
        imports.add("import1");
        imports.add("import2");
        process.setImports(imports);
        
        Map<String, String> globals = new HashMap<String, String>();
        globals.put("name1", "type1");
        globals.put("name2", "type2");
        process.setGlobals(globals);
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("variable1");
        variable.setType(new StringDataType());
        variable.setValue("value");
        variables.add(variable);
        variable = new Variable();
        variable.setName("variable2");
        variable.setType(new IntegerDataType());
        variable.setValue(2);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);
        
        StartNode startNode = new StartNode();
        startNode.setName("start");
        startNode.setMetaData("x", 1);
        startNode.setMetaData("y", 2);
        startNode.setMetaData("width", 3);
        startNode.setMetaData("height", 4);
        process.addNode(startNode);
        
        ActionNode actionNode = new ActionNode();
        actionNode.setName("action");
        actionNode.setMetaData("x", 1);
        actionNode.setMetaData("y", 2);
        actionNode.setMetaData("width", 3);
        actionNode.setMetaData("height", 4);
        DroolsConsequenceAction action = new DroolsConsequenceAction("dialect", "consequence");
        actionNode.setAction(action);
        process.addNode(actionNode);
        
        RuleSetNode ruleSetNode = new RuleSetNode();
        ruleSetNode.setName("action");
        ruleSetNode.setMetaData("x", 1);
        ruleSetNode.setMetaData("y", 2);
        ruleSetNode.setMetaData("width", 3);
        ruleSetNode.setMetaData("height", 4);
        ruleSetNode.setRuleFlowGroup("ruleFlowGroup");
        process.addNode(ruleSetNode);
        
        Split split = new Split();
        split.setName("split");
        split.setMetaData("x", 1);
        split.setMetaData("y", 2);
        split.setMetaData("width", 3);
        split.setMetaData("height", 4);
        split.setType(Split.TYPE_XOR);
        Connection connection = new ConnectionImpl(split, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
        Constraint constraint = new ConstraintImpl();
        constraint.setName("constraint1");
        constraint.setPriority(1);
        constraint.setDialect("dialect1");
        constraint.setType("type1");
        constraint.setConstraint("constraint-text1");
        split.setConstraint(connection, constraint);
        connection = new ConnectionImpl(split, Node.CONNECTION_DEFAULT_TYPE, ruleSetNode, Node.CONNECTION_DEFAULT_TYPE);
        constraint = new ConstraintImpl();
        constraint.setName("constraint2");
        constraint.setPriority(2);
        constraint.setDialect("dialect2");
        constraint.setType("type2");
        constraint.setConstraint("constraint-text2");
        split.setConstraint(connection, constraint);
        process.addNode(split);
        new ConnectionImpl(startNode, Node.CONNECTION_DEFAULT_TYPE, split, Node.CONNECTION_DEFAULT_TYPE);
        
        Join join = new Join();
        join.setName("join");
        join.setMetaData("x", 1);
        join.setMetaData("y", 2);
        join.setMetaData("width", 3);
        join.setMetaData("height", 4);
        join.setType(Join.TYPE_XOR);
        process.addNode(join);
        new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, join, Node.CONNECTION_DEFAULT_TYPE);
        new ConnectionImpl(ruleSetNode, Node.CONNECTION_DEFAULT_TYPE, join, Node.CONNECTION_DEFAULT_TYPE);

        
        MilestoneNode milestone = new MilestoneNode();
        milestone.setName("milestone");
        milestone.setMetaData("x", 1);
        milestone.setMetaData("y", 2);
        milestone.setMetaData("width", 3);
        milestone.setMetaData("height", 4);
        milestone.setConstraint("constraint");
        process.addNode(milestone);
        connection = new ConnectionImpl(join, Node.CONNECTION_DEFAULT_TYPE, milestone, Node.CONNECTION_DEFAULT_TYPE);
        connection.setMetaData("bendpoints", "[10,10;20;20]");
        
        SubProcessNode subProcess = new SubProcessNode();
        subProcess.setName("subProcess");
        subProcess.setMetaData("x", 1);
        subProcess.setMetaData("y", 2);
        subProcess.setMetaData("width", 3);
        subProcess.setMetaData("height", 4);
        subProcess.setProcessId("processId");
        subProcess.setWaitForCompletion(false);
        subProcess.setIndependent(false);
        subProcess.addInMapping("subvar1", "var1");
        subProcess.addOutMapping("subvar2", "var2");
        process.addNode(subProcess);
        connection = new ConnectionImpl(milestone, Node.CONNECTION_DEFAULT_TYPE, subProcess, Node.CONNECTION_DEFAULT_TYPE);
        connection.setMetaData("bendpoints", "[10,10]");

        WorkItemNode workItemNode = new WorkItemNode();
        Work work = new WorkImpl();
        work.setName("workname");
        Set<ParameterDefinition> parameterDefinitions = new HashSet<ParameterDefinition>();
        ParameterDefinition parameterDefinition = new ParameterDefinitionImpl("param1", new StringDataType());
        parameterDefinitions.add(parameterDefinition);
        parameterDefinition = new ParameterDefinitionImpl("param2", new IntegerDataType());
        parameterDefinitions.add(parameterDefinition);
        work.setParameterDefinitions(parameterDefinitions);
        work.setParameter("param1", "value1");
        work.setParameter("param2", 1);
        workItemNode.setWork(work);
        workItemNode.setWaitForCompletion(false);
        workItemNode.addInMapping("param1", "var1");
        workItemNode.addOutMapping("param2", "var2");
        process.addNode(workItemNode);
        connection = new ConnectionImpl(subProcess, Node.CONNECTION_DEFAULT_TYPE, workItemNode, Node.CONNECTION_DEFAULT_TYPE);
        connection.setMetaData("bendpoints", "[]");
        
        TimerNode timerNode = new TimerNode();
        timerNode.setName("timer");
        timerNode.setMetaData("x", 1);
        timerNode.setMetaData("y", 2);
        timerNode.setMetaData("width", 3);
        timerNode.setMetaData("height", 4);
        Timer timer = new Timer();
        timer.setDelay(1000);
        timer.setPeriod(1000);
        timerNode.setTimer(timer);
        process.addNode(timerNode);
        new ConnectionImpl(workItemNode, Node.CONNECTION_DEFAULT_TYPE, timerNode, Node.CONNECTION_DEFAULT_TYPE);
        
        EndNode endNode = new EndNode();
        endNode.setName("end");
        endNode.setMetaData("x", 1);
        endNode.setMetaData("y", 2);
        endNode.setMetaData("width", 3);
        endNode.setMetaData("height", 4);
        process.addNode(endNode);
        new ConnectionImpl(timerNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        
        String xml = XmlRuleFlowProcessDumper.INSTANCE.dump(process, true);
        if (xml == null) {
            throw new IllegalArgumentException("Failed to persist empty nodes!");
        }
        
//        System.out.println(xml);
        
        XmlProcessReader reader = new XmlProcessReader(
            new PackageBuilderConfiguration().getSemanticModules());
        process = (RuleFlowProcess) reader.read(new StringReader(xml));
        if (process == null) {
            throw new IllegalArgumentException("Failed to reload process!");
        }
        
        assertEquals(10, process.getNodes().length);
        
//        System.out.println("************************************");
        
        String xml2 = XmlRuleFlowProcessDumper.INSTANCE.dump(process, true);
        if (xml2 == null) {
            throw new IllegalArgumentException("Failed to persist empty nodes!");
        }
        
        System.out.println(xml2);
        
//        assertEquals(xml, xml2);
    }
    
    public void testSpecialCharacters() {
        // TODO
    }
}
