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
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jbpm.compiler.canonical.ReflectionUtils;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class ServiceTaskDescriptor extends AbstractServiceTaskDescriptor {

    public static final String TYPE = "Service Task";

    public static class Argument {
        private String type;
        private String name;

        public Argument(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Argument{" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    protected final List<Argument> parameters;
    private final String mangledName;
    private final Class<?> cls;
    private final Method method;

    protected ServiceTaskDescriptor(WorkItemNode workItemNode, ClassLoader contextClassLoader) {
        super(workItemNode);
        mangledName = mangledHandlerName(interfaceName, operationName, String.valueOf(workItemNode.getId()));
        this.parameters = extractTaskParameters();
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
        try {
            method = ReflectionUtils.getMethod(contextClassLoader, cls, operationName, parameters.stream().map(Argument::getType).collect(Collectors.toList()));
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
    protected boolean isEmptyResult() {
        return method.getReturnType().equals(void.class);
    }

    @Override
    protected Collection<Class<?>> getCompleteWorkItemExceptionTypes() {
        return Arrays.asList(method.getExceptionTypes());
    }

    @Override
    protected void handleParametersForServiceCall(final BlockStmt executeWorkItemBody, final MethodCallExpr callServiceMethod) {
        Parameter[] methodParameter = method.getParameters();
        for (int i = 0; i < this.parameters.size(); i++) {
            Argument argument = this.parameters.get(i);
            MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), "getParameter").addArgument(new StringLiteralExpr(argument.getName()));
            Class<?> clazz = methodParameter[i].getType();

            callServiceMethod.addArgument(
                    new CastExpr(clazz.isPrimitive() ? new PrimitiveType(Primitive.valueOf(clazz.getCanonicalName().toUpperCase())) : parseClassOrInterfaceType(clazz.getCanonicalName()),
                            getParamMethod));

        }

        // adding a dynamic argument at the end of all parameters of the class
        if (methodParameter.length > this.parameters.size() && KogitoProcessContext.class.isAssignableFrom(methodParameter[this.parameters.size()].getType())) {
            callServiceMethod.addArgument(new MethodCallExpr(new TypeExpr(parseClassOrInterfaceType(ContextFactory.class.getCanonicalName())), "fromItem").addArgument(new NameExpr("workItem")));
        }
    }

    private List<Argument> extractTaskParameters() {
        String type = (String) workItemNode.getWork().getParameter("ParameterType");
        List<Argument> arguments = new ArrayList<>();

        if (type != null) {
            type = inferParameterType(type);
            arguments.add(new Argument(type, "Parameter"));
        } else {
            for (ParameterDefinition def : workItemNode.getWork().getParameterDefinitions()) {
                arguments.add(new Argument(def.getType().getStringType(), def.getName()));
            }
        }
        return arguments;
    }

    private String inferParameterType(String type) {
        if (type.equals("java.lang.Object") || type.equals("Object")) {
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
        return type;
    }

    private String mangledHandlerName(String interfaceName, String operationName, String nodeName) {
        return String.format("%s_%s_%s_Handler", interfaceName, operationName, nodeName);
    }

}
