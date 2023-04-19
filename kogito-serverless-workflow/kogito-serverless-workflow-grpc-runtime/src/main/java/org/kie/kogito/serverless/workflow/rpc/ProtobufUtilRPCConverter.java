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

import java.io.IOException;
import java.io.UncheckedIOException;

import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.util.JsonFormat;

class ProtobufUtilRPCConverter implements RPCConverter {
    @Override
    public Builder buildMessage(Object object, Builder builder) {
        try {
            JsonFormat.parser().merge(ObjectMapperFactory.get().writeValueAsString(object), builder);
            return builder;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public JsonNode getJsonNode(Message message) {
        StringBuilder sb = new StringBuilder();
        try {
            JsonFormat.printer().appendTo(message, sb);
            return ObjectMapperFactory.listenerAware().readTree(sb.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
