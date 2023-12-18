/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.quarkus.bean;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;

import io.quarkus.arc.DefaultBean;

/**
 * Throws an exception if an application tries to inject beans and the OptaPlanner Quarkus extension is skipped
 * due to missing domain classes.
 */
public class UnavailableOptaPlannerBeanProvider {

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolverFactory<Solution_> solverFactory() {
        throw createException(SolverFactory.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager() {
        throw createException(SolverManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleScore> scoreManager_workaroundSimpleScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleLongScore> scoreManager_workaroundSimpleLongScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleBigDecimalScore> scoreManager_workaroundSimpleBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftScore> scoreManager_workaroundHardSoftScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftLongScore> scoreManager_workaroundHardSoftLongScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftBigDecimalScore> scoreManager_workaroundHardSoftBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftScore> scoreManager_workaroundHardMediumSoftScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftLongScore> scoreManager_workaroundHardMediumSoftLongScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftBigDecimalScore>
            scoreManager_workaroundHardMediumSoftBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, BendableScore> scoreManager_workaroundBendableScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, BendableLongScore> scoreManager_workaroundBendableLongScore() {
        throw createException(ScoreManager.class);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, BendableBigDecimalScore> scoreManager_workaroundBendableBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, SimpleScore> solutionManager_workaroundSimpleScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, SimpleLongScore> solutionManager_workaroundSimpleLongScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, SimpleBigDecimalScore> solutionManager_workaroundSimpleBigDecimalScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardSoftScore> solutionManager_workaroundHardSoftScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardSoftLongScore> solutionManager_workaroundHardSoftLongScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardSoftBigDecimalScore> solutionManager_workaroundHardSoftBigDecimalScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardMediumSoftScore> solutionManager_workaroundHardMediumSoftScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardMediumSoftLongScore> solutionManager_workaroundHardMediumSoftLongScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardMediumSoftBigDecimalScore>
            solutionManager_workaroundHardMediumSoftBigDecimalScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, BendableScore> solutionManager_workaroundBendableScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, BendableLongScore> solutionManager_workaroundBendableLongScore() {
        throw createException(SolutionManager.class);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, BendableBigDecimalScore> solutionManager_workaroundBendableBigDecimalScore() {
        throw createException(SolutionManager.class);
    }

    private RuntimeException createException(Class<?> beanClass) {
        return new IllegalStateException("The " + beanClass.getName() + " is not available as there are no @"
                + PlanningSolution.class.getSimpleName() + " or @" + PlanningEntity.class.getSimpleName()
                + " annotated classes."
                + "\nIf your domain classes are located in a dependency of this project, maybe try generating"
                + " the Jandex index by using the jandex-maven-plugin in that dependency, or by adding"
                + "application.properties entries (quarkus.index-dependency.<name>.group-id"
                + " and quarkus.index-dependency.<name>.artifact-id).");
    }
}
