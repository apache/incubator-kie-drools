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

package org.kie.kogito.serialization.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.jbpm.process.core.context.variable.Variable;
import org.junit.jupiter.api.Test;
import org.kie.kogito.serialization.process.impl.ProtobufMarshallerReaderContext;
import org.kie.kogito.serialization.process.impl.ProtobufProcessMarshallerWriteContext;
import org.kie.kogito.serialization.process.impl.ProtobufVariableReader;
import org.kie.kogito.serialization.process.impl.ProtobufVariableWriter;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;

public class ProcessInstanceMarshallTest {

    @Test
    public void testRoundtripIntVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("integer", 1);
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripStringVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("string", "hello");
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripBoolVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("bool", Boolean.TRUE);
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripFloatVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("float", 2f);
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripDoubleVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("double", 3d);
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripDateVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("date", new Date());
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripLongVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("long", 5L);
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    public static class MarshableObject implements Serializable {

        private static final long serialVersionUID = 1481370154514125687L;

        private String name;

        public MarshableObject() {
        }

        public MarshableObject(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MarshableObject) {
                return this.name.equals(((MarshableObject) obj).name);
            }
            return false;
        }
    }

    @Test
    public void testRoundtripCustomObjectVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("object", new MarshableObject("henry"));
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    @Test
    public void testRoundtripNullVarMarshaller() {
        Map<String, Object> in = new HashMap<>();
        in.put("object", null);
        Map<String, Object> out = roundtrip(in);
        Assertions.assertThat(in).isEqualTo(out);
    }

    private Map<String, Object> roundtrip(Map<String, Object> toMarshall) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtobufProcessMarshallerWriteContext ctxOut = new ProtobufProcessMarshallerWriteContext(out);
        ctxOut.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ProtobufVariableWriter writer = new ProtobufVariableWriter(ctxOut);
        List<KogitoTypesProtobuf.Variable> variables = writer.buildVariables(toMarshall.entrySet().stream().collect(Collectors.toList()));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ProtobufMarshallerReaderContext ctxIn = new ProtobufMarshallerReaderContext(in);
        ctxIn.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ProtobufVariableReader reader = new ProtobufVariableReader(ctxIn);
        List<Variable> unmarshalledVars = reader.buildVariables(variables);
        Map<String, Object> outcome = new HashMap<>();
        for (Variable var : unmarshalledVars) {
            outcome.put(var.getName(), var.getValue());
        }
        return outcome;
    }

    private ObjectMarshallerStrategy[] defaultStrategies() {
        List<ObjectMarshallerStrategy> strats = new ArrayList<>();
        ServiceLoader<ObjectMarshallerStrategy> loader = ServiceLoader.load(ObjectMarshallerStrategy.class);

        for (ObjectMarshallerStrategy strategy : loader) {
            strats.add(strategy);
        }
        return strats.stream().toArray(ObjectMarshallerStrategy[]::new);
    }
}
