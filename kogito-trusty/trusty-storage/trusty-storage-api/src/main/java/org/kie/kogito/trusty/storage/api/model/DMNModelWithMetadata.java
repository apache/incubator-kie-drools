/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.storage.api.model;

import org.kie.kogito.tracing.decision.event.model.ModelEvent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMNModelWithMetadata {

    public static final String GROUP_ID_FIELD = "groupId";
    public static final String ARTIFACT_ID_FIELD = "artifactId";
    public static final String MODEL_VERSION_FIELD = "modelVersion";
    public static final String DMN_VERSION_FIELD = "dmnVersion";
    public static final String NAME_FIELD = "name";
    public static final String NAMESPACE_FIELD = "namespace";
    public static final String MODEL_FIELD = "model";

    @JsonProperty(GROUP_ID_FIELD)
    private String groupId;

    @JsonProperty(ARTIFACT_ID_FIELD)
    private String artifactId;

    @JsonProperty(MODEL_VERSION_FIELD)
    private String modelVersion;

    @JsonProperty(DMN_VERSION_FIELD)
    private String dmnVersion;

    @JsonProperty(NAME_FIELD)
    private String name;

    @JsonProperty(NAMESPACE_FIELD)
    private String namespace;

    @JsonProperty(MODEL_FIELD)
    private String model;

    public DMNModelWithMetadata() {
    }

    public DMNModelWithMetadata(String groupId, String artifactId, String modelVersion, String dmnVersion, String name, String namespace, String model) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.modelVersion = modelVersion;
        this.dmnVersion = dmnVersion;
        this.name = name;
        this.namespace = namespace;
        this.model = model;
    }

    public static DMNModelWithMetadata fromCloudEvent(ModelEvent modelEvent) {
        return new DMNModelWithMetadata(modelEvent.getGav().getGroupId(),
                modelEvent.getGav().getArtifactId(),
                modelEvent.getGav().getVersion(),
                modelEvent.getDecisionModelMetadata().getSpecVersion(),
                modelEvent.getName(),
                modelEvent.getNamespace(),
                modelEvent.getDefinition());
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getDmnVersion() {
        return dmnVersion;
    }

    public void setDmnVersion(String dmnVersion) {
        this.dmnVersion = dmnVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
