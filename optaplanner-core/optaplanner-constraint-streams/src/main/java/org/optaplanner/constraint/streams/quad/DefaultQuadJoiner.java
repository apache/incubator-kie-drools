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

package org.optaplanner.constraint.streams.quad;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.constraint.streams.common.AbstractJoiner;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.impl.score.stream.JoinerType;

public final class DefaultQuadJoiner<A, B, C, D> extends AbstractJoiner<D> implements QuadJoiner<A, B, C, D> {

    private static final DefaultQuadJoiner NONE =
            new DefaultQuadJoiner(new TriFunction[0], new JoinerType[0], new Function[0]);

    private final TriFunction<A, B, C, ?>[] leftMappings;

    public <Property_> DefaultQuadJoiner(TriFunction<A, B, C, Property_> leftMapping, JoinerType joinerType,
            Function<D, Property_> rightMapping) {
        super(rightMapping, joinerType);
        this.leftMappings = new TriFunction[] { leftMapping };
    }

    private <Property_> DefaultQuadJoiner(TriFunction<A, B, C, Property_>[] leftMappings, JoinerType[] joinerTypes,
            Function<D, Property_>[] rightMappings) {
        super(rightMappings, joinerTypes);
        this.leftMappings = leftMappings;
    }

    public static <A, B, C, D> DefaultQuadJoiner<A, B, C, D> merge(List<DefaultQuadJoiner<A, B, C, D>> joinerList) {
        if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return joinerList.stream().reduce(NONE, DefaultQuadJoiner::and);
    }

    @Override
    public DefaultQuadJoiner<A, B, C, D> and(QuadJoiner<A, B, C, D> otherJoiner) {
        DefaultQuadJoiner<A, B, C, D> castJoiner = (DefaultQuadJoiner<A, B, C, D>) otherJoiner;
        int joinerCount = getJoinerCount();
        int castJoinerCount = castJoiner.getJoinerCount();
        int newJoinerCount = joinerCount + castJoinerCount;
        JoinerType[] newJoinerTypes = Arrays.copyOf(this.joinerTypes, newJoinerCount);
        TriFunction[] newLeftMappings = Arrays.copyOf(this.leftMappings, newJoinerCount);
        Function[] newRightMappings = Arrays.copyOf(this.rightMappings, newJoinerCount);
        for (int i = 0; i < castJoiner.getJoinerCount(); i++) {
            int newJoinerIndex = i + joinerCount;
            newJoinerTypes[newJoinerIndex] = castJoiner.getJoinerType(i);
            newLeftMappings[newJoinerIndex] = castJoiner.getLeftMapping(i);
            newRightMappings[newJoinerIndex] = castJoiner.getRightMapping(i);
        }
        return new DefaultQuadJoiner<>(newLeftMappings, newJoinerTypes, newRightMappings);
    }

    public TriFunction<A, B, C, Object> getLeftMapping(int index) {
        return (TriFunction<A, B, C, Object>) leftMappings[index];
    }

    public boolean matches(A a, B b, C c, D d) {
        int joinerCount = getJoinerCount();
        for (int i = 0; i < joinerCount; i++) {
            JoinerType joinerType = getJoinerType(i);
            Object leftMapping = getLeftMapping(i).apply(a, b, c);
            Object rightMapping = getRightMapping(i).apply(d);
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
        if (!(o instanceof DefaultQuadJoiner)) {
            return false;
        }
        DefaultQuadJoiner<?, ?, ?, ?> other = (DefaultQuadJoiner<?, ?, ?, ?>) o;
        return Arrays.equals(joinerTypes, other.joinerTypes)
                && Arrays.equals(leftMappings, other.leftMappings)
                && Arrays.equals(rightMappings, other.rightMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(joinerTypes), Arrays.hashCode(leftMappings), Arrays.hashCode(rightMappings));
    }

}
