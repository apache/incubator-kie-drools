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

import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.CounterfactualResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult.STATUS_DETAILS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult.STATUS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualResult.COUNTERFACTUAL_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualResult.COUNTERFACTUAL_SOLUTION_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualResult.EXECUTION_ID_FIELD;

public class CounterfactualResultMarshallerTest extends MarshallerTestTemplate<CounterfactualResult> {

    private static final List<AbstractTestField<CounterfactualResult, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(EXECUTION_ID_FIELD, "executionId", CounterfactualResult::getExecutionId, CounterfactualResult::setExecutionId),
            new EnumTestField<>(STATUS_FIELD,
                    ExplainabilityStatus.SUCCEEDED,
                    CounterfactualResult::getStatus,
                    CounterfactualResult::setStatus,
                    ExplainabilityStatus.class),
            new StringTestField<>(STATUS_DETAILS_FIELD, "explanation", CounterfactualResult::getStatusDetails, CounterfactualResult::setStatusDetails),
            new StringTestField<>(COUNTERFACTUAL_ID_FIELD, "counterfactualId", CounterfactualResult::getCounterfactualId, CounterfactualResult::setCounterfactualId),
            new StringTestField<>(COUNTERFACTUAL_SOLUTION_ID_FIELD, "solutionId", CounterfactualResult::getSolutionId, CounterfactualResult::setSolutionId));

    public CounterfactualResultMarshallerTest() {
        super(CounterfactualResult.class);
    }

    @Override
    protected CounterfactualResult buildEmptyObject() {
        return new CounterfactualResult();
    }

    @Override
    protected MessageMarshaller<CounterfactualResult> buildMarshaller() {
        return new CounterfactualResultMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<CounterfactualResult, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
