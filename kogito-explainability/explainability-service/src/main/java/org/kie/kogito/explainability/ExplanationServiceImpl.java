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

package org.kie.kogito.explainability;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.models.ExplainabilityRequest;

@ApplicationScoped
public class ExplanationServiceImpl implements ExplanationService {

    @Inject
    ManagedExecutor executor;

    @Override
    public CompletionStage<ExplainabilityResultDto> explainAsync(ExplainabilityRequest request) {
        // TODO: get explainability from expl library https://issues.redhat.com/browse/KOGITO-2920
        return CompletableFuture.supplyAsync(() -> new ExplainabilityResultDto(request.getExecutionId()), executor);
    }
}
