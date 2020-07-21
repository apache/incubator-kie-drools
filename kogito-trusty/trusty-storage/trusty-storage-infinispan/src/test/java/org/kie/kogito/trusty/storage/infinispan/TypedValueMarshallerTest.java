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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.TypedValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.JsonNodeTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.TypedValue.NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedValue.TYPE_REF_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedValue.VALUE_FIELD;

public class TypedValueMarshallerTest extends MarshallerTestTemplate<TypedValue> {

    private static final List<AbstractTestField<TypedValue, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(NAME_FIELD, "testName", TypedValue::getName, TypedValue::setName),
            new StringTestField<>(TYPE_REF_FIELD, "testTypeRef", TypedValue::getTypeRef, TypedValue::setTypeRef),
            new JsonNodeTestField<>(VALUE_FIELD, null, TypedValue::getValue, TypedValue::setValue)
    );

    public TypedValueMarshallerTest() {
        super(TypedValue.class);
    }

    @Override
    protected TypedValue buildEmptyObject() {
        return new TypedValue();
    }

    @Override
    protected MessageMarshaller<TypedValue> buildMarshaller() {
        return new TypedValueMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<TypedValue, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
