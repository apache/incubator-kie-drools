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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.datatype.DataTypeResolver;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;
import org.kie.kogito.process.workitem.WorkItemExecutionException;
import org.kie.kogito.process.workitems.impl.expr.ExpressionWorkItemResolver;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static java.util.Objects.requireNonNull;

public class OpenApiTaskDescriptor extends AbstractServiceTaskDescriptor {

    public static final String TYPE = "OpenApi Task";
    private static final String PARAM_META_PARAM_RESOLVER_TYPE = "ParamResolverType";
    private static final String PARAM_META_SPEC_PARAMETERS = "SpecParameters";
    private static final String MODEL_PARAMETER = "ModelParameter";

    private static final String METHOD_GET_PARAM = "getParameter";
    private static final NameExpr workItemNameExpr = new NameExpr("workItem");

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

    private static Collection<String> getParameters(WorkItemNode workItemNode) {
        return workItemNode.getWork().getParameterDefinitions().stream().map(ParameterDefinition::getName).collect(Collectors.toList());
    }

    @Override
    protected void handleParametersForServiceCall(final BlockStmt executeWorkItemBody, final MethodCallExpr callServiceMethod) {
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, (String) workItemNode.getMetaData(PARAM_META_PARAM_RESOLVER_TYPE));
        getParameters(workItemNode)
                .forEach(p -> callServiceMethod.addArgument(new CastExpr(type, new MethodCallExpr(workItemNameExpr, METHOD_GET_PARAM).addArgument(new StringLiteralExpr(p)))));
    }

    /**
     * Builder for {@link WorkItemNode}s for OpenApi Service Tasks
     * The result WorkItem has the same attributes as one created by a BPMN Editor.
     */
    public static final class WorkItemBuilder {

        private final String operation;
        private final String interfaceResource;
        private String exprLang = "jq";
        private Class<? extends ExpressionWorkItemResolver> paramResolverClass;
        private Class<?> paramResolverOutputType;
        private String modelParameter = "Parameter";
        private Map<String, Object> functionArgs;

        private WorkItemBuilder(final String interfaceResource, final String operation) {
            this.operation = operation;
            this.interfaceResource = interfaceResource;
        }

        public WorkItemBuilder withArgs(Map<String, Object> map, Class<? extends ExpressionWorkItemResolver> resolverClass, Class<?> outputClass) {
            this.functionArgs = map;
            this.paramResolverClass = resolverClass;
            this.paramResolverOutputType = outputClass;
            return this;
        }

        /**
         * Set expression language
         * 
         * @param exprLang
         * @return
         */
        public WorkItemBuilder withExprLang(String exprLang) {
            this.exprLang = exprLang;
            return this;
        }

        /**
         * 
         * @param modelParameter
         * @return
         */
        public WorkItemBuilder withModelParameter(String modelParameter) {
            this.modelParameter = modelParameter;
            return this;
        }

        public <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> build(WorkItemNodeFactory<T> factory) {
            factory.metaData(KEY_WORKITEM_TYPE, TYPE);
            factory.workName(TYPE);
            factory.workParameter(KEY_SERVICE_IMPL, DEFAULT_SERVICE_IMPL);
            factory.workParameter(KEY_WORKITEM_INTERFACE, this.interfaceResource);
            factory.workParameter(KEY_WORKITEM_OPERATION, this.operation);
            if (functionArgs != null) {
                factory.metaData(PARAM_META_PARAM_RESOLVER_TYPE, this.paramResolverOutputType.getCanonicalName());
                functionArgs.entrySet().forEach(entry -> build(entry, factory));
            }
            factory.metaData(MODEL_PARAMETER, modelParameter);
            return factory;
        }

        private <T extends RuleFlowNodeContainerFactory<T, ?>> void build(Entry<String, Object> entry, WorkItemNodeFactory<T> factory) {
            factory.workParameter(entry.getKey(), processWorkItemValue(exprLang, entry.getValue(), this.modelParameter, this.paramResolverClass, true))
                    .workParameterDefinition(entry.getKey(), DataTypeResolver.fromObject(entry.getValue(), true));
        }

        protected WorkItemNode build() {
            WorkItemNode workItemNode = new WorkItemNode();
            workItemNode.setMetaData(KEY_WORKITEM_TYPE, TYPE);

            Work work = new WorkImpl();
            work.setName(TYPE);
            work.setParameter(KEY_SERVICE_IMPL, DEFAULT_SERVICE_IMPL);
            work.setParameter(KEY_WORKITEM_INTERFACE, this.interfaceResource);
            work.setParameter(KEY_WORKITEM_OPERATION, this.operation);

            if (functionArgs != null) {
                workItemNode.setMetaData(PARAM_META_PARAM_RESOLVER_TYPE, this.paramResolverOutputType.getCanonicalName());
                functionArgs.entrySet().forEach(entry -> build(entry, work));
            }
            workItemNode.setMetaData(MODEL_PARAMETER, modelParameter);

            workItemNode.setWork(work);
            return workItemNode;
        }

        private <T extends RuleFlowNodeContainerFactory<T, ?>> void build(Entry<String, Object> entry, Work work) {
            work.setParameter(entry.getKey(), processWorkItemValue(exprLang, entry.getValue(), modelParameter, this.paramResolverClass, true));
            work.addParameterDefinition(new ParameterDefinitionImpl(entry.getKey(), DataTypeResolver.fromObject(entry.getValue(), true)));
        }

    }

    /**
     * Facilitates the interaction with a given OpenApi {@link WorkItemNode}.
     */
    public static final class WorkItemModifier {
        private final WorkItemNode workItemNode;
        private Set<String> specParameters;

        private WorkItemModifier(final WorkItemNode workItemNode) {
            this.workItemNode = workItemNode;
            this.specParameters = Collections.emptySet();
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
            this.specParameters = new LinkedHashSet<>(specParams);
            this.workItemNode.setMetaData(PARAM_META_SPEC_PARAMETERS, this.specParameters);
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
         * Adds all non-required parameters
         */
        private void validateAndAddMissingParameters() {
            final Collection<String> paramResolvers = getParameters(workItemNode);
            if (this.specParameters.size() != paramResolvers.size() || this.specParameters.size() > 1) {
                this.specParameters.stream()
                        .filter(p -> !paramResolvers.contains(p))
                        .forEach(this::addParameterFromSpec);
                final List<String> unexpectedParams = paramResolvers.stream().filter(p -> !this.specParameters.contains(p)).collect(Collectors.toList());
                if (!unexpectedParams.isEmpty()) {
                    throw new IllegalArgumentException("Found unexpected parameters in the Task definition: " + unexpectedParams + ". Expected parameters are: " + this.specParameters);
                }
            }
        }

        private void addParameterFromSpec(String key) {
            Work work = this.workItemNode.getWork();
            work.setParameter(key, null);
            work.addParameterDefinition(new ParameterDefinitionImpl(key, new ObjectDataType()));
        }
    }
}
