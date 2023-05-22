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
package org.kie.kogito.event;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class Subscription<T, S> {
    private final Function<T, CompletionStage<?>> consumer;
    private final Converter<S, T> converter;

    public Subscription(Function<T, CompletionStage<?>> consumer, Converter<S, T> converter) {
        this.consumer = consumer;
        this.converter = converter;
    }

    public Function<T, CompletionStage<?>> getConsumer() {
        return consumer;
    }

    public Converter<S, T> getConverter() {
        return converter;
    }
}
