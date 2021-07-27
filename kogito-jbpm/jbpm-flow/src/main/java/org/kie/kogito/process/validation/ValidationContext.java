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
package org.kie.kogito.process.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ValidationContext {

    private ThreadLocal<Map<String, Collection<ValidationError>>> context;
    private ThreadLocal<Exception> exceptionHolder;

    private static ValidationContext instance;

    private ValidationContext() {
        this.context = new ThreadLocal<>();
        this.exceptionHolder = new ThreadLocal<>();
    }

    public static ValidationContext get() {
        return Optional.ofNullable(ValidationContext.instance)
                .orElseGet(() -> {
                    ValidationContext.instance = new ValidationContext();
                    return ValidationContext.instance;
                });
    }

    public ValidationContext add(String resourceId, ValidationError error) {
        getErrors(resourceId).add(error);
        return this;
    }

    public ValidationContext putException(Exception exception) {
        exceptionHolder.set(exception);
        return this;
    }

    public Optional<Exception> exception() {
        return Optional.ofNullable(exceptionHolder.get());
    }

    public ValidationContext add(String resourceId, Collection<ValidationError> errors) {
        getErrors(resourceId).addAll(errors);
        return this;
    }

    private Collection<ValidationError> getErrors(String resourceId) {
        getContext().putIfAbsent(resourceId, new ArrayList<>());
        return getContext().get(resourceId);
    }

    public Collection<ValidationError> errors(String resourceId) {
        return Collections.unmodifiableCollection(getContext().get(resourceId));
    }

    public boolean hasErrors(String resourceId) {
        return getContext().containsKey(resourceId);
    }

    public boolean hasErrors() {
        return !getContext().isEmpty();
    }

    public void clear() {
        if (Objects.isNull(context.get())) {
            return;
        }
        context.get().values().forEach(Collection::clear);
        context.get().clear();
        context.remove();

        exceptionHolder.remove();
    }

    public Set<String> resourcesWithError() {
        return getContext().keySet();
    }

    private Map<String, Collection<ValidationError>> getContext() {
        return Optional.ofNullable(context.get())
                .orElseGet(() -> {
                    context.set(new HashMap<>());
                    return context.get();
                });
    }
}
