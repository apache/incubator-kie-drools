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

import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.DecisionInput.ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionInput.NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionInput.VALUE_FIELD;

public class DecisionInputMarshallerTest extends MarshallerTestTemplate<DecisionInput> {

    private static final List<AbstractTestField<DecisionInput, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(ID_FIELD, "ID", DecisionInput::getId, DecisionInput::setId),
            new StringTestField<>(NAME_FIELD, "name", DecisionInput::getName, DecisionInput::setName),
            new ObjectTestField<>(VALUE_FIELD, null, DecisionInput::getValue, DecisionInput::setValue, TypedVariableWithValue.class));

    public DecisionInputMarshallerTest() {
        super(DecisionInput.class);
    }

    @Override
    protected DecisionInput buildEmptyObject() {
        return new DecisionInput();
    }

    @Override
    protected MessageMarshaller<DecisionInput> buildMarshaller() {
        return new DecisionInputMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<DecisionInput, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
