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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static java.util.Objects.requireNonNull;

public class OpenApiTaskDescriptor extends AbstractServiceTaskDescriptor {

    static final String TYPE = "OpenApi Task";
    static final String PARAM_PREFIX = "ServiceParam_";
    static final String PARAM_META_PARAM_RESOLVER_TYPE = "ParamResolverType";
    static final String PARAM_META_RESULT_HANDLER = "ResultHandler";
    static final String PARAM_META_RESULT_HANDLER_TYPE = "ResultHandlerType";
    static final String PARAM_META_SPEC_PARAMETERS = "SpecParameters";

    private static final String VAR_INPUT_MODEL = "inputModel";
    private static final String METHOD_GET_PARAM = "getParameter";

    protected OpenApiTaskDescriptor(WorkItemNode workItemNode) {
        super(workItemNode);
    }

    /**
     * Creates a new {@link WorkItemBuilder} based on this descriptor
     *
     * @param interfaceResource the OpenApi Specification Resource (normally a URI)
     * @param operation the OpenApi operation identification
     * @return a new {@link WorkItemBuilder}
     */
    public static WorkItemBuilder builderFor(final String interfaceResource, final String operation) {
        return new WorkItemBuilder(interfaceResource, operation);
    }

    /**
     * Creates a new {@link WorkItemModifier} for the given {@link WorkItemNode}, normally created with {@link #builderFor(String, String)}.
     *
     * @param workItemNode the given {@link WorkItemNode}
     * @return a new {@link WorkItemModifier}
     */
    public static WorkItemModifier modifierFor(final WorkItemNode workItemNode) {
        return new WorkItemModifier(workItemNode);
    }

    public static boolean isOpenApiTask(Node node) {
        return node instanceof WorkItemNode &&
                ((WorkItemNode) node).getWork() != null &&
                TYPE.equals(((WorkItemNode) node).getWork().getName());
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return String.format("%s_%s_%s_Handler", interfaceName, operationName, workItemNode.getName()).replaceAll("\\s+", "");
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
        return Collections.emptyList();
    }

    @Override
    protected void handleParametersForServiceCall(final BlockStmt executeWorkItemBody, final MethodCallExpr callServiceMethod) {
        // declare the input model
        final MethodCallExpr getInputModel = new MethodCallExpr(new NameExpr("workItem"), METHOD_GET_PARAM).addArgument(new StringLiteralExpr("Parameter"));
        final VariableDeclarationExpr inputModel =
                new VariableDeclarationExpr(new VariableDeclarator(new ClassOrInterfaceType(null, Object.class.getCanonicalName()), VAR_INPUT_MODEL, getInputModel));
        executeWorkItemBody.addStatement(inputModel);
        final ClassOrInterfaceType resolverType = new ClassOrInterfaceType(null, workItemNode.getMetaData(PARAM_META_PARAM_RESOLVER_TYPE).toString());

        workItemNode.getWork().getParameters().entrySet()
                .stream()
                .filter(p -> p.getKey().startsWith(PARAM_PREFIX))
                .forEach(p -> {
                    if (p.getValue() != null) {
                        // method to get the param resolver instance
                        final MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), METHOD_GET_PARAM).addArgument(new StringLiteralExpr(p.getKey()));
                        // cast to the given param resolver type
                        final CastExpr castToResolver = new CastExpr(resolverType, getParamMethod);
                        // temp to hold the param resolver with the correct cast
                        final VariableDeclarationExpr paramResolver =
                                new VariableDeclarationExpr(new VariableDeclarator(castToResolver.getType(), "resolver" + p.getKey(), castToResolver));
                        executeWorkItemBody.addStatement(paramResolver);
                        // param resolver apply method
                        final MethodCallExpr paramResolverApplyMethod =
                                new MethodCallExpr(paramResolver.getVariable(0).getNameAsExpression(), "apply").addArgument(inputModel.getVariable(0).getNameAsExpression());
                        callServiceMethod.addArgument(paramResolverApplyMethod);
                    } else {
                        callServiceMethod.addArgument(new NullLiteralExpr());
                    }
                });
    }

    @Override
    protected Expression handleServiceCallResult(final BlockStmt executeWorkItemBody, final MethodCallExpr callService) {
        // fetch the handler type
        final ClassOrInterfaceType resultHandlerType = new ClassOrInterfaceType(null, workItemNode.getMetaData(PARAM_META_RESULT_HANDLER_TYPE).toString());
        // get the handler
        final MethodCallExpr getResultHandler = new MethodCallExpr(new NameExpr("workItem"), METHOD_GET_PARAM).addArgument(new StringLiteralExpr(PARAM_META_RESULT_HANDLER));
        // convert the result into the given type
        final CastExpr castToHandler = new CastExpr(resultHandlerType, getResultHandler);
        // temp to hold the result handler with the correct cast
        final VariableDeclarationExpr resultHandler =
                new VariableDeclarationExpr(new VariableDeclarator(castToHandler.getType(), "resultHandler", castToHandler));
        executeWorkItemBody.addStatement(resultHandler);
        return new MethodCallExpr(resultHandler.getVariable(0).getNameAsExpression(), "apply")
                .addArgument(new NameExpr(VAR_INPUT_MODEL))
                .addArgument(callService);
    }

    /**
     * Builder for {@link WorkItemNode}s for OpenApi Service Tasks
     * The result WorkItem has the same attributes as one created by a BPMN Editor.
     */
    public static final class WorkItemBuilder {

        private final String operation;
        private final String interfaceResource;
        private final Map<String, Supplier<Expression>> paramResolvers;
        private String paramResolverType;
        private String resultHandlerType;
        private Supplier<Expression> resultHandlerExpression;

        private WorkItemBuilder(final String interfaceResource, final String operation) {
            this.operation = operation;
            this.interfaceResource = interfaceResource;
            this.paramResolvers = new HashMap<>();
        }

        /**
         * Class type responsible for resolving parameters in the service call in runtime.
         *
         * @param paramResolverType The class canonical name
         * @return the {@link WorkItemBuilder}
         */
        public WorkItemBuilder withParamResolverType(final String paramResolverType) {
            this.paramResolverType = paramResolverType;
            return this;
        }

        /**
         * Class type responsible for handling the service call result in runtime.
         *
         * @param resultHandlerType The class canonical name
         * @return the {@link WorkItemBuilder}
         */
        public WorkItemBuilder withResultHandlerType(final String resultHandlerType) {
            this.resultHandlerType = resultHandlerType;
            return this;
        }

        /**
         * The JavaParser @{@link Expression} to get a reference for the result handler in runtime.
         * The Expression is used by the {@link OpenApiTaskDescriptor} to generate the runtime code.
         * 
         * @param resultHandler the @{@link Expression}
         * @return the {@link WorkItemBuilder}
         */
        public WorkItemBuilder withResultHandler(final Supplier<Expression> resultHandler) {
            this.resultHandlerExpression = resultHandler;
            return this;
        }

        /**
         * Adds a new parameter resolver to this builder.
         *
         * @param name the parameter name
         * @param paramResolver the JavaParser @{@link Expression} responsible for creating the resolver in runtime.
         *        This expression is used by the {@link OpenApiTaskDescriptor} to generate the runtime code.
         * @return the {@link WorkItemBuilder} so you can keep adding parameters to the same reference.
         */
        public WorkItemBuilder addParamResolver(final String name, final Supplier<Expression> paramResolver) {
            this.paramResolvers.put(PARAM_PREFIX + name, paramResolver);
            return this;
        }

        public WorkItemNode build() {
            WorkItemNode workItemNode = new WorkItemNode();
            workItemNode.setMetaData(KEY_WORKITEM_TYPE, TYPE);

            Work work = new WorkImpl();
            work.setName(TYPE);
            work.setParameter(KEY_SERVICE_IMPL, DEFAULT_SERVICE_IMPL);
            work.setParameter(KEY_WORKITEM_INTERFACE, this.interfaceResource);
            work.setParameter(KEY_WORKITEM_OPERATION, this.operation);

            this.paramResolvers.forEach(work::setParameter);

            if (this.paramResolverType != null && !this.paramResolverType.isEmpty()) {
                workItemNode.setMetaData(PARAM_META_PARAM_RESOLVER_TYPE, this.paramResolverType);
            }
            if (this.resultHandlerType != null && !this.resultHandlerType.isEmpty()) {
                workItemNode.setMetaData(PARAM_META_RESULT_HANDLER_TYPE, this.resultHandlerType);
            }
            if (this.resultHandlerExpression != null) {
                work.setParameter(PARAM_META_RESULT_HANDLER, this.resultHandlerExpression);
            }

            workItemNode.setWork(work);
            return workItemNode;
        }

    }

    /**
     * Facilitates the interaction with a given OpenApi {@link WorkItemNode}.
     */
    public static final class WorkItemModifier {
        private final WorkItemNode workItemNode;
        private final Set<String> specParameters;

        private WorkItemModifier(final WorkItemNode workItemNode) {
            this.workItemNode = workItemNode;
            this.specParameters = new LinkedHashSet<>();
        }

        public String getOperation() {
            return (String) this.workItemNode.getWork().getParameter(KEY_WORKITEM_OPERATION);
        }

        public String getInterface() {
            return (String) this.workItemNode.getWork().getParameter(KEY_WORKITEM_INTERFACE);
        }

        /**
         * Set all the runtime information to the given {@link WorkItemNode}
         *
         * @param generatedClass canonical name of OpenApi generated class
         * @param methodName method name for the generated class responsible to perform the REST call
         * @param specParams parameters as defined by the OpenApi Spec definition
         */
        public void modify(final String generatedClass, final String methodName, final List<String> specParams) {
            this.defineJavaImplementation(generatedClass, methodName);
            specParams.forEach(this::addSpecParameter);
            this.validateAndAddMissingParameters();
        }

        /**
         * Defines the generated Java class and method for the given Task.
         *
         * @param generatedClass canonical name of OpenApi generated class
         * @param methodName method name for the generated class responsible to perform the REST call
         */
        private void defineJavaImplementation(final String generatedClass, final String methodName) {
            requireNonNull(methodName, "Method name for Java implementation can't be null");
            requireNonNull(generatedClass, "Generated class for Java implementation can't be null");
            this.workItemNode.getWork().setParameter(KEY_WORKITEM_OPERATION, methodName);
            this.workItemNode.getWork().setParameter(KEY_WORKITEM_INTERFACE, generatedClass);
        }

        /**
         * Adds a parameter as defined in a given OpenApi Spec file. The internal list will retain the added order.
         *
         * @param name the name of the parameter
         */
        private void addSpecParameter(final String name) {
            this.specParameters.add(PARAM_PREFIX + name);
            this.workItemNode.setMetaData(PARAM_META_SPEC_PARAMETERS, this.specParameters);
        }

        /**
         * Adds all non-required parameters
         */
        private void validateAndAddMissingParameters() {
            final List<String> paramResolvers =
                    this.workItemNode.getWork().getParameters().keySet().stream().filter(o -> o.startsWith(PARAM_PREFIX)).collect(Collectors.toList());
            if (this.specParameters.size() != paramResolvers.size() || this.specParameters.size() > 1) {
                this.specParameters.stream()
                        .filter(p -> !paramResolvers.contains(p))
                        .forEach(p -> this.workItemNode.getWork().setParameter(p, null));
                final List<String> unexpectedParams = paramResolvers.stream().filter(p -> !this.specParameters.contains(p)).collect(Collectors.toList());
                if (!unexpectedParams.isEmpty()) {
                    throw new IllegalArgumentException("Found unexpected parameters in the Task definition: " + unexpectedParams + ". Expected parameters are: " + this.specParameters);
                }
            }
        }

    }
}
