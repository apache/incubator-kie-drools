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
package org.kie.kogito.quarkus.serverless.workflow.asyncapi;

import java.util.ServiceLoader;

import org.kie.kogito.quarkus.common.deployment.KogitoAddonsPreGeneratedSourcesBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.common.deployment.LiveReloadExecutionBuildItem;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.quarkiverse.asyncapi.config.AsyncAPISupplier;
import io.quarkiverse.asyncapi.config.MapAsyncAPIRegistry;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;

public class AsyncAPIProcessor {

    @BuildStep
    void asyncAPIContext(LiveReloadExecutionBuildItem reload, KogitoBuildContextBuildItem context, BuildProducer<KogitoAddonsPreGeneratedSourcesBuildItem> sources) {
        context.getKogitoBuildContext().addContextAttribute(ParserContext.ASYNC_CONVERTER_KEY, new AsyncAPIInfoConverter(
                new MapAsyncAPIRegistry(ServiceLoader.load(AsyncAPISupplier.class, reload.getClassLoader().orElse(Thread.currentThread().getContextClassLoader())))));
    }
}
