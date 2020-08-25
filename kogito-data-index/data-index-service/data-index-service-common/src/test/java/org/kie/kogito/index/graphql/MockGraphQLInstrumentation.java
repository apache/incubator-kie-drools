/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql;

import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.reactive.CompletionStageMappingPublisher;
import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class MockGraphQLInstrumentation extends GraphQLInstrumentation {

    private CompletableFuture<Void> future;

    public void setFuture(CompletableFuture<Void> future) {
        this.future = future;
    }

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        CompletableFuture<ExecutionResult> result = super.instrumentExecutionResult(executionResult, parameters);
        if (future != null && executionResult.getData() instanceof CompletionStageMappingPublisher) {
            return result.whenComplete((r, t) -> future.complete(null));
        } else {
            return result;
        }
    }
}
