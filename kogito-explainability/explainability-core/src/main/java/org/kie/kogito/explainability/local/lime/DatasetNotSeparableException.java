/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime;

import java.util.Map;

import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.Output;

/**
 * Exception thrown when a dataset encoded for LIME is not (linearly) separable.
 */
public class DatasetNotSeparableException extends LocalExplanationException {

    public DatasetNotSeparableException(Output output, Map<Double, Long> classBalance) {
        super("LIME dataset not separable for output '" + output.getName() + "' of type '" + output.getType() + "' with '"
                      + output.getValue() + "' (" + classBalance + ")");
    }
}
