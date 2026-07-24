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
package org.kie.kogito.explainability;

import java.util.Collection;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.kie.kogito.explainability.api.HasNameValue;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.tracing.typedvalue.TypedValue;

import io.vertx.mutiny.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PredictionProviderFactoryImpl implements PredictionProviderFactory {

    private final Vertx vertx;
    private final ThreadContext threadContext;
    private final ManagedExecutor managedExecutor;

    @Inject
    public PredictionProviderFactoryImpl(
            Vertx vertx,
            ThreadContext threadContext,
            ManagedExecutor managedExecutor) {

        this.vertx = vertx;
        this.threadContext = threadContext;
        this.managedExecutor = managedExecutor;
    }

    @Override
    public PredictionProvider createPredictionProvider(String serviceUrl,
            ModelIdentifier modelIdentifier,
            Collection<? extends HasNameValue<TypedValue>> predictionOutputs) {
        return new RemotePredictionProvider(serviceUrl,
                modelIdentifier,
                predictionOutputs,
                vertx,
                threadContext,
                managedExecutor);
    }
}
