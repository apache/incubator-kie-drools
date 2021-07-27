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
package org.jbpm.bpmn2.xml;

import java.util.Arrays;

import org.kie.kogito.process.validation.ValidationException;

public class ProcessParsingValidationException extends ValidationException {

    //TODO: inject processId or fileName to identify the the process
    public ProcessParsingValidationException(String message) {
        super(null, Arrays.asList(() -> message));
    }

    public ProcessParsingValidationException(String processId, String message) {
        super(processId, Arrays.asList(() -> message));
    }
}
