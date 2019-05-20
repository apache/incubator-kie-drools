/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.runner.model;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * java.util.Optional clone to have the null result
 * @param <T>
 */
public class ResultWrapper<T> {

    private final boolean satisfied;

    private final T result;
    private final T expected;
    private final String errorMessage;

    private ResultWrapper(T result, T expected, boolean satisfied, String errorMessage) {
        this.satisfied = satisfied;
        this.result = result;
        this.expected = expected;
        this.errorMessage = errorMessage;
    }

    public static <T> ResultWrapper<T> createResult(T result) {
        return new ResultWrapper<>(result, null, true, null);
    }

    public static <T> ResultWrapper<T> createErrorResult(T result, T expected) {
        return new ResultWrapper<>(result, expected, false, null);
    }

    public static <T> ResultWrapper<T> createErrorResultWithErrorMessage(String errorMessage) {
        return new ResultWrapper<>(null, null, false, errorMessage);
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public T getResult() {
        return result;
    }

    public T getExpected() {
        return expected;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public T orElse(T defaultValue) {
        return satisfied ? result : defaultValue;
    }

    public T orElseGet(Supplier<T> defaultSupplier) {
        return satisfied ? result : defaultSupplier.get();
    }
}
