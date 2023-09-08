/**
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
package org.drools.drlonyaml.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class AbstractThenDeserializer extends StdDeserializer<AbstractThen> { 

    public AbstractThenDeserializer() { 
        this(null); 
    } 

    public AbstractThenDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public AbstractThen deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isTextual()) {
            return StringThen.from(node.textValue());
/* if additional RHS/then will be needed for abstracting from Java (snippet), then the extension mechanism can support additional
   types providing something ~like:
        } else if (node.get("something") != null) {
          return jp.getCodec().treeToValue(node, ConcreteClass.class);*/
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
