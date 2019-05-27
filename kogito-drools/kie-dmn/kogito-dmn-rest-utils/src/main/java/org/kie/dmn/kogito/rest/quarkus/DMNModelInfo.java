/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.kogito.rest.quarkus;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.json.bind.annotation.JsonbProperty;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.ast.ItemDefNodeImpl;

public class DMNModelInfo implements Serializable {

    private String namespace;
    private String name;
    private String id;
    private Collection<DMNDecisionInfo> decisions = new HashSet<>();
    private Collection<DMNInputDataInfo> inputs = new HashSet<>();
    private Collection<DMNItemDefinitionInfo> itemDefinitions = new HashSet<>();
    private Collection<DMNDecisionServiceInfo> decisionServices = new HashSet<>();

    public DMNModelInfo() {
        // Intentionally blank.
    }

    public static DMNModelInfo of(DMNModel model) {
        DMNModelInfo res = new DMNModelInfo();
        res.setNamespace(model.getNamespace());
        res.setName(model.getName());
        res.setId(model.getDefinitions().getId());
        res.setDecisions(model.getDecisions().stream().map(DMNDecisionInfo::of).collect(Collectors.toSet()));
        res.setDecisionServices(model.getDecisionServices().stream().map(DMNDecisionServiceInfo::of).collect(Collectors.toSet()));
        res.setInputs(model.getInputs().stream().map(DMNInputDataInfo::of).collect(Collectors.toSet()));
        res.setItemDefinitions(model.getItemDefinitions().stream().map(id -> DMNItemDefinitionInfo.of(((ItemDefNodeImpl) id).getItemDef())).collect(Collectors.toSet()));
        return res;
    }

    @JsonbProperty("model-namespace")
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonbProperty("model-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonbProperty("model-id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonbProperty("decisions")
    public Collection<DMNDecisionInfo> getDecisions() {
        return decisions;
    }

    public void setDecisions(Collection<DMNDecisionInfo> decisions) {
        this.decisions = decisions;
    }

    @JsonbProperty("inputs")
    public Collection<DMNInputDataInfo> getInputs() {
        return inputs;
    }

    public void setInputs(Collection<DMNInputDataInfo> inputs) {
        this.inputs = inputs;
    }

    @JsonbProperty("itemDefinitions")
    public Collection<DMNItemDefinitionInfo> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(Collection<DMNItemDefinitionInfo> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }

    @JsonbProperty("decisionServices")
    public Collection<DMNDecisionServiceInfo> getDecisionServices() {
        return decisionServices;
    }

    public void setDecisionServices(Collection<DMNDecisionServiceInfo> decisionServices) {
        this.decisionServices = decisionServices;
    }
}
