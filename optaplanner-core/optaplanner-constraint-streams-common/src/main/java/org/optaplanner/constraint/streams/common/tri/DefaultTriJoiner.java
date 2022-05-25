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

package org.optaplanner.constraint.streams.common.tri;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.common.AbstractJoiner;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.JoinerType;

public final class DefaultTriJoiner<A, B, C> extends AbstractJoiner<C> implements TriJoiner<A, B, C> {

    private static final DefaultTriJoiner NONE =
            new DefaultTriJoiner(new BiFunction[0], new JoinerType[0], new Function[0]);

    private final BiFunction<A, B, ?>[] leftMappings;

    public <Property_> DefaultTriJoiner(BiFunction<A, B, Property_> leftMapping, JoinerType joinerType,
            Function<C, Property_> rightMapping) {
        super(rightMapping, joinerType);
        this.leftMappings = new BiFunction[] { leftMapping };
    }

    private <Property_> DefaultTriJoiner(BiFunction<A, B, Property_>[] leftMappings, JoinerType[] joinerTypes,
            Function<C, Property_>[] rightMappings) {
        super(rightMappings, joinerTypes);
        this.leftMappings = leftMappings;
    }

    public static <A, B, C> DefaultTriJoiner<A, B, C> merge(List<DefaultTriJoiner<A, B, C>> joinerList) {
        if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return joinerList.stream().reduce(NONE, DefaultTriJoiner::and);
    }

    @Override
    public DefaultTriJoiner<A, B, C> and(TriJoiner<A, B, C> otherJoiner) {
        DefaultTriJoiner<A, B, C> castJoiner = (DefaultTriJoiner<A, B, C>) otherJoiner;
        int joinerCount = getJoinerCount();
        int castJoinerCount = castJoiner.getJoinerCount();
        int newJoinerCount = joinerCount + castJoinerCount;
        JoinerType[] newJoinerTypes = Arrays.copyOf(this.joinerTypes, newJoinerCount);
        BiFunction[] newLeftMappings = Arrays.copyOf(this.leftMappings, newJoinerCount);
        Function[] newRightMappings = Arrays.copyOf(this.rightMappings, newJoinerCount);
        for (int i = 0; i < castJoinerCount; i++) {
            int newJoinerIndex = i + joinerCount;
            newJoinerTypes[newJoinerIndex] = castJoiner.getJoinerType(i);
            newLeftMappings[newJoinerIndex] = castJoiner.getLeftMapping(i);
            newRightMappings[newJoinerIndex] = castJoiner.getRightMapping(i);
        }
        return new DefaultTriJoiner<>(newLeftMappings, newJoinerTypes, newRightMappings);
    }

    public BiFunction<A, B, Object> getLeftMapping(int index) {
        return (BiFunction<A, B, Object>) leftMappings[index];
    }

    public boolean matches(A a, B b, C c) {
        int joinerCount = getJoinerCount();
        for (int i = 0; i < joinerCount; i++) {
            JoinerType joinerType = getJoinerType(i);
            Object leftMapping = getLeftMapping(i).apply(a, b);
            Object rightMapping = getRightMapping(i).apply(c);
            if (!joinerType.matches(leftMapping, rightMapping)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultTriJoiner)) {
            return false;
        }
        DefaultTriJoiner<?, ?, ?> other = (DefaultTriJoiner<?, ?, ?>) o;
        return Arrays.equals(joinerTypes, other.joinerTypes)
                && Arrays.equals(leftMappings, other.leftMappings)
                && Arrays.equals(rightMappings, other.rightMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(joinerTypes), Arrays.hashCode(leftMappings), Arrays.hashCode(rightMappings));
    }

}
