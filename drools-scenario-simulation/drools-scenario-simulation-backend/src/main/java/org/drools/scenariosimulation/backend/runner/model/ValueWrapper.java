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
 * Utility class to wrap a value with the possibility to specify error message or propose valid value.
 * Note: null can be used as value.
 * @param <T>
 */
public class ValueWrapper<T> {

    private final boolean valid;

    private final T value;
    private final T expected;
    private final String errorMessage;

    private ValueWrapper(T value, T expected, boolean valid, String errorMessage) {
        this.valid = valid;
        this.value = value;
        this.expected = expected;
        this.errorMessage = errorMessage;
    }

    public static <T> ValueWrapper<T> of(T value) {
        return new ValueWrapper<>(value, null, true, null);
    }

    public static <T> ValueWrapper<T> errorWithValidValue(T value, T expected) {
        return new ValueWrapper<>(value, expected, false, null);
    }

    public static <T> ValueWrapper<T> errorWithMessage(String message) {
        return new ValueWrapper<>(null, null, false, message);
    }

    public static <T> ValueWrapper<T> errorEmptyMessage() {
        return new ValueWrapper<>(null, null, false, null);
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

    public T orElse(T defaultValue) {
        return valid ? value : defaultValue;
    }

    public T orElseGet(Supplier<T> defaultSupplier) {
        return valid ? value : defaultSupplier.get();
    }
}
