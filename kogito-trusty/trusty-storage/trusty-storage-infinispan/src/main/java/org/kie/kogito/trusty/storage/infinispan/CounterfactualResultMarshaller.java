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

import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.CounterfactualResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CounterfactualResultMarshaller extends AbstractModelMarshaller<CounterfactualResult> {

    public CounterfactualResultMarshaller(ObjectMapper mapper) {
        super(mapper, CounterfactualResult.class);
    }

    @Override
    public CounterfactualResult readFrom(ProtoStreamReader reader) throws IOException {
        return new CounterfactualResult(
                reader.readString(CounterfactualResult.EXECUTION_ID_FIELD),
                enumFromString(reader.readString(BaseExplainabilityResult.STATUS_FIELD), ExplainabilityStatus.class),
                reader.readString(BaseExplainabilityResult.STATUS_DETAILS_FIELD),
                reader.readString(CounterfactualResult.COUNTERFACTUAL_ID_FIELD),
                reader.readString(CounterfactualResult.COUNTERFACTUAL_SOLUTION_ID_FIELD));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, CounterfactualResult input) throws IOException {
        writer.writeString(CounterfactualResult.EXECUTION_ID_FIELD, input.getExecutionId());
        writer.writeString(BaseExplainabilityResult.STATUS_FIELD, stringFromEnum(input.getStatus()));
        writer.writeString(BaseExplainabilityResult.STATUS_DETAILS_FIELD, input.getStatusDetails());
        writer.writeString(CounterfactualResult.COUNTERFACTUAL_ID_FIELD, input.getCounterfactualId());
        writer.writeString(CounterfactualResult.COUNTERFACTUAL_SOLUTION_ID_FIELD, input.getSolutionId());
    }
}
