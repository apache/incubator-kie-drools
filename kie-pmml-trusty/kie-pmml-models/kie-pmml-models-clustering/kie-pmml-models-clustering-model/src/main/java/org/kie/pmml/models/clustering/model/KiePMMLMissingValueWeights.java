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
package org.kie.pmml.models.clustering.model;

import java.util.Arrays;
import java.util.List;

public class KiePMMLMissingValueWeights {

    private final List<Double> values;

    public KiePMMLMissingValueWeights(Double... values) {
        this.values = Arrays.asList(values);
    }

    public List<Double> getValues() {
        return values;
    }
}
