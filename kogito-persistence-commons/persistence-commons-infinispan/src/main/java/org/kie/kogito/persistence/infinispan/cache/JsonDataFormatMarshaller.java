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
package org.kie.kogito.persistence.infinispan.cache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.AbstractMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class JsonDataFormatMarshaller extends AbstractMarshaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataFormatMarshaller.class);

    @Inject
    ObjectMapper mapper;

    @Override
    protected ByteBuffer objectToBuffer(Object object, int estimatedSize) {
        String json = object.toString();
        LOGGER.debug("Serializing JSON: \n{}", json);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return ByteBufferImpl.create(bytes, 0, bytes.length);
    }

    @Override
    public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException {
        return mapper.readTree(buf);
    }

    @Override
    public boolean isMarshallable(Object o) {
        return o instanceof ObjectNode;
    }

    @Override
    public MediaType mediaType() {
        return MediaType.APPLICATION_JSON;
    }
}
