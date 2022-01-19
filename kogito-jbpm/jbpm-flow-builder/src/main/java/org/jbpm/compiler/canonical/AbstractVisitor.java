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

import java.util.Map;
import java.util.Set;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.kie.api.definition.process.NodeContainer;
import org.kie.kogito.internal.process.runtime.KogitoNode;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.utils.StringEscapeUtils;

import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_ADD_COMPENSATION_CONTEXT;
import static org.jbpm.ruleflow.core.RuleFlowProcessFactory.METHOD_VARIABLE;
import static org.jbpm.ruleflow.core.factory.NodeFactory.METHOD_METADATA;

public abstract class AbstractVisitor {

    protected static final String FACTORY_FIELD_NAME = "factory";
    protected static final String KCONTEXT_VAR = "kcontext";

    protected MethodCallExpr getFactoryMethod(String object, String methodName, Expression... args) {
        MethodCallExpr variableMethod = new MethodCallExpr(new NameExpr(object), methodName);

        for (Expression arg : args) {
            variableMethod.addArgument(arg);
        }
        return variableMethod;
    }

    protected String getOrDefault(String value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    protected Expression getOrNullExpr(String value) {
        if (value == null) {
            return new NullLiteralExpr();
        }
        return new StringLiteralExpr(value);
    }

    protected void visitMetaData(Map<String, Object> metadata, BlockStmt body, String variableName) {
        metadata.forEach((k, v) -> {
            Expression expression = null;
            if (v instanceof Boolean) {
                expression = new BooleanLiteralExpr((Boolean) v);
            } else if (v instanceof Integer) {
                expression = new IntegerLiteralExpr((Integer) v);
            } else if (v instanceof Long) {
                expression = new LongLiteralExpr((Long) v);
            } else if (v instanceof String) {
                expression = new StringLiteralExpr(StringEscapeUtils.escapeJava(v.toString()));
            }
            if (expression != null) {
                body.addStatement(getFactoryMethod(variableName, METHOD_METADATA, new StringLiteralExpr(k), expression));
            }
        });
    }

    protected void visitVariableScope(String field, VariableScope variableScope, BlockStmt body, Set<String> visitedVariables, String contextClass) {
        if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            for (Variable variable : variableScope.getVariables()) {
                if (!visitedVariables.add(variable.getName())) {
                    continue;
                }
                String tags = (String) variable.getMetaData(Variable.VARIABLE_TAGS);
                Object defaultValue = variable.getValue();
                body.tryAddImportToParentCompilationUnit(variable.getType().getClass());
                body.addStatement(getFactoryMethod(field, METHOD_VARIABLE, new StringLiteralExpr(variable.getName()),
                        new MethodCallExpr(DataTypeResolver.class.getName() + ".fromClass", new ClassExpr(new ClassOrInterfaceType(null, variable.getType().getStringType()))),
                        defaultValue != null ? new StringLiteralExpr(defaultValue.toString()) : new NullLiteralExpr(), new StringLiteralExpr(Variable.VARIABLE_TAGS),
                        tags != null ? new StringLiteralExpr(tags) : new NullLiteralExpr()));
            }
        }
    }

    protected final void visitCompensationScope(ContextContainer contextContainer, BlockStmt body, String factoryField) {
        Context context = getContext(contextContainer);
        if (context != null && context instanceof CompensationScope) {
            String contextId = ((CompensationScope) context).getContextContainerId();
            body.addStatement(getFactoryMethod(factoryField, METHOD_ADD_COMPENSATION_CONTEXT, new StringLiteralExpr(contextId)));
        }
    }

    private Context getContext(ContextContainer contextContainer) {
        Context context = contextContainer.getDefaultContext(CompensationScope.COMPENSATION_SCOPE);
        if (context == null && contextContainer instanceof KogitoNode) {
            NodeContainer parentContainer = ((KogitoNode) contextContainer).getParentContainer();
            if (parentContainer instanceof ContextContainer) {
                return getContext((ContextContainer) parentContainer);
            }
        }
        return context;
    }
}
