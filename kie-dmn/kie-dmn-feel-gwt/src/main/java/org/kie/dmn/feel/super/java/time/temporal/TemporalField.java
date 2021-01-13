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

import java.util.Locale;
import java.util.Map;

import java.time.format.ResolverStyle;

public interface TemporalField {

    default String getDisplayName(Locale locale) {
        return null;
    }

    TemporalUnit getBaseUnit();

    TemporalUnit getRangeUnit();

    ValueRange range();

    boolean isDateBased();

    boolean isTimeBased();

    boolean isSupportedBy(TemporalAccessor temporal);

    ValueRange rangeRefinedBy(TemporalAccessor temporal);

    long getFrom(TemporalAccessor temporal);

    <R extends Temporal> R adjustInto(R temporal, long newValue);

    default TemporalAccessor resolve(Map<java.time.temporal.TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
        return null;
    }

    String toString();
}
