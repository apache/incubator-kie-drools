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

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.bi.SingleBiJoiner;

public final class Joiners {

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

    public static <A, B, Property_> BiJoiner<A, B> on(
            Function<A, Property_> leftMapping, JoinerType joinerType, Function <B, Property_> rightMapping) {
        return new SingleBiJoiner<A, B>(leftMapping, joinerType, rightMapping);
    }



    public static <A, B, C, Property_> TriJoiner<A, B, C> equalTo(
            BiFunction<A, B, Property_> leftMapping, Function <C, Property_> rightMapping) {
        return new TriJoiner<>(leftMapping, JoinerType.EQUAL_TO, rightMapping);
    }

    // TODO other TriJoiner methods

    public static <A, B, C, Property_> TriJoiner<A, B, C> on(
            BiFunction<A, B, Property_> leftMapping, JoinerType joinerType, Function <C, Property_> rightMapping) {
        return new TriJoiner<>(leftMapping, joinerType, rightMapping);
    }





    public static <A, B, Property_> BiFunction<A, B, Property_> firstBi(Function<A, Property_> mapping) {
        return (A a, B b) -> mapping.apply(a);
    }

    public static <A, B, Property_> BiFunction<A, B, Property_> secondBi(Function<B, Property_> mapping) {
        return (A a, B b) -> mapping.apply(b);
    }

    // TODO TriFunction firstTri, etc

    private Joiners() {}

}
