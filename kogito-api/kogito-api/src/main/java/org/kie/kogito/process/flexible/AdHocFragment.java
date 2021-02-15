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
package org.kie.kogito.process.flexible;

import java.io.Serializable;

import org.kie.api.definition.process.Node;

public class AdHocFragment implements Serializable {

    private final String type;
    private final String name;
    private final boolean autoStart;

    public AdHocFragment(String type, String name, boolean autoStart) {
        this.type = type;
        this.name = name;
        this.autoStart = autoStart;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    @Override
    public String toString() {
        return "AdHocFragment{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", autoStart=" + autoStart +
                '}';
    }

    public static class Builder {
        private String type;
        private String name;
        private boolean autoStart;

        public Builder(Class<? extends Node> clazz) {
            this.type = clazz.getSimpleName();
        }

        public Builder(String type) {
            this.type = type;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAutoStart(boolean autoStart) {
            this.autoStart = autoStart;
            return this;
        }

        public AdHocFragment build() {
            return new AdHocFragment(type, name, autoStart);
        }
    }
}
