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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.tracing.typedvalue.TypedValue.Kind;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.JsonNodeTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.TypedVariable.COMPONENTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.KIND_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.TYPE_REF_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.VALUE_FIELD;

public class TypedVariableMarshallerTest extends MarshallerTestTemplate<TypedVariable> {

    private static final List<AbstractTestField<TypedVariable, ?>> TEST_FIELD_LIST = List.of(
            new EnumTestField<>(KIND_FIELD, Kind.UNIT, TypedVariable::getKind, TypedVariable::setKind, Kind.class),
            new StringTestField<>(NAME_FIELD, "testName", TypedVariable::getName, TypedVariable::setName),
            new StringTestField<>(TYPE_REF_FIELD, "testTypeRef", TypedVariable::getTypeRef, TypedVariable::setTypeRef),
            new JsonNodeTestField<>(VALUE_FIELD, null, TypedVariable::getValue, TypedVariable::setValue),
            new CollectionTestField<>(COMPONENTS_FIELD, Collections.emptyList(), TypedVariable::getComponents, TypedVariable::setComponents, TypedVariable.class)
    );

    public TypedVariableMarshallerTest() {
        super(TypedVariable.class);
    }

    @Override
    protected TypedVariable buildEmptyObject() {
        return new TypedVariable();
    }

    @Override
    protected MessageMarshaller<TypedVariable> buildMarshaller() {
        return new TypedVariableMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<TypedVariable, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
