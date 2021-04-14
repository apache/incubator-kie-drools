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

import java.util.Collections;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.tracing.typedvalue.TypedValue.Kind;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.JsonNodeTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.COMPONENTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.KIND_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.TYPE_REF_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.VALUE_FIELD;

public class TypedVariableWithValueMarshallerTest extends MarshallerTestTemplate<TypedVariableWithValue> {

    private static final List<AbstractTestField<TypedVariableWithValue, ?>> TEST_FIELD_LIST = List.of(
            new EnumTestField<>(KIND_FIELD, Kind.UNIT, TypedVariableWithValue::getKind, TypedVariableWithValue::setKind, Kind.class),
            new StringTestField<>(NAME_FIELD, "testName", TypedVariableWithValue::getName, TypedVariableWithValue::setName),
            new StringTestField<>(TYPE_REF_FIELD, "testTypeRef", TypedVariableWithValue::getTypeRef, TypedVariableWithValue::setTypeRef),
            new JsonNodeTestField<>(VALUE_FIELD, null, TypedVariableWithValue::getValue, TypedVariableWithValue::setValue),
            new CollectionTestField<>(COMPONENTS_FIELD, Collections.emptyList(), TypedVariableWithValue::getComponents, TypedVariableWithValue::setComponents, TypedVariableWithValue.class));

    public TypedVariableWithValueMarshallerTest() {
        super(TypedVariableWithValue.class);
    }

    @Override
    protected TypedVariableWithValue buildEmptyObject() {
        return new TypedVariableWithValue();
    }

    @Override
    protected MessageMarshaller<TypedVariableWithValue> buildMarshaller() {
        return new TypedVariableWithValueMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<TypedVariableWithValue, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
