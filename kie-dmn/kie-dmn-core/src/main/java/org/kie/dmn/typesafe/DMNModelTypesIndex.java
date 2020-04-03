/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.typesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;

import static org.kie.dmn.typesafe.DMNAllTypesIndex.namespace;

class DMNModelTypesIndex {

    Map<String, String> classesNamespaceIndex = new HashMap<>();
    private final List<DMNType> typesToGenerate = new ArrayList<>();
    private DMNModel model;
    private final String namespace;

    public DMNModelTypesIndex(DMNModel model) {
        this.model = model;
        this.namespace = namespace(model);

        createIndex();
    }

    public void createIndex() {
        List<DMNType> itemDefinitions = model.getItemDefinitions()
                .stream().map(ItemDefNode::getType).collect(Collectors.toList());

        itemDefinitions.forEach(this::index);
        itemDefinitions.stream().flatMap(this::innerTypes).forEach(this::index);
    }

    private Stream<DMNType> innerTypes(DMNType type) {
        if (type.isComposite()) {
            return type.getFields().values().stream().filter(this::shouldIndex);
        } else {
            return Stream.empty();
        }
    }

    private boolean shouldIndex(DMNType dmnType) {
        if (dmnType.isCollection() || !dmnType.isComposite()) {
            return false;
        }
        String internalFEELUri = model.getDefinitions().getURIFEEL();
        return dmnType.getNamespace().equals(internalFEELUri);
    }

    private void index(DMNType innerType) {
        classesNamespaceIndex.put(innerType.getName(), namespace);
        typesToGenerate.add(innerType);
    }

    public Map<String, String> getClassesNamespaceIndex() {
        return classesNamespaceIndex;
    }

    public List<DMNType> getTypesToGenerate() {
        return typesToGenerate;
    }
}