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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import org.optaplanner.core.api.score.Score;
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
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import io.quarkus.arc.DefaultBean;

@ApplicationScoped
public class DefaultOptaPlannerBeanProvider {

    private SolverFactory<?> solverFactory;

    private SolverManager<?, ?> solverManager;

    private SolutionManager<?, ?> solutionManager;

    private ScoreManager<?, ?> scoreManager;

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolverFactory<Solution_> solverFactory(SolverConfig solverConfig) {
        synchronized (this) {
            if (solverFactory == null) {
                solverFactory = SolverFactory.create(solverConfig);
            }
        }
        return (SolverFactory<Solution_>) solverFactory;
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager(SolverFactory<Solution_> solverFactory,
            SolverManagerConfig solverManagerConfig) {
        synchronized (this) {
            if (solverManager == null) {
                solverManager = SolverManager.create(solverFactory, solverManagerConfig);
            }
        }
        return (SolverManager<Solution_, ProblemId_>) solverManager;
    }

    // Quarkus-ARC-Weld can't deal with enum pattern generics such as Score<S extends Score<S>>.
    // See https://github.com/quarkusio/quarkus/pull/12137
    //    @DefaultBean
    //    @Dependent
    //    @Produces
    @Deprecated(forRemoval = true)
    <Solution_, Score_ extends Score<Score_>> ScoreManager<Solution_, Score_> scoreManager(
            SolverFactory<Solution_> solverFactory) {
        synchronized (this) {
            if (scoreManager == null) {
                scoreManager = ScoreManager.create(solverFactory);
            }
        }
        return (ScoreManager<Solution_, Score_>) scoreManager;
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleScore> scoreManager_workaroundSimpleScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleLongScore> scoreManager_workaroundSimpleLongScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleBigDecimalScore> scoreManager_workaroundSimpleBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftScore> scoreManager_workaroundHardSoftScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftLongScore> scoreManager_workaroundHardSoftLongScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftBigDecimalScore> scoreManager_workaroundHardSoftBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftScore> scoreManager_workaroundHardMediumSoftScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftLongScore> scoreManager_workaroundHardMediumSoftLongScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftBigDecimalScore> scoreManager_workaroundHardMediumSoftBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, BendableScore> scoreManager_workaroundBendableScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, BendableLongScore> scoreManager_workaroundBendableLongScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    @Deprecated(forRemoval = true)
    @DefaultBean
    @Dependent
    @Produces
    <Solution_> ScoreManager<Solution_, BendableBigDecimalScore> scoreManager_workaroundBendableBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return scoreManager(solverFactory);
    }

    // Quarkus-ARC-Weld can't deal with enum pattern generics such as Score<S extends Score<S>>.
    // See https://github.com/quarkusio/quarkus/pull/12137
    //    @DefaultBean
    //    @Dependent
    //    @Produces
    <Solution_, Score_ extends Score<Score_>> SolutionManager<Solution_, Score_> solutionManager(
            SolverFactory<Solution_> solverFactory) {
        synchronized (this) {
            if (solutionManager == null) {
                solutionManager = SolutionManager.create(solverFactory);
            }
        }
        return (SolutionManager<Solution_, Score_>) solutionManager;
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, SimpleScore> solutionManager_workaroundSimpleScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, SimpleLongScore> solutionManager_workaroundSimpleLongScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, SimpleBigDecimalScore> solutionManager_workaroundSimpleBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardSoftScore> solutionManager_workaroundHardSoftScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardSoftLongScore> solutionManager_workaroundHardSoftLongScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardSoftBigDecimalScore> solutionManager_workaroundHardSoftBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardMediumSoftScore> solutionManager_workaroundHardMediumSoftScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardMediumSoftLongScore> solutionManager_workaroundHardMediumSoftLongScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, HardMediumSoftBigDecimalScore>
            solutionManager_workaroundHardMediumSoftBigDecimalScore(SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, BendableScore> solutionManager_workaroundBendableScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, BendableLongScore> solutionManager_workaroundBendableLongScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

    @DefaultBean
    @Dependent
    @Produces
    <Solution_> SolutionManager<Solution_, BendableBigDecimalScore> solutionManager_workaroundBendableBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return solutionManager(solverFactory);
    }

}
