/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.vertx;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

@ApplicationScoped
public class ObjectNodeMessageCodec implements MessageCodec<ObjectNode, ObjectNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectNodeMessageCodec.class);

    @Override
    public void encodeToWire(Buffer buffer, ObjectNode node) {
        try {
            buffer.appendBytes(getObjectMapper().writeValueAsBytes(node));
        } catch (IOException ex) {
            LOGGER.error("Error trying to parse ObjectNode to byte[]: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ObjectNode decodeFromWire(int pos, Buffer buffer) {
        try {
            return (ObjectNode) getObjectMapper().readTree(buffer.getBytes());
        } catch (IOException ex) {
            LOGGER.error("Error trying to parse byte[] to ObjectNode: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ObjectNode transform(ObjectNode node) {
        return node;
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
