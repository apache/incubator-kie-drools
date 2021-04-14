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
import org.kie.kogito.trusty.storage.api.model.CounterfactualRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.CounterfactualRequest.COUNTERFACTUAL_GOALS;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualRequest.COUNTERFACTUAL_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualRequest.COUNTERFACTUAL_SEARCH_DOMAINS;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualRequest.EXECUTION_ID_FIELD;

public class CounterfactualRequestMarshallerTest extends MarshallerTestTemplate<CounterfactualRequest> {

    private static final List<AbstractTestField<CounterfactualRequest, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(EXECUTION_ID_FIELD, "executionId", CounterfactualRequest::getExecutionId, CounterfactualRequest::setExecutionId),
            new StringTestField<>(COUNTERFACTUAL_ID_FIELD, "test", CounterfactualRequest::getCounterfactualId, CounterfactualRequest::setCounterfactualId),
            new CollectionTestField<>(COUNTERFACTUAL_GOALS, Collections.emptyList(), CounterfactualRequest::getGoals, CounterfactualRequest::setGoals, TypedVariableWithValue.class),
            new CollectionTestField<>(COUNTERFACTUAL_SEARCH_DOMAINS, Collections.emptyList(), CounterfactualRequest::getSearchDomains, CounterfactualRequest::setSearchDomains,
                    CounterfactualSearchDomain.class));

    public CounterfactualRequestMarshallerTest() {
        super(CounterfactualRequest.class);
    }

    @Override
    protected CounterfactualRequest buildEmptyObject() {
        return new CounterfactualRequest();
    }

    @Override
    protected MessageMarshaller<CounterfactualRequest> buildMarshaller() {
        return new CounterfactualMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<CounterfactualRequest, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
