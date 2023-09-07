/*
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

package org.optaplanner.constraint.streams.common.inliner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintJustification;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;

/**
 * Allows to create justifications and indictments lazily if and only if constraint matches are enabled.
 *
 * Justification and indictment creation is performance expensive and constraint matches are typically disabled.
 * So justifications and indictments are created lazily, outside of the typical hot path.
 */
public final class JustificationsSupplier {

    public static JustificationsSupplier empty() {
        return new JustificationsSupplier(DefaultConstraintJustification::of, Collections::emptyList);
    }

    public static <A> JustificationsSupplier of(Constraint constraint,
            BiFunction<A, Score<?>, ConstraintJustification> justificationMapping,
            Function<A, Collection<Object>> indictedObjectsMapping,
            A a) {
        Function<Score<?>, ConstraintJustification> explainingJustificationMapping = impact -> {
            try {
                return justificationMapping.apply(a, impact);
            } catch (Exception e) {
                throw createJustificationException(constraint, e, a);
            }
        };
        Supplier<Collection<Object>> explainingIndictedObjectsSupplier = () -> {
            try {
                return indictedObjectsMapping.apply(a);
            } catch (Exception e) {
                throw createIndictmentException(constraint, e, a);
            }
        };
        return new JustificationsSupplier(explainingJustificationMapping, explainingIndictedObjectsSupplier);
    }

    private static RuntimeException createJustificationException(Constraint constraint, Exception cause, Object... facts) {
        throw new IllegalStateException("Consequence of a constraint (" + constraint.getConstraintId()
                + ") threw an exception creating constraint justification from a tuple (" + factsToString(facts) + ").", cause);
    }

    private static String factsToString(Object... facts) {
        return Arrays.stream(facts)
                .map(Object::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private static RuntimeException createIndictmentException(Constraint constraint, Exception cause, Object... facts) {
        throw new IllegalStateException("Consequence of a constraint (" + constraint.getConstraintId()
                + ") threw an exception collecting indicted objects from a tuple (" + factsToString(facts) + ").", cause);
    }

    public static <A, B> JustificationsSupplier of(Constraint constraint,
            TriFunction<A, B, Score<?>, ConstraintJustification> justificationMapping,
            BiFunction<A, B, Collection<Object>> indictedObjectsMapping,
            A a, B b) {
        Function<Score<?>, ConstraintJustification> explainingJustificationMapping = impact -> {
            try {
                return justificationMapping.apply(a, b, impact);
            } catch (Exception e) {
                throw createJustificationException(constraint, e, a, b);
            }
        };
        Supplier<Collection<Object>> explainingIndictedObjectsSupplier = () -> {
            try {
                return indictedObjectsMapping.apply(a, b);
            } catch (Exception e) {
                throw createIndictmentException(constraint, e, a, b);
            }
        };
        return new JustificationsSupplier(explainingJustificationMapping, explainingIndictedObjectsSupplier);
    }

    public static <A, B, C> JustificationsSupplier of(Constraint constraint,
            QuadFunction<A, B, C, Score<?>, ConstraintJustification> justificationMapping,
            TriFunction<A, B, C, Collection<Object>> indictedObjectsMapping,
            A a, B b, C c) {
        Function<Score<?>, ConstraintJustification> explainingJustificationMapping = impact -> {
            try {
                return justificationMapping.apply(a, b, c, impact);
            } catch (Exception e) {
                throw createJustificationException(constraint, e, a, b, c);
            }
        };
        Supplier<Collection<Object>> explainingIndictedObjectsSupplier = () -> {
            try {
                return indictedObjectsMapping.apply(a, b, c);
            } catch (Exception e) {
                throw createIndictmentException(constraint, e, a, b, c);
            }
        };
        return new JustificationsSupplier(explainingJustificationMapping, explainingIndictedObjectsSupplier);
    }

    public static <A, B, C, D> JustificationsSupplier of(Constraint constraint,
            PentaFunction<A, B, C, D, Score<?>, ConstraintJustification> justificationMapping,
            QuadFunction<A, B, C, D, Collection<Object>> indictedObjectsMapping,
            A a, B b, C c, D d) {
        Function<Score<?>, ConstraintJustification> explainingJustificationMapping = impact -> {
            try {
                return justificationMapping.apply(a, b, c, d, impact);
            } catch (Exception e) {
                throw createJustificationException(constraint, e, a, b, c, d);
            }
        };
        Supplier<Collection<Object>> explainingIndictedObjectsSupplier = () -> {
            try {
                return indictedObjectsMapping.apply(a, b, c, d);
            } catch (Exception e) {
                throw createIndictmentException(constraint, e, a, b, c, d);
            }
        };
        return new JustificationsSupplier(explainingJustificationMapping, explainingIndictedObjectsSupplier);
    }

    private final Function<Score<?>, ConstraintJustification> constraintJustificationSupplier;
    private final Supplier<Collection<Object>> indictedObjectsSupplier;

    private JustificationsSupplier(Function<Score<?>, ConstraintJustification> constraintJustificationSupplier,
            Supplier<Collection<Object>> indictedObjectsSupplier) {
        this.constraintJustificationSupplier = Objects.requireNonNull(constraintJustificationSupplier);
        this.indictedObjectsSupplier = Objects.requireNonNull(indictedObjectsSupplier);
    }

    public ConstraintJustification createConstraintJustification(Score<?> impact) {
        return constraintJustificationSupplier.apply(impact);
    }

    public Collection<Object> createIndictedObjects() {
        return indictedObjectsSupplier.get();
    }

}
