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
package org.jbpm.flow.serialization.impl;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.ObjectMarshallerStrategy;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.DataTypeResolver;

import com.google.protobuf.Any;

public class ProtobufVariableReader {

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
            storedVar.setType(DataTypeResolver.fromObject(varValue));
            if (varValue != null) {
                storedVar.setValue(varValue);
            }
            variables.add(storedVar);
        }
        return variables;
    }

}
