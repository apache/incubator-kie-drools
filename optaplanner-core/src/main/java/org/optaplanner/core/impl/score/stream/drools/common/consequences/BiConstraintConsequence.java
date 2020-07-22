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
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.core.impl.score.stream.drools.common.nodes.BiConstraintGraphNode;

public interface BiConstraintConsequence extends ConstraintConsequence<BiConstraintGraphNode> {

    /**
     * {@inheritDoc}
     *
     * @return Null.
     *         When this method returns {@link ConsequenceMatchWeightType#INTEGER}, implementations of this interface
     *         are guaranteed to implement {@link ToIntBiFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#LONG}, implementations of this interface are
     *         guaranteed to implement {@link ToLongBiFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#BIG_DECIMAL}, implementations of this interface are
     *         guaranteed to implement {@link BiFunction} to {@link BigDecimal}.
     */
    @Override
    ConsequenceMatchWeightType getMatchWeightType();

}
