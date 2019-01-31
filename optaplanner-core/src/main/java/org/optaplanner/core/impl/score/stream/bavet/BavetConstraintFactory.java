/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintWeightDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.InnerConstraintFactory;

public final class BavetConstraintFactory<Solution_> implements InnerConstraintFactory<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final List<BavetConstraint<Solution_>> constraintList = new ArrayList<>(20);

    public BavetConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    @Override
    public Constraint newConstraint(String constraintName) {
        return newConstraint(null, constraintName);
    }

    @Override
    public Constraint newConstraint(String constraintPackage, String constraintName) {
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor
                = solutionDescriptor.getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            throw new IllegalStateException("The constraint with package (" + constraintPackage
                    + ") and name (" + constraintName + ") does not hard-code a constraint weight "
                    +" and there is no @" + ConstraintConfigurationProvider.class.getSimpleName()
                    + " on the solution class (" + solutionDescriptor.getSolutionClass() + ").\n"
                    + "Maybe add a @" + ConstraintConfiguration.class + ".\n"
                    + "Or maybe call newConstraintWithWeight() instead.\n");
        }
        if (constraintPackage == null) {
            constraintPackage = configurationDescriptor.getConstraintPackage();
        }
        ConstraintWeightDescriptor<Solution_> weightDescriptor = configurationDescriptor.findConstraintWeightDescriptor(constraintPackage, constraintName);
        if (weightDescriptor == null) {
            throw new IllegalStateException("The constraint with package (" + constraintPackage
                    + ") and name (" + constraintName + ") does not hard-code a constraint weight "
                    +" and there is no such @" + ConstraintWeight.class.getSimpleName()
                    + " on the constraintConfigurationClass (" + configurationDescriptor.getConstraintConfigurationClass() + ").\n"
                    + "Maybe there is a typo in the constraintPackage or constraintName of one of the @" + ConstraintWeight.class + " members.\n"
                    + "Maybe add a @" + ConstraintWeight.class + " member for it.\n");
        }
        Function<Solution_, Score<?>> constraintWeightExtractor = weightDescriptor.createExtractor();
        return newConstraint(constraintPackage, constraintName, constraintWeightExtractor);
    }

    @Override
    public Constraint newConstraintWithWeight(String constraintName, Score<?> constraintWeight) {
        return newConstraintWithWeight(null, constraintName, constraintWeight);
    }

    @Override
    public Constraint newConstraintWithWeight(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        // Duplicates validation when the session is build, but this fail-faster when weights are hard coded
        solutionDescriptor.validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        Function<Solution_, Score<?>> constraintWeightExtractor = solution -> constraintWeight;
        return newConstraint(constraintPackage, constraintName, constraintWeightExtractor);
    }

    private Constraint newConstraint(String constraintPackage, String constraintName, Function<Solution_, Score<?>> constraintWeightExtractor) {
        BavetConstraint<Solution_> constraint = new BavetConstraint<>(this, constraintPackage, constraintName, constraintWeightExtractor);
        // TODO adding the same constraintId twice should throw an error
        constraintList.add(constraint);
        return constraint;
    }

    @Override
    public ConstraintSession<Solution_> buildSession(Solution_ workingSolution) {
        Score<?> zeroScore = solutionDescriptor.getScoreDefinition().getZeroScore();
        Map<BavetConstraint<Solution_>, Score<?>> constraintToWeightMap = new LinkedHashMap<>(constraintList.size());
        for (BavetConstraint<Solution_> constraint : constraintList) {
            Score<?> constraintWeight = constraint.extractConstraintWeight(workingSolution);
            if (!constraintWeight.equals(zeroScore)) {
                constraintToWeightMap.put(constraint, constraintWeight);
            }
        }
        return new BavetConstraintSession<>(constraintToWeightMap);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public SolutionDescriptor<?> getSolutionDescriptor() {
        return solutionDescriptor;
    }

}
