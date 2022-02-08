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
package org.kie.kogito.explainability.model.domain;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Set;

public class DurationFeatureDomain extends NumericalFeatureDomain {

    private final TemporalUnit unit;

    private DurationFeatureDomain(double lowerBound, double upperBound, TemporalUnit unit) {
        super(lowerBound, upperBound);
        this.unit = unit;
    }

    /**
     * Create a {@link FeatureDomain} for a continuous feature
     *
     * @param lowerBound The start point of the search space
     * @param upperBound The end point of the search space
     * @return A {@link FeatureDomain}
     */
    public static FeatureDomain<Duration> create(double lowerBound, double upperBound, TemporalUnit unit) {
        return new DurationFeatureDomain(lowerBound, upperBound, unit);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Double getLowerBound() {
        return this.lowerBound;
    }

    @Override
    public Double getUpperBound() {
        return this.upperBound;
    }

    @Override
    public Set<Duration> getCategories() {
        return null;
    }

    public TemporalUnit getUnit() {
        return unit;
    }
}
