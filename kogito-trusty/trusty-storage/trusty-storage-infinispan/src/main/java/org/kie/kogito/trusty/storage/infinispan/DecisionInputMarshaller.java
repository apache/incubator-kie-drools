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

import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DecisionInputMarshaller extends AbstractModelMarshaller<DecisionInput> {

    public DecisionInputMarshaller(ObjectMapper mapper) {
        super(mapper, DecisionInput.class);
    }

    @Override
    public DecisionInput readFrom(ProtoStreamReader reader) throws IOException {
        return new DecisionInput(
                reader.readString(DecisionInput.ID_FIELD),
                reader.readString(DecisionInput.NAME_FIELD),
                reader.readObject(DecisionInput.VALUE_FIELD, TypedVariableWithValue.class));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, DecisionInput input) throws IOException {
        writer.writeString(DecisionInput.ID_FIELD, input.getId());
        writer.writeString(DecisionInput.NAME_FIELD, input.getName());
        writer.writeObject(DecisionInput.VALUE_FIELD, input.getValue(), TypedVariableWithValue.class);
    }
}
