/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.OnOverflow.Strategy;

public class OnOverflowInfo {

    private final Strategy strategy;

    private final Optional<Long> bufferSize;

    protected OnOverflowInfo(Strategy strategy, Optional<Long> bufferSize) {
        this.strategy = strategy;
        this.bufferSize = bufferSize;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Optional<Long> getBufferSize() {
        return bufferSize;
    }

    @Override
    public String toString() {
        return "OnOverflowInfo [strategy=" + strategy + ", bufferSize=" + bufferSize + "]";
    }
}
