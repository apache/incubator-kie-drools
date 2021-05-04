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
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest.COUNTERFACTUAL_GOALS;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest.COUNTERFACTUAL_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest.COUNTERFACTUAL_SEARCH_DOMAINS;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest.EXECUTION_ID_FIELD;

public class CounterfactualExplainabilityRequestMarshallerTest extends MarshallerTestTemplate<CounterfactualExplainabilityRequest> {

    private static final List<AbstractTestField<CounterfactualExplainabilityRequest, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(EXECUTION_ID_FIELD, "executionId", CounterfactualExplainabilityRequest::getExecutionId, CounterfactualExplainabilityRequest::setExecutionId),
            new StringTestField<>(COUNTERFACTUAL_ID_FIELD, "test", CounterfactualExplainabilityRequest::getCounterfactualId, CounterfactualExplainabilityRequest::setCounterfactualId),
            new CollectionTestField<>(COUNTERFACTUAL_GOALS, Collections.emptyList(), CounterfactualExplainabilityRequest::getGoals, CounterfactualExplainabilityRequest::setGoals,
                    TypedVariableWithValue.class),
            new CollectionTestField<>(COUNTERFACTUAL_SEARCH_DOMAINS, Collections.emptyList(), CounterfactualExplainabilityRequest::getSearchDomains,
                    CounterfactualExplainabilityRequest::setSearchDomains,
                    CounterfactualSearchDomain.class));

    public CounterfactualExplainabilityRequestMarshallerTest() {
        super(CounterfactualExplainabilityRequest.class);
    }

    @Override
    protected CounterfactualExplainabilityRequest buildEmptyObject() {
        return new CounterfactualExplainabilityRequest();
    }

    @Override
    protected MessageMarshaller<CounterfactualExplainabilityRequest> buildMarshaller() {
        return new CounterfactualExplainabilityRequestMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<CounterfactualExplainabilityRequest, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
