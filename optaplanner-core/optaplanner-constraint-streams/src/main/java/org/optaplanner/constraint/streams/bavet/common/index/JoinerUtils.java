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

import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.AbstractJoiner;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;

public final class JoinerUtils {

    private static final NoneIndexProperties NONE_INDEX_PROPERTY = NoneIndexProperties.INSTANCE;

    private JoinerUtils() {

    }

    public static <A, B> Function<A, IndexProperties> combineLeftMappings(DefaultBiJoiner<A, B> joiner) {
        int joinerCount = joiner.getJoinerCount();
        if (joinerCount == 0) {
            return (A a) -> NONE_INDEX_PROPERTY;
        } else if (joinerCount == 1) {
            Function<A, Object> mapping = joiner.getLeftMapping(0);
            return (A a) -> new SingleIndexProperties(mapping.apply(a));
        } else {
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
        if (joinerCount == 0) {
            return (A a, B b) -> NONE_INDEX_PROPERTY;
        } else if (joinerCount == 1) {
            BiFunction<A, B, Object> mapping = joiner.getLeftMapping(0);
            return (A a, B b) -> new SingleIndexProperties(mapping.apply(a, b));
        } else {
            return (A a, B b) -> {
                Object[] mappings = new Object[joinerCount];
                for (int i = 0; i < joinerCount; i++) {
                    mappings[i] = joiner.getLeftMapping(i).apply(a, b);
                }
                return new ManyIndexProperties(mappings);
            };
        }
    }

    public static <Right_> Function<Right_, IndexProperties> combineRightMappings(AbstractJoiner<Right_> joiner) {
        int joinerCount = joiner.getJoinerCount();
        if (joinerCount == 0) {
            return (Right_ x) -> NONE_INDEX_PROPERTY;
        } else if (joinerCount == 1) {
            Function<Right_, Object> mapping = joiner.getRightMapping(0);
            return (Right_ x) -> new SingleIndexProperties(mapping.apply(x));
        } else {
            return (Right_ x) -> {
                Object[] mappings = new Object[joinerCount];
                for (int i = 0; i < joinerCount; i++) {
                    mappings[i] = joiner.getRightMapping(i).apply(x);
                }
                return new ManyIndexProperties(mappings);
            };
        }
    }
}
