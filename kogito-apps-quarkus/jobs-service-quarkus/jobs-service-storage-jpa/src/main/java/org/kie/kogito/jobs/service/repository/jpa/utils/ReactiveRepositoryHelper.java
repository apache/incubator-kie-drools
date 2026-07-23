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

package org.kie.kogito.jobs.service.repository.jpa.utils;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.narayana.jta.TransactionRunnerOptions;
import io.vertx.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReactiveRepositoryHelper {

    private Vertx vertx;

    @Inject
    public ReactiveRepositoryHelper(Vertx vertx) {
        this.vertx = vertx;
    }

    public <T> CompletionStage<T> runAsync(Supplier<T> blockingFunction) {
        return vertx.executeBlocking(() -> wrapInTransaction(blockingFunction)).toCompletionStage();
    }

    private <T> T wrapInTransaction(Supplier<T> function) {
        TransactionRunnerOptions runner = QuarkusTransaction.isActive() ? QuarkusTransaction.joiningExisting() : QuarkusTransaction.requiringNew();

        return runner.call(function::get);
    }
}
