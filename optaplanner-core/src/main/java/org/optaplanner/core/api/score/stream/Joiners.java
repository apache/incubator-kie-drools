/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.FilteringBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.SingleBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.penta.AbstractPentaJoiner;
import org.optaplanner.core.impl.score.stream.penta.FilteringPentaJoiner;
import org.optaplanner.core.impl.score.stream.penta.SinglePentaJoiner;
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.FilteringQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.SingleQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.FilteringTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.SingleTriJoiner;

/**
 * Creates an {@link BiJoiner}, {@link TriJoiner}, ... instance
 * for use in {@link UniConstraintStream#join(Class, BiJoiner)}, ...
 */
public final class Joiners {

    // TODO Support using non-natural comparators, such as lessThan(leftMapping, rightMapping, comparator).

    // ************************************************************************
    // BiJoiner
    // ************************************************************************

    /**
     * Joins every A and B that are equal.
     * Delegates to {@link #equal(Function)} with {@link Function#identity()} as the argument.
     *
     * @param <A> the type of both objects
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where A and B
     *         are equal
     */
    public static <A> BiJoiner<A, A> equal() {
        return equal(Function.identity());
    }

    /**
     * Joins every A and B that share a property.
     * These are exactly the pairs where {@code mapping.apply(A).equals(mapping.apply(B))}.
     * Delegates to {@link #equal(Function, Function)} with both arguments using the same mapping.
     *
     * @param <A> the type of both objects
     * @param <Property_> the type of the property to compare
     * @param mapping mapping function to apply to both A and B
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where the same
     *         property of A and B is equal
     */
    public static <A, Property_> BiJoiner<A, A> equal(Function<A, Property_> mapping) {
        return equal(mapping, mapping);
    }

    /**
     * Joins every A and B that share a property.
     * These are exactly the pairs where {@code leftMapping.apply(A).equals(rightMapping.apply(B))}.
     *
     * @param <A> the type of object on the left
     * @param <B> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @param leftMapping mapping function to apply to A
     * @param rightMapping mapping function to apply to B
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A equals that of B
     */
    public static <A, B, Property_> BiJoiner<A, B> equal(Function<A, Property_> leftMapping,
            Function<B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.EQUAL, rightMapping);
    }

    /**
     * Joins every A and B where a value of property on A is less than the value of the same property on B.
     * These are exactly the pairs where {@code mapping.apply(A).compareTo(mapping.apply(B)) < 0}.
     * Delegates to {@link #lessThan(Function, Function)} with both arguments using the same mapping.
     *
     * @param mapping mapping function to apply
     * @param <A> the type of both objects
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is less than that of B
     */
    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> lessThan(Function<A, Property_> mapping) {
        return lessThan(mapping, mapping);
    }

    /**
     * Joins every A and B where a value of property on A is less than the value of a property on B.
     * These are exactly the pairs where {@code leftMapping.apply(A).compareTo(rightMapping.apply(B)) < 0}.
     *
     * @param leftMapping mapping function to apply to A
     * @param rightMapping mapping function to apply to B
     * @param <A> the type of object on the left
     * @param <B> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is less than a property of B
     */
    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> lessThan(
            Function<A, Property_> leftMapping, Function<B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.LESS_THAN, rightMapping);
    }

    /**
     * Joins every A and B where a value of property on A is less than or equal to the value of the same property on B.
     * These are exactly the pairs where {@code mapping.apply(A).compareTo(mapping.apply(B)) <= 0}.
     * Delegates to {@link #lessThanOrEqual(Function, Function)} with both arguments using the same mapping.
     *
     * @param mapping mapping function to apply
     * @param <A> the type of both objects
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is less than or equal to that of B
     */
    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> lessThanOrEqual(
            Function<A, Property_> mapping) {
        return lessThanOrEqual(mapping, mapping);
    }

    /**
     * Joins every A and B where a value of property on A is less than or equal to the value of a property on B.
     * These are exactly the pairs where {@code leftMapping.apply(A).compareTo(rightMapping.apply(B)) <= 0}.
     *
     * @param leftMapping mapping function to apply to A
     * @param rightMapping mapping function to apply to B
     * @param <A> the type of object on the left
     * @param <B> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is less than or equal to a property of B
     */
    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> lessThanOrEqual(
            Function<A, Property_> leftMapping, Function<B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.LESS_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Joins every A and B where a value of property on A is greater than the value of the same property on B.
     * These are exactly the pairs where {@code mapping.apply(A).compareTo(mapping.apply(B)) > 0}.
     * Delegates to {@link #greaterThan(Function, Function)} with both arguments using the same mapping.
     *
     * @param mapping mapping function to apply
     * @param <A> the type of both objects
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is greater than that of B
     */
    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> greaterThan(
            Function<A, Property_> mapping) {
        return greaterThan(mapping, mapping);
    }

    /**
     * Joins every A and B where a value of property on A is greater than the value of a property on B.
     * These are exactly the pairs where {@code leftMapping.apply(A).compareTo(rightMapping.apply(B)) > 0}.
     *
     * @param leftMapping mapping function to apply to A
     * @param rightMapping mapping function to apply to B
     * @param <A> the type of object on the left
     * @param <B> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is greater than a property of B
     */
    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> greaterThan(
            Function<A, Property_> leftMapping, Function<B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.GREATER_THAN, rightMapping);
    }

    /**
     * Joins every A and B where a value of property on A is greater than or equal to the value of
     * the same property on B.
     * These are exactly the pairs where {@code mapping.apply(A).compareTo(mapping.apply(B)) >= 0}.
     * Delegates to {@link #greaterThanOrEqual(Function, Function)} with both arguments using the same mapping.
     *
     * @param mapping mapping function to apply
     * @param <A> the type of both objects
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is greater than or equal to that of B
     */
    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> greaterThanOrEqual(
            Function<A, Property_> mapping) {
        return greaterThanOrEqual(mapping, mapping);
    }

    /**
     * Joins every A and B where a value of property on A is greater than or equal to the value of a property on B.
     * These are exactly the pairs where {@code leftMapping.apply(A).compareTo(rightMapping.apply(B)) >= 0}.
     *
     * @param leftMapping mapping function to apply to A
     * @param rightMapping mapping function to apply to B
     * @param <A> the type of object on the left
     * @param <B> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where a property
     *         of A is greater than or equal to a property of B
     */
    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> greaterThanOrEqual(
            Function<A, Property_> leftMapping, Function<B, Property_> rightMapping) {
        return new SingleBiJoiner<>(leftMapping, JoinerType.GREATER_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Applies a filter to the joined tuple, with the semantics of {@link BiConstraintStream#filter(BiPredicate)}.
     *
     * @param filter never null, filter to apply
     * @param <A> type of the first fact in the tuple
     * @param <B> type of the second fact in the tuple
     * @return never null
     */
    public static <A, B> BiJoiner<A, B> filtering(BiPredicate<A, B> filter) {
        return new FilteringBiJoiner<>(filter);
    }

    /*
     * // TODO implement these joiners
     * public static <A, B, Property_> BiJoiner<A, B> containing(
     * Function<A, ? extends Collection<Property_>> leftMapping, Function <B, Property_> rightMapping) {
     * return new SingleBiJoiner<>(leftMapping, JoinerType.CONTAINING, rightMapping);
     * }
     *
     * // TODO containedBy (inverse contains relationship)
     *
     * public static <A, Property_> BiJoiner<A, A> intersecting(
     * Function<A, ? extends Collection<Property_>> mapping) {
     * return intersecting(mapping, mapping);
     * }
     *
     * public static <A, B, Property_> BiJoiner<A, B> intersecting(
     * Function<A, ? extends Collection<Property_>> leftMapping,
     * Function <B, ? extends Collection<Property_>> rightMapping) {
     * return new SingleBiJoiner<>(leftMapping, JoinerType.INTERSECTING, rightMapping);
     * }
     *
     * public static <A, Property_> BiJoiner<A, A> disjoint(Function<A, ? extends Collection<Property_>> mapping) {
     * return disjoint(mapping, mapping);
     * }
     *
     * public static <A, B, Property_> BiJoiner<A, B> disjoint(Function<A, ? extends Collection<Property_>> leftMapping,
     * Function <B, ? extends Collection<Property_>> rightMapping) {
     * return new SingleBiJoiner<>(leftMapping, JoinerType.DISJOINT, rightMapping);
     * }
     */

    /**
     * Joins every A and B that overlap for an interval which is specified by a start and end property on both A and B.
     * These are exactly the pairs where {@code A.start < B.end} and {@code A.end > B.start}.
     *
     * @param startMapping maps the argument to the start point of its interval (inclusive)
     * @param endMapping maps the argument to the end point of its interval (exclusive)
     *
     * @param <A> the type of both the first and second argument
     * @param <Property_> the type used to define the interval, comparable
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where A's and B's
     *         intervals (as defined by the function mapping) overlap
     */
    public static <A, Property_ extends Comparable<Property_>> BiJoiner<A, A> overlapping(
            Function<A, Property_> startMapping,
            Function<A, Property_> endMapping) {
        return overlapping(startMapping, endMapping, startMapping, endMapping);
    }

    /**
     * As defined by {@link #overlapping(Function, Function)}.
     *
     * @param leftStartMapping maps the first argument to its interval start point (inclusive)
     * @param leftEndMapping maps the first argument to its interval end point (exclusive)
     * @param rightStartMapping maps the second argument to its interval start point (inclusive)
     * @param rightEndMapping maps the second argument to its interval end point (exclusive)
     *
     * @param <A> the type of the first argument
     * @param <B> the type of the second argument
     * @param <Property_> the type used to define the interval, comparable
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B) where A's and B's
     *         intervals (as defined by the function mapping) overlap
     */
    public static <A, B, Property_ extends Comparable<Property_>> BiJoiner<A, B> overlapping(
            Function<A, Property_> leftStartMapping,
            Function<A, Property_> leftEndMapping,
            Function<B, Property_> rightStartMapping,
            Function<B, Property_> rightEndMapping) {
        return AbstractBiJoiner.merge(Joiners.lessThan(leftStartMapping, rightEndMapping),
                Joiners.greaterThan(leftEndMapping, rightStartMapping));
    }

    // ************************************************************************
    // TriJoiner
    // ************************************************************************

    /**
     * Joins every (A,B) and C that share a property.
     * These are exactly the pairs where {@code leftMapping.apply(A, B).equals(rightMapping.apply(C))}.
     *
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the object on the right
     * @param <Property_> the type of the property to compare
     * @param leftMapping mapping function to apply to (A,B)
     * @param rightMapping mapping function to apply to C
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C) where a property
     *         of (A,B) equals that of C
     */
    public static <A, B, C, Property_> TriJoiner<A, B, C> equal(BiFunction<A, B, Property_> leftMapping,
            Function<C, Property_> rightMapping) {
        return new SingleTriJoiner<>(leftMapping, JoinerType.EQUAL, rightMapping);
    }

    /**
     * Joins every (A,B) and C where a value of property of (A,B) is less than the value of a property of C.
     * These are exactly the pairs where {@code leftMapping.apply(A, B).compareTo(rightMapping.apply(C)) < 0}.
     *
     * @param leftMapping mapping function to apply to (A,B)
     * @param rightMapping mapping function to apply to C
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C)
     *         where a property of (A,B) is less than a property of C
     */
    public static <A, B, C, Property_ extends Comparable<Property_>> TriJoiner<A, B, C> lessThan(
            BiFunction<A, B, Property_> leftMapping, Function<C, Property_> rightMapping) {
        return new SingleTriJoiner<>(leftMapping, JoinerType.LESS_THAN, rightMapping);
    }

    /**
     * Joins every (A,B) and C where a value of property of (A,B) is less than or equal to the value of a property of C.
     * These are exactly the pairs where {@code leftMapping.apply(A, B).compareTo(rightMapping.apply(C)) <= 0}.
     *
     * @param leftMapping mapping function to apply to (A,B)
     * @param rightMapping mapping function to apply to C
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C)
     *         where a property of (A,B) is less than or equal to a property of C
     */
    public static <A, B, C, Property_ extends Comparable<Property_>> TriJoiner<A, B, C> lessThanOrEqual(
            BiFunction<A, B, Property_> leftMapping, Function<C, Property_> rightMapping) {
        return new SingleTriJoiner<>(leftMapping, JoinerType.LESS_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Joins every (A,B) and C where a value of property of (A,B) is greater than the value of a property of C.
     * These are exactly the pairs where {@code leftMapping.apply(A, B).compareTo(rightMapping.apply(C)) > 0}.
     *
     * @param leftMapping mapping function to apply to (A,B)
     * @param rightMapping mapping function to apply to C
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C)
     *         where a property of (A,B) is greater than a property of C
     */
    public static <A, B, C, Property_ extends Comparable<Property_>> TriJoiner<A, B, C> greaterThan(
            BiFunction<A, B, Property_> leftMapping, Function<C, Property_> rightMapping) {
        return new SingleTriJoiner<>(leftMapping, JoinerType.GREATER_THAN, rightMapping);
    }

    /**
     * Joins every (A,B) and C where a value of property of (A,B) is greater than or equal to
     * the value of a property of C.
     * These are exactly the pairs where {@code leftMapping.apply(A, B).compareTo(rightMapping.apply(C)) >= 0}.
     *
     * @param leftMapping mapping function to apply to (A,B)
     * @param rightMapping mapping function to apply to C
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C)
     *         where a property of (A,B) is greater than or equal to a property of C
     */
    public static <A, B, C, Property_ extends Comparable<Property_>> TriJoiner<A, B, C> greaterThanOrEqual(
            BiFunction<A, B, Property_> leftMapping, Function<C, Property_> rightMapping) {
        return new SingleTriJoiner<>(leftMapping, JoinerType.GREATER_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Applies a filter to the joined tuple (A,B,C), with the semantics of
     * {@link TriConstraintStream#filter(TriPredicate)}.
     *
     * @param filter never null, filter to apply
     * @param <A> type of the first fact in the tuple
     * @param <B> type of the second fact in the tuple
     * @param <C> type of the third fact in the tuple
     * @return never null
     */
    public static <A, B, C> TriJoiner<A, B, C> filtering(TriPredicate<A, B, C> filter) {
        return new FilteringTriJoiner<>(filter);
    }

    /**
     * As defined by {@link #overlapping(Function, Function)}.
     *
     * @param leftStartMapping maps the first and second arguments to their interval start point (inclusive)
     * @param leftEndMapping maps the first and second arguments to their interval end point (exclusive)
     * @param rightStartMapping maps the third argument to its interval start point (inclusive)
     * @param rightEndMapping maps the third argument to its interval end point (exclusive)
     *
     * @param <A> the type of the first argument
     * @param <B> the type of the second argument
     * @param <C> the type of the third argument
     * @param <Property_> the type used to define the interval, comparable
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C) where (A,B)'s
     *         and C's intervals (as defined by the function mapping) overlap
     */
    public static <A, B, C, Property_ extends Comparable<Property_>> TriJoiner<A, B, C> overlapping(
            BiFunction<A, B, Property_> leftStartMapping,
            BiFunction<A, B, Property_> leftEndMapping,
            Function<C, Property_> rightStartMapping,
            Function<C, Property_> rightEndMapping) {
        return AbstractTriJoiner.merge(Joiners.lessThan(leftStartMapping, rightEndMapping),
                Joiners.greaterThan(leftEndMapping, rightStartMapping));
    }

    // ************************************************************************
    // QuadJoiner
    // ************************************************************************

    /**
     * Joins every (A,B,C) and D that share a property.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C).equals(rightMapping.apply(D))}.
     *
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of the object on the right
     * @param <Property_> the type of the property to compare
     * @param leftMapping mapping function to apply to (A, B, C)
     * @param rightMapping mapping function to apply to D
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D) where a
     *         property of (A,B,C) equals that of D
     */
    public static <A, B, C, D, Property_> QuadJoiner<A, B, C, D> equal(
            TriFunction<A, B, C, Property_> leftMapping, Function<D, Property_> rightMapping) {
        return new SingleQuadJoiner<>(leftMapping, JoinerType.EQUAL, rightMapping);
    }

    /**
     * Joins every (A,B,C) and D where a value of property of (A,B,C) is less than the value of a property of D.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C).compareTo(rightMapping.apply(D)) < 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C)
     * @param rightMapping mapping function to apply to D
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D)
     *         where a property of (A,B,C) is less than a property of D
     */
    public static <A, B, C, D, Property_ extends Comparable<Property_>> QuadJoiner<A, B, C, D> lessThan(
            TriFunction<A, B, C, Property_> leftMapping, Function<D, Property_> rightMapping) {
        return new SingleQuadJoiner<>(leftMapping, JoinerType.LESS_THAN, rightMapping);
    }

    /**
     * Joins every (A,B,C) and D where a value of property of (A,B,C) is less than or equal to
     * the value of a property of D.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C).compareTo(rightMapping.apply(D)) <= 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C)
     * @param rightMapping mapping function to apply to D
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D)
     *         where a property of (A,B,C) is less than or equal to a property of D
     */
    public static <A, B, C, D, Property_ extends Comparable<Property_>> QuadJoiner<A, B, C, D> lessThanOrEqual(
            TriFunction<A, B, C, Property_> leftMapping, Function<D, Property_> rightMapping) {
        return new SingleQuadJoiner<>(leftMapping, JoinerType.LESS_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Joins every (A,B,C) and D where a value of property of (A,B,C) is greater than the value of a property of D.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C).compareTo(rightMapping.apply(D)) > 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C)
     * @param rightMapping mapping function to apply to D
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D)
     *         where a property of (A,B,C) is greater than a property of D
     */
    public static <A, B, C, D, Property_ extends Comparable<Property_>> QuadJoiner<A, B, C, D> greaterThan(
            TriFunction<A, B, C, Property_> leftMapping, Function<D, Property_> rightMapping) {
        return new SingleQuadJoiner<>(leftMapping, JoinerType.GREATER_THAN, rightMapping);
    }

    /**
     * Joins every (A,B,C) and D where a value of property of (A,B,C) is greater than or equal to
     * the value of a property of D.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C).compareTo(rightMapping.apply(D)) >= 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C)
     * @param rightMapping mapping function to apply to D
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D)
     *         where a property of (A,B,C) is greater than or equal to a property of D
     */
    public static <A, B, C, D, Property_ extends Comparable<Property_>> QuadJoiner<A, B, C, D> greaterThanOrEqual(
            TriFunction<A, B, C, Property_> leftMapping, Function<D, Property_> rightMapping) {
        return new SingleQuadJoiner<>(leftMapping, JoinerType.GREATER_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Applies a filter to the joined tuple (A,B,C,D), with the semantics of
     * {@link QuadConstraintStream#filter(QuadPredicate)}.
     *
     * @param filter never null, filter to apply
     * @param <A> type of the first fact in the tuple
     * @param <B> type of the second fact in the tuple
     * @param <C> type of the third fact in the tuple
     * @param <D> type of the fourth fact in the tuple
     * @return never null
     */
    public static <A, B, C, D> QuadJoiner<A, B, C, D> filtering(QuadPredicate<A, B, C, D> filter) {
        return new FilteringQuadJoiner<>(filter);
    }

    /**
     * As defined by {@link #overlapping(Function, Function)}.
     *
     * @param leftStartMapping maps the first, second and third arguments to their interval start point (inclusive)
     * @param leftEndMapping maps the first, second and third arguments to their interval end point (exclusive)
     * @param rightStartMapping maps the fourth argument to its interval start point (inclusive)
     * @param rightEndMapping maps the fourth argument to its interval end point (exclusive)
     *
     * @param <A> the type of the first argument
     * @param <B> the type of the second argument
     * @param <C> the type of the third argument
     * @param <D> the type of the fourth argument
     * @param <Property_> the type used to define the interval, comparable
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D)
     *         where (A,B,C)'s and D's intervals (as defined by the function mapping) overlap
     */
    public static <A, B, C, D, Property_ extends Comparable<Property_>> QuadJoiner<A, B, C, D> overlapping(
            TriFunction<A, B, C, Property_> leftStartMapping,
            TriFunction<A, B, C, Property_> leftEndMapping,
            Function<D, Property_> rightStartMapping,
            Function<D, Property_> rightEndMapping) {
        return AbstractQuadJoiner.merge(Joiners.lessThan(leftStartMapping, rightEndMapping),
                Joiners.greaterThan(leftEndMapping, rightStartMapping));
    }

    // ************************************************************************
    // PentaJoiner
    // ************************************************************************

    /**
     * Joins every (A,B,C,D) and E that share a property.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C, D).equals(rightMapping.apply(E))}.
     *
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of the fourth object on the left
     * @param <E> the type of the object on the right
     * @param <Property_> the type of the property to compare
     * @param leftMapping mapping function to apply to (A,B,C,D)
     * @param rightMapping mapping function to apply to E
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D,E) where a
     *         property of (A,B,C,D) equals that of E
     */
    public static <A, B, C, D, E, Property_> PentaJoiner<A, B, C, D, E> equal(
            QuadFunction<A, B, C, D, Property_> leftMapping, Function<E, Property_> rightMapping) {
        return new SinglePentaJoiner<>(leftMapping, JoinerType.EQUAL, rightMapping);
    }

    /**
     * Joins every (A,B,C,D) and E where a value of property of (A,B,C,D) is less than the value of a property of E.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C, D).compareTo(rightMapping.apply(E)) < 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C,D)
     * @param rightMapping mapping function to apply to E
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of the fourth object on the left
     * @param <E> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D,E)
     *         where a property of (A,B,C,D) is less than a property of E
     */
    public static <A, B, C, D, E, Property_ extends Comparable<Property_>> PentaJoiner<A, B, C, D, E> lessThan(
            QuadFunction<A, B, C, D, Property_> leftMapping, Function<E, Property_> rightMapping) {
        return new SinglePentaJoiner<>(leftMapping, JoinerType.LESS_THAN, rightMapping);
    }

    /**
     * Joins every (A,B,C,D) and E where a value of property of (A,B,C,D) is less than or equal to
     * the value of a property of E.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C, D).compareTo(rightMapping.apply(E)) <= 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C,D)
     * @param rightMapping mapping function to apply to E
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of the fourth object on the left
     * @param <E> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D,E)
     *         where a property of (A,B,C,D) is less than or equal to a property of E
     */
    public static <A, B, C, D, E, Property_ extends Comparable<Property_>> PentaJoiner<A, B, C, D, E> lessThanOrEqual(
            QuadFunction<A, B, C, D, Property_> leftMapping, Function<E, Property_> rightMapping) {
        return new SinglePentaJoiner<>(leftMapping, JoinerType.LESS_THAN_OR_EQUAL, rightMapping);
    }

    /**
     * Joins every (A,B,C,D) and E where a value of property of (A,B,C,D) is greater than the value of a property of E.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C, D).compareTo(rightMapping.apply(E)) > 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C,D)
     * @param rightMapping mapping function to apply to E
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of the fourth object on the left
     * @param <E> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D,E)
     *         where a property of (A,B,C,D) is greater than a property of E
     */
    public static <A, B, C, D, E, Property_ extends Comparable<Property_>> PentaJoiner<A, B, C, D, E> greaterThan(
            QuadFunction<A, B, C, D, Property_> leftMapping, Function<E, Property_> rightMapping) {
        return new SinglePentaJoiner<>(leftMapping, JoinerType.GREATER_THAN, rightMapping);
    }

    /**
     * Joins every (A,B,C,D) and E where a value of property of (A,B,C,D) is greater than or equal to
     * the value of a property of E.
     * These are exactly the pairs where {@code leftMapping.apply(A, B, C, D).compareTo(rightMapping.apply(E)) >= 0}.
     *
     * @param leftMapping mapping function to apply to (A,B,C,D)
     * @param rightMapping mapping function to apply to E
     * @param <A> the type of the first object on the left
     * @param <B> the type of the second object on the left
     * @param <C> the type of the third object on the left
     * @param <D> the type of the fourth object on the left
     * @param <E> the type of object on the right
     * @param <Property_> the type of the property to compare
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D,E)
     *         where a property of (A,B,C,D) is greater than or equal to a property of E
     */
    public static <A, B, C, D, E, Property_ extends Comparable<Property_>> PentaJoiner<A, B, C, D, E> greaterThanOrEqual(
            QuadFunction<A, B, C, D, Property_> leftMapping, Function<E, Property_> rightMapping) {
        return new SinglePentaJoiner<>(leftMapping, JoinerType.GREATER_THAN_OR_EQUAL, rightMapping);
    }

    public static <A, B, C, D, E> PentaJoiner<A, B, C, D, E> filtering(PentaPredicate<A, B, C, D, E> filter) {
        return new FilteringPentaJoiner<>(filter);
    }

    /**
     * As defined by {@link #overlapping(Function, Function)}.
     *
     * @param leftStartMapping maps the first, second, third and fourth arguments to their interval start point (inclusive)
     * @param leftEndMapping maps the first, second, third and fourth arguments to their interval end point (exclusive)
     * @param rightStartMapping maps the fifth argument to its interval start point (inclusive)
     * @param rightEndMapping maps the fifth argument to its interval end point (exclusive)
     *
     * @param <A> the type of the first argument
     * @param <B> the type of the second argument
     * @param <C> the type of the third argument
     * @param <D> the type of the fourth argument
     * @param <E> the type of the fifth argument
     * @param <Property_> the type used to define the interval, comparable
     *
     * @return never null, a joiner that filters the constraint stream to only include elements (A,B,C,D,E)
     *         where (A,B,C,D)'s and E's intervals (as defined by the function mapping) overlap
     */
    public static <A, B, C, D, E, Property_ extends Comparable<Property_>> PentaJoiner<A, B, C, D, E> overlapping(
            QuadFunction<A, B, C, D, Property_> leftStartMapping,
            QuadFunction<A, B, C, D, Property_> leftEndMapping,
            Function<E, Property_> rightStartMapping,
            Function<E, Property_> rightEndMapping) {
        return AbstractPentaJoiner.merge(Joiners.lessThan(leftStartMapping, rightEndMapping),
                Joiners.greaterThan(leftEndMapping, rightStartMapping));
    }

    private Joiners() {
    }

}
