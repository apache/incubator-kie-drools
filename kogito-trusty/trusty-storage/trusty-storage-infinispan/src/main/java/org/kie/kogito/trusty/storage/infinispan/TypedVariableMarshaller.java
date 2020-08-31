/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.tracing.typedvalue.TypedValue.Kind;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

public class TypedVariableMarshaller extends AbstractModelMarshaller<TypedVariable> {

    public TypedVariableMarshaller(ObjectMapper mapper) {
        super(mapper, TypedVariable.class);
    }

    @Override
    public TypedVariable readFrom(ProtoStreamReader reader) throws IOException {
        return new TypedVariable(
                enumFromString(reader.readString(TypedVariable.KIND_FIELD), Kind.class),
                reader.readString(TypedVariable.NAME_FIELD),
                reader.readString(TypedVariable.TYPE_REF_FIELD),
                jsonFromString(reader.readString(TypedVariable.VALUE_FIELD)),
                reader.readCollection(TypedVariable.COMPONENTS_FIELD, new ArrayList<>(), TypedVariable.class)
        );
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, TypedVariable input) throws IOException {
        writer.writeString(TypedVariable.KIND_FIELD, stringFromEnum(input.getKind()));
        writer.writeString(TypedVariable.NAME_FIELD, input.getName());
        writer.writeString(TypedVariable.TYPE_REF_FIELD, input.getTypeRef());
        writer.writeString(TypedVariable.VALUE_FIELD, stringFromJson(input.getValue()));
        writer.writeCollection(TypedVariable.COMPONENTS_FIELD, input.getComponents(), TypedVariable.class);
    }

}
