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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KiePMMLCluster {

    private final List<Double> values;
    private final Optional<String> id;
    private final Optional<String> name;

    public KiePMMLCluster(String id, String name, Double... values) {
        this.values = Collections.unmodifiableList(Stream.of(values).collect(Collectors.toList()));
        this.id = Optional.ofNullable(id);
        this.name = Optional.ofNullable(name);
    }

    public List<Double> getValues() {
        return values;
    }

    public double[] getValuesArray() {
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public Optional<String> getId() {
        return id;
    }

    public Optional<String> getName() {
        return name;
    }
}
