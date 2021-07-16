/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.experimental.api;

public interface IntervalCluster<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>>
        extends Iterable<Interval_> {
    int size();

    boolean hasOverlap();

    Difference_ getLength();

    Point_ getStart();

    Point_ getEnd();
}
