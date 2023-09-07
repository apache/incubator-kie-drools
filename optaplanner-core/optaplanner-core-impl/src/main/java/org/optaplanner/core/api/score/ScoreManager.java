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

package org.optaplanner.core.api.score;

import static org.optaplanner.core.api.solver.SolutionUpdatePolicy.UPDATE_ALL;

import java.util.UUID;

import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolutionUpdatePolicy;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.impl.score.DefaultScoreManager;

/**
 * A stateless service to help calculate {@link Score}, {@link ConstraintMatchTotal}, {@link Indictment}, etc.
 * <p>
 * To create a ScoreManager, use {@link #create(SolverFactory)}.
 * <p>
 * These methods are thread-safe unless explicitly stated otherwise.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the actual score type
 * @deprecated Use {@link SolutionManager} instead.
 */
@Deprecated(forRemoval = true)
public interface ScoreManager<Solution_, Score_ extends Score<Score_>> {

    // ************************************************************************
    // Static creation methods: SolverFactory
    // ************************************************************************

    /**
     * Uses a {@link SolverFactory} to build a {@link ScoreManager}.
     *
     * @param solverFactory never null
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @param <Score_> the actual score type
     */
    static <Solution_, Score_ extends Score<Score_>> ScoreManager<Solution_, Score_> create(
            SolverFactory<Solution_> solverFactory) {
        return new DefaultScoreManager<>(SolutionManager.<Solution_, Score_> create(solverFactory));
    }

    /**
     * Uses a {@link SolverManager} to build a {@link ScoreManager}.
     *
     * @param solverManager never null
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @param <Score_> the actual score type
     * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}
     */
    static <Solution_, Score_ extends Score<Score_>, ProblemId_> ScoreManager<Solution_, Score_> create(
            SolverManager<Solution_, ProblemId_> solverManager) {
        return new DefaultScoreManager<>(SolutionManager.<Solution_, Score_, ProblemId_> create(solverManager));
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Calculates the {@link Score} of a {@link PlanningSolution} and updates its {@link PlanningScore} member.
     *
     * @param solution never null
     */
    Score_ updateScore(Solution_ solution);

    /**
     * Returns a diagnostic text that explains the solution through the {@link ConstraintMatch} API to identify which
     * constraints or planning entities cause that score quality.
     * In case of an {@link Score#isFeasible() infeasible} solution, this can help diagnose the cause of that.
     * <p>
     * Do not parse this string.
     * Instead, to provide this information in a UI or a service, use {@link #explainScore(Object)}
     * to retrieve {@link ScoreExplanation#getConstraintMatchTotalMap()} and {@link ScoreExplanation#getIndictmentMap()}
     * and convert those into a domain specific API.
     *
     * @param solution never null
     * @return null if {@link #updateScore(Object)} returns null with the same solution
     * @throws IllegalStateException when constraint matching is disabled or not supported by the underlying score
     *         calculator, such as {@link EasyScoreCalculator}.
     */
    String getSummary(Solution_ solution);

    /**
     * Calculates and retrieves {@link ConstraintMatchTotal}s and {@link Indictment}s necessary for describing the
     * quality of a particular solution.
     *
     * @param solution never null
     * @return never null
     * @throws IllegalStateException when constraint matching is disabled or not supported by the underlying score
     *         calculator, such as {@link EasyScoreCalculator}.
     */
    ScoreExplanation<Solution_, Score_> explainScore(Solution_ solution);

    /**
     * As defined by {@link #update(Object, SolutionUpdatePolicy)},
     * using {@link SolutionUpdatePolicy#UPDATE_ALL}.
     *
     */
    default Score_ update(Solution_ solution) {
        return update(solution, UPDATE_ALL);
    }

    /**
     * Updates the given solution according to the {@link SolutionUpdatePolicy}.
     *
     * @param solution never null
     * @param solutionUpdatePolicy never null; if unsure, pick {@link SolutionUpdatePolicy#UPDATE_ALL}
     * @return possibly null if already null and {@link SolutionUpdatePolicy} didn't cause its update
     * @see SolutionUpdatePolicy Description of individual policies with respect to performance trade-offs.
     */
    Score_ update(Solution_ solution, SolutionUpdatePolicy solutionUpdatePolicy);

    /**
     * As defined by {@link #explain(Object)},
     * using {@link SolutionUpdatePolicy#UPDATE_ALL}.
     */
    default ScoreExplanation<Solution_, Score_> explain(Solution_ solution) {
        return explain(solution, UPDATE_ALL);
    }

    /**
     * Calculates and retrieves {@link ConstraintMatchTotal}s and {@link Indictment}s necessary for describing the
     * quality of a particular solution.
     *
     * @param solution never null
     * @param solutionUpdatePolicy never null; if unsure, pick {@link SolutionUpdatePolicy#UPDATE_ALL}
     * @return never null
     * @throws IllegalStateException when constraint matching is disabled or not supported by the underlying score
     *         calculator, such as {@link EasyScoreCalculator}.
     * @see SolutionUpdatePolicy Description of individual policies with respect to performance trade-offs.
     */
    ScoreExplanation<Solution_, Score_> explain(Solution_ solution, SolutionUpdatePolicy solutionUpdatePolicy);

}
