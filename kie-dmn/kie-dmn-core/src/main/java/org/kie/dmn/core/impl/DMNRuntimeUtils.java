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
import java.util.Collections;
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
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to support <code>DMNRuntimeImpl</code>
 */
public class DMNRuntimeUtils {

    private static final Logger logger = LoggerFactory.getLogger(DMNRuntimeUtils.class);

    private DMNRuntimeUtils() {
        // singleton
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
                                                                  DMNModelImpl.ModelImportTuple topmostModelTuple,
                                                                  DMNModelImpl model) {
        Object storedValue = context.get(topmostInputName); // This could be either a raw value, or a map
        if (storedValue instanceof Map storedMap && storedMap.containsKey(topmostInputName)) { // The check is needed to avoid looping reference
            return;
        }
        // Eventually, we need to create a map with the importing name and populate it with the input data
        replaceContextMap(context, topmostModelTuple, topmostInputName,
                          storedValue);
    }

    static void processTopmostModelInputDataNodeWithoutClashingNames(DMNContext context, String topmostInputName,
                                                                     DMNModelImpl.ModelImportTuple topmostModelTuple,
                                                                     DMNModelImpl model) {
        Object storedValue = context.get(topmostInputName);
        if (storedValue != null) {
            Object parentData = context.get(topmostModelTuple.getImportName());
            if (parentData instanceof Map mappedData) {
                addValueInsideMap(mappedData, topmostInputName, storedValue);
            } else if (parentData == null) {
                updateContextMap(context, model.getImportChainAliases(), topmostModelTuple, topmostInputName,
                                 storedValue);
            }
        }
    }

    static void replaceContextMap(DMNContext context,
                                  DMNModelImpl.ModelImportTuple topmostModelTuple, String inputName,
                                  Object storedValue) {
        Map<String, Object> mappedData = new HashMap<>();
        mappedData.put(inputName, storedValue);
        context.set(topmostModelTuple.getImportName(), mappedData);
    }

    static void updateContextMap(DMNContext context, Map<String, Collection<List<String>>> importChainAliases,
                                 DMNModelImpl.ModelImportTuple topmostModelTuple, String inputName,
                                 Object storedValue) {
        Map<String, Object> mappedData = new HashMap<>();
        mappedData.put(inputName, storedValue);
        populateContextWithInheritedData(context, mappedData,
                                         topmostModelTuple.getImportName(),
                                         topmostModelTuple.getModel().getNamespace(), importChainAliases);
    }

    /**
     * This method populate the given <code>DMNContext</code> with a new entry whose key is the import name and whose value is the provided <code>Map</code> <b>toStore</b>,
     * if those are related with the namespace of the topmost parent
     * @param toPopulate
     * @param toStore
     * @param importName
     * @param topmostNamespace
     * @param importChainAliases
     */
    static void populateContextWithInheritedData(DMNContext toPopulate, Map<String, Object> toStore,
                                                 String importName, String topmostNamespace, Map<String,
                    Collection<List<String>>> importChainAliases) {
        for (List<String> chainedModels : importChainAliases.get(topmostNamespace)) {
            // The order is: first one -> importing model; last one -> parent model
            for (String chainedModel : chainedModels) {
                if (chainedModel.equals(importName)) {
                    continue;
                }
                if (toStore.get(chainedModel) != null && toStore.get(chainedModel) instanceof Map alreadyMapped) {
                    addValueInsideMap(alreadyMapped, importName, toStore);
                } else {
                    addNewMapToContext(toPopulate, importName, toStore, chainedModel);
                }
            }
        }
    }

    /**
     * Create a new <code>Map</code> with the given <code>importName</code> value set to the given <code>Map</code> <b>toStore</b>.
     * Then, set this newly-created Map in the given <code>DMNContext</code> as <b>chainedModel</b>
     * @param toPopulate
     * @param importName
     * @param toStore
     * @param chainedModel
     */
    static void addNewMapToContext(DMNContext toPopulate, String importName, Map<String, Object> toStore, String chainedModel) {
        Map<String, Object> chainedMap = new HashMap<>();
        chainedMap.put(importName, toStore);
        toPopulate.set(chainedModel, chainedMap);
    }

    /**
     * Depending on how the context has been instantiated, the provided <code>Map</code> could be unmodifiable, which
     * is an expected condition
     * @param mappedData
     * @param inputName
     * @param storedValue
     */
    static void addValueInsideMap(Map<String, Object> mappedData, String inputName, Object storedValue) {
        try {
            mappedData.put(inputName, storedValue);
        } catch (Exception e) {
            logger.warn("Failed to put {} -> {} to map {} ", inputName, storedValue, mappedData, e);
        }
    }

    /**
     * Method to return the <b>topmost parents</b> of the given model.
     * There could be more then one, since a model may import multiple others.
     * It is a <code>Set</code> to avoid duplicating the same entry
     * @param model
     * @return
     */
    static Optional<Set<DMNModelImpl.ModelImportTuple>> getTopmostModel(DMNModelImpl model) {
        return model.getTopmostParents();
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
    static Object coerceSingleItemCollectionToValue(Object value, DMNType type) {
        if (!type.isCollection() && value instanceof Collection && ((Collection<?>) value).size() == 1) {
            // as per Decision evaluation result.
            return ((Collection<?>) value).toArray()[0];
        } else {
            return value;
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

    /**
     * If the given nodes have the same namespace, returns the <code>getIdentifier</code> of the <code>calledNode</code>
     * Otherwise, returns the <code>getPrefixedIdentifier</code> of the <code>calledNode</code>
     * @param callerNode
     * @param calledNode
     * @return
     */
    static String getDependencyIdentifier(DMNNode callerNode, DMNNode calledNode) {
        if (calledNode.getModelNamespace().equals(callerNode.getModelNamespace())) {
            return getIdentifier(calledNode);
        } else {
            return getPrefixedIdentifier(callerNode, calledNode);
        }
    }

    /**
     * Returns the <code>getIdentifier</code> of the <code>calledNode</code> prefixed.
     * If the <code>callerNode</code> has an import alias for the <code>calledNode</code>, the prefix is the import
     * alias, otherwise the prefix is the namespace of the <code>calledNode</code>
     * @param callerNode
     * @param calledNode
     * @return
     */
    static String getPrefixedIdentifier(DMNNode callerNode, DMNNode calledNode) {
        Optional<String> importAlias = callerNode.getModelImportAliasFor(calledNode.getModelNamespace(),
                                                                         calledNode.getModelName());
        String prefix = importAlias.orElse(String.format("{%s}", calledNode.getModelNamespace()));
        return prefix + "." + getIdentifier(calledNode);
    }

    /**
     * Returns the name of the node, if set, otherwise the id
     * @param node
     * @return
     */
    static String getIdentifier(DMNNode node) {
        return node.getName() != null ? node.getName() : node.getId();
    }
}
