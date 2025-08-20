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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.util.MsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;

/**
 * Utility class to support <code>DMNRuntimeImpl</code>
 */
public class DMNRuntimeUtils {

    private static final Logger logger = LoggerFactory.getLogger(DMNRuntimeUtils.class);


    private DMNRuntimeUtils() {
        // singleton
    }

    static void populateResultContextWithTopmostParentsValues(DMNContext context, DMNModelImpl model) {
        Optional<Set<DMNModelImpl.ModelImportTuple>> optionalTopmostModels = getTopmostModel(model);
        optionalTopmostModels.ifPresent(topmostModels -> populateInputsFromTopmostModel(context, model, topmostModels));
    }

    static void populateInputsFromTopmostModel(DMNContext context, DMNModelImpl model,
                                               Set<DMNModelImpl.ModelImportTuple> topmostModels) {
        for (DMNModelImpl.ModelImportTuple topmostModelTuple : topmostModels) {
            processTopmostModelTuple(context, topmostModelTuple, model);
        }
    }

    static void processTopmostModelTuple(DMNContext context, DMNModelImpl.ModelImportTuple topmostModelTuple,
                                         DMNModelImpl model) {
        DMNModelImpl topmostModel = topmostModelTuple.getModel();
        for (InputDataNode topmostInput : topmostModel.getInputs()) {
            processTopmostModelInputDataNode(context, topmostInput.getName(), topmostModelTuple, model);
        }
    }

    static void processTopmostModelInputDataNode(DMNContext context, String topmostInputName,
                                                 DMNModelImpl.ModelImportTuple topmostModelTuple, DMNModelImpl model) {
        if (Objects.equals(topmostInputName, topmostModelTuple.getImportName())) {
            processTopmostModelInputDataNodeWithClashingNames(context, topmostInputName,
                                                               topmostModelTuple, model);
        } else {
            processTopmostModelInputDataNodeWithoutClashingNames(context, topmostInputName,
                                                              topmostModelTuple, model);
        }
    }

    static void processTopmostModelInputDataNodeWithClashingNames(DMNContext context, String topmostInputName,
                                                 DMNModelImpl.ModelImportTuple topmostModelTuple, DMNModelImpl model) {
        Object storedValue = context.get(topmostInputName); // This could be either a raw value, or a map
        if (storedValue instanceof Map storedMap && storedMap.containsKey(topmostInputName)) { // This is tricky/error prone, since an input data could also be defined as map
            return;
        }
        // If it is not a Map, we need to create a map with the importing name and populate it with the input data
        replaceContextMap(context, topmostModelTuple, topmostInputName,
                         storedValue);
    }

    static void processTopmostModelInputDataNodeWithoutClashingNames(DMNContext context, String topmostInputName,
                                                                  DMNModelImpl.ModelImportTuple topmostModelTuple, DMNModelImpl model) {
        Object storedValue = context.get(topmostInputName);
        if (storedValue != null) {
            Object parentData = context.get(topmostModelTuple.getImportName());
            if (parentData instanceof Map mappedData) {
                addValueInsideTopmostModelMap(mappedData, topmostInputName, storedValue, parentData);
            } else if (parentData == null) {
                updateContextMap(context, model.getImportChainAliases(), topmostModelTuple, topmostInputName,
                                 storedValue);
            }
        }
    }


    /**
     * Depending on how the context has been instantiated, the provided <code>Map</code> could be unmodifiable, which
     * is an expected condition
     * @param mappedData
     * @param inputName
     * @param storedValue
     * @param parentData
     */
    static void addValueInsideTopmostModelMap(Map mappedData, String inputName, Object storedValue, Object parentData) {
        try {
            mappedData.put(inputName, storedValue);
        } catch (Exception e) {
            logger.warn("Failed to add {} to map {} ", storedValue, parentData, e);
        }
    }

    static void replaceContextMap(DMNContext context,
                                 DMNModelImpl.ModelImportTuple topmostModelTuple, String inputName,
                                 Object storedValue) {
        Map mappedData = new HashMap<>();
        mappedData.put(inputName, storedValue);
        context.set(topmostModelTuple.getImportName(), mappedData);
    }

    static void updateContextMap(DMNContext context, Map<String, Collection<List<String>>> importChainAliases,
                                 DMNModelImpl.ModelImportTuple topmostModelTuple, String inputName,
                                 Object storedValue) {
        Map mappedData = new HashMap<>();
        mappedData.put(inputName, storedValue);
        populateContextWithInheritedData(context, mappedData,
                                         topmostModelTuple.getImportName(),
                                         topmostModelTuple.getModel().getNamespace(), importChainAliases);
    }

    static void populateContextWithInheritedData(DMNContext toPopulate, Map<String, Object> toStore,
                                                 String importName, String topmostNamespace, Map<String,
                    Collection<List<String>>> importChainAliases) {
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
        } else if (isInUnnamedImport(calledNode, (DMNModelImpl) result.getModel())) {
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

    /**
     * Method used to catch StackOverflowError and allow nice handling of them
     * @return
     */
    static String getObjectString(Object toPrint) {
        try {
            return MsgUtil.clipString(Objects.toString(toPrint), 50);
        } catch (StackOverflowError e) {
            logger.error("Stack overflow error while trying to String {}", toPrint.getClass());
            return "(_undefined_)";
        }
    }

    static String getIdentifier(DMNNode node) {
        return node.getName() != null ? node.getName() : node.getId();
    }

    static String getDependencyIdentifier(DMNNode callerNode, DMNNode node) {
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

}
