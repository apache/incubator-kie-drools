/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.decision.DecisionModelType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelEvent {

    private final GAV gav;

    private final String name;

    private final String namespace;

    private final String identifier;

    private final DecisionModelType type;

    private final String definition;

    public static class GAV {

        private String groupId;
        private String artifactId;
        private String version;

        @JsonCreator
        public GAV(final @JsonProperty("groupId") String groupId,
                   final @JsonProperty("artifactId") String artifactId,
                   final @JsonProperty("version") String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        public static GAV from(final org.kie.api.management.GAV gav) {
            return new GAV(gav.getGroupId(),
                           gav.getArtifactId(),
                           gav.getVersion());
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getVersion() {
            return version;
        }
    }

    @JsonCreator
    public ModelEvent(final @JsonProperty("gav") GAV gav,
                      final @JsonProperty("name") String name,
                      final @JsonProperty("namespace") String namespace,
                      final @JsonProperty("identifier") String identifier,
                      final @JsonProperty("type") DecisionModelType type,
                      final @JsonProperty("definition") String definition) {
        this.gav = gav;
        this.name = name;
        this.namespace = namespace;
        this.identifier = identifier;
        this.type = type;
        this.definition = definition;
    }

    public GAV getGav() {
        return gav;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getIdentifier() {
        return identifier;
    }

    public DecisionModelType getModelType() {
        return type;
    }

    public String getDefinition() {
        return definition;
    }
}
