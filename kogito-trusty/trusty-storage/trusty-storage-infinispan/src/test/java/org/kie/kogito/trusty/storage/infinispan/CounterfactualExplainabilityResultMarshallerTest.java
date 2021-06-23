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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CounterfactualExplainabilityResultMarshallerTest extends MarshallerTestTemplate {

    @Test
    public void testWriteAndRead() throws IOException {
        List<TypedVariableWithValue> data = Collections.singletonList(TypedVariableWithValue.buildUnit("unitIn", "number", JsonNodeFactory.instance.numberNode(10)));
        CounterfactualExplainabilityResult explainabilityResult = new CounterfactualExplainabilityResult("executionId",
                "counterfactualId",
                "solutionId",
                0L,
                ExplainabilityStatus.SUCCEEDED,
                "statusDetail",
                true,
                CounterfactualExplainabilityResult.Stage.FINAL,
                data,
                data);
        CounterfactualExplainabilityResultMarshaller marshaller = new CounterfactualExplainabilityResultMarshaller(new ObjectMapper());

        marshaller.writeTo(writer, explainabilityResult);
        CounterfactualExplainabilityResult retrieved = marshaller.readFrom(reader);

        Assertions.assertEquals(explainabilityResult.getExecutionId(), retrieved.getExecutionId());
        Assertions.assertEquals(explainabilityResult.getCounterfactualId(), retrieved.getCounterfactualId());
        Assertions.assertEquals(explainabilityResult.getSolutionId(), retrieved.getSolutionId());
        Assertions.assertEquals(explainabilityResult.getSequenceId(), retrieved.getSequenceId());
        Assertions.assertEquals(explainabilityResult.getStatus(), retrieved.getStatus());
        Assertions.assertEquals(explainabilityResult.getStatusDetails(), retrieved.getStatusDetails());
        Assertions.assertEquals(explainabilityResult.getStage(), retrieved.getStage());
        Assertions.assertEquals(data.get(0).getName(), retrieved.getInputs().stream().findFirst().get().getName());
        Assertions.assertEquals(data.get(0).getName(), retrieved.getOutputs().stream().findFirst().get().getName());
    }
}
