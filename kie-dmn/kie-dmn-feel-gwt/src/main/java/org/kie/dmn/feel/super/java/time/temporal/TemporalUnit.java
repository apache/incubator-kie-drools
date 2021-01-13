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
package java.time.temporal;

import java.time.Duration;

public interface TemporalUnit {

    Duration getDuration();

    boolean isDurationEstimated();

    boolean isDateBased();

    boolean isTimeBased();

    default boolean isSupportedBy(Temporal temporal) {
        return false;
    }

    <R extends Temporal> R addTo(R temporal, long amount);

    long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive);

    String toString();
}
