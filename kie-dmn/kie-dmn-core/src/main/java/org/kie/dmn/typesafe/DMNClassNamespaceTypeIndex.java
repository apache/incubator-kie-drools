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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

public class DMNClassNamespaceTypeIndex {
    private final List<DMNModel> allModels;

    public DMNClassNamespaceTypeIndex(List<DMNModel> allModels) {
        this.allModels = allModels;
    }

    public Map<String, String> completeIndex() {
        Map<String, String> mapNamespaceIndex = new HashMap<>();
        for(DMNModel m : allModels) {
            mapNamespaceIndex.putAll(indexFromModel(m));
        }
        return mapNamespaceIndex;
    }

    public Map<String, String> indexFromModel(DMNModel dmnModel) {
        Map<String, String> classesNamespaceIndex = new HashMap<>();
        Set<ItemDefNode> itemDefinitions = dmnModel.getItemDefinitions();
        String namespace = namespace(dmnModel);
        for (ItemDefNode i : itemDefinitions) {
            DMNType type = i.getType();
            classesNamespaceIndex.put(type.getName(), namespace);
            if (type.isComposite()) {
                for (DMNType innerType : type.getFields().values()) {
                    classesNamespaceIndex.put(innerType.getName(), namespace);
                }
            }
        }
        return classesNamespaceIndex;
    }

    public static String namespace(DMNModel dmnModel) {
        return CodegenStringUtil.escapeIdentifier(dmnModel.getNamespace() + dmnModel.getName());
    }

}
