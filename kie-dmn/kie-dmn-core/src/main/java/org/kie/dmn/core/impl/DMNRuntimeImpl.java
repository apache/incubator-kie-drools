/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.impl;

import javax.xml.namespace.QName;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNDecisionServiceEvaluator;
import org.kie.dmn.core.ast.DMNFunctionWithReturnType;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.core.compiler.DMNOption;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeModeOption;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.EVALUATING;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.FAILED;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SKIPPED;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;
import static org.kie.dmn.core.util.CoerceUtil.coerceValue;

public class DMNRuntimeImpl
        implements DMNRuntime {

    private static final Logger logger = LoggerFactory.getLogger(DMNRuntimeImpl.class);

    private DMNRuntimeEventManagerImpl eventManager;
    private final DMNRuntimeKB runtimeKB;

    private boolean overrideRuntimeTypeCheck = false;
    private RuntimeModeOption.MODE runtimeModeOption = RuntimeModeOption.MODE.LENIENT;

    private DMNResultImplFactory dmnResultFactory = new DMNResultImplFactory();

    public DMNRuntimeImpl(DMNRuntimeKB runtimeKB) {
        this.runtimeKB = runtimeKB != null ? runtimeKB : new VoidDMNRuntimeKB();
        this.eventManager = new DMNRuntimeEventManagerImpl();
        for (DMNRuntimeEventListener listener : this.runtimeKB.getListeners()) {
            this.addListener(listener);
        }
    }

    static void populateResultContextWithTopmostParentsValues(DMNContext context, DMNModelImpl model) {
        Optional<Set<DMNModelImpl.ModelImportTuple>> optionalTopmostModels = getTopmostModel(model);
        optionalTopmostModels.ifPresent(topmostModels -> populateInputsFromTopmostModel(context, model, topmostModels));
    }

    static void populateInputsFromTopmostModel(DMNContext context,DMNModelImpl model, Set<DMNModelImpl.ModelImportTuple> topmostModels) {
        for (DMNModelImpl.ModelImportTuple topmostModelTuple : topmostModels) {
            processTopmostModelTuple(context, topmostModelTuple, model);
        }
    }

    static void processTopmostModelTuple(DMNContext context, DMNModelImpl.ModelImportTuple topmostModelTuple, DMNModelImpl model) {
        DMNModelImpl topmostModel = topmostModelTuple.getModel();
        for (InputDataNode topmostInput : topmostModel.getInputs()) {
            processTopmostModelInputDataNode(context, topmostInput.getName(), topmostModelTuple, model);
        }
    }

    static void processTopmostModelInputDataNode( DMNContext context, String topmostInputName, DMNModelImpl.ModelImportTuple topmostModelTuple, DMNModelImpl model) {
        Object storedValue = context.get(topmostInputName);
        if (storedValue != null) {
            Object parentData = context.get(topmostModelTuple.getImportName());
            if (parentData instanceof Map mappedData) {
                processTopmostModelMap(mappedData, topmostInputName, storedValue, parentData);
            } else if (parentData == null) {
                updateContextMap(context, model.getImportChainAliases(), topmostModelTuple, topmostInputName, storedValue);
            }
        }
    }

    /**
     * Depending on how the context has been instantiated, the provided <code>Map</code> could be unmodifiable, which is an expected condition
     * @param mappedData
     * @param inputName
     * @param storedValue
     * @param parentData
     */
    static void processTopmostModelMap(Map mappedData, String inputName, Object storedValue, Object parentData) {
        try {
            mappedData.put(inputName, storedValue);
        } catch (Exception e) {
            logger.warn("Failed to add {} to map {} ", storedValue, parentData, e);
        }
    }

    static void updateContextMap(DMNContext context, Map<String, Collection<List<String>>>  importChainAliases, DMNModelImpl.ModelImportTuple topmostModelTuple, String inputName, Object storedValue) {
        Map mappedData = new HashMap<>();
        mappedData.put(inputName, storedValue);
        populateContextWithInheritedData(context, mappedData,
                topmostModelTuple.getImportName(), topmostModelTuple.getModel().getNamespace(), importChainAliases);
    }

    static void populateContextWithInheritedData(DMNContext toPopulate, Map<String, Object> toStore, String importName, String topmostNamespace,Map<String, Collection<List<String>>> importChainAliases) {
        for (List<String> chainedModels : importChainAliases.get(topmostNamespace)) {
            // The order is: first one -> importing model; last one -> parent model
            for (String chainedModel : chainedModels) {
                if (chainedModel.equals(importName)) {
                    continue;
                }
                if (toStore.get(chainedModel) != null && toStore.get(chainedModel) instanceof Map<?, ?> alreadyMapped) {
                    try {
                        ((Map<String, Object>) alreadyMapped).put(importName, toStore);
                    } catch (Exception e) {
                        logger.warn("Failed to add {} to map {} ", toStore, alreadyMapped, e);
                    }
                } else {
                    Map<String, Object> chainedMap = new HashMap<>();
                    chainedMap.put(importName, toStore);
                    toPopulate.set(chainedModel, chainedMap);
                }
            }
        }
    }

    static Optional<Set<DMNModelImpl.ModelImportTuple>> getTopmostModel(DMNModelImpl model) {
        return model.getTopmostParents();
    }

    @Override
    public List<DMNModel> getModels() {
        return runtimeKB.getModels();
    }

    @Override
    public DMNModel getModel(String namespace, String modelName) {
        Objects.requireNonNull(namespace, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "namespace"));
        Objects.requireNonNull(modelName, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "modelName"));
        return runtimeKB.getModel(namespace, modelName);
    }

    @Override
    public DMNModel getModelById(String namespace, String modelId) {
        Objects.requireNonNull(namespace, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "namespace"));
        Objects.requireNonNull(modelId, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "modelId"));
        return runtimeKB.getModelById(namespace, modelId);
    }

    @Override
    public DMNResult evaluateAll(DMNModel model, DMNContext context) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        Objects.requireNonNull(context, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "context"));
        boolean performRuntimeTypeCheck = performRuntimeTypeCheck(model);
        boolean strictMode = this.runtimeModeOption.equals(RuntimeModeOption.MODE.STRICT);
        DMNResultImpl result = createResult(model, context);
        DMNRuntimeEventManagerUtils.fireBeforeEvaluateAll(eventManager, model, result);
        // the engine should evaluate all Decisions belonging to the "local" model namespace, not imported decision explicitly.
        Set<DecisionNode> decisions = model.getDecisions().stream()
                .filter(d -> d.getModelNamespace().equals(model.getNamespace())).collect(Collectors.toSet());
        for (DecisionNode decision : decisions) {
            evaluateDecision(context, result, decision, performRuntimeTypeCheck, strictMode);
        }
        DMNRuntimeEventManagerUtils.fireAfterEvaluateAll(eventManager, model, result);
        return result;
    }

    @Override
    @Deprecated
    public DMNResult evaluateDecisionByName(DMNModel model, String decisionName, DMNContext context) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        Objects.requireNonNull(decisionName, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "decisionName"));
        Objects.requireNonNull(context, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "context"));
        return evaluateByName(model, context, decisionName);
    }

    @Override
    @Deprecated
    public DMNResult evaluateDecisionById(DMNModel model, String decisionId, DMNContext context) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        Objects.requireNonNull(decisionId, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "decisionId"));
        Objects.requireNonNull(context, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "context"));
        return evaluateById(model, context, decisionId);
    }

    @Override
    public DMNResult evaluateByName(DMNModel model, DMNContext context, String... decisionNames) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        Objects.requireNonNull(context, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "context"));
        Objects.requireNonNull(decisionNames, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "decisionNames"));
        if (decisionNames.length == 0) {
            throw new IllegalArgumentException(MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_EMPTY, "decisionNames"));
        }
        final DMNResultImpl result = createResult(model, context);
        boolean strictMode = this.runtimeModeOption.equals(RuntimeModeOption.MODE.STRICT);
        for (String name : decisionNames) {
            evaluateByNameInternal(model, context, result, name, strictMode);
        }
        return result;
    }

    private void evaluateByNameInternal(DMNModel model, DMNContext context, DMNResultImpl result, String name,
                                        boolean strictMode) {
        boolean performRuntimeTypeCheck = performRuntimeTypeCheck(model);
        Optional<DecisionNode> decision = Optional.ofNullable(model.getDecisionByName(name));
        if (decision.isPresent()) {
            final boolean walkingIntoScope = walkIntoImportScopeInternalDecisionInvocation(result, model,
                                                                                           decision.get());
            evaluateDecision(context, result, decision.get(), performRuntimeTypeCheck, strictMode);
            if (walkingIntoScope) {
                result.getContext().popScope();
            }
        } else {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  null,
                                  result,
                                  null,
                                  null,
                                  Msg.DECISION_NOT_FOUND_FOR_NAME,
                                  name);
        }
    }

    @Override
    public DMNResult evaluateById(DMNModel model, DMNContext context, String... decisionIds) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        Objects.requireNonNull(context, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "context"));
        Objects.requireNonNull(decisionIds, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "decisionIds"));
        if (decisionIds.length == 0) {
            throw new IllegalArgumentException(MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_EMPTY, "decisionIds"));
        }
        final DMNResultImpl result = createResult(model, context);
        boolean strictMode = this.runtimeModeOption.equals(RuntimeModeOption.MODE.STRICT);
        for (String id : decisionIds) {
            evaluateByIdInternal(model, context, result, id, strictMode);
        }
        return result;
    }

    private void evaluateByIdInternal(DMNModel model, DMNContext context, DMNResultImpl result, String id,
                                      boolean strictMode) {
        boolean performRuntimeTypeCheck = performRuntimeTypeCheck(model);
        Optional<DecisionNode> decision = Optional.ofNullable(model.getDecisionById(id));
        if (decision.isPresent()) {
            final boolean walkingIntoScope = walkIntoImportScopeInternalDecisionInvocation(result, model,
                                                                                           decision.get());
            evaluateDecision(context, result, decision.get(), performRuntimeTypeCheck, strictMode);
            if (walkingIntoScope) {
                result.getContext().popScope();
            }
        } else {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  null,
                                  result,
                                  null,
                                  null,
                                  Msg.DECISION_NOT_FOUND_FOR_ID,
                                  id);
        }
    }

    @Override
    public void addListener(DMNRuntimeEventListener listener) {
        this.eventManager.addListener(listener);
    }

    @Override
    public void removeListener(DMNRuntimeEventListener listener) {
        this.eventManager.removeListener(listener);
    }

    @Override
    public boolean hasListeners() {
        return this.eventManager.hasListeners();
    }

    @Override
    public Set<DMNRuntimeEventListener> getListeners() {
        return this.eventManager.getListeners();
    }

    private DMNResultImpl createResult(DMNModel model, DMNContext context) {
        DMNResultImpl result = createResultImpl(model, context);

        for (DecisionNode decision :
                model.getDecisions().stream().filter(d -> d.getModelNamespace().equals(model.getNamespace())).collect(Collectors.toSet())) {
            result.addDecisionResult(new DMNDecisionResultImpl(decision.getId(), decision.getName()));
        }
        return result;
    }

    private DMNResultImpl createResultImpl(DMNModel model, DMNContext context) {
        DMNResultImpl result = dmnResultFactory.newDMNResultImpl(model);
        result.setContext(context.clone()); // DMNContextFPAImpl.clone() creates DMNContextImpl
        populateResultContextWithTopmostParentsValues(result.getContext(), (DMNModelImpl) model);
        return result;
    }

    public void setDMNResultImplFactory(DMNResultImplFactory dmnResultFactory) {
        this.dmnResultFactory = dmnResultFactory;
    }

    @Override
    public DMNResult evaluateDecisionService(DMNModel model, DMNContext context, String decisionServiceName) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        Objects.requireNonNull(context, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "context"));
        Objects.requireNonNull(decisionServiceName, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "decisionServiceName"));
        boolean typeCheck = performRuntimeTypeCheck(model);
        DMNResultImpl result = createResultImpl(model, context);

        // the engine should evaluate all belonging to the "local" model namespace, not imported nodes explicitly.
        Optional<DecisionServiceNode> lookupDS = model.getDecisionServices().stream()
                .filter(d -> d.getModelNamespace().equals(model.getNamespace()))
                .filter(ds -> ds.getName().equals(decisionServiceName))
                .findFirst();
        if (lookupDS.isPresent()) {
            DecisionServiceNodeImpl decisionService = (DecisionServiceNodeImpl) lookupDS.get();
            for (DMNNode dep : decisionService.getInputParameters().values()) {
                if (!isNodeValueDefined(result, decisionService, dep)) {
                    DMNMessage message = MsgUtil.reportMessage(logger,
                                                               DMNMessage.Severity.WARN,
                                                               decisionService.getSource(),
                                                               result,
                                                               null,
                                                               null,
                                                               Msg.REQ_INPUT_NOT_FOUND_FOR_DS,
                                                               getDependencyIdentifier(decisionService, dep),
                                                               getIdentifier(decisionService));
                    final boolean walkingIntoScope = walkIntoImportScope(result, decisionService, dep);
                    result.getContext().set(dep.getName(), null);
                    if (walkingIntoScope) {
                        result.getContext().popScope();
                    }
                } else {
                    final boolean walkingIntoScope = walkIntoImportScope(result, decisionService, dep);
                    final Object originalValue = result.getContext().get(dep.getName());
                    DMNType depType = ((DMNModelImpl) model).getTypeRegistry().unknown();
                    if (dep instanceof InputDataNode) {
                        depType = ((InputDataNode) dep).getType();
                    } else if (dep instanceof DecisionNode) {
                        depType = ((DecisionNode) dep).getResultType();
                    }
                    Object c = coerceUsingType(originalValue,
                                               depType,
                                               typeCheck,
                                               (r, t) -> MsgUtil.reportMessage(logger,
                                                                               DMNMessage.Severity.WARN,
                                                                               decisionService.getDecisionService(),
                                                                               result,
                                                                               null,
                                                                               null,
                                                                               Msg.PARAMETER_TYPE_MISMATCH_DS,
                                                                               dep.getName(),
                                                                               t,
                                                                               MsgUtil.clipString(r.toString(), 50)));
                    if (c != originalValue) { //intentional by-reference
                        result.getContext().set(dep.getName(), c);
                    }
                    if (walkingIntoScope) {
                        result.getContext().popScope();
                    }
                }
            }
            EvaluatorResult evaluate = new DMNDecisionServiceEvaluator(decisionService, true, false).evaluate(this,
                                                                                                              result); // please note singleton output coercion does not influence anyway when invoked DS on a model.
        } else {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  null,
                                  result,
                                  null,
                                  null,
                                  Msg.DECISION_SERVICE_NOT_FOUND_FOR_NAME,
                                  decisionServiceName);
        }
        return result;
    }

    private void evaluateDecisionService(DMNContext context, DMNResultImpl result, DecisionServiceNode d,
                                         boolean typeCheck) {
        DecisionServiceNodeImpl ds = (DecisionServiceNodeImpl) d;
        if (isNodeValueDefined(result, ds, ds)) {
            // already resolved
            return;
        }
        // Note: a Decision Service is expected to always have an evaluator, it does not depend on an xml expression,
        // it is done always by the compiler.
        try {
            // a Decision Service when is evaluated as a function does not require any dependency check, as they will
            // be passed as params.

            EvaluatorResult er = ds.getEvaluator().evaluate(this, result);
            if (er.getResultType() == EvaluatorResult.ResultType.SUCCESS) {
                FEELFunction resultFn = (FEELFunction) er.getResult();
                result.getContext().set(ds.getName(), resultFn);
            }
        } catch (Throwable t) {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  ds.getSource(),
                                  result,
                                  t,
                                  null,
                                  Msg.ERROR_EVAL_DS_NODE,
                                  getIdentifier(ds),
                                  t.getMessage());
        }
    }

    private void evaluateBKM(DMNContext context, DMNResultImpl result, BusinessKnowledgeModelNode b,
                             boolean typeCheck) {
        BusinessKnowledgeModelNodeImpl bkm = (BusinessKnowledgeModelNodeImpl) b;
        if (isNodeValueDefined(result, bkm, bkm)) {
            // already resolved
            // TODO: do we need to check if the defined variable is a function as it should?
            return;
        }
        // TODO: do we need to check/resolve dependencies?
        if (bkm.getEvaluator() == null) {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.WARN,
                                  bkm.getSource(),
                                  result,
                                  null,
                                  null,
                                  Msg.MISSING_EXPRESSION_FOR_BKM,
                                  getIdentifier(bkm));
            return;
        }
        try {
            DMNRuntimeEventManagerUtils.fireBeforeEvaluateBKM(eventManager, bkm, result);
            for (DMNNode dep : bkm.getDependencies().values()) {
                if (typeCheck && !checkDependencyValueIsValid(dep, result)) {
                    MsgUtil.reportMessage(logger,
                                          DMNMessage.Severity.ERROR,
                                          ((DMNBaseNode) dep).getSource(),
                                          result,
                                          null,
                                          null,
                                          Msg.ERROR_EVAL_NODE_DEP_WRONG_TYPE,
                                          getIdentifier(bkm),
                                          getDependencyIdentifier(bkm, dep),
                                          MsgUtil.clipString(Objects.toString(result.getContext().get(dep.getName()))
                                                  , 50),
                                          ((DMNBaseNode) dep).getType()
                    );
                    return;
                }
                if (!isNodeValueDefined(result, bkm, dep)) {
                    boolean walkingIntoScope = walkIntoImportScope(result, bkm, dep);
                    if (dep instanceof BusinessKnowledgeModelNode) {
                        evaluateBKM(context, result, (BusinessKnowledgeModelNode) dep, typeCheck);
                    } else if (dep instanceof DecisionServiceNode) {
                        evaluateDecisionService(context, result, (DecisionServiceNode) dep, typeCheck);
                    } else {
                        MsgUtil.reportMessage(logger,
                                              DMNMessage.Severity.ERROR,
                                              bkm.getSource(),
                                              result,
                                              null,
                                              null,
                                              Msg.REQ_DEP_NOT_FOUND_FOR_NODE,
                                              getDependencyIdentifier(bkm, dep),
                                              getIdentifier(bkm)
                        );
                        return;
                    }
                    if (walkingIntoScope) {
                        result.getContext().popScope();
                    }
                }
            }

            EvaluatorResult er = bkm.getEvaluator().evaluate(this, result);
            if (er.getResultType() == EvaluatorResult.ResultType.SUCCESS) {
                final FEELFunction original_fn = (FEELFunction) er.getResult();
                FEELFunction resultFn = original_fn;
                if (typeCheck) {
                    DMNType resultType = b.getResultType();
                    resultFn = new DMNFunctionWithReturnType(original_fn, resultType, result, b);
                }
                result.getContext().set(bkm.getBusinessKnowledModel().getVariable().getName(), resultFn);
            }
        } catch (Throwable t) {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  bkm.getSource(),
                                  result,
                                  t,
                                  null,
                                  Msg.ERROR_EVAL_BKM_NODE,
                                  getIdentifier(bkm),
                                  t.getMessage());
        } finally {
            DMNRuntimeEventManagerUtils.fireAfterEvaluateBKM(eventManager, bkm, result);
        }
    }

    public static Object coerceUsingType(Object value, DMNType type, boolean typeCheck,
                                         BiConsumer<Object, DMNType> nullCallback) {
        if (typeCheck) {
            if (type.isAssignableValue(value)) {
                return coerceSingleItemCollectionToValue(value, type);
            } else {
                nullCallback.accept(value, type);
                return null;
            }
        } else {
            return coerceSingleItemCollectionToValue(value, type);
        }
    }

    /**
     * Checks a type and if it is not a collection type, checks if the specified value is a collection
     * that contains only a single value and if yes, coerces the collection to the single item itself.
     * E.g. [1] becomes 1. Basically it unwraps the single item from a collection, if it is required.
     * @param value Value that is checked and potentially coerced to a single item.
     * @param type Required type. Based on this type, it is determined, if the coercion happens.
     * If the requirement is for a non-collection type and the value is a single item collection,
     * the coercion happens.
     * @return If all requirements are met, returns coerced value. Otherwise returns the original value.
     */
    private static Object coerceSingleItemCollectionToValue(Object value, DMNType type) {
        if (!type.isCollection() && value instanceof Collection && ((Collection<?>) value).size() == 1) {
            // as per Decision evaluation result.
            return ((Collection<?>) value).toArray()[0];
        } else {
            return value;
        }
    }

    private boolean isNodeValueDefined(DMNResultImpl result, DMNNode callerNode, DMNNode calledNode) {
        if (calledNode.getModelNamespace().equals(result.getContext().scopeNamespace().orElse(result.getModel()
                                                                                                .getNamespace()))) {
            return result.getContext().isDefined(calledNode.getName());
        }  else if (isInUnnamedImport(calledNode, (DMNModelImpl) result.getModel())) {
            // the node is an unnamed import
            return result.getContext().isDefined(calledNode.getName());
        } else {
            Optional<String> importAlias = callerNode.getModelImportAliasFor(calledNode.getModelNamespace(), calledNode
            .getModelName());
            if (importAlias.isPresent()) {
                Object aliasContext = result.getContext().get(importAlias.get());
                if (aliasContext instanceof Map<?, ?> mappedContext) {
                    return mappedContext.containsKey(calledNode.getName());
                }
            }
            return false;
        }
    }

    private boolean walkIntoImportScopeInternalDecisionInvocation(DMNResultImpl result, DMNModel dmnModel,
                                                                  DMNNode destinationNode) {
        if (destinationNode.getModelNamespace().equals(dmnModel.getNamespace())) {
            return false;
        } else {
            DMNModelImpl model = (DMNModelImpl) dmnModel;
            Optional<String> importAlias = model.getImportAliasFor(destinationNode.getModelNamespace(), destinationNode.getModelName());
            if (importAlias.isPresent()) {
                result.getContext().pushScope(importAlias.get(), destinationNode.getModelNamespace());
                return true;
            } else {
                MsgUtil.reportMessage(logger,
                                      DMNMessage.Severity.ERROR,
                                      dmnModel.getDefinitions(),
                                      result,
                                      null,
                                      null,
                                      Msg.IMPORT_NOT_FOUND_FOR_NODE_MISSING_ALIAS,
                                      new QName(destinationNode.getModelNamespace(), destinationNode.getModelName()),
                                      dmnModel.getName());
                return false;
            }
        }
    }

    private boolean walkIntoImportScope(DMNResultImpl result, DMNNode callerNode, DMNNode destinationNode) {
        if (result.getContext().scopeNamespace().isEmpty()) {
            if (destinationNode.getModelNamespace().equals(result.getModel().getNamespace())) {
                return false;
            } else if (isInUnnamedImport(destinationNode, (DMNModelImpl) result.getModel())) {
                // the destinationNode is an unnamed import
                return false;
            } else {
                Optional<String> importAlias = callerNode.getModelImportAliasFor(destinationNode.getModelNamespace(), destinationNode.getModelName());
                if (importAlias.isPresent()) {
                    result.getContext().pushScope(importAlias.get(), destinationNode.getModelNamespace());
                    return true;
                } else {
                    MsgUtil.reportMessage(logger,
                                          DMNMessage.Severity.ERROR,
                                          ((DMNBaseNode) callerNode).getSource(),
                                          result,
                                          null,
                                          null,
                                          Msg.IMPORT_NOT_FOUND_FOR_NODE_MISSING_ALIAS,
                                          new QName(destinationNode.getModelNamespace(), destinationNode.getModelName()),
                                          callerNode.getName()
                    );
                    return false;
                }
            }
        } else { // this branch is: result context scopeNamespace Optional isPresent == true
            if (destinationNode.getModelNamespace().equals(result.getContext().scopeNamespace().orElseThrow(IllegalStateException::new))) {
                return false;
            } else {
                Optional<String> importAlias = callerNode.getModelImportAliasFor(destinationNode.getModelNamespace(),
                                                                                 destinationNode.getModelName());
                if (importAlias.isPresent()) {
                    result.getContext().pushScope(importAlias.get(), destinationNode.getModelNamespace());
                    return true;
                } else {
                    MsgUtil.reportMessage(logger,
                                          DMNMessage.Severity.ERROR,
                                          ((DMNBaseNode) callerNode).getSource(),
                                          result,
                                          null,
                                          null,
                                          Msg.IMPORT_NOT_FOUND_FOR_NODE_MISSING_ALIAS,
                                          new QName(destinationNode.getModelNamespace(), destinationNode.getModelName()),
                                          callerNode.getName());
                    return false;
                }
            }
        }
    }

    private boolean evaluateDecision(DMNContext context, DMNResultImpl result, DecisionNode d, boolean typeCheck,
                                     boolean strictMode) {
        DecisionNodeImpl decision = (DecisionNodeImpl) d;
        String decisionId = d.getModelNamespace().equals(result.getModel().getNamespace()) ? decision.getId() :
                decision.getModelNamespace() + "#" + decision.getId();
        if (isNodeValueDefined(result, decision, decision)) {
            // already resolved
            return true;
        } else {
            // check if the decision was already evaluated before and returned error
            DMNDecisionResult.DecisionEvaluationStatus status = Optional.ofNullable(result.getDecisionResultById(decisionId))
                    .map(DMNDecisionResult::getEvaluationStatus)
                    .orElse(DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED); // it might be an imported Decision.
            if (FAILED == status || SKIPPED == status || EVALUATING == status) {
                return false;
            }
        }
        BeforeEvaluateDecisionEvent beforeEvaluateDecisionEvent = null;
        try {
            beforeEvaluateDecisionEvent = DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecision(eventManager, decision, result);
            DMNDecisionResultImpl dr = (DMNDecisionResultImpl) result.getDecisionResultById(decisionId);
            if (dr == null) { // an imported Decision now evaluated, requires the creation of the decision result:
                String decisionResultName = d.getName();
                Optional<String> importAliasFor =
                        ((DMNModelImpl) result.getModel()).getImportAliasFor(d.getModelNamespace(), d.getModelName());
                if (importAliasFor.isPresent()) {
                    decisionResultName = importAliasFor.get() + "." + d.getName();
                }
                dr = new DMNDecisionResultImpl(decisionId, decisionResultName);
                if (importAliasFor.isPresent()) { // otherwise is a transitive, skipped and not to be added to the results:
                    result.addDecisionResult(dr);
                }
            }
            dr.setEvaluationStatus(DMNDecisionResult.DecisionEvaluationStatus.EVALUATING);

            boolean missingInput = isMissingInputForDependencies(context, decision, result, dr, typeCheck, strictMode);
            if (missingInput) {
                return false;
            }
            if (decision.getEvaluator() == null) {
                DMNMessage message = MsgUtil.reportMessage(logger,
                                                           DMNMessage.Severity.WARN,
                                                           decision.getSource(),
                                                           result,
                                                           null,
                                                           null,
                                                           Msg.MISSING_EXPRESSION_FOR_DECISION,
                                                           getIdentifier(decision));

                reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED);
                return false;
            }
            try {
                EvaluatorResult er = decision.getEvaluator().evaluate(this, result);
                // if result messages contains errors && runtime mode = strict -> stop execution and return null
                if (strictMode && result.hasErrors()) {
                    logger.warn("Immediately return due to strict mode");
                    return false;
                } else if (er.getResultType() == EvaluatorResult.ResultType.SUCCESS ||
                        (((DMNModelImpl) result.getModel()).getFeelDialect().equals(FEELDialect.BFEEL) && er.getResult() != null)) {
                    Object value = coerceValue(decision.getResultType(), er.getResult());
                    try {
                        if (typeCheck && !d.getResultType().isAssignableValue(value)) {
                            DMNMessage message = MsgUtil.reportMessage(logger,
                                                                       DMNMessage.Severity.ERROR,
                                                                       decision.getSource(),
                                                                       result,
                                                                       null,
                                                                       null,
                                                                       Msg.ERROR_EVAL_NODE_RESULT_WRONG_TYPE,
                                                                       getIdentifier(decision),
                                                                       decision.getResultType(),
                                                                       value);
                            reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.FAILED);
                            return false;
                        }
                    } catch (Exception e) {
                        MsgUtil.reportMessage(logger,
                                              DMNMessage.Severity.ERROR,
                                              decision.getSource(),
                                              result,
                                              e,
                                              null,
                                              Msg.ERROR_CHECKING_ALLOWED_VALUES,
                                              getIdentifier(decision),
                                              e.getMessage());
                        return false;
                    }

                    result.getContext().set(decision.getDecision().getVariable().getName(), value);
                    dr.setResult(value);
                    dr.setEvaluationStatus(DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
                } else {
                    DMNMessage message = MsgUtil.reportMessage(logger,
                                                               DMNMessage.Severity.ERROR,
                                                               decision.getSource(),
                                                               result,
                                                               null,
                                                               null,
                                                               Msg.ERROR_EVAL_DECISION_NODE,
                                                               getIdentifier(decision),
                                                               decision.getResultType());
                    reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.FAILED);
                    return false;
                }
            } catch (Throwable t) {
                DMNMessage message = MsgUtil.reportMessage(logger,
                                                           DMNMessage.Severity.ERROR,
                                                           decision.getSource(),
                                                           result,
                                                           t,
                                                           null,
                                                           Msg.ERROR_EVAL_DECISION_NODE,
                                                           getIdentifier(decision),
                                                           t.getMessage());
                reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.FAILED);
            }
            return true;
        } finally {
            DMNRuntimeEventManagerUtils.fireAfterEvaluateDecision(eventManager, decision, result,
                                                                  beforeEvaluateDecisionEvent);
        }
    }

    private boolean checkDependencyValueIsValid(DMNNode dep, DMNResultImpl result) {
        if (dep instanceof InputDataNode) {
            InputDataNodeImpl inputDataNode = (InputDataNodeImpl) dep;
            BaseDMNTypeImpl dmnType = (BaseDMNTypeImpl) inputDataNode.getType();
            return dmnType.isAssignableValue(result.getContext().get(dep.getName()));
        }
        // if the dependency is NOT an InputData, the type coherence was checked at evaluation result assignment.
        return true;
    }

    private boolean isMissingInputForDependencies(DMNContext context, DecisionNodeImpl decision, DMNResultImpl result
            , DMNDecisionResultImpl dr, boolean typeCheck, boolean strictMode) {
        boolean toReturn = false;
        for (DMNNode dep : decision.getDependencies().values()) {
            toReturn |= isMissingInputForDependency(dep, decision, result, dr, typeCheck);
            if (toReturn && strictMode) {
                logger.warn("Immediately return due to strict mode");
                return toReturn;
            }
            if (!isNodeValueDefined(result, decision, dep)) {
                boolean walkingIntoScope = walkIntoImportScope(result, decision, dep);
                if (dep instanceof DecisionNode) {
                    if (!evaluateDecision(context, result, (DecisionNode) dep, typeCheck, strictMode)) {
                        toReturn = true;
                        DMNMessage message = MsgUtil.reportMessage(logger,
                                                                   DMNMessage.Severity.ERROR,
                                                                   decision.getSource(),
                                                                   result,
                                                                   null,
                                                                   null,
                                                                   Msg.UNABLE_TO_EVALUATE_DECISION_REQ_DEP,
                                                                   getIdentifier(decision),
                                                                   getDependencyIdentifier(decision, dep));
                        reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED);
                    }
                } else if (dep instanceof BusinessKnowledgeModelNode) {
                    evaluateBKM(context, result, (BusinessKnowledgeModelNode) dep, typeCheck);
                } else if (dep instanceof DecisionServiceNode) {
                    evaluateDecisionService(context, result, (DecisionServiceNode) dep, typeCheck);
                } else {
                    toReturn = true;
                    DMNMessage message = MsgUtil.reportMessage(logger,
                                                               DMNMessage.Severity.ERROR,
                                                               decision.getSource(),
                                                               result,
                                                               null,
                                                               null,
                                                               Msg.REQ_DEP_NOT_FOUND_FOR_NODE,
                                                               getDependencyIdentifier(decision, dep),
                                                               getIdentifier(decision)
                    );
                    reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED);
                }
                if (walkingIntoScope) {
                    result.getContext().popScope();
                }
            }
        }
        return toReturn;
    }

    private boolean isMissingInputForDependency(DMNNode dep, DecisionNodeImpl decision, DMNResultImpl result,
                                                DMNDecisionResultImpl dr, boolean typeCheck) {
        boolean toReturn = false;
        try {
            if (typeCheck && !checkDependencyValueIsValid(dep, result)) {
                toReturn = true;
                DMNMessage message = MsgUtil.reportMessage(logger,
                                                           DMNMessage.Severity.ERROR,
                                                           ((DMNBaseNode) dep).getSource(),
                                                           result,
                                                           null,
                                                           null,
                                                           Msg.ERROR_EVAL_NODE_DEP_WRONG_TYPE,
                                                           getIdentifier(decision),
                                                           getDependencyIdentifier(decision, dep),
                                                           MsgUtil.clipString(Objects.toString(result.getContext().get(dep.getName())), 50),
                                                           ((DMNBaseNode) dep).getType()
                );
                reportFailure(dr, message, DMNDecisionResult.DecisionEvaluationStatus.SKIPPED);
            }
        } catch (Exception e) {
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  ((DMNBaseNode) dep).getSource(),
                                  result,
                                  e,
                                  null,
                                  Msg.ERROR_CHECKING_ALLOWED_VALUES,
                                  getDependencyIdentifier(decision, dep),
                                  e.getMessage());
        }
        return toReturn;
    }

    private static String getIdentifier(DMNNode node) {
        return node.getName() != null ? node.getName() : node.getId();
    }

    private static String getDependencyIdentifier(DMNNode callerNode, DMNNode node) {
        if (node.getModelNamespace().equals(callerNode.getModelNamespace())) {
            return getIdentifier(node);
        } else {
            Optional<String> importAlias = callerNode.getModelImportAliasFor(node.getModelNamespace(),
                                                                             node.getModelName());
            String prefix = "{" + node.getModelNamespace() + "}";
            if (importAlias.isPresent()) {
                prefix = importAlias.get();
            }
            return prefix + "." + getIdentifier(node);
        }
    }

    public boolean performRuntimeTypeCheck(DMNModel model) {
        Objects.requireNonNull(model, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "model"));
        return overrideRuntimeTypeCheck || ((DMNModelImpl) model).isRuntimeTypeCheck();
    }

    public final <T extends DMNOption> void setOption(T option) {
        if (option instanceof RuntimeTypeCheckOption) {
            this.overrideRuntimeTypeCheck = ((RuntimeTypeCheckOption) option).isRuntimeTypeCheck();
        } else if (option instanceof RuntimeModeOption) {
            this.runtimeModeOption = ((RuntimeModeOption) option).getRuntimeMode();
        }
    }

    private void reportFailure(DMNDecisionResultImpl dr, DMNMessage message,
                               DMNDecisionResult.DecisionEvaluationStatus status) {
        dr.getMessages().add(message);
        dr.setEvaluationStatus(status);
    }

    @Override
    public DMNContext newContext() {
        return DMNFactory.newContext();
    }

    @Override
    public DMNRuntime getRuntime() {
        return this;
    }

    public List<DMNProfile> getProfiles() {
        return runtimeKB.getProfiles();
    }

    @Override
    public ClassLoader getRootClassLoader() {
        return runtimeKB.getRootClassLoader();
    }

    public InternalKnowledgeBase getInternalKnowledgeBase() {
        return runtimeKB.getInternalKnowledgeBase();
    }

    public DMNRuntimeKB getRuntimeKB() {
        return runtimeKB;
    }
}
