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

package org.optaplanner.core.impl.score.stream.drools.quad;

import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsFilterQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final DroolsQuadCondition<A, B, C, D, ?> condition;

    public DroolsFilterQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent, QuadPredicate<A, B, C, D> predicate) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andFilter(predicate);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsQuadCondition<A, B, C, D, ?> getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "QuadFilter() with " + getChildStreams().size() + " children";
    }

}
