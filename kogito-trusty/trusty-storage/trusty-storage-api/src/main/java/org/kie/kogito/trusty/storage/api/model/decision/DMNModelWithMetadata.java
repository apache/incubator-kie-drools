/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.trusty.storage.api.model.decision;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.trusty.storage.api.model.ModelWithMetadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class DMNModelWithMetadata extends ModelWithMetadata<DMNModelMetadata> {

    public static final String MODEL_FIELD = "model";

    @JsonProperty(MODEL_FIELD)
    private String model;

    public DMNModelWithMetadata() {
    }

    public DMNModelWithMetadata(String groupId, String artifactId, String modelVersion, String dmnVersion,
            String name, String namespace, String model) {
        this(new DMNModelMetadata(groupId, artifactId, modelVersion, dmnVersion, name, namespace), model);
    }

    public DMNModelWithMetadata(DMNModelMetadata dmnModelMetadata, String model) {
        super(dmnModelMetadata, ModelDomain.DECISION);
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDmnVersion() {
        return modelMetaData.getDmnVersion();
    }

    public String getName() {
        return modelMetaData.getName();
    }

    public String getNamespace() {
        return modelMetaData.getNamespace();
    }
}
