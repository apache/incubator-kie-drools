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
package org.kie.kogito.explainability.local.lime;

import java.util.function.DoublePredicate;
import java.util.stream.DoubleStream;

public class HighScoreNumericFeatureZones implements DoublePredicate {

    private final double[] highFeatureScorePoint;
    private final double tolerance;

    public HighScoreNumericFeatureZones(double[] highFeatureScorePoint, double tolerance) {
        this.highFeatureScorePoint = highFeatureScorePoint;
        this.tolerance = tolerance;
    }

    @Override
    public boolean test(double point) {
        return DoubleStream.of(highFeatureScorePoint).anyMatch(d -> point > (d - tolerance) && point < (d + tolerance));
    }
}
