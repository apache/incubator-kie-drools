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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.util.List;

import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;

public final class DroolsExistsBiConstraintStream<Solution_, A, B>
        extends DroolsAbstractBiConstraintStream<Solution_, A, B> {

    private final DroolsAbstractConstraintStream<Solution_> parent;
    private final DroolsBiCondition<A, B, ?> condition;
    private final String streamName;

    public <C> DroolsExistsBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, boolean shouldExist, Class<C> otherClass,
            TriJoiner<A, B, C>... joiners) {
        super(constraintFactory);
        this.parent = parent;
        this.streamName = shouldExist ? "BiIfExists()" : "BiIfNotExists()";
        this.condition = shouldExist ? parent.getCondition().andIfExists(otherClass, joiners)
                : parent.getCondition().andIfNotExists(otherClass, joiners);
    }

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    @Override
    protected DroolsAbstractConstraintStream<Solution_> getParent() {
        return parent;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsBiCondition<A, B, ?> getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return streamName + " with " + getChildStreams().size() + " children";
    }

}
