/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;

public class DefaultEnumRpcDecorator implements RPCDecorator {

    @Override
    public JsonNode decorate(JsonNode node, Descriptor descriptor) {
        for (FieldDescriptor field : descriptor.getFields()) {
            if (node.has(field.getName())) {
                if (field.isRepeated()) {
                    node.get(field.getName()).forEach(n -> decorate(n, field.getMessageType()));
                } else if (field.getType() == Type.MESSAGE) {
                    decorate(node.get(field.getName()), field.getMessageType());
                }
            } else if (field.getType() == Type.ENUM) {
                ((ObjectNode) node).put(field.getName(), field.getDefaultValue().toString());
            }
        }
        return node;
    }
}
