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

import java.util.Collections;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.BooleanTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult.STATUS_DETAILS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult.STATUS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.COUNTERFACTUAL_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.COUNTERFACTUAL_SOLUTION_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.EXECUTION_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.INPUTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.IS_VALID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.OUTPUTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult.STAGE_FIELD;

public class CounterfactualExplainabilityResultMarshallerTest extends MarshallerTestTemplate<CounterfactualExplainabilityResult> {

    private static final List<AbstractTestField<CounterfactualExplainabilityResult, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(EXECUTION_ID_FIELD,
                    "executionId",
                    CounterfactualExplainabilityResult::getExecutionId,
                    CounterfactualExplainabilityResult::setExecutionId),
            new EnumTestField<>(STATUS_FIELD,
                    ExplainabilityStatus.SUCCEEDED,
                    CounterfactualExplainabilityResult::getStatus,
                    CounterfactualExplainabilityResult::setStatus,
                    ExplainabilityStatus.class),
            new StringTestField<>(STATUS_DETAILS_FIELD,
                    "explanation",
                    CounterfactualExplainabilityResult::getStatusDetails,
                    CounterfactualExplainabilityResult::setStatusDetails),
            new StringTestField<>(COUNTERFACTUAL_ID_FIELD,
                    "counterfactualId",
                    CounterfactualExplainabilityResult::getCounterfactualId,
                    CounterfactualExplainabilityResult::setCounterfactualId),
            new StringTestField<>(COUNTERFACTUAL_SOLUTION_ID_FIELD,
                    "solutionId",
                    CounterfactualExplainabilityResult::getSolutionId,
                    CounterfactualExplainabilityResult::setSolutionId),
            new BooleanTestField<>(IS_VALID_FIELD,
                    Boolean.TRUE,
                    CounterfactualExplainabilityResult::isValid,
                    CounterfactualExplainabilityResult::setValid),
            new EnumTestField<>(STAGE_FIELD,
                    CounterfactualExplainabilityResult.Stage.FINAL,
                    CounterfactualExplainabilityResult::getStage,
                    CounterfactualExplainabilityResult::setStage,
                    CounterfactualExplainabilityResult.Stage.class),
            new CollectionTestField<>(INPUTS_FIELD,
                    Collections.emptyList(),
                    CounterfactualExplainabilityResult::getInputs,
                    CounterfactualExplainabilityResult::setInputs,
                    TypedVariableWithValue.class),
            new CollectionTestField<>(OUTPUTS_FIELD,
                    Collections.emptyList(),
                    CounterfactualExplainabilityResult::getOutputs,
                    CounterfactualExplainabilityResult::setOutputs,
                    TypedVariableWithValue.class));

    public CounterfactualExplainabilityResultMarshallerTest() {
        super(CounterfactualExplainabilityResult.class);
    }

    @Override
    protected CounterfactualExplainabilityResult buildEmptyObject() {
        return new CounterfactualExplainabilityResult();
    }

    @Override
    protected MessageMarshaller<CounterfactualExplainabilityResult> buildMarshaller() {
        return new CounterfactualExplainabilityResultMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<CounterfactualExplainabilityResult, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
