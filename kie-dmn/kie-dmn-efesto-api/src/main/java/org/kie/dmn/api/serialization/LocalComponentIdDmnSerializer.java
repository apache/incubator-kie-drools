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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.Serial;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class LocalComponentIdDmnSerializer extends StdSerializer<LocalComponentIdDmn> {

    @Serial
    private static final long serialVersionUID = -4763041597774117202L;

    public LocalComponentIdDmnSerializer() {
        this(null);
    }

    public LocalComponentIdDmnSerializer(Class<LocalComponentIdDmn> t) {
        super(t);
    }

    @Override
    public void serialize(LocalComponentIdDmn value, JsonGenerator gen,
                          SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("nameSpace", value.getNameSpace());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("basePath", decodedPath(value.basePath()));
        gen.writeStringField("fullPath", decodedPath(value.fullPath()));
        gen.writeEndObject();
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