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
package org.kie.kogito.serverless.workflow.python;

import java.io.IOException;
import java.util.Objects;

import org.kie.kogito.jackson.utils.FunctionBaseJsonNode;
import org.kie.kogito.jackson.utils.JsonObjectUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import jep.python.PyObject;

public class PyObject2JsonNode extends FunctionBaseJsonNode {

    private static final long serialVersionUID = 1L;
    private final PyObject object;

    public PyObject2JsonNode(PyObject object) {
        this.object = Objects.requireNonNull(object, "PyObject must not be null");
    }

    @Override
    public JsonNode get(String fieldName) {
        return JsonObjectUtils.fromValue(object.getAttr(fieldName));
    }

    @Override
    public String asText() {
        return object.toString();
    }

    @Override
    public void serialize(JsonGenerator g, SerializerProvider ctxt) throws IOException {
        g.writeString(object.toString());
    }

    @Override
    public void serializeWithType(JsonGenerator g, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(this, asToken()));
        serialize(g, ctxt);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PyObject2JsonNode) {
            return object.equals(((PyObject2JsonNode) o).object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }
}
