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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility class to wrap a value with the possibility to specify error message or propose valid value.
 * Note: null can be used as value.
 * @param <T>
 */
public class ValueWrapper<T> {

    private final boolean valid;

    private final T value;
    private final T expected;
    private final String errorMessage;
    private final List<String> pathToValue;

    private ValueWrapper(T value, T expected, boolean valid, String errorMessage, List<String> pathToValue) {
        this.valid = valid;
        this.value = value;
        this.expected = expected;
        this.errorMessage = errorMessage;
        this.pathToValue = pathToValue;
    }

    public static <T> ValueWrapper<T> of(T value) {
        return new ValueWrapper<>(value, null, true, null, null);
    }

    public static <T> ValueWrapper<T> errorWithValidValue(T value, T expected) {
        return new ValueWrapper<>(value, expected, false, null, null);
    }

    public static <T> ValueWrapper<T> errorWithMessage(String message) {
        return new ValueWrapper<>(null, null, false, message, null);
    }

    public static <T> ValueWrapper<T> errorEmptyMessage() {
        return new ValueWrapper<>(null, null, false, null, null);
    }

    public static <T> ValueWrapper<T> errorWithPath(T value, List<String> path) {
        return new ValueWrapper<>(value, null, false, null, path);
    }

    public boolean isValid() {
        return valid;
    }

    public T getValue() {
        return value;
    }

    public T getExpected() {
        return expected;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public List<String> getPathToValue() {
        return pathToValue;
    }

    public T orElse(T defaultValue) {
        return valid ? value : defaultValue;
    }

    public T orElseGet(Supplier<T> defaultSupplier) {
        return valid ? value : defaultSupplier.get();
    }
}
