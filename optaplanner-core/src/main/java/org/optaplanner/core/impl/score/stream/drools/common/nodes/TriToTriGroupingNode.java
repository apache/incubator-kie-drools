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

package org.optaplanner.core.impl.score.stream.drools.common.nodes;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

final class TriToTriGroupingNode<A, B, C, NewA, NewB, NewC>
        extends AbstractConstraintModelGroupingNode<TriFunction<A, B, C, ?>, TriConstraintCollector<A, B, C, ?, ?>>
        implements TriConstraintGraphNode {

    TriToTriGroupingNode(TriFunction<A, B, C, NewA> aMapping, TriFunction<A, B, C, NewB> bMapping,
            TriConstraintCollector<A, B, C, ?, NewC> collector) {
        super(asList(aMapping, bMapping), singletonList(collector));
    }

}
