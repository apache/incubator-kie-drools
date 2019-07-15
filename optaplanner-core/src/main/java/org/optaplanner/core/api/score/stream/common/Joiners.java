/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.common;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.bi.SingleBiJoiner;
import org.optaplanner.core.impl.score.stream.tri.SingleTriJoiner;

public final class Joiners {

    // ************************************************************************
    // BiJoiner
    // ************************************************************************

    public static <A, Property_> BiJoiner<A, A> equalTo(
            Function<A, Property_> mapping) {
        return equalTo(mapping, mapping);
    }

    public static <A, B, Property_> BiJoiner<A, B> equalTo(
            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.EQUAL_TO, rightMapping);
    }

    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> lessThan(
            Function<A, Property_> mapping) {
        return lessThan(mapping, mapping);
    }

    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> lessThan(
            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.LESS_THAN, rightMapping);
    }

    // TODO Support using a non-natural comparator:
//    public static <A, B, Property_> BiJoiner<A, B> lessThan(
//            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping, Comparable<Property_> comparator);

    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> lessThanOrEqualTo(
            Function<A, Property_> mapping) {
        return lessThanOrEqualTo(mapping, mapping);
    }

    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> lessThanOrEqualTo(
            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.LESS_THAN_OR_EQUAL_TO, rightMapping);
    }

    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> greaterThan(
            Function<A, Property_> mapping) {
        return greaterThan(mapping, mapping);
    }

    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> greaterThan(
            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.GREATER_THAN, rightMapping);
    }

    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> greaterThanOrEqualTo(
            Function<A, Property_> mapping) {
        return greaterThanOrEqualTo(mapping, mapping);
    }

    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> greaterThanOrEqualTo(
            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.GREATER_THAN_OR_EQUAL_TO, rightMapping);
    }

    public static <A, B, Property_> BiJoiner<A, B> contains(
            Function<A, ? extends Collection<Property_>> leftMapping, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.CONTAINS, rightMapping);
    }

    public static <A, B, Property_> BiJoiner<A, B> isContainedBy(
            Function<A, Property_> leftMapping, Function <B, ? extends Collection<Property_>> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.IS_CONTAINED_BY, rightMapping);
    }

    public static <A, Property_> BiJoiner<A, A> intersectingWith(
            Function<A, ? extends Collection<Property_>> mapping) {
        return intersectingWith(mapping, mapping);
    }

    public static <A, B, Property_> BiJoiner<A, B> intersectingWith(
            Function<A, ? extends Collection<Property_>> leftMapping, Function <B, ? extends Collection<Property_>> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.INTERSECTING_WITH, rightMapping);
    }

    public static <A, Property_> BiJoiner<A, A> disjoint(
            Function<A, ? extends Collection<Property_>> mapping) {
        return disjoint(mapping, mapping);
    }

    public static <A, B, Property_> BiJoiner<A, B> disjoint(
            Function<A, ? extends Collection<Property_>> leftMapping, Function <B, ? extends Collection<Property_>> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.DISJOINT, rightMapping);
    }

    // TODO
    // join(..., planningVariableContainsCached(Talk::getPeriod, (Period a, Period b) -> a.overlaps(b)))
    // get the period value range, does a cartesian product on it, so it maps every period to an overlapping periodList
    // then keep an index from every period to all talks in an overlapping period (possible the same period)

    // ************************************************************************
    // TriJoiner
    // ************************************************************************

    public static <A, B, C, Property_> TriJoiner<A, B, C> equalTo(
            BiFunction<A, B, Property_> leftMapping, Function <C, Property_> rightMapping) {
        return new SingleTriJoiner<>(leftMapping, JoinerType.EQUAL_TO, rightMapping);
    }

    // TODO other TriJoiner methods

    private Joiners() {}

}
