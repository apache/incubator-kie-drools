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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CounterfactualExplainabilityResultMarshallerTest extends MarshallerTestTemplate {

    @Test
    public void testWriteAndRead() throws IOException {
        List<NamedTypedValue> inputs = List.of(new NamedTypedValue("unitIn",
                new UnitValue("number",
                        "number",
                        JsonNodeFactory.instance.numberNode(10))));

        List<NamedTypedValue> outputs = List.of(new NamedTypedValue("unitOut",
                new UnitValue("number",
                        "number",
                        JsonNodeFactory.instance.numberNode(55))));

        CounterfactualExplainabilityResult explainabilityResult = new CounterfactualExplainabilityResult("executionId",
                "counterfactualId",
                "solutionId",
                0L,
                ExplainabilityStatus.SUCCEEDED,
                "statusDetail",
                true,
                CounterfactualExplainabilityResult.Stage.FINAL,
                inputs,
                outputs);
        CounterfactualExplainabilityResultMarshaller marshaller = new CounterfactualExplainabilityResultMarshaller(new ObjectMapper());

        marshaller.writeTo(writer, explainabilityResult);
        CounterfactualExplainabilityResult retrieved = marshaller.readFrom(reader);
        List<NamedTypedValue> retrievedInputs = List.of(retrieved.getInputs().toArray(new NamedTypedValue[0]));
        List<NamedTypedValue> retrievedOutputs = List.of(retrieved.getOutputs().toArray(new NamedTypedValue[0]));

        Assertions.assertEquals(explainabilityResult.getExecutionId(), retrieved.getExecutionId());
        Assertions.assertEquals(explainabilityResult.getCounterfactualId(), retrieved.getCounterfactualId());
        Assertions.assertEquals(explainabilityResult.getSolutionId(), retrieved.getSolutionId());
        Assertions.assertEquals(explainabilityResult.getSequenceId(), retrieved.getSequenceId());
        Assertions.assertEquals(explainabilityResult.getStatus(), retrieved.getStatus());
        Assertions.assertEquals(explainabilityResult.getStatusDetails(), retrieved.getStatusDetails());
        Assertions.assertEquals(explainabilityResult.getStage(), retrieved.getStage());

        Assertions.assertEquals(1, retrievedInputs.size());
        Assertions.assertEquals(inputs.get(0).getName(), retrievedInputs.get(0).getName());
        Assertions.assertEquals(inputs.get(0).getValue().getKind(), retrievedInputs.get(0).getValue().getKind());
        Assertions.assertEquals(inputs.get(0).getValue().getType(), retrievedInputs.get(0).getValue().getType());
        Assertions.assertEquals(inputs.get(0).getValue().toUnit().getValue(), retrievedInputs.get(0).getValue().toUnit().getValue());

        Assertions.assertEquals(1, retrievedOutputs.size());
        Assertions.assertEquals(outputs.get(0).getName(), retrievedOutputs.get(0).getName());
        Assertions.assertEquals(outputs.get(0).getValue().getKind(), retrievedOutputs.get(0).getValue().getKind());
        Assertions.assertEquals(outputs.get(0).getValue().getType(), retrievedOutputs.get(0).getValue().getType());
        Assertions.assertEquals(outputs.get(0).getValue().toUnit().getValue(), retrievedOutputs.get(0).getValue().toUnit().getValue());
    }
}
