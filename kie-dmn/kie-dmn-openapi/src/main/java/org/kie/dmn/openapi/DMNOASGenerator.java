/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.openapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.openapi.model.DMNModelIOSets;
import org.kie.dmn.typesafe.DMNTypeUtils;

public class DMNOASGenerator {

    private final Set<DMNModel> dmnModels;
    private final List<DMNModelIOSets> ioSets = new ArrayList<>();
    private final Set<DMNType> typesIndex = new HashSet<>();
    private TempNamingPolicy namingPolicy;

    public DMNOASGenerator(Collection<DMNModel> models) {
        this.dmnModels = new HashSet<>(models);
    }

    public void build() {
        for (DMNModel model : dmnModels) {
            DMNModelIOSets s = new DMNModelIOSets(model);
            ioSets.add(s);
            visitForIndexing(s.getOutputSet());
            visitForIndexing(s.getInputSet());
        }
        assignNamesToIOSets();
        determineNamingPolicy();
        System.out.println("finished");
    }

    private void determineNamingPolicy() {
        this.namingPolicy = new TempNamingPolicy();
    }

    private void assignNamesToIOSets() {
        for (int i = 0; i < ioSets.size(); i++) {
            DMNModelIOSets si = ioSets.get(i);
            si.setInputSetName(findUniqueNameUsing("InputSet", i + 1));
            si.setOutputSetName(findUniqueNameUsing("OutputSet", i + 1));
        }
    }

    private String findUniqueNameUsing(String radix, int suffixH) {
        String candidate = dmnModels.size() > 1 ? radix + suffixH : radix;
        while (indexContainsName(candidate)) {
            candidate = "_" + candidate;
        }
        return candidate;
    }

    private boolean indexContainsName(String candidate) {
        return typesIndex.stream().map(DMNType::getName).anyMatch(candidate::equals);
    }

    private void visitForIndexing(DMNType idnType) {
        if (typesIndex.contains(idnType)) {
            return; // no need to re-visit and already indexed type.
        }
        if (DMNTypeUtils.isFEELBuiltInType(idnType)) {
            return; // not indexing FEEL built-in types.
        }
        typesIndex.add(idnType);
        if (idnType instanceof CompositeTypeImpl) {
            CompositeTypeImpl compType = (CompositeTypeImpl) idnType;
            for (DMNType v : compType.getFields().values()) {
                visitForIndexing(v);
            }
        }
    }

}
