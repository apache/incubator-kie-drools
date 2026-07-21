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
package org.kie.kogito.events.mongodb.codec;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.kie.kogito.events.mongodb.codec.CodecUtils.codec;

public class UserTaskInstanceDataEventCodec implements CollectibleCodec<UserTaskInstanceDataEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTaskInstanceDataEventCodec.class);

    @Override
    public UserTaskInstanceDataEvent<?> generateIdIfAbsentFromDocument(UserTaskInstanceDataEvent userTaskInstanceDataEvent) {
        return userTaskInstanceDataEvent;
    }

    @Override
    public boolean documentHasId(UserTaskInstanceDataEvent userTaskInstanceDataEvent) {
        return userTaskInstanceDataEvent.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(UserTaskInstanceDataEvent userTaskInstanceDataEvent) {
        return new BsonString(userTaskInstanceDataEvent.getId());
    }

    @Override
    public UserTaskInstanceDataEvent decode(BsonReader bsonReader, DecoderContext decoderContext) {
        // The events persist in an outbox collection
        // The events are deleted immediately (in the same transaction)
        // "decode" is not supposed to take place in any scenario
        return null;
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserTaskInstanceDataEvent userTaskInstanceDataEvent, EncoderContext encoderContext) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            Document document = Document.parse(mapper.writeValueAsString(userTaskInstanceDataEvent));
            document.put(CodecUtils.ID, userTaskInstanceDataEvent.getId());
            codec().encode(bsonWriter, document, encoderContext);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not process json event", e);
        }
    }

    @Override
    public Class<UserTaskInstanceDataEvent> getEncoderClass() {
        return UserTaskInstanceDataEvent.class;
    }
}
