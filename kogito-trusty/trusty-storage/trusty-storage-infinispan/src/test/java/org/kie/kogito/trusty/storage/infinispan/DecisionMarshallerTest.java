/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.storage.infinispan;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.ExecutionTypeEnum;
import org.kie.kogito.trusty.storage.api.model.TypedValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.BooleanTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.LongTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.Decision.INPUTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Decision.OUTCOMES_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Execution.EXECUTED_MODEL_NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Execution.EXECUTION_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Execution.EXECUTION_TIMESTAMP_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Execution.EXECUTION_TYPE_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Execution.EXECUTOR_NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Execution.HAS_SUCCEEDED_FIELD;

public class DecisionMarshallerTest extends MarshallerTestTemplate<Decision> {

    private static final List<AbstractTestField<Decision, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(EXECUTION_ID_FIELD, "test", Decision::getExecutionId, Decision::setExecutionId),
            new LongTestField<>(EXECUTION_TIMESTAMP_FIELD, 0L, Decision::getExecutionTimestamp, Decision::setExecutionTimestamp),
            new StringTestField<>(EXECUTOR_NAME_FIELD, "jack", Decision::getExecutorName, Decision::setExecutorName),
            new StringTestField<>(EXECUTED_MODEL_NAME_FIELD, "model", Decision::getExecutedModelName, Decision::setExecutedModelName),
            new BooleanTestField<>(HAS_SUCCEEDED_FIELD, Boolean.TRUE, Decision::hasSucceeded, Decision::setSuccess),
            new EnumTestField<>(EXECUTION_TYPE_FIELD, ExecutionTypeEnum.DECISION, Decision::getExecutionType, Decision::setExecutionType, ExecutionTypeEnum.class),
            new CollectionTestField<>(INPUTS_FIELD, Collections.emptyList(), Decision::getInputs, Decision::setInputs, TypedValue.class),
            new CollectionTestField<>(OUTCOMES_FIELD, Collections.emptyList(), Decision::getOutcomes, Decision::setOutcomes, DecisionOutcome.class)
    );

    public DecisionMarshallerTest() {
        super(Decision.class);
    }

    @Override
    protected Decision buildEmptyObject() {
        return new Decision();
    }

    @Override
    protected MessageMarshaller<Decision> buildMarshaller() {
        return new DecisionMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<Decision, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
