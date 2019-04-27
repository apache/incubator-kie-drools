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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AssignExpr;
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
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

import org.drools.core.util.StringUtils;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.FaultNode;
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

public class ProcessToExecModelGenerator extends AbstractVisitor {

	public static final ProcessToExecModelGenerator INSTANCE = new ProcessToExecModelGenerator(Collections.emptyMap());


	private static final String PROCESS_CLASS_SUFFIX = "Process";
	private static final String MODEL_CLASS_SUFFIX = "Model";
	private static final String TASK_INTPUT_CLASS_SUFFIX = "TaskInput";
	private static final String TASK_OUTTPUT_CLASS_SUFFIX = "TaskOutput";

	private Map<Class<?>, AbstractVisitor> nodesVisitors = new HashMap<>();

	public ProcessToExecModelGenerator(Map<String, ModelMetaData> processToModel) {

	    this.nodesVisitors.put(StartNode.class, new StartNodeVisitor());
	    this.nodesVisitors.put(ActionNode.class, new ActionNodeVisitor());
	    this.nodesVisitors.put(EndNode.class, new EndNodeVisitor());
	    this.nodesVisitors.put(HumanTaskNode.class, new HumanTaskNodeVisitor());
	    this.nodesVisitors.put(WorkItemNode.class, new WorkItemNodeVisitor());
	    this.nodesVisitors.put(SubProcessNode.class, new LambdaSubProcessNodeVisitor(processToModel));
	    this.nodesVisitors.put(Split.class, new SplitNodeVisitor());
	    this.nodesVisitors.put(Join.class, new JoinNodeVisitor());
	    this.nodesVisitors.put(FaultNode.class, new FaultNodeVisitor());
    }

    public ProcessMetaData generate(WorkflowProcess process) {

        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ProcessTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        Optional<ClassOrInterfaceDeclaration> processMethod = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        String extractedProcessId = extractProcessId(process.getId());

        if (!processMethod.isPresent()) {
            throw new RuntimeException("Cannot find class declaration in the template");
        }
        ClassOrInterfaceDeclaration processClazz = processMethod.get();
        processClazz.setName(StringUtils.capitalize(extractedProcessId + PROCESS_CLASS_SUFFIX));
        ProcessMetaData metadata = new ProcessMetaData(process.getId(),
                                                       extractedProcessId,
                                                       process.getName(),
                                                       process.getVersion(),
                                                       (clazz.getPackageDeclaration().isPresent() ? clazz.getPackageDeclaration().get().getNameAsString() + "." : "") + processClazz.getNameAsString());

        Optional<MethodDeclaration> pmethod = clazz.findFirst(MethodDeclaration.class, sl -> sl.getName().asString().equals("process"));

        visitProcess(process, pmethod.get(), metadata);

        metadata.setGeneratedClassModel(clazz);
        return metadata;
    }


    public MethodDeclaration generateMethod(WorkflowProcess process) {

        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ProcessTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());

        String extractedProcessId = extractProcessId(process.getId());

        ProcessMetaData metadata = new ProcessMetaData(process.getId(),
                                                       extractedProcessId,
                                                       process.getName(),
                                                       process.getVersion(),
                                                       (clazz.getPackageDeclaration().isPresent() ? clazz.getPackageDeclaration().get().getNameAsString() + "." : "") + "process");

        MethodDeclaration processMethod = new MethodDeclaration();
        visitProcess(process, processMethod, metadata);

        return processMethod;
    }

    public ModelMetaData generateModel(WorkflowProcess process) {
        String packageName = process.getPackageName();
        String name = StringUtils.capitalize(extractProcessId(process.getId()) + MODEL_CLASS_SUFFIX);

        return new ModelMetaData(packageName, name, (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE));
    }
    
    public List<UserTaskModelMetaData> generateUserTaskModel(WorkflowProcess process) {
        String packageName = process.getPackageName();
        List<UserTaskModelMetaData> usertaskModels = new ArrayList<>();
        
        VariableScope variableScope = (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        
        for (Node node : process.getNodes()) {
            if (node instanceof HumanTaskNode) {
                HumanTaskNode humanTaskNode = (HumanTaskNode) node;
                usertaskModels.add(new UserTaskModelMetaData(packageName, variableScope, humanTaskNode, process.getId()));
            }
        }
        
        return usertaskModels;
    }

	protected void visitProcess(WorkflowProcess process, MethodDeclaration processMethod, ProcessMetaData metadata) {
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
        visitNodes(processNodes, body, variableScope, metadata);
        visitConnections(process.getNodes(), body);


        addFactoryMethodWithArgs(body, "validate");

        MethodCallExpr getProcessMethod = new MethodCallExpr(new NameExpr(FACTORY_FIELD_NAME), "getProcess");
        body.addStatement(new ReturnStmt(getProcessMethod));
        processMethod.setBody(body);

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

    public void visitNodes(List<org.jbpm.workflow.core.Node> nodes, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {

        for (Node node: nodes) {
            AbstractVisitor visitor = nodesVisitors.get(node.getClass());

            if (visitor == null) {
                throw new IllegalStateException("No visitor found for node " + node.getClass().getName());
            }

            visitor.visitNode(node, body, variableScope, metadata);
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



    public static String extractProcessId(String processId) {
        if (processId.contains(".")) {
            return processId.substring(processId.lastIndexOf(".") + 1);
        }

        return processId;
    }


}
