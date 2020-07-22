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
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphNode;

public interface UniConstraintConsequence extends ConstraintConsequence<UniConstraintGraphNode> {

    /**
     * {@inheritDoc}
     *
     * @return Never null.
     *         When this method returns {@link ConsequenceMatchWeightType#INTEGER}, implementations of this interface
     *         are guaranteed to implement {@link ToIntFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#LONG}, implementations of this interface are
     *         guaranteed to implement {@link ToLongFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#BIG_DECIMAL}, implementations of this interface are
     *         guaranteed to implement {@link Function} to {@link BigDecimal}.
     */
    @Override
    ConsequenceMatchWeightType getMatchWeightType();
}
