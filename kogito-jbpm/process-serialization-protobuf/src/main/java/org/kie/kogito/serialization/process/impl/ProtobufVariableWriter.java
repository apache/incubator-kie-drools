/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serialization.process.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.kie.kogito.serialization.process.MarshallerWriterContext;
import org.kie.kogito.serialization.process.ObjectMarshallerStrategy;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;

public class ProtobufVariableWriter {

    private MarshallerWriterContext context;

    public ProtobufVariableWriter(MarshallerWriterContext context) {
        this.context = context;
    }

    public List<KogitoTypesProtobuf.Variable> buildVariables(List<Map.Entry<String, Object>> variables) {
        Comparator<Map.Entry<String, Object>> comparator = (o1, o2) -> o1.getKey().compareTo(o2.getKey());
        Collections.sort(variables, comparator);

        List<KogitoTypesProtobuf.Variable> variablesProtobuf = new ArrayList<>();
        for (Map.Entry<String, Object> entry : variables) {
            KogitoTypesProtobuf.Variable.Builder variableBuilder = KogitoTypesProtobuf.Variable.newBuilder();
            variableBuilder.setName(entry.getKey());
            if (entry.getValue() != null) {
                Object value = entry.getValue();
                ObjectMarshallerStrategy strategy = context.findObjectMarshallerStrategyFor(value);
                variableBuilder.setDataType(entry.getValue().getClass().getName()).setValue(strategy.marshall(value));
            } else {
                variableBuilder.setValue(Any.pack(BytesValue.of(ByteString.EMPTY)));
            }
            variablesProtobuf.add(variableBuilder.build());
        }
        return variablesProtobuf;
    }

}
