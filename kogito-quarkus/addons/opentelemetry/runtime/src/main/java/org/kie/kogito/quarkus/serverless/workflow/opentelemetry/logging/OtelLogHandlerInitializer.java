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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry.logging;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class OtelLogHandlerInitializer {

    private static final OtelLogHandler handler = new OtelLogHandler();

    void onStart(@Observes StartupEvent ev) {
        Logger rootLogger = Logger.getLogger("");
        handler.setMinimumLevel("INFO");
        handler.setLevel(Level.INFO);
        if (Arrays.stream(rootLogger.getHandlers()).noneMatch(h -> h instanceof OtelLogHandler)) {
            rootLogger.addHandler(handler);
        }

        // Note: Kogito logs are automatically captured via root logger inheritance.
        // Previously registered handler on "org.kie.kogito" logger caused duplication
        // because the same handler processed logs at both levels due to propagation.
    }
}
