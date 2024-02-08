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
package org.kie.kogito.index.jdbc;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.AttributeConverter;

public class JsonBinaryConverter implements AttributeConverter<ObjectNode, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(ObjectNode attribute) {
        try {
            return attribute == null ? null : ObjectMapperFactory.get().writeValueAsBytes(attribute);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ObjectNode convertToEntityAttribute(byte[] dbData) {
        try {
            return dbData == null ? null : ObjectMapperFactory.get().readValue(dbData, ObjectNode.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
