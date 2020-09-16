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

package org.optaplanner.quarkus;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

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
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import io.quarkus.arc.DefaultBean;

public class OptaPlannerBeanProvider {

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> SolverFactory<Solution_> solverFactory(SolverConfig solverConfig) {
        return SolverFactory.create(solverConfig);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager(SolverFactory<Solution_> solverFactory,
            SolverManagerConfig solverManagerConfig) {
        return SolverManager.create(solverFactory, solverManagerConfig);
    }

    // Quarkus-ARC-Weld can't deal with enum pattern generics such as Score<S extends Score<S>>.
    // See https://github.com/quarkusio/quarkus/pull/12137
    //    @DefaultBean
    //    @Singleton
    //    @Produces
    //    <Solution_, Score_ extends Score<Score_>> ScoreManager<Solution_, Score_> scoreManager(
    //            SolverFactory<Solution_> solverFactory) {
    //        return ScoreManager.create(solverFactory);
    //    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleScore> scoreManager_workaroundSimpleScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleLongScore> scoreManager_workaroundSimpleLongScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleBigDecimalScore> scoreManager_workaroundSimpleBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftScore> scoreManager_workaroundHardSoftScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftLongScore> scoreManager_workaroundHardSoftLongScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftBigDecimalScore> scoreManager_workaroundHardSoftBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftScore> scoreManager_workaroundHardMediumSoftScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftLongScore> scoreManager_workaroundHardMediumSoftLongScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftBigDecimalScore> scoreManager_workaroundHardMediumSoftBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, BendableScore> scoreManager_workaroundBendableScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, BendableLongScore> scoreManager_workaroundBendableLongScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, BendableBigDecimalScore> scoreManager_workaroundBendableBigDecimalScore(
            SolverFactory<Solution_> solverFactory) {
        return ScoreManager.create(solverFactory);
    }

}
