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

package org.kie.kogito.dmn.rest;

import java.io.Serializable;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.model.api.InputData;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DMNInputDataInfo implements Serializable {

    private String id;
    private String name;
    private String typeRef;

    public DMNInputDataInfo() {
        // Intentionally blank.
    }

    public static DMNInputDataInfo of(InputDataNode inputDataNode) {
        DMNInputDataInfo res = new DMNInputDataInfo();
        res.setName(inputDataNode.getName());
        res.setId(inputDataNode.getId());
        InputData id = ((InputDataNodeImpl) inputDataNode).getInputData();
        QName typeRef = id.getVariable().getTypeRef();
        // for InputData sometimes the NS is not really valorized inside the jdk QName as internally ns are resolved by prefix directly.
        if (typeRef != null && XMLConstants.NULL_NS_URI.equals(typeRef.getNamespaceURI())) {
            String actualNS = id.getNamespaceURI(typeRef.getPrefix());
            typeRef = new QName(actualNS, typeRef.getLocalPart(), typeRef.getPrefix());
        }
        if (typeRef != null) {
            res.setTypeRef(typeRef.getLocalPart());
        }
        return res;
    }

    @JsonProperty("inputdata-id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("inputdata-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("inputdata-typeRef")
    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }
}
