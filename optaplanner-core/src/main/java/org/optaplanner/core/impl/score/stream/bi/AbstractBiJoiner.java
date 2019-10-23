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

package org.optaplanner.core.impl.score.stream.bi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.impl.score.stream.common.AbstractJoiner;

public abstract class AbstractBiJoiner<A, B> extends AbstractJoiner implements BiJoiner<A, B> {

    @SafeVarargs
    public final static <A, B> AbstractBiJoiner<A, B> merge(BiJoiner<A, B>... joiners) {
        List<SingleBiJoiner<A, B>> joinerList = new ArrayList<>(joiners.length);
        for (BiJoiner<A, B> joiner : joiners) {
            if (joiner instanceof NoneBiJoiner) {
                // Ignore it
            } else if (joiner instanceof SingleBiJoiner) {
                joinerList.add((SingleBiJoiner<A, B>) joiner);
            } else if (joiner instanceof CompositeBiJoiner) {
                joinerList.addAll(((CompositeBiJoiner<A, B>) joiner).getJoinerList());
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        if (joinerList.isEmpty()) {
            return new NoneBiJoiner<>();
        } else if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return new CompositeBiJoiner<>(joinerList);
    }

    public abstract Function<A, Object> getLeftMapping(int joinerId);

    public abstract Function<A, Object[]> getLeftCombinedMapping();

    public abstract Function<B, Object> getRightMapping(int joinerId);

    public abstract Function<B, Object[]> getRightCombinedMapping();

}
