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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.process.core.context.variable.Variable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.kie.kogito.serialization.process.impl.ProtobufMarshallerReaderContext;
import org.kie.kogito.serialization.process.impl.ProtobufProcessMarshallerWriteContext;
import org.kie.kogito.serialization.process.impl.ProtobufVariableReader;
import org.kie.kogito.serialization.process.impl.ProtobufVariableWriter;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class ProcessInstanceMarshallTest {

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

    private static Stream<Arguments> testRoundTrip() throws Exception {
        return Stream.of(
                Arguments.of(1),
                Arguments.of("hello"),
                Arguments.of(Boolean.TRUE),
                Arguments.of(2f),
                Arguments.of(3d),
                Arguments.of(5l),
                Arguments.of(BigDecimal.valueOf(10l)),
                Arguments.of(new MarshableObject("henry")),
                Arguments.of(new ObjectMapper().readTree("{ \"key\" : \"value\" }")),
                Arguments.of(new ObjectMapper().valueToTree(new MarshableObject("henry"))),
                Arguments.of(new Date()),
                Arguments.of(Instant.now()),
                Arguments.of(OffsetDateTime.now()),
                Arguments.of(LocalDateTime.now()),
                Arguments.of(LocalDate.now()),
                Arguments.of(ZonedDateTime.now()),
                Arguments.of(new Timestamp(System.currentTimeMillis())),
                Arguments.of(Duration.ofDays(1))

        );
    }

    @ParameterizedTest
    @MethodSource
    @NullSource
    public void testRoundTrip(Object toMarshall) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtobufProcessMarshallerWriteContext ctxOut = new ProtobufProcessMarshallerWriteContext(out);
        ctxOut.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ProtobufVariableWriter writer = new ProtobufVariableWriter(ctxOut);
        List<KogitoTypesProtobuf.Variable> variables = writer.buildVariables(singletonMap("var", toMarshall).entrySet().stream().collect(Collectors.toList()));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ProtobufMarshallerReaderContext ctxIn = new ProtobufMarshallerReaderContext(in);
        ctxIn.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, defaultStrategies());
        ProtobufVariableReader reader = new ProtobufVariableReader(ctxIn);
        List<Variable> unmarshalledVars = reader.buildVariables(variables);
        assertThat(unmarshalledVars).hasSize(1);
        assertThat(unmarshalledVars.get(0).getValue()).isEqualTo(toMarshall);
    }

    private ObjectMarshallerStrategy[] defaultStrategies() {
        List<ObjectMarshallerStrategy> strats = new ArrayList<>();
        ServiceLoader<ObjectMarshallerStrategy> loader = ServiceLoader.load(ObjectMarshallerStrategy.class);

        for (ObjectMarshallerStrategy strategy : loader) {
            strats.add(strategy);
        }
        Collections.sort(strats);
        return strats.stream().toArray(ObjectMarshallerStrategy[]::new);
    }
}
