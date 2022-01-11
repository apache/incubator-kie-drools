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
package org.kie.kogito.decision;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.event.ModelMetadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class DecisionModelMetadata extends ModelMetadata {

    @JsonProperty("specVersion")
    private String specVersion;

    public DecisionModelMetadata() {
        super(ModelDomain.DECISION);
    }

    public DecisionModelMetadata(String specVersion) {
        super(ModelDomain.DECISION);
        this.specVersion = specVersion;
    }

    public String getSpecVersion() {
        return specVersion;
    }

}
