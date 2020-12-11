/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.taskassigning.index.service.client.graphql.util.JsonUtils.OBJECT_MAPPER;

public abstract class ArgumentContainer implements Argument {

    public static class ArgumentEntry {

        private String name;
        private Argument value;

        public ArgumentEntry(String name, Argument value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Argument getValue() {
            return value;
        }
    }

    protected List<ArgumentEntry> arguments = new ArrayList<>();

    public void add(String name, Argument argument) {
        arguments.add(new ArgumentEntry(name, argument));
    }

    public List<ArgumentEntry> getArguments() {
        return arguments;
    }

    public boolean isEmpty() {
        return arguments == null || arguments.isEmpty();
    }

    @Override
    public JsonNode asJson() {
        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        getArguments().forEach(entry -> {
            if (entry.getValue() == null) {
                result.putNull(entry.getName());
            } else {
                result.set(entry.getName(), entry.getValue().asJson());
            }
        });
        return result;
    }
}
