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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.util.List;
import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsFilterUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final DroolsAbstractUniConstraintStream<Solution_, A> parent;
    private final Predicate<A> predicate;

    public DroolsFilterUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Predicate<A> predicate) {
        super(constraintFactory);
        this.parent = parent;
        this.predicate = predicate;
    }

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsUniCondition<A> createCondition() {
        return parent.createCondition().andFilter(predicate);
    }

    @Override
    public String toString() {
        return "Filter() with " + getChildStreams().size()  + " children";
    }

}
