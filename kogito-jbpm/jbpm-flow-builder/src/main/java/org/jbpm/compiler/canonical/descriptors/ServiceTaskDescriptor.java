/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.compiler.canonical.descriptors;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jbpm.compiler.canonical.ReflectionUtils;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ServiceTaskDescriptor extends AbstractServiceTaskDescriptor {

    public static final String TYPE = "Service Task";
    protected final Map<String, String> parameters;
    private final ClassLoader contextClassLoader;
    private final String mangledName;
    private Class<?> cls;

    protected ServiceTaskDescriptor(WorkItemNode workItemNode, ClassLoader contextClassLoader) {
        super(workItemNode);
        this.contextClassLoader = contextClassLoader;

        mangledName = mangledHandlerName(interfaceName, operationName, String.valueOf(workItemNode.getId()));
        this.parameters = extractTaskParameters();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return mangledName;
    }

    @Override
    public CompilationUnit generateHandlerClassForService() {
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.handlers");
        compilationUnit.getTypes().add(classDeclaration());
        compilationUnit.addImport(WorkItemExecutionException.class);
        return compilationUnit;
    }

    @Override
    protected Collection<Class<?>> getCompleteWorkItemExceptionTypes() {
        return Arrays.asList(getOperationMethod().getExceptionTypes());
    }

    @Override
    protected void handleParametersForServiceCall(final BlockStmt executeWorkItemBody, final MethodCallExpr callServiceMethod) {
        for (Map.Entry<String, String> paramEntry : parameters.entrySet()) {
            MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), "getParameter").addArgument(new StringLiteralExpr(paramEntry.getKey()));
            callServiceMethod.addArgument(new CastExpr(new ClassOrInterfaceType(null, paramEntry.getValue()), getParamMethod));
        }
    }

    protected Map<String, String> extractTaskParameters() {
        String type = (String) workItemNode.getWork().getParameter("ParameterType");
        Map<String, String> parameters = null;
        if (type != null) {
            if (isDefaultParameterType(type)) {
                type = inferParameterType();
            }

            parameters = Collections.singletonMap("Parameter", type);
        } else {
            parameters = new LinkedHashMap<>();

            for (ParameterDefinition def : workItemNode.getWork().getParameterDefinitions()) {
                parameters.put(def.getName(), def.getType().getStringType());
            }
        }
        return parameters;
    }

    // assume 1 single arg as above
    private String inferParameterType() {
        loadClass();
        for (Method m : cls.getMethods()) {
            if (m.getName().equals(operationName) && m.getParameterCount() == 1) {
                return m.getParameterTypes()[0].getCanonicalName();
            }
        }
        throw new IllegalArgumentException(
                MessageFormat
                        .format(
                                "Invalid work item \"{0}\": could not find a method called \"{1}\" in class \"{2}\"",
                                workItemNode.getName(),
                                operationName,
                                interfaceName));
    }

    private void loadClass() {
        if (cls != null) {
            return;
        }
        try {
            cls = contextClassLoader.loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    MessageFormat
                            .format(
                                    "Invalid work item \"{0}\": class not found for interfaceName \"{1}\"",
                                    workItemNode.getName(),
                                    interfaceName));
        }
    }

    private boolean isDefaultParameterType(String type) {
        return type.equals("java.lang.Object") || type.equals("Object");
    }

    private String mangledHandlerName(String interfaceName, String operationName, String nodeName) {
        return String.format("%s_%s_%s_Handler", interfaceName, operationName, nodeName);
    }

    private Method getOperationMethod() {
        loadClass();
        try {
            return ReflectionUtils.getMethod(contextClassLoader, cls, operationName, parameters.values());
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException(
                    MessageFormat
                            .format(
                                    "Invalid work item \"{0}\": could not find a method called \"{1}\" in class \"{2}\" with proper arguments \"{3}\", error \"{4}\"",
                                    workItemNode.getName(),
                                    operationName,
                                    interfaceName,
                                    parameters,
                                    ex));
        }
    }
}
