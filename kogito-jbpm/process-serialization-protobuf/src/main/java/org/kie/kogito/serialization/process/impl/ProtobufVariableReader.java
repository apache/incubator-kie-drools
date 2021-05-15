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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.FloatDataType;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ListDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;
import org.kie.kogito.serialization.process.MarshallerReaderContext;
import org.kie.kogito.serialization.process.ObjectMarshallerStrategy;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;

import com.google.protobuf.Any;

public class ProtobufVariableReader {

    private DataType[] dataTypes = { new BooleanDataType(), new FloatDataType(), new IntegerDataType(),
            new ListDataType(), new StringDataType() };

    private MarshallerReaderContext context;

    public ProtobufVariableReader(MarshallerReaderContext context) {
        this.context = context;
    }

    public List<Variable> buildVariables(List<KogitoTypesProtobuf.Variable> variablesProtobuf) {
        List<Variable> variables = new ArrayList<>();
        for (KogitoTypesProtobuf.Variable var : variablesProtobuf) {
            Variable storedVar = new Variable();
            storedVar.setName(var.getName());
            Any value = var.getValue();
            ObjectMarshallerStrategy strategy = context.findObjectUnmarshallerStrategyFor(value);
            Object varValue = strategy.unmarshall(value);
            if (varValue != null) {
                storedVar.setType(buildDataType(varValue.getClass().getCanonicalName()));
                storedVar.setValue(varValue);
            } else {
                storedVar.setType(new ObjectDataType("java.lang.Object"));
            }
            variables.add(storedVar);
        }
        return variables;
    }

    private DataType buildDataType(String dataType) {
        try {
            if (dataType == null || dataType.isEmpty()) {
                return new ObjectDataType("java.lang.Object");
            }

            Optional<DataType> foundDataType = Arrays.stream(dataTypes)
                    .filter(e -> e.getStringType().equals(dataType))
                    .findAny();
            if (foundDataType.isPresent()) {
                return foundDataType.get();
            }

            Class.forName(dataType);
            return new ObjectDataType(dataType);
        } catch (ClassNotFoundException e) {
            return new ObjectDataType("java.lang.Object");
        }
    }
}
