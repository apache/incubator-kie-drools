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

package org.optaplanner.core.impl.score.stream.quad;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.impl.score.stream.common.AbstractJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public abstract class AbstractQuadJoiner<A, B, C, D> extends AbstractJoiner implements QuadJoiner<A, B, C, D> {

    private final QuadPredicate<A, B, C, D> filter;

    protected AbstractQuadJoiner() {
        this.filter = null;
    }

    protected AbstractQuadJoiner(QuadPredicate<A, B, C, D> filter) {
        this.filter = filter;
    }

    @SafeVarargs
    public static <A, B, C, D> AbstractQuadJoiner<A, B, C, D> merge(QuadJoiner<A, B, C, D>... joiners) {
        List<SingleQuadJoiner<A, B, C, D>> joinerList = new ArrayList<>();
        for (QuadJoiner<A, B, C, D> joiner : joiners) {
            if (joiner instanceof NoneQuadJoiner) {
                // Ignore it
            } else if (joiner instanceof SingleQuadJoiner) {
                joinerList.add((SingleQuadJoiner<A, B, C, D>) joiner);
            } else if (joiner instanceof CompositeQuadJoiner) {
                joinerList.addAll(((CompositeQuadJoiner<A, B, C, D>) joiner).getJoinerList());
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        if (joinerList.isEmpty()) {
            return new NoneQuadJoiner<>();
        } else if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return new CompositeQuadJoiner<>(joinerList);
    }

    public boolean matches(A a, B b, C c, D d) {
        JoinerType[] joinerTypes = getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            Object leftMapping = getLeftMapping(i).apply(a, b, c);
            Object rightMapping = getRightMapping(i).apply(d);
            if (!joinerType.matches(leftMapping, rightMapping)) {
                return false;
            }
        }
        return true;
    }

    public abstract TriFunction<A, B, C, Object> getLeftMapping(int index);

    public abstract TriFunction<A, B, C, Object[]> getLeftCombinedMapping();

    public abstract Function<D, Object> getRightMapping(int index);

    public abstract Function<D, Object[]> getRightCombinedMapping();

    public QuadPredicate<A, B, C, D> getFilter() {
        return filter;
    }

}
