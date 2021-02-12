/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.taskassigning.core.model.solver;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import static org.kie.kogito.taskassigning.core.model.solver.DefaultTaskAssigningConstraints.hardLevelWeight;
import static org.kie.kogito.taskassigning.core.model.solver.DefaultTaskAssigningConstraints.softLevelWeight;

public class DefaultTaskAssigningConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                requiredPotentialOwner(constraintFactory),
                requiredSkills(constraintFactory),
                planningUserAssignment(constraintFactory),
                highLevelPriority(constraintFactory),
                desiredAffinities(constraintFactory),
                minimizeMakespan(constraintFactory),
                mediumLevelPriority(constraintFactory),
                lowLevelPriority(constraintFactory)
        };
    }

    protected Constraint requiredPotentialOwner(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.requiredPotentialOwner(constraintFactory, hardLevelWeight(0, 1));
    }

    protected Constraint requiredSkills(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.requiredSkills(constraintFactory, hardLevelWeight(1, 1));
    }

    protected Constraint planningUserAssignment(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.planningUserAssignment(constraintFactory, softLevelWeight(0, 1));
    }

    protected Constraint highLevelPriority(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.highLevelPriority(constraintFactory, softLevelWeight(1, 1));
    }

    protected Constraint desiredAffinities(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.desiredAffinities(constraintFactory, softLevelWeight(2, 1));
    }

    protected Constraint minimizeMakespan(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.minimizeMakespan(constraintFactory, softLevelWeight(3, 1));
    }

    protected Constraint mediumLevelPriority(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.mediumLevelPriority(constraintFactory, softLevelWeight(4, 1));
    }

    protected Constraint lowLevelPriority(ConstraintFactory constraintFactory) {
        return DefaultTaskAssigningConstraints.lowLevelPriority(constraintFactory, softLevelWeight(5, 1));
    }
}