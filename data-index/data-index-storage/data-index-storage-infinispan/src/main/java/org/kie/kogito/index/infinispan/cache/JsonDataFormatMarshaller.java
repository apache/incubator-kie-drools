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

package org.kie.kogito.index.infinispan.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.AbstractMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDataFormatMarshaller extends AbstractMarshaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataFormatMarshaller.class);

    @Override
    protected ByteBuffer objectToBuffer(Object object, int estimatedSize) throws IOException, InterruptedException {
        String json = object.toString();
        LOGGER.debug("Serializing JSON: \n{}", json);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return new ByteBufferImpl(bytes, 0, bytes.length);
    }

    @Override
    public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
        try (JsonReader reader = Json.createReader(new ByteArrayInputStream(buf, offset, length))) {
            return reader.readObject();
        }
    }

    @Override
    public boolean isMarshallable(Object o) throws Exception {
        return o instanceof JsonObject;
    }

    @Override
    public MediaType mediaType() {
        return MediaType.APPLICATION_JSON;
    }
}
