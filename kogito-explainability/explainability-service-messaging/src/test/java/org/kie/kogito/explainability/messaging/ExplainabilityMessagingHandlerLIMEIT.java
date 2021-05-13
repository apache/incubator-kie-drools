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

package org.kie.kogito.explainability.messaging;

import java.util.Collections;
import java.util.function.Consumer;

import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequestDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class ExplainabilityMessagingHandlerLIMEIT extends BaseExplainabilityMessagingHandlerIT {

    @Override
    protected BaseExplainabilityRequestDto buildRequest() {
        return new LIMEExplainabilityRequestDto(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER_DTO,
                Collections.emptyMap(),
                Collections.emptyMap());
    }

    @Override
    protected BaseExplainabilityResultDto buildResult() {
        return LIMEExplainabilityResultDto.buildSucceeded(EXECUTION_ID, Collections.emptyMap());
    }

    @Override
    protected void assertResult(BaseExplainabilityResultDto result) {
        assertTrue(result instanceof LIMEExplainabilityResultDto);
    }

    @Override
    protected int getTotalExpectedEventCountWithIntermediateResults() {
        return 1;
    }

    @Override
    protected void mockExplainAsyncInvocationWithIntermediateResults(Consumer<BaseExplainabilityResultDto> callback) {
        //LIME does not support intermediate results; so nothing to do!
    }
}
