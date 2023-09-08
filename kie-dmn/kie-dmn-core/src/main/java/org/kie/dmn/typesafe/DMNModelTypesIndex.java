/**
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

class DMNModelTypesIndex {

    Map<IndexKey, IndexValue> classesNamespaceIndex = new HashMap<>();
    private final List<DMNType> typesToGenerate = new ArrayList<>();
    private DMNModel model;
    private final DMNTypeSafePackageName.Factory packageName;

    public DMNModelTypesIndex(DMNModel model, DMNTypeSafePackageName.Factory dmnTypeSafePackageName) {
        this.model = model;
        this.packageName = dmnTypeSafePackageName;

        createIndex();
    }

    public void createIndex() {
        List<DMNType> itemDefinitions = model.getItemDefinitions()
                .stream()
                .map(ItemDefNode::getType)
                .filter(this::shouldIndex)
                .collect(Collectors.toList());

        itemDefinitions.forEach(this::index);
        itemDefinitions.stream().flatMap(this::innerTypes).forEach(this::index);
    }

    private Stream<DMNType> innerTypes(DMNType type) {
        if (type.isComposite()) {
            return type.getFields().values().stream().filter(DMNTypeUtils::isInnerComposite).map(t -> t.isCollection() ? DMNTypeUtils.genericOfCollection(t) : t);
        } else {
            return Stream.empty();
        }
    }

    private boolean shouldIndex(DMNType dmnType) {
        // Don't index a collection type of a declared type. e.g. tPersonList which is a collection of tPerson
        return !dmnType.getNamespace().equals(model.getDefinitions().getURIFEEL()) && dmnType.isComposite() && !(dmnType.isCollection() && dmnType.getBaseType() != null);
    }

    private void index(DMNType innerType) {
        classesNamespaceIndex.put(IndexKey.from(innerType), new IndexValue(packageName.create(model)));
        typesToGenerate.add(innerType);
    }

    public Map<IndexKey, IndexValue> getIndex() {
        return classesNamespaceIndex;
    }

    public List<DMNType> getTypesToGenerate() {
        return typesToGenerate;
    }

    static class IndexValue {
        final DMNTypeSafePackageName packageName;

        public IndexValue(DMNTypeSafePackageName packageName) {
            this.packageName = packageName;
        }

        public DMNTypeSafePackageName getPackageName() {
            return packageName;
        }
    }
}