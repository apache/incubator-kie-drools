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
package org.kie.dmn.core.compiler;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.*;

/**
 * Class meant to provide helper methods to deal with unnamed model imports
 */
public class UnnamedImportUtils {

    private UnnamedImportUtils() {
    }

    public static boolean isInUnnamedImport(DMNNode node, DMNModelImpl model) {
        for (Import imported : model.getDefinitions().getImport()) {
            String importedName = imported.getName();
            if ((node.getModelNamespace().equals(imported.getNamespace()) &&
                    (importedName != null && importedName.isEmpty()))) {
                return true;
            }
        }
        return false;
    }

    public static void processMergedModel(DMNModelImpl importingModel, DMNModelImpl importedModel) {
        // incubator-kie-issues#852: The idea is to not treat the anonymous models as import, but to "merge" them with original opne,
        // Here we try to put all the definitions from the "imported" model inside the parent one
        Definitions importingDefinitions = importingModel.getDefinitions();
        Definitions importedDefinitions = importedModel.getDefinitions();

        mergeDefinitions(importingDefinitions, importedDefinitions);

        importedModel.getTypeRegistry().getTypes().forEach((s, stringDMNTypeMap) ->
                                                                 stringDMNTypeMap.values().
                                                                         forEach(dmnType -> importingModel.getTypeRegistry().registerType(dmnType)));
        importedModel.getDecisions().forEach(importingModel::addDecision);
        importedModel.getInputs().forEach(importingModel::addInput);
        importedModel.getImportAliasesForNS().forEach((s, qName) -> importingModel.setImportAliasForNS(s, qName.getNamespaceURI(), qName.getLocalPart()));
    }

    public static void mergeDefinitions(Definitions importingDefinitions, Definitions importedDefinitions) {
        // incubator-kie-issues#852: The idea is to not treat the anonymous models as import, but to "merge" them with original one,
        // Here we try to put all the definitions from the "imported" model inside the parent one
        importingDefinitions.getArtifact().addAll(importedDefinitions.getArtifact());
        addIfNotPresent(importingDefinitions.getDecisionService(), importedDefinitions.getDecisionService(), DecisionService.class);
        addIfNotPresent(importingDefinitions.getBusinessContextElement(), importedDefinitions.getBusinessContextElement(), BusinessContextElement.class);
        addIfNotPresent(importingDefinitions.getDrgElement(), importedDefinitions.getDrgElement(), DRGElement.class);
        addIfNotPresent(importingDefinitions.getImport(), importedDefinitions.getImport(), Import.class);
        addIfNotPresent(importingDefinitions.getItemDefinition(), importedDefinitions.getItemDefinition(), ItemDefinition.class);
        importedDefinitions.getChildren().forEach(importingDefinitions::addChildren);
    }

    static <T extends NamedElement> void addIfNotPresent(Collection<T> target, Collection<T> source, Class expectedClass) {
        source.forEach(sourceElement -> {
            if(!expectedClass.isAssignableFrom(sourceElement.getClass())) {
                throw new IllegalStateException("type mismatch : " + "Expected " + expectedClass.getName() + ", but found " + sourceElement.getClass().getName());
            }
            addIfNotPresent(target, sourceElement, expectedClass);
        });

    }

    static <T extends NamedElement> void addIfNotPresent(Collection<T> target, T source, Class expectedClass) {
        if (checkIfNotPresent(target, source, expectedClass)) {
            target.add(source);
        }
    }

    static <T extends NamedElement> boolean checkIfNotPresent(Collection<T> target, T source, Class expectedClass) {
        for (T namedElement : target) {
            if(!expectedClass.isAssignableFrom(namedElement.getClass()) ) {
                throw new IllegalStateException("type mismatch : " + "Expected " + expectedClass.getName() + ", but found " + namedElement.getClass().getName());
            }
            if (Objects.equals(namedElement.getName(), source.getName())) {
                if (!(namedElement instanceof Import &&
                        namedElement.getName() != null &&
                        namedElement.getName().isEmpty())) {
                    return false;
                }
            }
        }
        return true;
    }


}