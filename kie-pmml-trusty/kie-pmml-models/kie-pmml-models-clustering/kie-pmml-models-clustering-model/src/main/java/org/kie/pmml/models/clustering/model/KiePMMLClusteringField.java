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

import java.util.Optional;

public class KiePMMLClusteringField {

    private final String field;
    private final Double fieldWeight;
    private final Boolean isCenterField;
    private final Optional<KiePMMLCompareFunction> compareFunction;
    private final Optional<Double> similarityScale;

    public KiePMMLClusteringField(String field, Double fieldWeight, Boolean isCenterField, KiePMMLCompareFunction compareFunction, Double similarityScale) {
        this.field = field;
        this.fieldWeight = fieldWeight;
        this.isCenterField = isCenterField;
        this.compareFunction = Optional.ofNullable(compareFunction);
        this.similarityScale = Optional.ofNullable(similarityScale);
    }

    public String getField() {
        return field;
    }

    public Double getFieldWeight() {
        return fieldWeight;
    }

    public Boolean getCenterField() {
        return isCenterField;
    }

    public Optional<KiePMMLCompareFunction> getCompareFunction() {
        return compareFunction;
    }

    public Optional<Double> getSimilarityScale() {
        return similarityScale;
    }
}
