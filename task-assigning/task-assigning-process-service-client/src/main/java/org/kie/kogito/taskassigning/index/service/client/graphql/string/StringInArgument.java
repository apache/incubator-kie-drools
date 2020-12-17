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

package org.kie.kogito.taskassigning.index.service.client.graphql.string;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.taskassigning.util.JsonUtils.OBJECT_MAPPER;

public class StringInArgument extends StringArgument<List<String>> {

    public StringInArgument(List<String> value) {
        super(value, Condition.IN);
        if (value == null) {
            throw new IllegalArgumentException("Cannot set a null list of values");
        }
    }

    @Override
    public JsonNode asJson() {
        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        ArrayNode valueArray = result.putArray(getCondition().getFunction());
        value.forEach(strValue -> {
            if (strValue == null) {
                valueArray.addNull();
            } else {
                valueArray.add(strValue);
            }
        });
        return result;
    }
}
