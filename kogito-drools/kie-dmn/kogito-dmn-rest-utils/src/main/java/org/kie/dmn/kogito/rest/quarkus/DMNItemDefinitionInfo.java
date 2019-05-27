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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.bind.annotation.JsonbProperty;

import org.kie.dmn.model.api.ItemDefinition;

public class DMNItemDefinitionInfo implements Serializable {

    private String id;
    private String name;
    private String typeRef;
    private DMNUnaryTestsInfo allowedValues;
    private List<DMNItemDefinitionInfo> itemComponent = new ArrayList<>();
    private String typeLanguage;
    private Boolean isCollection;

    public DMNItemDefinitionInfo() {
        // Intentionally blank.
    }

    public static DMNItemDefinitionInfo of(ItemDefinition itemDef) {
        DMNItemDefinitionInfo res = new DMNItemDefinitionInfo();
        res.setId(itemDef.getId());
        res.setName(itemDef.getName());
        if (itemDef.getTypeRef() != null) {
            res.setTypeRef(itemDef.getTypeRef().getLocalPart());
        }
        if (itemDef.getAllowedValues() != null) {
            DMNUnaryTestsInfo av = new DMNUnaryTestsInfo();
            av.setText(itemDef.getAllowedValues().getText());
            av.setExpressionLanguage(itemDef.getAllowedValues().getExpressionLanguage());
            res.setAllowedValues(av);
        }
        if (itemDef.getItemComponent() != null && !itemDef.getItemComponent().isEmpty()) {
            List<DMNItemDefinitionInfo> components = itemDef.getItemComponent().stream().map(DMNItemDefinitionInfo::of).collect(Collectors.toList());
            res.setItemComponent(components);
        }
        res.setTypeLanguage(itemDef.getTypeLanguage());
        res.setIsCollection(itemDef.isIsCollection());
        return res;
    }

    @JsonbProperty("itemdefinition-id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonbProperty("itemdefinition-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonbProperty("itemdefinition-typeRef")
    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    @JsonbProperty("itemdefinition-allowedValues")
    public DMNUnaryTestsInfo getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(DMNUnaryTestsInfo allowedValues) {
        this.allowedValues = allowedValues;
    }

    @JsonbProperty("itemdefinition-itemComponent")
    public List<DMNItemDefinitionInfo> getItemComponent() {
        return itemComponent;
    }

    public void setItemComponent(List<DMNItemDefinitionInfo> itemComponent) {
        this.itemComponent = itemComponent;
    }

    @JsonbProperty("itemdefinition-typeLanguage")
    public String getTypeLanguage() {
        return typeLanguage;
    }

    public void setTypeLanguage(String typeLanguage) {
        this.typeLanguage = typeLanguage;
    }

    @JsonbProperty("itemdefinition-isCollection")
    public Boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Boolean isCollection) {
        this.isCollection = isCollection;
    }
}
