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
package org.kie.kogito.tracing.decision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Startup
public class QuarkusStartupModelPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusStartupModelPublisher.class);

    @Inject
    QuarkusModelEventEmitter modelEventEmitter;

    // The bean that fires the event at startup must be separated from the kafka connector due to https://github.com/quarkusio/quarkus/issues/12820
    public void publish(@Observes StartupEvent event) {
        LOGGER.debug("Publishing decision models to the kafka topic");
        modelEventEmitter.publishDecisionModels();
    }
}
