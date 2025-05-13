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
package org.kie.dmn.api.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.io.Serial;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class LocalComponentIdDmnDeserializer extends StdDeserializer<LocalComponentIdDmn> {

    @Serial
    private static final long serialVersionUID = -4284725569069463059L;

    public LocalComponentIdDmnDeserializer() {
        this(null);
    }

    public LocalComponentIdDmnDeserializer(Class<LocalComponentIdDmn> t) {
        super(t);
    }

    @Override
    public LocalComponentIdDmn deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String basePath = decodedPath(node.get("basePath").asText());
        String[] parts = basePath.split(SLASH);
        String name = parts[parts.length - 1];
        String nameSpace = basePath.startsWith(SLASH) ? basePath.substring(1) : basePath;
        nameSpace = nameSpace.substring(0, nameSpace.lastIndexOf(SLASH));
        return new LocalComponentIdDmn(nameSpace, name);
    }

    static String decodedPath(String toDecode) {
        StringTokenizer tok = new StringTokenizer(toDecode, SLASH);
        StringBuilder builder = new StringBuilder();
        while (tok.hasMoreTokens()) {
            builder.append(SLASH);
            builder.append(decodeString(tok.nextToken()));
        }
        return builder.toString();
    }

    static String decodeString(String toDecode) {
        return URLDecoder.decode(toDecode, StandardCharsets.UTF_8);
    }
}
