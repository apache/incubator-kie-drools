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
package org.jbpm.flow.serialization.impl.marshallers;

import org.jbpm.flow.serialization.ObjectMarshallerStrategy;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufJsonNodeMessageMarshaller implements ObjectMarshallerStrategy {

    @Override
    public boolean acceptForMarshalling(Object value) {
        return value instanceof JsonNode;
    }

    @Override
    public boolean acceptForUnmarshalling(Any value) {
        return value.is(KogitoTypesProtobuf.JsonNode.class);
    }

    @Override
    public Any marshall(Object unmarshalled) {
        KogitoTypesProtobuf.JsonNode.Builder builder = KogitoTypesProtobuf.JsonNode.newBuilder();
        JsonNode node = (JsonNode) unmarshalled;
        builder.setContent(node.toPrettyString());
        return Any.pack(builder.build());
    }

    @Override
    public Object unmarshall(Any data) {
        try {
            KogitoTypesProtobuf.JsonNode storedValue = data.unpack(KogitoTypesProtobuf.JsonNode.class);
            return ObjectMapperFactory.get().readTree(storedValue.getContent());
        } catch (InvalidProtocolBufferException | JsonProcessingException e1) {
            throw new ProcessInstanceMarshallerException("Error trying to unmarshalling a Json Node value", e1);
        }
    }
}