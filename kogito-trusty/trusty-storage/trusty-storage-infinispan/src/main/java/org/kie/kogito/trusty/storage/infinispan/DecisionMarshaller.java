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

package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.ExecutionTypeEnum;
import org.kie.kogito.trusty.storage.api.model.TypedValue;

public class DecisionMarshaller extends AbstractModelMarshaller<Decision> {

    public DecisionMarshaller(ObjectMapper mapper) {
        super(mapper, Decision.class);
    }

    @Override
    public Decision readFrom(ProtoStreamReader reader) throws IOException {
        ExecutionTypeEnum executionType = enumFromString(reader.readString(Execution.EXECUTION_TYPE_FIELD), ExecutionTypeEnum.class);
        if (executionType != ExecutionTypeEnum.DECISION) {
            throw new IllegalStateException("Unsupported execution type: " + executionType);
        }
        return new Decision(
                reader.readString(Execution.EXECUTION_ID_FIELD),
                reader.readLong(Execution.EXECUTION_TIMESTAMP_FIELD),
                reader.readBoolean(Execution.HAS_SUCCEEDED_FIELD),
                reader.readString(Execution.EXECUTOR_NAME_FIELD),
                reader.readString(Execution.EXECUTED_MODEL_NAME_FIELD),
                reader.readCollection(Decision.INPUTS_FIELD, new ArrayList<>(), TypedValue.class),
                reader.readCollection(Decision.OUTCOMES_FIELD, new ArrayList<>(), DecisionOutcome.class)
        );
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Decision input) throws IOException {
        writer.writeString(Execution.EXECUTION_TYPE_FIELD, stringFromEnum(input.getExecutionType()));
        writer.writeString(Execution.EXECUTION_ID_FIELD, input.getExecutionId());
        writer.writeLong(Execution.EXECUTION_TIMESTAMP_FIELD, input.getExecutionTimestamp());
        writer.writeBoolean(Execution.HAS_SUCCEEDED_FIELD, input.hasSucceeded());
        writer.writeString(Execution.EXECUTOR_NAME_FIELD, input.getExecutorName());
        writer.writeString(Execution.EXECUTED_MODEL_NAME_FIELD, input.getExecutedModelName());
        writer.writeCollection(Decision.INPUTS_FIELD, input.getInputs(), TypedValue.class);
        writer.writeCollection(Decision.OUTCOMES_FIELD, input.getOutcomes(), DecisionOutcome.class);
    }
}
