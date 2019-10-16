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

import java.util.Collections;
import java.util.List;

import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsFromUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final Class<A> fromClass;

    public DroolsFromUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory, Class<A> fromClass) {
        super(constraintFactory);
        if (fromClass == null) {
            throw new IllegalArgumentException("The fromClass (null) cannot be null.");
        }
        this.fromClass = fromClass;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return Collections.singletonList((DroolsFromUniConstraintStream<Solution_, Object>) this);
    }

    @Override
    public DroolsUniCondition<A> createCondition() {
        return new DroolsUniCondition<>(fromClass);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public Class<A> getFromClass() {
        return fromClass;
    }

    @Override
    public String toString() {
        return "From(" + fromClass.getSimpleName() + ") with " + getChildStreams().size()  + " children";
    }

}
