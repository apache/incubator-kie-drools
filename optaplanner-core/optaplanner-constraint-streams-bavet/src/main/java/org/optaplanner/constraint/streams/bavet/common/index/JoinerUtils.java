/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.common.AbstractJoiner;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.penta.DefaultPentaJoiner;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;

public final class JoinerUtils {

    private static final NoneIndexProperties NONE_INDEX_PROPERTY = NoneIndexProperties.INSTANCE;

    private JoinerUtils() {

    }

    public static <A, B> Function<A, IndexProperties> combineLeftMappings(DefaultBiJoiner<A, B> joiner) {
        int joinerCount = joiner.getJoinerCount();
        switch (joinerCount) {
            case 0:
                return (A a) -> NONE_INDEX_PROPERTY;
            case 1:
                Function<A, Object> mapping = joiner.getLeftMapping(0);
                return (A a) -> new SingleIndexProperties(mapping.apply(a));
            case 2:
                Function<A, Object> mappingX = joiner.getLeftMapping(0);
                Function<A, Object> mappingY = joiner.getLeftMapping(1);
                return (A a) -> new TwoIndexProperties(mappingX.apply(a), mappingY.apply(a));
            case 3:
                Function<A, Object> mapping1 = joiner.getLeftMapping(0);
                Function<A, Object> mapping2 = joiner.getLeftMapping(1);
                Function<A, Object> mapping3 = joiner.getLeftMapping(2);
                return (A a) -> new ThreeIndexProperties(mapping1.apply(a), mapping2.apply(a), mapping3.apply(a));
            default:
                return (A a) -> {
                    Object[] mappings = new Object[joinerCount];
                    for (int i = 0; i < joinerCount; i++) {
                        mappings[i] = joiner.getLeftMapping(i).apply(a);
                    }
                    return new ManyIndexProperties(mappings);
                };
        }
    }

    public static <A, B, C> BiFunction<A, B, IndexProperties> combineLeftMappings(DefaultTriJoiner<A, B, C> joiner) {
        int joinerCount = joiner.getJoinerCount();
        switch (joinerCount) {
            case 0:
                return (A a, B b) -> NONE_INDEX_PROPERTY;
            case 1:
                BiFunction<A, B, Object> mapping = joiner.getLeftMapping(0);
                return (A a, B b) -> new SingleIndexProperties(mapping.apply(a, b));
            case 2:
                BiFunction<A, B, Object> mappingX = joiner.getLeftMapping(0);
                BiFunction<A, B, Object> mappingY = joiner.getLeftMapping(1);
                return (A a, B b) -> new TwoIndexProperties(mappingX.apply(a, b), mappingY.apply(a, b));
            case 3:
                BiFunction<A, B, Object> mapping1 = joiner.getLeftMapping(0);
                BiFunction<A, B, Object> mapping2 = joiner.getLeftMapping(1);
                BiFunction<A, B, Object> mapping3 = joiner.getLeftMapping(2);
                return (A a, B b) -> new ThreeIndexProperties(mapping1.apply(a, b), mapping2.apply(a, b),
                        mapping3.apply(a, b));
            default:
                return (A a, B b) -> {
                    Object[] mappings = new Object[joinerCount];
                    for (int i = 0; i < joinerCount; i++) {
                        mappings[i] = joiner.getLeftMapping(i).apply(a, b);
                    }
                    return new ManyIndexProperties(mappings);
                };
        }
    }

    public static <A, B, C, D> TriFunction<A, B, C, IndexProperties> combineLeftMappings(DefaultQuadJoiner<A, B, C, D> joiner) {
        int joinerCount = joiner.getJoinerCount();
        switch (joinerCount) {
            case 0:
                return (A a, B b, C c) -> NONE_INDEX_PROPERTY;
            case 1:
                TriFunction<A, B, C, Object> mapping = joiner.getLeftMapping(0);
                return (A a, B b, C c) -> new SingleIndexProperties(mapping.apply(a, b, c));
            case 2:
                TriFunction<A, B, C, Object> mappingX = joiner.getLeftMapping(0);
                TriFunction<A, B, C, Object> mappingY = joiner.getLeftMapping(1);
                return (A a, B b, C c) -> new TwoIndexProperties(mappingX.apply(a, b, c), mappingY.apply(a, b, c));
            case 3:
                TriFunction<A, B, C, Object> mapping1 = joiner.getLeftMapping(0);
                TriFunction<A, B, C, Object> mapping2 = joiner.getLeftMapping(1);
                TriFunction<A, B, C, Object> mapping3 = joiner.getLeftMapping(2);
                return (A a, B b, C c) -> new ThreeIndexProperties(mapping1.apply(a, b, c), mapping2.apply(a, b, c),
                        mapping3.apply(a, b, c));
            default:
                return (A a, B b, C c) -> {
                    Object[] mappings = new Object[joinerCount];
                    for (int i = 0; i < joinerCount; i++) {
                        mappings[i] = joiner.getLeftMapping(i).apply(a, b, c);
                    }
                    return new ManyIndexProperties(mappings);
                };
        }
    }

    public static <A, B, C, D, E> QuadFunction<A, B, C, D, IndexProperties> combineLeftMappings(
            DefaultPentaJoiner<A, B, C, D, E> joiner) {
        int joinerCount = joiner.getJoinerCount();
        switch (joinerCount) {
            case 0:
                return (A a, B b, C c, D d) -> NONE_INDEX_PROPERTY;
            case 1:
                QuadFunction<A, B, C, D, Object> mapping = joiner.getLeftMapping(0);
                return (A a, B b, C c, D d) -> new SingleIndexProperties(mapping.apply(a, b, c, d));
            case 2:
                QuadFunction<A, B, C, D, Object> mappingX = joiner.getLeftMapping(0);
                QuadFunction<A, B, C, D, Object> mappingY = joiner.getLeftMapping(1);
                return (A a, B b, C c, D d) -> new TwoIndexProperties(mappingX.apply(a, b, c, d),
                        mappingY.apply(a, b, c, d));
            case 3:
                QuadFunction<A, B, C, D, Object> mapping1 = joiner.getLeftMapping(0);
                QuadFunction<A, B, C, D, Object> mapping2 = joiner.getLeftMapping(1);
                QuadFunction<A, B, C, D, Object> mapping3 = joiner.getLeftMapping(2);
                return (A a, B b, C c, D d) -> new ThreeIndexProperties(mapping1.apply(a, b, c, d),
                        mapping2.apply(a, b, c, d), mapping3.apply(a, b, c, d));
            default:
                return (A a, B b, C c, D d) -> {
                    Object[] mappings = new Object[joinerCount];
                    for (int i = 0; i < joinerCount; i++) {
                        mappings[i] = joiner.getLeftMapping(i).apply(a, b, c, d);
                    }
                    return new ManyIndexProperties(mappings);
                };
        }
    }

    public static <Right_> Function<Right_, IndexProperties> combineRightMappings(AbstractJoiner<Right_> joiner) {
        int joinerCount = joiner.getJoinerCount();
        switch (joinerCount) {
            case 0:
                return (Right_ right) -> NONE_INDEX_PROPERTY;
            case 1:
                Function<Right_, Object> mapping = joiner.getRightMapping(0);
                return (Right_ right) -> new SingleIndexProperties(mapping.apply(right));
            case 2:
                Function<Right_, Object> mappingX = joiner.getRightMapping(0);
                Function<Right_, Object> mappingY = joiner.getRightMapping(1);
                return (Right_ right) -> new TwoIndexProperties(mappingX.apply(right), mappingY.apply(right));
            case 3:
                Function<Right_, Object> mapping1 = joiner.getRightMapping(0);
                Function<Right_, Object> mapping2 = joiner.getRightMapping(1);
                Function<Right_, Object> mapping3 = joiner.getRightMapping(2);
                return (Right_ right) -> new ThreeIndexProperties(mapping1.apply(right), mapping2.apply(right),
                        mapping3.apply(right));
            default:
                return (Right_ right) -> {
                    Object[] mappings = new Object[joinerCount];
                    for (int i = 0; i < joinerCount; i++) {
                        mappings[i] = joiner.getRightMapping(i).apply(right);
                    }
                    return new ManyIndexProperties(mappings);
                };
        }
    }
}
