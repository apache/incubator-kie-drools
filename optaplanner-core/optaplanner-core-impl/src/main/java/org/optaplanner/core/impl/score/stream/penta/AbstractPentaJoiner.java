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

package org.optaplanner.core.impl.score.stream.penta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.impl.score.stream.common.AbstractJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public abstract class AbstractPentaJoiner<A, B, C, D, E> extends AbstractJoiner implements PentaJoiner<A, B, C, D, E> {

    private final PentaPredicate<A, B, C, D, E> filter;

    protected AbstractPentaJoiner() {
        this.filter = null;
    }

    protected AbstractPentaJoiner(PentaPredicate<A, B, C, D, E> filter) {
        this.filter = filter;
    }

    @SafeVarargs
    public static <A, B, C, D, E> AbstractPentaJoiner<A, B, C, D, E> merge(PentaJoiner<A, B, C, D, E>... joiners) {
        List<SinglePentaJoiner<A, B, C, D, E>> joinerList = new ArrayList<>();
        for (PentaJoiner<A, B, C, D, E> joiner : joiners) {
            if (joiner instanceof NonePentaJoiner) {
                // Ignore it
            } else if (joiner instanceof SinglePentaJoiner) {
                joinerList.add((SinglePentaJoiner<A, B, C, D, E>) joiner);
            } else if (joiner instanceof CompositePentaJoiner) {
                joinerList.addAll(((CompositePentaJoiner<A, B, C, D, E>) joiner).getJoinerList());
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        if (joinerList.isEmpty()) {
            return new NonePentaJoiner<>();
        } else if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return new CompositePentaJoiner<>(joinerList);
    }

    public boolean matches(A a, B b, C c, D d, E e) {
        JoinerType[] joinerTypes = getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            Object leftMapping = getLeftMapping(i).apply(a, b, c, d);
            Object rightMapping = getRightMapping(i).apply(e);
            if (!joinerType.matches(leftMapping, rightMapping)) {
                return false;
            }
        }
        return true;
    }

    public abstract QuadFunction<A, B, C, D, Object> getLeftMapping(int index);

    public abstract QuadFunction<A, B, C, D, Object[]> getLeftCombinedMapping();

    public abstract Function<E, Object> getRightMapping(int index);

    public abstract Function<E, Object[]> getRightCombinedMapping();

    public PentaPredicate<A, B, C, D, E> getFilter() {
        return filter;
    }

}
