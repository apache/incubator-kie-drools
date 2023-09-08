/**
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
package org.kie.api.builder.model;

import static java.util.EnumSet.allOf;

/**
 * ListenerModel is a model allowing to programmatically define a Listener and wire it to a KieSession
 */
public interface ListenerModel {

    public enum Kind {
        AGENDA_EVENT_LISTENER("agendaEventListener"),
        RULE_RUNTIME_EVENT_LISTENER("ruleRuntimeEventListener"),
        PROCESS_EVENT_LISTENER("processEventListener");

        private final String name;

        private Kind(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Kind fromString(String name) {
            for (Kind kind : allOf(Kind.class)) {
                if (kind.toString().equals(name)) {
                    return kind;
                }
            }
            return null;
        }
    }

    /**
     * Returns the type of this ListenerModel
     * (i.e. the name of the class implementing the listener)
     */
    String getType();

    /**
     * Returns the Kind of this ListenerModel
     */
    ListenerModel.Kind getKind();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
