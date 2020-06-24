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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.ExecutionTypeEnum;
import org.kie.kogito.trusty.storage.infinispan.DecisionMarshaller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

public class DecisionMarshallerTest {

    private static final List<Method> decisionSetters = Arrays.stream(Decision.class.getMethods()).filter(x -> x.getName().startsWith("set")).collect(Collectors.toList());

    @Test
    public void allPropertiesOfDecisionObjectAreCoveredByTheMarshaller() throws IOException {
        Decision decision = new Decision();
        decision.setExecutionId("test");
        decision.setExecutionTimestamp(0L);
        decision.setExecutorName("jack");
        decision.setExecutedModelName("model");
        decision.setExecutionType(ExecutionTypeEnum.DECISION);
        decision.setSuccess(true);

        DecisionMarshaller marshaller = new DecisionMarshaller(new ObjectMapper());
        ProtoStreamWriterMock protoStreamWriter = new ProtoStreamWriterMock();
        marshaller.writeTo(protoStreamWriter, decision);

        List<String> usedFields = protoStreamWriter.getWrittenFieldNames();

        Assertions.assertEquals(decisionSetters.size(), usedFields.size());
        Assertions.assertTrue(usedFields.contains(Execution.EXECUTION_ID));
        Assertions.assertTrue(usedFields.contains(Execution.EXECUTION_TIMESTAMP));
        Assertions.assertTrue(usedFields.contains(Execution.EXECUTOR_NAME));
        Assertions.assertTrue(usedFields.contains(Execution.EXECUTED_MODEL_NAME));
        Assertions.assertTrue(usedFields.contains(Execution.HAS_SUCCEEDED));
        Assertions.assertTrue(usedFields.contains(Execution.EXECUTION_TYPE));
    }

    @Test
    public void allPropertiesOfDecisionObjectAreCoveredByTheUnmarshaller() throws IOException {
        DecisionMarshaller marshaller = new DecisionMarshaller(new ObjectMapper());
        MessageMarshaller.ProtoStreamReader protoStreamReader = mock(MessageMarshaller.ProtoStreamReader.class);
        when(protoStreamReader.readString(eq(Execution.EXECUTION_ID))).thenReturn("test");
        when(protoStreamReader.readLong(eq(Execution.EXECUTION_TIMESTAMP))).thenReturn(0L);
        when(protoStreamReader.readString(eq(Execution.EXECUTOR_NAME))).thenReturn("jack");
        when(protoStreamReader.readString(eq(Execution.EXECUTED_MODEL_NAME))).thenReturn("model");
        when(protoStreamReader.readString(eq(Execution.EXECUTION_TYPE))).thenReturn("\"DECISION\"");
        when(protoStreamReader.readBoolean(eq(Execution.HAS_SUCCEEDED))).thenReturn(true);

        Decision decision = marshaller.readFrom(protoStreamReader);

        Assertions.assertEquals(decisionSetters.size(), mockingDetails(protoStreamReader).getInvocations().size());
        Assertions.assertNotNull(decision.getExecutionId());
        Assertions.assertNotNull(decision.getExecutionTimestamp());
        Assertions.assertNotNull(decision.getExecutedModelName());
        Assertions.assertNotNull(decision.getExecutorName());
        Assertions.assertNotNull(decision.getExecutionType());
    }
}
