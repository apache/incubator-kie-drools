/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.ArrayList;

import org.kie.kogito.tracing.typedvalue.TypedValue.Kind;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TypedVariableWithValueMarshaller extends AbstractModelMarshaller<TypedVariableWithValue> {

    public TypedVariableWithValueMarshaller(ObjectMapper mapper) {
        super(mapper, TypedVariableWithValue.class);
    }

    @Override
    public TypedVariableWithValue readFrom(ProtoStreamReader reader) throws IOException {
        return new TypedVariableWithValue(
                enumFromString(reader.readString(TypedVariableWithValue.KIND_FIELD), Kind.class),
                reader.readString(TypedVariableWithValue.NAME_FIELD),
                reader.readString(TypedVariableWithValue.TYPE_REF_FIELD),
                jsonFromString(reader.readString(TypedVariableWithValue.VALUE_FIELD)),
                reader.readCollection(TypedVariableWithValue.COMPONENTS_FIELD, new ArrayList<>(), TypedVariableWithValue.class));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, TypedVariableWithValue input) throws IOException {
        writer.writeString(TypedVariableWithValue.KIND_FIELD, stringFromEnum(input.getKind()));
        writer.writeString(TypedVariableWithValue.NAME_FIELD, input.getName());
        writer.writeString(TypedVariableWithValue.TYPE_REF_FIELD, input.getTypeRef());
        writer.writeString(TypedVariableWithValue.VALUE_FIELD, stringFromJson(input.getValue()));
        writer.writeCollection(TypedVariableWithValue.COMPONENTS_FIELD, input.getComponents(), TypedVariableWithValue.class);
    }
}
