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

package org.optaplanner.core.impl.score.stream.drools.common.consequences;

import java.math.BigDecimal;

import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.TriConstraintGraphNode;

public interface TriConstraintConsequence extends ConstraintConsequence<TriConstraintGraphNode> {

    /**
     * {@inheritDoc}
     *
     * @return Never null.
     *         When this method returns {@link ConsequenceMatchWeightType#INTEGER}, implementations of this interface
     *         are guaranteed to implement {@link ToIntTriFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#LONG}, implementations of this interface are
     *         guaranteed to implement {@link ToLongTriFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#BIG_DECIMAL}, implementations of this interface are
     *         guaranteed to implement {@link TriFunction} to {@link BigDecimal}.
     */
    @Override
    ConsequenceMatchWeightType getMatchWeightType();
}
