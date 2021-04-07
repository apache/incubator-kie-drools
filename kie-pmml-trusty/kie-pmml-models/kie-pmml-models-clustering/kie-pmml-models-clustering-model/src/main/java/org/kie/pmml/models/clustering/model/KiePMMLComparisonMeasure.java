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

import org.kie.pmml.models.clustering.model.aggregate.AggregateFunction;
import org.kie.pmml.models.clustering.model.compare.CompareFunction;

public class KiePMMLComparisonMeasure {

    private final Kind kind;
    private final AggregateFunction aggregateFunction;
    private final CompareFunction compareFunction;

    public KiePMMLComparisonMeasure(Kind kind, AggregateFunction aggregateFunction, CompareFunction compareFunction) {
        this.kind = kind;
        this.aggregateFunction = aggregateFunction;
        this.compareFunction = compareFunction;
    }

    public Kind getKind() {
        return kind;
    }

    public AggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    public CompareFunction getCompareFunction() {
        return compareFunction;
    }

    public enum Kind {
        DISTANCE,
        SIMILARITY
    }
}
