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
import java.util.Optional;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;

public class DMNAllTypesIndex {

    private final List<DMNType> typesToGenerate = new ArrayList<>();

    Map<String, DMNTypeSafePackageName> mapNamespaceIndex = new HashMap<>();

    public DMNAllTypesIndex(DMNTypeSafePackageName packageName, DMNModel... allModels) {
        for (DMNModel m : allModels) {
            DMNModelTypesIndex indexFromModel = new DMNModelTypesIndex(m, packageName);
            mapNamespaceIndex.putAll(indexFromModel.getClassesNamespaceIndex());
            allTypesToGenerate().addAll(indexFromModel.getTypesToGenerate());
        }
    }

    public DMNAllTypesIndex(List<DMNModel> allModels, DMNTypeSafePackageName packageName) {
        this(packageName, allModels.toArray(new DMNModel[0]));
    }

    public List<DMNType> allTypesToGenerate() {
        return typesToGenerate;
    }

    public Optional<DMNTypeSafePackageName> namespaceOfClass(String typeName) {
        return Optional.ofNullable(mapNamespaceIndex.get(typeName));
    }
}
