/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.evaluator.api.exceptions;

import org.kie.pmml.commons.exceptions.KiePMMLInternalException;

/**
 * Exception raised whenever there is an error on the <code>KiePMMLModel</code> as whole (e.g. un unexpected implementation received)
 */
public class KiePMMLModelException extends KiePMMLInternalException {

    public KiePMMLModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLModelException(String message) {
        super(message);
    }
}
