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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    private final String processId;
    private final Collection<ValidationError> errors;

    public ValidationException(String processId, Collection<? extends ValidationError> errors) {
        this.processId = processId;
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public ValidationException(String processId, ValidationError error) {
        this(processId, Collections.singleton(error));
    }

    public ValidationException(String processId, String errorMessage) {
        this(processId, Collections.singleton(() -> errorMessage));
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }

    public String getProcessId() {
        return processId;
    }

    @Override
    public String getMessage() {
        return errors.stream().map(ValidationError::getMessage).collect(Collectors.joining(" "));
    }
}
