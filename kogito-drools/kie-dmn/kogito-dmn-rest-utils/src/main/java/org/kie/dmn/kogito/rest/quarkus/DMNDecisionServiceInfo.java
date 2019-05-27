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

import javax.json.bind.annotation.JsonbProperty;

import org.kie.dmn.api.core.ast.DecisionServiceNode;

public class DMNDecisionServiceInfo implements Serializable {

    private String id;
    private String name;

    public DMNDecisionServiceInfo() {
        // Intentionally blank.
    }

    public static DMNDecisionServiceInfo of(DecisionServiceNode dsNode) {
        DMNDecisionServiceInfo res = new DMNDecisionServiceInfo();
        res.setName(dsNode.getName());
        res.setId(dsNode.getId());
        return res;
    }

    @JsonbProperty("decision-service-id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonbProperty("decision-service-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
