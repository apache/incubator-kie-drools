/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.clustering.model;

import org.kie.pmml.api.enums.Named;

public class KiePMMLComparisonMeasure {

    public enum Kind implements Named {
        DISTANCE("distance"),
        SIMILARITY("similarity");

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private final Kind kind;
    private final KiePMMLAggregateFunction aggregateFunction;
    private final KiePMMLCompareFunction compareFunction;

    public KiePMMLComparisonMeasure(Kind kind, KiePMMLAggregateFunction aggregateFunction, KiePMMLCompareFunction compareFunction) {
        this.kind = kind;
        this.aggregateFunction = aggregateFunction;
        this.compareFunction = compareFunction;
    }

    public Kind getKind() {
        return kind;
    }

    public KiePMMLAggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    public KiePMMLCompareFunction getCompareFunction() {
        return compareFunction;
    }
}
