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

package org.optaplanner.core.impl.score.stream.common;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.stream.common.JoinerType;

public abstract class AbstractJoiner {

    protected static <A, J extends AbstractJoiner> Function<A, Object[]> buildCombinedMappingUni(
            List<J> joinerList, Function<J, Function<A, ?>> mappingFunction) {
        int size = joinerList.size();
        if (size == 1) {
            Function<A, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            return (A a) -> new Object[]{
                    mapping0.apply(a)
            };
        } else if (size == 2) {
            Function<A, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            Function<A, ?> mapping1 = mappingFunction.apply(joinerList.get(1));
            return (A a) -> new Object[]{
                    mapping0.apply(a),
                    mapping1.apply(a)
            };
        } else if (size == 3) {
            Function<A, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            Function<A, ?> mapping1 = mappingFunction.apply(joinerList.get(1));
            Function<A, ?> mapping2 = mappingFunction.apply(joinerList.get(2));
            return (A a) -> new Object[]{
                    mapping0.apply(a),
                    mapping1.apply(a),
                    mapping2.apply(a)
            };
        } else {
            List<? extends Function<A, ?>> mappingList = joinerList.stream()
                    .map(mappingFunction)
                    .collect(Collectors.toList());
            return (A a) -> mappingList.stream()
                    .map(mapping -> mapping.apply(a))
                    .toArray();
        }
    }

    protected static <A, B, J extends AbstractJoiner> BiFunction<A, B, Object[]> buildCombinedMappingBi(
            List<J> joinerList, Function<J, BiFunction<A, B, ?>> mappingFunction) {
        int size = joinerList.size();
        if (size == 1) {
            BiFunction<A, B, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            return (A a, B b) -> new Object[]{
                    mapping0.apply(a, b)
            };
        } else if (size == 2) {
            BiFunction<A, B, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            BiFunction<A, B, ?> mapping1 = mappingFunction.apply(joinerList.get(1));
            return (A a, B b) -> new Object[]{
                    mapping0.apply(a, b),
                    mapping1.apply(a, b)
            };
        } else if (size == 3) {
            BiFunction<A, B, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            BiFunction<A, B, ?> mapping1 = mappingFunction.apply(joinerList.get(1));
            BiFunction<A, B, ?> mapping2 = mappingFunction.apply(joinerList.get(2));
            return (A a, B b) -> new Object[]{
                    mapping0.apply(a, b),
                    mapping1.apply(a, b),
                    mapping2.apply(a, b)
            };
        } else {
            List<? extends BiFunction<A, B, ?>> mappingList = joinerList.stream()
                    .map(mappingFunction)
                    .collect(Collectors.toList());
            return (A a, B b) -> mappingList.stream()
                    .map(mapping -> mapping.apply(a, b))
                    .toArray();
        }
    }

    public abstract JoinerType[] getJoinerTypes();

}
