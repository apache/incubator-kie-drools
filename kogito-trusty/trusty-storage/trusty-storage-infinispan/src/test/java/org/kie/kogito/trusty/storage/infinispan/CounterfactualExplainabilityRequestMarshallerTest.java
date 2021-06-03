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
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CounterfactualExplainabilityRequestMarshallerTest extends MarshallerTestTemplate {

    @Test
    public void testWriteAndRead() throws IOException {
        List<TypedVariableWithValue> goals = Collections.singletonList(TypedVariableWithValue.buildUnit("unitIn", "number", JsonNodeFactory.instance.numberNode(10)));
        List<CounterfactualSearchDomain> searchDomains = Collections.singletonList(
                new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                        "unitIn",
                        "number",
                        null,
                        false,
                        new CounterfactualDomainRange(JsonNodeFactory.instance.numberNode(0), JsonNodeFactory.instance.numberNode(10))));
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest("executionId", "counterfactualId", goals, searchDomains);
        CounterfactualExplainabilityRequestMarshaller marshaller = new CounterfactualExplainabilityRequestMarshaller(new ObjectMapper());

        marshaller.writeTo(writer, request);
        CounterfactualExplainabilityRequest retrieved = marshaller.readFrom(reader);

        Assertions.assertEquals(request.getExecutionId(), retrieved.getExecutionId());
        Assertions.assertEquals(request.getCounterfactualId(), retrieved.getCounterfactualId());
        Assertions.assertEquals(goals.get(0).getName(), retrieved.getGoals().stream().findFirst().get().getName());
        Assertions.assertEquals(searchDomains.get(0).getName(), retrieved.getSearchDomains().stream().findFirst().get().getName());
        Assertions.assertEquals(0, ((CounterfactualDomainRange) retrieved.getSearchDomains().stream().findFirst().get().getDomain()).getLowerBound().asInt());
    }
}
