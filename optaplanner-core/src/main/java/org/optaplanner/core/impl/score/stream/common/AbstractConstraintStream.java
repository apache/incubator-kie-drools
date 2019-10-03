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

import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintWeightDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.stream.InnerConstraintFactory;

public abstract class AbstractConstraintStream<Solution_> implements ConstraintStream {

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    protected Function<Solution_, Score<?>> buildConstraintWeightExtractor(String constraintPackage, String constraintName) {
        validateConstraintId(constraintPackage, constraintName);
        SolutionDescriptor<Solution_> solutionDescriptor = getConstraintFactory().getSolutionDescriptor();
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor
                = solutionDescriptor.getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            throw new IllegalStateException("The constraint (" + constraintName + ") of package (" + constraintPackage
                    + ") does not hard-code a constraint weight"
                    +" and there is no @" + ConstraintConfigurationProvider.class.getSimpleName()
                    + " on the solution class (" + solutionDescriptor.getSolutionClass() + ").\n"
                    + "Maybe add a @" + ConstraintConfiguration.class.getSimpleName() + " class"
                    + " or use " + ConstraintStream.class.getSimpleName() + ".penalize()/reward()"
                    + " instead of penalizeConfigurable()/rewardConfigurable.");
        }
        ConstraintWeightDescriptor<Solution_> weightDescriptor = configurationDescriptor.findConstraintWeightDescriptor(constraintPackage, constraintName);
        if (weightDescriptor == null) {
            throw new IllegalStateException("The constraint (" + constraintName + ") of package (" + constraintPackage
                    + ") does not hard-code a constraint weight"
                    +" and there is no such @" + ConstraintWeight.class.getSimpleName()
                    + " on the constraintConfigurationClass (" + configurationDescriptor.getConstraintConfigurationClass() + ").\n"
                    + "Maybe there is a typo in the constraintPackage or constraintName of one of the @" + ConstraintWeight.class.getSimpleName() + " members.\n"
                    + "Maybe add a @" + ConstraintWeight.class.getSimpleName() + " member for it.");
        }
        return weightDescriptor.createExtractor();
    }

    protected Function<Solution_, Score<?>> buildConstraintWeightExtractor(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        validateConstraintId(constraintPackage, constraintName);
        // Duplicates validation when the session is build, but this fail-faster when weights are hard coded
        getConstraintFactory().getSolutionDescriptor().validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        return solution -> constraintWeight;
    }

    protected void validateConstraintId(String constraintPackage, String constraintName) {
        if (constraintPackage == null) {
            throw new IllegalStateException("The constraint (" + constraintName
                    + ") cannot have a null package (" + constraintPackage + ").");
        }
        if (constraintName == null) {
            throw new IllegalStateException("The constraint of package (" + constraintPackage
                    + ") cannot have a null name (" + constraintName + ").");
        }
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public abstract InnerConstraintFactory<Solution_> getConstraintFactory();

    @Override
    public final Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return impactScore(constraintPackage, constraintName, constraintWeight, false);
    }

    @Override
    public final Constraint penalizeConfigurable(String constraintPackage, String constraintName) {
        return impactScoreConfigurable(constraintPackage, constraintName, false);
    }

    @Override
    public final Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        return impactScore(constraintPackage, constraintName, constraintWeight,true);
    }

    @Override
    public final Constraint rewardConfigurable(String constraintPackage, String constraintName) {
        return impactScoreConfigurable(constraintPackage, constraintName, true);
    }

    abstract protected Constraint impactScore(String constraintPackage, String constraintName,
            Score<?> constraintWeight, boolean positive);

    abstract protected Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            boolean positive);

}
