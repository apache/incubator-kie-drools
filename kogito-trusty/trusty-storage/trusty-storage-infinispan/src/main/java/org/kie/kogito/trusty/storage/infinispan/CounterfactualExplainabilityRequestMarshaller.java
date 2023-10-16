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

import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CounterfactualExplainabilityRequestMarshaller extends AbstractModelMarshaller<CounterfactualExplainabilityRequest> {

    public CounterfactualExplainabilityRequestMarshaller(ObjectMapper mapper) {
        super(mapper, CounterfactualExplainabilityRequest.class);
    }

    @Override
    public CounterfactualExplainabilityRequest readFrom(ProtoStreamReader reader) throws IOException {
        return mapper.readValue(reader.readString(Constants.RAW_OBJECT_FIELD), CounterfactualExplainabilityRequest.class);
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, CounterfactualExplainabilityRequest input) throws IOException {
        writer.writeString(CounterfactualExplainabilityRequest.EXECUTION_ID_FIELD, input.getExecutionId());
        writer.writeString(CounterfactualExplainabilityRequest.COUNTERFACTUAL_ID_FIELD, input.getCounterfactualId());
        writer.writeString(Constants.RAW_OBJECT_FIELD, mapper.writeValueAsString(input));
    }
}
