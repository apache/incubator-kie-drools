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

package org.kie.dmn.openapi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.openapi.DMNOASGenerator;
import org.kie.dmn.openapi.NamingPolicy;
import org.kie.dmn.openapi.model.DMNModelIOSets;
import org.kie.dmn.openapi.model.DMNModelIOSets.DSIOSets;
import org.kie.dmn.openapi.model.DMNOASResult;
import org.kie.dmn.typesafe.DMNTypeUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.runtime.io.JsonUtil;
import io.smallrye.openapi.runtime.io.schema.SchemaWriter;

public class DMNOASGeneratorImpl implements DMNOASGenerator {

    private final List<DMNModel> dmnModels;
    private final List<DMNModelIOSets> ioSets = new ArrayList<>();
    private final Set<DMNType> typesIndex = new HashSet<>();
    private final String refPrefix;
    private NamingPolicy namingPolicy;
    private final Map<DMNType, Schema> schemas = new HashMap<>();
    private ObjectNode jsonSchema;

    public DMNOASGeneratorImpl(Collection<DMNModel> models, String refPrefix) {
        this.dmnModels = new ArrayList<>(models);
        this.refPrefix = refPrefix;
    }

    @Override
    public DMNOASResult build() {
        indexModels();
        assignNamesToIOSets();
        determineNamingPolicy();
        schemas.putAll(new DMNTypeSchemas(ioSets, typesIndex, namingPolicy).generateSchemas());
        prepareSerializaton();
        return new DMNOASResult(jsonSchema, ioSets, schemas, namingPolicy);
    }

    private void indexModels() {
        for (DMNModel model : dmnModels) {
            DMNModelIOSets s = new DMNModelIOSets(model);
            ioSets.add(s);
            visitForIndexing(s);
        }
    }

    private void prepareSerializaton() {
        ObjectNode tree = JsonUtil.objectNode();
        ObjectNode definitions = JsonUtil.objectNode();
        tree.set("definitions", definitions);
        for (Entry<DMNType, Schema> kv : schemas.entrySet()) {
            SchemaWriter.writeSchema(definitions, kv.getValue(), namingPolicy.getName(kv.getKey()));
        }
        jsonSchema = tree;
    }

    private void determineNamingPolicy() {
        this.namingPolicy = new DefaultNamingPolicy(refPrefix);
        if (namingIntegrityCheck()) {
            return;
        }
        this.namingPolicy = new NamespaceAwareNamingPolicy(dmnModels, refPrefix);
        if (namingIntegrityCheck()) {
            return;
        }
        reportCollisions();
    }

    private void reportCollisions() {
        List<String> messages = new ArrayList<>();
        Map<String, Long> countByName = typesIndex.stream().collect(Collectors.groupingBy(namingPolicy::getName, Collectors.counting()));
        for (Entry<String, Long> kv : countByName.entrySet()) {
            if (kv.getValue() > 1) {
                List<DMNType> collidingOverName = typesIndex.stream().filter(t -> namingPolicy.getName(t).equals(kv.getKey())).collect(Collectors.toList());
                List<DMNModel> fromModels = dmnModels.stream().filter(m -> m.getItemDefinitions().stream().anyMatch(itemDef -> collidingOverName.contains(itemDef.getType()))).collect(Collectors.toList());
                String modelsCoords = fromModels.stream().map(m -> String.format("Model '%s' (namespace '%s')", m.getName(), m.getNamespace())).collect(Collectors.joining(", "));
                String message = String.format("Naming collision for types named: %s defined in the DMN models: %s (colliding on '%s')", collidingOverName, modelsCoords, kv.getKey());
                messages.add(message);
            }
        }
        throw new IllegalStateException("Couldn't determine unique naming policy.\nEnsure all DMN models are defined in their own namespace.\n" + messages.stream().collect(Collectors.joining("\n")));
    }

    private boolean namingIntegrityCheck() {
        return typesIndex.size() == typesIndex.stream().map(namingPolicy::getName).distinct().count();
    }

    private void assignNamesToIOSets() {
        for (int i = 0; i < ioSets.size(); i++) {
            DMNModelIOSets si = ioSets.get(i);
            si.setInputSetName(findUniqueNameUsing("InputSet", i + 1));
            si.setOutputSetName(findUniqueNameUsing("OutputSet", i + 1));
            int j = 1;
            for (DSIOSets ds : si.getDSIOSets()) {
                ds.setDSInputSetName(findUniqueNameUsing("InputSetDS" + j, i + 1));
                ds.setDSOutputSetName(findUniqueNameUsing("OutputSetDS" + j, i + 1));
                j++;
            }
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

    private void visitForIndexing(DMNModelIOSets s) {
        visitForIndexing(s.getOutputSet());
        visitForIndexing(s.getInputSet());
        for (DSIOSets ds : s.getDSIOSets()) {
            visitForIndexing(ds.getDSOutputSet());
            visitForIndexing(ds.getDSInputSet());
        }
    }

    private void visitForIndexing(DMNType idnType) {
        if (typesIndex.contains(idnType)) {
            return; // no need to re-visit and already indexed type.
        }
        if (DMNTypeUtils.isFEELBuiltInType(idnType)) {
            return; // not indexing FEEL built-in types.
        }
        typesIndex.add(idnType);
        if (idnType.getBaseType() != null) {
            visitForIndexing(idnType.getBaseType());
        }
        if (idnType instanceof CompositeTypeImpl) {
            CompositeTypeImpl compType = (CompositeTypeImpl) idnType;
            for (DMNType v : compType.getFields().values()) {
                visitForIndexing(v);
            }
        }
    }

}
