/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.canonical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.WorkflowProcess;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ProcessToExecModelGenerator extends AbstractVisitor {

	public static final ProcessToExecModelGenerator INSTANCE = new ProcessToExecModelGenerator();
	
	
	private static final String PROCESS_CLASS_SUFFIX = "Process";
	private static final String MODEL_CLASS_SUFFIX = "Model";
	
	private Map<Class<?>, AbstractVisitor> nodesVisitors = new HashMap<>();
    
	private ProcessToExecModelGenerator() {
   
	    this.nodesVisitors.put(StartNode.class, new StartNodeVisitor());
	    this.nodesVisitors.put(ActionNode.class, new ActionNodeVisitor());
	    this.nodesVisitors.put(EndNode.class, new EndNodeVisitor());
	    this.nodesVisitors.put(HumanTaskNode.class, new HumanTaskNodeVisitor());
	    this.nodesVisitors.put(WorkItemNode.class, new WorkItemNodeVisitor());
	    this.nodesVisitors.put(SubProcessNode.class, new SubProcessNodeVisitor());
	    this.nodesVisitors.put(Split.class, new SplitNodeVisitor());
	    this.nodesVisitors.put(Join.class, new JoinNodeVisitor());
    }

    public String generate(WorkflowProcess process) {
        
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ProcessTemplate.java"));                
        clazz.setPackageDeclaration(process.getPackageName());
        Optional<ClassOrInterfaceDeclaration> processMethod = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        if (processMethod.isPresent()) {
            processMethod.get().setName(StringUtils.capitalize(exctactProcessId(process.getId()) + PROCESS_CLASS_SUFFIX));
        }
                
        visitProcess(process, clazz);
        
        return clazz.toString();
    }
    
    public String generateModel(WorkflowProcess process) {
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ModelTemplate.java"));                
        clazz.setPackageDeclaration(process.getPackageName());
        Optional<ClassOrInterfaceDeclaration> processMethod = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        if (processMethod.isPresent()) {
            ClassOrInterfaceDeclaration modelClass = processMethod.get();
            
            modelClass.setName(StringUtils.capitalize(exctactProcessId(process.getId()) + MODEL_CLASS_SUFFIX));
        
            // setup of the toMap method body
            BlockStmt toMapBody = new BlockStmt();
            ClassOrInterfaceType toMap = new ClassOrInterfaceType(null, new SimpleName(Map.class.getSimpleName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getSimpleName()), new ClassOrInterfaceType(null, Object.class.getSimpleName())));            
            VariableDeclarationExpr paramsField = new VariableDeclarationExpr(toMap, "params");                                  
            toMapBody.addStatement(new AssignExpr(paramsField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null, HashMap.class.getSimpleName()), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));
            
            // setup of fromMap method body
            ClassOrInterfaceType modelType = new ClassOrInterfaceType(null, modelClass.getNameAsString());
            BlockStmt fromMapBody = new BlockStmt();
            VariableDeclarationExpr itemField = new VariableDeclarationExpr(modelType, "item");
            fromMapBody.addStatement(new AssignExpr(itemField, new ObjectCreationExpr(null, modelType, NodeList.nodeList()), AssignExpr.Operator.ASSIGN));
            NameExpr item = new NameExpr("item");
            FieldAccessExpr idField = new FieldAccessExpr(item, "id");
            fromMapBody.addStatement(new AssignExpr(idField, new NameExpr("id"), Operator.ASSIGN));
            
            VariableScope variableScope = (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
            
            if (variableScope != null && !variableScope.getVariables().isEmpty()) {
                for (Variable variable: variableScope.getVariables()) {
                    ClassOrInterfaceType type = JavaParser.parseClassOrInterfaceType(variable.getType().getStringType());
                    
                    modelClass.addField(variable.getType().getStringType(), variable.getName(), Keyword.PRIVATE);
                    // getter
                    modelClass
                    .addMethod("get" + StringUtils.capitalize(variable.getName()), Keyword.PUBLIC)
                    .setType(type)
                    .createBody().addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), variable.getName())));
                    
                    // setter
                    modelClass
                    .addMethod("set" + StringUtils.capitalize(variable.getName()), Keyword.PUBLIC)
                    .addParameter(variable.getType().getStringType(), variable.getName())
                    .createBody().addStatement( new AssignExpr(new FieldAccessExpr(new ThisExpr(), variable.getName()), new NameExpr(variable.getName()), Operator.ASSIGN));
                    
                    // toMap method body
                    MethodCallExpr putVariable = new MethodCallExpr(new NameExpr("params"), "put");
                    putVariable.addArgument(new StringLiteralExpr(variable.getName()));
                    putVariable.addArgument(new FieldAccessExpr(new ThisExpr(), variable.getName()));
                    toMapBody.addStatement(putVariable);
                    
                    // fromMap method body                    
                    FieldAccessExpr field = new FieldAccessExpr(item, variable.getName());
                    
                    fromMapBody.addStatement(new AssignExpr(field, new CastExpr(
                                                                                type,
                                                                                new MethodCallExpr(
                                                                                        new NameExpr("params"),
                                                                                        "get")
                                                                                        .addArgument(new StringLiteralExpr(variable.getName()))), AssignExpr.Operator.ASSIGN));
                }
            }
            
            Optional<MethodDeclaration> toMapMethod = clazz.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("toMap"));
            if (processMethod.isPresent()) {
                
                toMapBody.addStatement(new ReturnStmt(new NameExpr("params")));
                toMapMethod.get().setBody(toMapBody);
            }
            
            Optional<MethodDeclaration> fromMapMethod = clazz.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("fromMap"));
            if (fromMapMethod.isPresent()) {
                MethodDeclaration fromMap = fromMapMethod.get();
                fromMap.setType(modelClass.getNameAsString());
                fromMapBody.addStatement(new ReturnStmt(new NameExpr("item")));
                fromMap.setBody(fromMapBody);
            }
        }
        return clazz.toString();
    }	

	protected void visitProcess(WorkflowProcess process, CompilationUnit clazz) {
        BlockStmt body = new BlockStmt();
        
        ClassOrInterfaceType processFactoryType = new ClassOrInterfaceType(null, RuleFlowProcessFactory.class.getSimpleName());
        
        // create local variable factory and assign new fluent process to it
        VariableDeclarationExpr factoryField = new VariableDeclarationExpr(processFactoryType, FACTORY_FIELD_NAME);        
        MethodCallExpr assignFactoryMethod = new MethodCallExpr(new NameExpr(processFactoryType.getName().asString()), "createProcess");
        assignFactoryMethod.addArgument(new StringLiteralExpr(process.getId()));                
        body.addStatement(new AssignExpr(factoryField, assignFactoryMethod, AssignExpr.Operator.ASSIGN));
        
    	// item definitions
        Set<String> visitedVariables = new HashSet<String>();
    	VariableScope variableScope = (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);

    	visitVariableScope(variableScope, body, visitedVariables);
    	visitSubVariableScopes(process.getNodes(), body, visitedVariables);

	    visitInterfaces(process.getNodes(), body);

	    // the process itself
	    addFactoryMethodWithArgs(body, "name", new StringLiteralExpr(process.getName()));
	    addFactoryMethodWithArgs(body, "packageName", new StringLiteralExpr(process.getPackageName()));
	    addFactoryMethodWithArgs(body, "dynamic", new BooleanLiteralExpr(((org.jbpm.workflow.core.WorkflowProcess) process).isDynamic()));
	    addFactoryMethodWithArgs(body, "version", new StringLiteralExpr(getOrDefault(process.getVersion(), "1.0")));
	    addFactoryMethodWithArgs(body, "visibility", new StringLiteralExpr(getOrDefault(((org.jbpm.workflow.core.WorkflowProcess) process).getVisibility(), WorkflowProcess.PUBLIC_VISIBILITY)));
	    
	    visitMetaData(process.getMetaData(), body, FACTORY_FIELD_NAME);

        visitHeader(process, body);
        
        List<org.jbpm.workflow.core.Node> processNodes = new ArrayList<org.jbpm.workflow.core.Node>();
        for( Node procNode : process.getNodes()) { 
            processNodes.add((org.jbpm.workflow.core.Node) procNode);
        }
        visitNodes(processNodes, body, variableScope);
        visitConnections(process.getNodes(), body);
        

        addFactoryMethodWithArgs(body, "validate");
        
    	Optional<MethodDeclaration> processMethod = clazz.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("process"));

    	if (processMethod.isPresent()) {
    	    
    	    MethodCallExpr getProcessMethod = new MethodCallExpr(new NameExpr(FACTORY_FIELD_NAME), "getProcess");
    	    body.addStatement(new ReturnStmt(getProcessMethod));
    	    processMethod.get().setBody(body);
    	}
        
    }

    private void visitVariableScope(VariableScope variableScope, BlockStmt body, Set<String> visitedVariables) {
        if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            for (Variable variable: variableScope.getVariables()) {
                
                if( !visitedVariables.add(variable.getName()) ) { 
                    continue;
                }
                ClassOrInterfaceType variableType = new ClassOrInterfaceType(null, ObjectDataType.class.getSimpleName());
                ObjectCreationExpr variableValue = new ObjectCreationExpr(null, variableType, new NodeList<>(new StringLiteralExpr(variable.getType().getStringType())));
                addFactoryMethodWithArgs(body, "variable", new StringLiteralExpr(variable.getName()), variableValue);                
            }
            
        }
    }

    private void visitSubVariableScopes(Node[] nodes, BlockStmt body, Set<String> visitedVariables) {
        for (Node node: nodes) {
            if (node instanceof ContextContainer) {
                VariableScope variableScope = (VariableScope) 
                    ((ContextContainer) node).getDefaultContext(VariableScope.VARIABLE_SCOPE);
                if (variableScope != null) {
                    visitVariableScope(variableScope, body, visitedVariables);
                }
            }
            if (node instanceof NodeContainer) {
                visitSubVariableScopes(((NodeContainer) node).getNodes(), body, visitedVariables);
            }
        }
    }

    protected void visitHeader(WorkflowProcess process, BlockStmt body) {
        Map<String, Object> metaData = getMetaData(process.getMetaData());
    	Set<String> imports = ((org.jbpm.process.core.Process) process).getImports();
    	Map<String, String> globals = ((org.jbpm.process.core.Process) process).getGlobals();
    	if ((imports != null && !imports.isEmpty()) || (globals != null && globals.size() > 0) || !metaData.isEmpty()) {    		
    		if (imports != null) {
	    		for (String s: imports) {	    			
	    			addFactoryMethodWithArgs(body, "imports", new StringLiteralExpr(s));
	    		}
    		}
    		if (globals != null) {
	    		for (Map.Entry<String, String> global: globals.entrySet()) {	    			
	    			addFactoryMethodWithArgs(body, "global", new StringLiteralExpr(global.getKey()), new StringLiteralExpr(global.getValue()));
	    		}
    		}
    	}        
    }

    public static Map<String, Object> getMetaData(Map<String, Object> input) {
    	Map<String, Object> metaData = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry: input.entrySet()) {
        	String name = entry.getKey();
        	if (entry.getKey().startsWith("custom") 
        			&& entry.getValue() instanceof String) {
        		metaData.put(name, entry.getValue());
        	}
        }
        return metaData;
    }

    protected void visitInterfaces(Node[] nodes, BlockStmt body) {
        for (Node node: nodes) {
            if (node instanceof WorkItemNode) {
                Work work = ((WorkItemNode) node).getWork();
                if (work != null) {
                }
            }
        }
    }

    public void visitNodes(List<org.jbpm.workflow.core.Node> nodes, BlockStmt body, VariableScope variableScope) {
    	
        for (Node node: nodes) {
            AbstractVisitor visitor = nodesVisitors.get(node.getClass());
            
            if (visitor == null) {
                throw new IllegalStateException("No visitor found for node " + node.getClass().getName());
            }
            
            visitor.visitNode(node, body, variableScope);
        }
        
    }

    private void visitConnections(Node[] nodes, BlockStmt body) {
    	
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: nodes) {
            for (List<Connection> connectionList: node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        for (Connection connection: connections) {
            visitConnection(connection, body);
        }
        
    }

    private boolean isConnectionRepresentingLinkEvent(Connection connection) {
        boolean bValue = connection.getMetaData().get("linkNodeHidden") != null;
        return bValue;
    }

    public void visitConnection(Connection connection, BlockStmt body) {
    	// if the connection was generated by a link event, don't dump.
        if (isConnectionRepresentingLinkEvent(connection)) {
        	return;
        }
        // if the connection is a hidden one (compensations), don't dump
        Object hidden = ((ConnectionImpl) connection).getMetaData("hidden");
        if( hidden != null && ((Boolean) hidden) ) { 
           return; 
        }
         
        addFactoryMethodWithArgs(body, "connection", new LongLiteralExpr(connection.getFrom().getId()), 
                                 new LongLiteralExpr(connection.getTo().getId()), 
                                 new StringLiteralExpr(getOrDefault((String) ((ConnectionImpl) connection).getMetaData().get("UniqueId"),  "")));        
    }
    
    
    
    public String exctactProcessId(String processId) {
        if (processId.contains(".")) {
            return processId.substring(processId.lastIndexOf(".") + 1);
        }
        
        return processId;
    }

    
}
