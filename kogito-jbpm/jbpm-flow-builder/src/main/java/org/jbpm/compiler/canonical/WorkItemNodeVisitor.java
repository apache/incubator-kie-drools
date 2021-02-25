/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.jbpm.workflow.core.node.WorkItemNode;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.factory.WorkItemNodeFactory.METHOD_WORK_NAME;
import static org.jbpm.ruleflow.core.factory.WorkItemNodeFactory.METHOD_WORK_PARAMETER;

public class WorkItemNodeVisitor<T extends WorkItemNode> extends AbstractNodeVisitor<T> {

    private enum ParamType {
        BOOLEAN(Boolean.class.getSimpleName()),
        INTEGER(Integer.class.getSimpleName()),
        FLOAT(Float.class.getSimpleName());

        final String name;

        public String getName() {
            return name;
        }

        ParamType(String name) {
            this.name = name;
        }

        public static ParamType fromString(String name) {
            for (ParamType p : ParamType.values()) {
                if (Objects.equals(p.name, name)) {
                    return p;
                }
            }
            return null;
        }
    }

    private final ClassLoader contextClassLoader;

    public WorkItemNodeVisitor(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    @Override
    protected String getNodeKey() {
        return "workItemNode";
    }

    @Override
    public void visitNode(String factoryField, T node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        Work work = node.getWork();
        String workName = node.getWork().getName();

        if (workName.equals("Service Task")) {
            ServiceTaskDescriptor d = new ServiceTaskDescriptor(node, contextClassLoader);
            String mangledName = d.mangledName();
            CompilationUnit generatedHandler = d.generateHandlerClassForService();
            metadata.getGeneratedHandlers().put(mangledName, generatedHandler);
            workName = mangledName;
        } else if (workName.equals("Rest Task")) {
            workName = RestTaskDescriptor.getClassName(metadata);
            metadata
                    .getGeneratedHandlers()
                    .computeIfAbsent(workName, RestTaskDescriptor::generateHandlerClassForService);
        }

        body.addStatement(getAssignedFactoryMethod(factoryField, WorkItemNodeFactory.class, getNodeId(node), getNodeKey(), new LongLiteralExpr(node.getId())))
                .addStatement(getNameMethod(node, work.getName()))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_WORK_NAME, new StringLiteralExpr(workName)));

        addWorkItemParameters(work, body, getNodeId(node));
        addNodeMappings(node, body, getNodeId(node));

        body.addStatement(getDoneMethod(getNodeId(node)));

        visitMetaData(node.getMetaData(), body, getNodeId(node));

        metadata.getWorkItems().add(workName);
    }

    protected void addWorkItemParameters(Work work, BlockStmt body, String variableName) {
        for (Entry<String, Object> entry : work.getParameters().entrySet()) {
            if (entry.getValue() == null) {
                continue; // interfaceImplementationRef ?
            }
            String paramType = null;
            if (work.getParameterDefinition(entry.getKey()) != null) {
                paramType = work.getParameterDefinition(entry.getKey()).getType().getStringType();
            }
            body.addStatement(getFactoryMethod(variableName, METHOD_WORK_PARAMETER, new StringLiteralExpr(entry.getKey()), getParameterExpr(paramType, entry.getValue())));
        }
    }

    private Expression getParameterExpr(String type, Object value) {
        if (value == null) {
            return new NullLiteralExpr();
        }
        ParamType pType = ParamType.fromString(type);
        if (pType == null) {
            if (value instanceof Supplier) {
                return ((Supplier<Expression>) value).get();
            } else {
                return new StringLiteralExpr(value.toString());
            }

        }
        switch (pType) {
            case BOOLEAN:
                return new BooleanLiteralExpr(asBoolean(value));
            case FLOAT:
                return new MethodCallExpr()
                        .setScope(new NameExpr(Float.class.getName()))
                        .setName("parseFloat")
                        .addArgument(new StringLiteralExpr(value.toString()));
            case INTEGER:
                return new IntegerLiteralExpr(asInteger(value));
            default:
                return new StringLiteralExpr(value.toString());
        }
    }

    private Boolean asBoolean(Object value) {
        return value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString());
    }

    private Integer asInteger(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
    }
}
