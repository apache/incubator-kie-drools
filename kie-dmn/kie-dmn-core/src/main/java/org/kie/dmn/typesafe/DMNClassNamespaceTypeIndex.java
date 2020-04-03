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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

public class DMNClassNamespaceTypeIndex {

    private final List<DMNModel> allModels;

    private final List<DMNType> typesToGenerate = new ArrayList<>();

    Map<String, String> mapNamespaceIndex = new HashMap<>();

    public DMNClassNamespaceTypeIndex(DMNModel... allModels) {
        this.allModels = Arrays.asList(allModels);
        for (DMNModel m : allModels) {
            mapNamespaceIndex.putAll(indexFromModel(m));
        }
    }

    public DMNClassNamespaceTypeIndex(List<DMNModel> allModels) {
        this(allModels.toArray(new DMNModel[0]));
    }

    public Map<String, String> indexFromModel(DMNModel dmnModel) {
        Map<String, String> classesNamespaceIndex = new HashMap<>();

        Set<ItemDefNode> itemDefinitions = dmnModel.getItemDefinitions();

        String namespace = namespace(dmnModel);

        for (ItemDefNode i : itemDefinitions) {
            DMNType type = i.getType();
            classesNamespaceIndex.put(type.getName(), namespace);
            typesToGenerate.add(type);
            if (type.isComposite()) {
                for (DMNType innerType : type.getFields().values()) {
//                    classesNamespaceIndex.put(innerType.getName(), namespace);
//                    typesToGenerate.add(innerType);
                }
            }
        }
        return classesNamespaceIndex;
    }

    public static String namespace(DMNModel dmnModel) {
        return CodegenStringUtil.escapeIdentifier(dmnModel.getNamespace() + dmnModel.getName());
    }

    public List<DMNType> allTypesToGenerate() {
        return typesToGenerate;
    }

    public Optional<String> namespaceOfClass(String typeName) {
        return Optional.ofNullable(mapNamespaceIndex.get(typeName));
    }
}
