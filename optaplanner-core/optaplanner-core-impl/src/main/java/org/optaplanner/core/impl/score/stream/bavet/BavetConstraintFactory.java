/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet;

import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.stream.InnerConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.common.RetrievalSemantics;

public final class BavetConstraintFactory<Solution_>
        extends InnerConstraintFactory<Solution_, BavetConstraint<Solution_>> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final String defaultConstraintPackage;

    public BavetConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor = solutionDescriptor
                .getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            Package pack = solutionDescriptor.getSolutionClass().getPackage();
            defaultConstraintPackage = (pack == null) ? "" : pack.getName();
        } else {
            defaultConstraintPackage = configurationDescriptor.getConstraintPackage();
        }
    }

    // ************************************************************************
    // from
    // ************************************************************************

    @Override
    public <A> UniConstraintStream<A> forEachIncludingNullVars(Class<A> sourceClass) {
        assertValidFromType(sourceClass);
        return new BavetFromUniConstraintStream<>(this, sourceClass, RetrievalSemantics.STANDARD);
    }

    @Override
    public <A> BavetAbstractUniConstraintStream<Solution_, A> fromUnfiltered(Class<A> fromClass) {
        assertValidFromType(fromClass);
        return new BavetFromUniConstraintStream<>(this, fromClass, RetrievalSemantics.LEGACY);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public String getDefaultConstraintPackage() {
        return defaultConstraintPackage;
    }

}
