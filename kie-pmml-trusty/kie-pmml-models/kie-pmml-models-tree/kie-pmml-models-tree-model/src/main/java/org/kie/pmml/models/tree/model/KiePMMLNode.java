/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.tree.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLNode extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -3166618610223066816L;

    protected KiePMMLNode(final String name,
                          final List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    protected static Optional<Object> getNestedScore(final List<Function<Map<String, Object>, Object>> nodeFunctions, final Map<String, Object> requestData) {
        Optional<Object> toReturn = Optional.empty();
        for (Function<Map<String, Object>, Object> function : nodeFunctions) {
            final Object evaluation = function.apply(requestData);
            toReturn = Optional.ofNullable(evaluation);
            if (toReturn.isPresent()) {
                break;
            }
        }
        return toReturn;
    }
}
