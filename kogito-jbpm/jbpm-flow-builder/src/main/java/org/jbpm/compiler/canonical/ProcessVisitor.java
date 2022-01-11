/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.stream.Stream;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static org.jbpm.ruleflow.core.Metadata.ASSOCIATION;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory.METHOD_ASSOCIATION;
import static org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory.METHOD_CONNECTION;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_DYNAMIC;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_ERROR_EXCEPTION_HANDLER;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_GLOBAL;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_IMPORTS;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_NAME;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_PACKAGE_NAME;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_VALIDATE;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_VERSION;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_VISIBILITY;

public class ProcessVisitor extends AbstractVisitor {

    public static final String DEFAULT_VERSION = "1.0";

    private Map<Class<?>, AbstractNodeVisitor<? extends org.kie.api.definition.process.Node>> nodesVisitors = new HashMap<>();

    public ProcessVisitor(ClassLoader contextClassLoader) {
        this.nodesVisitors.put(StartNode.class, new StartNodeVisitor());
        this.nodesVisitors.put(ActionNode.class, new ActionNodeVisitor());
        this.nodesVisitors.put(EndNode.class, new EndNodeVisitor());
        this.nodesVisitors.put(HumanTaskNode.class, new HumanTaskNodeVisitor());
        this.nodesVisitors.put(WorkItemNode.class, new WorkItemNodeVisitor<>(contextClassLoader));
        this.nodesVisitors.put(SubProcessNode.class, new LambdaSubProcessNodeVisitor());
        this.nodesVisitors.put(Split.class, new SplitNodeVisitor());
        this.nodesVisitors.put(Join.class, new JoinNodeVisitor());
        this.nodesVisitors.put(FaultNode.class, new FaultNodeVisitor());
        this.nodesVisitors.put(RuleSetNode.class, new RuleSetNodeVisitor(contextClassLoader));
        this.nodesVisitors.put(BoundaryEventNode.class, new BoundaryEventNodeVisitor());
        this.nodesVisitors.put(EventNode.class, new EventNodeVisitor());
        this.nodesVisitors.put(ForEachNode.class, new ForEachNodeVisitor(nodesVisitors));
        this.nodesVisitors.put(CompositeContextNode.class, new CompositeContextNodeVisitor<>(nodesVisitors));
        this.nodesVisitors.put(EventSubProcessNode.class, new EventSubProcessNodeVisitor(nodesVisitors));
        this.nodesVisitors.put(TimerNode.class, new TimerNodeVisitor());
        this.nodesVisitors.put(MilestoneNode.class, new MilestoneNodeVisitor());
        this.nodesVisitors.put(DynamicNode.class, new DynamicNodeVisitor(nodesVisitors));
        this.nodesVisitors.put(StateNode.class, new StateNodeVisitor(nodesVisitors));
        this.nodesVisitors.put(CatchLinkNode.class, new CatchLinkNodeVisitor());
        this.nodesVisitors.put(ThrowLinkNode.class, new ThrowLinkNodeVisitor());
    }

    public void visitProcess(WorkflowProcess process, MethodDeclaration processMethod, ProcessMetaData metadata) {
        BlockStmt body = new BlockStmt();
        processMethod.setBody(body);

        ClassOrInterfaceType processFactoryType = new ClassOrInterfaceType(null, RuleFlowProcessFactory.class.getSimpleName());

        // create local variable factory and assign new fluent process to it
        VariableDeclarationExpr factoryField = new VariableDeclarationExpr(processFactoryType, FACTORY_FIELD_NAME);
        MethodCallExpr assignFactoryMethod = new MethodCallExpr(new NameExpr(processFactoryType.getName().asString()), "createProcess");
        assignFactoryMethod.addArgument(new StringLiteralExpr(process.getId()));

        body.addStatement(new AssignExpr(factoryField, assignFactoryMethod, AssignExpr.Operator.ASSIGN));

        // item definitions
        Set<String> visitedVariables = new HashSet<>();
        VariableScope variableScope = (VariableScope) ((org.jbpm.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);

        visitVariableScope(FACTORY_FIELD_NAME, variableScope, body, visitedVariables, metadata.getProcessClassName());
        visitSubVariableScopes(process.getNodes(), body, visitedVariables);

        //exception scope
        visitExceptionScope(process, body);

        visitInterfaces(process.getNodes(), body);

        metadata.setDynamic(((org.jbpm.workflow.core.WorkflowProcess) process).isDynamic());
        // the process itself
        body.addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_NAME, new StringLiteralExpr(process.getName())))
                .addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_PACKAGE_NAME, new StringLiteralExpr(process.getPackageName())))
                .addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_DYNAMIC, new BooleanLiteralExpr(metadata.isDynamic())))
                .addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_VERSION, new StringLiteralExpr(getOrDefault(process.getVersion(), DEFAULT_VERSION))))
                .addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_VISIBILITY,
                        new StringLiteralExpr(getOrDefault(((KogitoWorkflowProcess) process).getVisibility(), KogitoWorkflowProcess.PUBLIC_VISIBILITY))));

        visitCompensationScope(process, body);
        visitMetaData(process.getMetaData(), body, FACTORY_FIELD_NAME);
        visitHeader(process, body);

        List<Node> processNodes = new ArrayList<>();
        for (org.kie.api.definition.process.Node procNode : process.getNodes()) {
            processNodes.add((Node) procNode);
        }
        visitNodes(processNodes, body, variableScope, metadata);
        visitConnections(process.getNodes(), body);

        body.addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_VALIDATE));

        MethodCallExpr getProcessMethod = new MethodCallExpr(new NameExpr(FACTORY_FIELD_NAME), "getProcess");
        body.addStatement(new ReturnStmt(getProcessMethod));
    }

    private void visitSubVariableScopes(org.kie.api.definition.process.Node[] nodes, BlockStmt body, Set<String> visitedVariables) {
        for (org.kie.api.definition.process.Node node : nodes) {
            if (node instanceof ContextContainer) {
                VariableScope variableScope = (VariableScope) ((ContextContainer) node).getDefaultContext(VariableScope.VARIABLE_SCOPE);
                if (variableScope != null) {
                    visitVariableScope(FACTORY_FIELD_NAME, variableScope, body, visitedVariables, node.getClass().getName());
                }
            }
            if (node instanceof NodeContainer) {
                visitSubVariableScopes(((NodeContainer) node).getNodes(), body, visitedVariables);
            }
        }
    }

    private void visitHeader(WorkflowProcess process, BlockStmt body) {
        Map<String, Object> metaData = getMetaData(process.getMetaData());
        Set<String> imports = ((org.jbpm.process.core.Process) process).getImports();
        Map<String, String> globals = ((org.jbpm.process.core.Process) process).getGlobals();
        if ((imports != null && !imports.isEmpty()) || (globals != null && globals.size() > 0) || !metaData.isEmpty()) {
            if (imports != null) {
                for (String s : imports) {
                    body.addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_IMPORTS, new StringLiteralExpr(s)));
                }
            }
            if (globals != null) {
                for (Map.Entry<String, String> global : globals.entrySet()) {
                    body.addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_GLOBAL, new StringLiteralExpr(global.getKey()), new StringLiteralExpr(global.getValue())));
                }
            }
        }
    }

    private Map<String, Object> getMetaData(Map<String, Object> input) {
        Map<String, Object> metaData = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String name = entry.getKey();
            if (entry.getKey().startsWith("custom")
                    && entry.getValue() instanceof String) {
                metaData.put(name, entry.getValue());
            }
        }
        return metaData;
    }

    private <U extends org.kie.api.definition.process.Node> void visitNodes(List<U> nodes, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        for (U node : nodes) {
            AbstractNodeVisitor<U> visitor = (AbstractNodeVisitor<U>) nodesVisitors.get(node.getClass());
            if (visitor == null) {
                throw new IllegalStateException("No visitor found for node " + node.getClass().getName());
            }
            visitor.visitNode(node, body, variableScope, metadata);
        }
    }

    private void visitConnections(org.kie.api.definition.process.Node[] nodes, BlockStmt body) {

        List<Connection> connections = new ArrayList<>();
        for (org.kie.api.definition.process.Node node : nodes) {
            for (List<Connection> connectionList : node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        for (Connection connection : connections) {
            visitConnection(connection, body);
        }
    }

    // KOGITO-1882 Finish implementation or delete completely
    private void visitInterfaces(org.kie.api.definition.process.Node[] nodes, BlockStmt body) {
        for (org.kie.api.definition.process.Node node : nodes) {
            if (node instanceof WorkItemNode) {
                Work work = ((WorkItemNode) node).getWork();
                if (work != null) {
                    // TODO - finish this method
                }
            }
        }
    }

    private void visitConnection(Connection connection, BlockStmt body) {
        String method = METHOD_CONNECTION;
        Object association = ((ConnectionImpl) connection).getMetaData(ASSOCIATION);
        if (association != null && ((Boolean) association)) {
            method = METHOD_ASSOCIATION;
        }
        body.addStatement(getFactoryMethod(FACTORY_FIELD_NAME, method,
                new LongLiteralExpr(connection.getFrom().getId()),
                new LongLiteralExpr(connection.getTo().getId()),
                new StringLiteralExpr(getOrDefault((String) connection.getMetaData().get(UNIQUE_ID), ""))));
    }

    private void visitCompensationScope(Process process, BlockStmt body) {
        if (process instanceof RuleFlowProcess) {
            visitCompensationScope((RuleFlowProcess) process, body, FACTORY_FIELD_NAME);
        }
    }

    private void visitExceptionScope(Process process, BlockStmt body) {
        if (!(process instanceof org.jbpm.workflow.core.WorkflowProcess)) {
            return;
        }
        org.jbpm.workflow.core.WorkflowProcess workflowProcess = (org.jbpm.workflow.core.WorkflowProcess) process;
        Context context = workflowProcess.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
        //root process
        visitContextExceptionScope(context, body);
        //visit sub-processes
        visitSubExceptionScope(workflowProcess.getNodes(), body);
    }

    private void visitContextExceptionScope(Context context, BlockStmt body) {
        if (context instanceof ExceptionScope) {
            ((ExceptionScope) context).getExceptionHandlers().entrySet().stream().forEach(e -> {
                String faultCode = e.getKey();
                ActionExceptionHandler handler = (ActionExceptionHandler) e.getValue();
                Optional<String> faultVariable = Optional.ofNullable(handler.getFaultVariable());
                SignalProcessInstanceAction action =
                        (SignalProcessInstanceAction) handler.getAction().getMetaData("Action");
                String signalName = action.getSignalName();
                body.addStatement(getFactoryMethod(FACTORY_FIELD_NAME, METHOD_ERROR_EXCEPTION_HANDLER,
                        new StringLiteralExpr(signalName),
                        new StringLiteralExpr(faultCode),
                        faultVariable.<Expression> map(StringLiteralExpr::new)
                                .orElse(new NullLiteralExpr())));
            });
        }
    }

    private void visitSubExceptionScope(org.kie.api.definition.process.Node[] nodes, BlockStmt body) {
        Stream.of(nodes)
                .peek(node -> {
                    //recursively handle subprocesses exception scope
                    if (node instanceof NodeContainer) {
                        visitSubExceptionScope(((NodeContainer) node).getNodes(), body);
                    }
                })
                .filter(ContextContainer.class::isInstance)
                .map(ContextContainer.class::cast)
                .map(container -> container.getDefaultContext(ExceptionScope.EXCEPTION_SCOPE))
                .forEach(context -> visitContextExceptionScope(context, body));
    }
}
