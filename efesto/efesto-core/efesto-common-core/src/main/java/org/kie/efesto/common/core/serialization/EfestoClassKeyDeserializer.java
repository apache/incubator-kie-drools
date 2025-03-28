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
package org.kie.efesto.common.core.serialization;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

public class EfestoClassKeyDeserializer extends StdDeserializer<EfestoClassKey> {

    private static final long serialVersionUID = -3468047979532504909L;

    public EfestoClassKeyDeserializer() {
        this(null);
    }

    public EfestoClassKeyDeserializer(Class<EfestoClassKey> t) {
        super(t);
    }

    @Override
    public EfestoClassKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        try {
            String rawTypeString = node.get("rawType").asText();
            Type rawType = Class.forName(rawTypeString);
            ArrayNode typeArgumentsNode = (ArrayNode) node.get("actualTypeArguments");
            List<Type> typeArguments = new ArrayList<>();
            Iterator<JsonNode> typeArgumentsIterator = typeArgumentsNode.elements();
            while (typeArgumentsIterator.hasNext()) {
                JsonNode typeArgumentNode = typeArgumentsIterator.next();
                String typeArgumentString = typeArgumentNode.asText();
                Type argumentType = Class.forName(typeArgumentString);
                typeArguments.add(argumentType);
            }
            return typeArguments.isEmpty() ? new EfestoClassKey(rawType) : new EfestoClassKey(rawType, typeArguments.toArray(new Type[0]));
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to deserialize %s as EfestoClassKey", node), e);
        }
    }
}
