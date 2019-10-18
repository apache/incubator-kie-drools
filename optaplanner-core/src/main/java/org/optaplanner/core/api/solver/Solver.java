/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.solver;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * A Solver solves a planning problem.
 * Clients usually call {@link #solve} and then {@link #getBestSolution()}.
 * <p>
 * These methods are not thread-safe and should be called from the same thread,
 * except for the methods that are explicitly marked as thread-safe.
 * Note that despite that {@link #solve} is not thread-safe for clients of this class,
 * that method is free to do multithreading inside itself.
 * <p>
 * Build by a {@link SolverFactory}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface Solver<Solution_> {

    /**
     * The best solution is the {@link PlanningSolution best solution} found during solving:
     * it might or might not be optimal, feasible or even initialized.
     * <p>
     * The {@link #solve} method also returns the best solution,
     * but this method is useful in rare asynchronous situations (although
     * {@link SolverEventListener#bestSolutionChanged(BestSolutionChangedEvent)} is often more appropriate).
     * <p>
     * This method is thread-safe.
     * @return never null (unless {@link #solve(Object)} hasn't been called yet),
     * but it can return the uninitialized {@link PlanningSolution} with a {@link Score} null.
     */
    Solution_ getBestSolution();

    /**
     * Returns the {@link Score} of the {@link #getBestSolution()}.
     * <p>
     * This is useful for generic code, which doesn't know the type of the {@link PlanningSolution}
     * to retrieve the {@link Score} from the {@link #getBestSolution()} easily.
     * <p>
     * This method is thread-safe.
     * @return null if the {@link PlanningSolution} is still uninitialized
     */
    Score getBestScore();

    /**
     * Returns a diagnostic text that explains the {@link #getBestSolution()} through the {@link ConstraintMatch} API
     * to identify which constraints or planning entities cause that {@link #getBestScore()} quality.
     * In case of an {@link FeasibilityScore#isFeasible() infeasible} solution,
     * this can help diagnose the cause of that.
     * <p>
     * Do not parse this string.
     * Instead, to provide this information in a UI or a service, use {@link SolverFactory#getScoreDirectorFactory()}
     * to retrieve {@link ScoreDirector#getConstraintMatchTotalMap()} and {@link ScoreDirector#getIndictmentMap()}
     * and convert those into a domain specific API.
     * <p>
     * This method is thread-safe.
     * @return null if {@link #getBestScore()} returns null
     * @see ScoreDirector#explainScore()
     */
    String explainBestScore();

    /**
     * Returns the amount of milliseconds spent solving since the last start.
     * If it hasn't started it yet, it returns 0.
     * If it hasn't ended yet, it returns the time between the last start and now.
     * If it has ended already, it returns the time between the last start and the ending.
     * <p>
     * A {@link #addProblemFactChange(ProblemFactChange)} triggers a restart which resets this time.
     * <p>
     * This method is thread-safe.
     * @return the amount of milliseconds spent solving since the last (re)start, at least 0
     */
    long getTimeMillisSpent();

    /**
     * Solves the planning problem and returns the best solution encountered
     * (which might or might not be optimal, feasible or even initialized).
     * <p>
     * It can take seconds, minutes, even hours or days before this method returns,
     * depending on the {@link Termination} configuration.
     * To terminate a {@link Solver} early, call {@link #terminateEarly()}.
     * @param problem never null, usually its planning variables are uninitialized
     * @return never null, but it can return the original, uninitialized {@link PlanningSolution} with a {@link Score} null.
     * @see #terminateEarly()
     */
    Solution_ solve(Solution_ problem);

    /**
     * This method is thread-safe.
     * @return true if the {@link #solve} method is still running.
     */
    boolean isSolving();

    /**
     * Notifies the solver that it should stop at its earliest convenience.
     * This method returns immediately, but it takes an undetermined time
     * for the {@link #solve} to actually return.
     * <p>
     * This method is thread-safe.
     * @return true if successful
     * @see #isTerminateEarly()
     * @see Future#cancel(boolean)
     */
    boolean terminateEarly();

    /**
     * This method is thread-safe.
     * @return true if terminateEarly has been called since the {@link Solver} started.
     * @see Future#isCancelled()
     */
    boolean isTerminateEarly();

    /**
     * Schedules a {@link ProblemFactChange} to be processed.
     * <p>
     * As a side-effect, this restarts the {@link Solver}, effectively resetting all {@link Termination}s,
     * but not {@link #terminateEarly()}.
     * <p>
     * This method is thread-safe.
     * Follows specifications of {@link BlockingQueue#add(Object)} with by default
     * a capacity of {@link Integer#MAX_VALUE}.
     * @param problemFactChange never null
     * @return true (as specified by {@link Collection#add})
     * @see #addProblemFactChanges(List)
     */
    boolean addProblemFactChange(ProblemFactChange<Solution_> problemFactChange);

    /**
     * Schedules multiple {@link ProblemFactChange}s to be processed.
     * <p>
     * As a side-effect, this restarts the {@link Solver}, effectively resetting all {@link Termination}s,
     * but not {@link #terminateEarly()}.
     * <p>
     * This method is thread-safe.
     * Follows specifications of {@link BlockingQueue#addAll(Collection)} with by default
     * a capacity of {@link Integer#MAX_VALUE}.
     * @param problemFactChangeList never null
     * @return true (as specified by {@link Collection#add})
     * @see #addProblemFactChange(ProblemFactChange)
     */
    boolean addProblemFactChanges(List<ProblemFactChange<Solution_>> problemFactChangeList);

    /**
     * Checks if all scheduled {@link ProblemFactChange}s have been processed.
     * <p>
     * This method is thread-safe.
     * @return true if there are no {@link ProblemFactChange}s left to do
     */
    boolean isEveryProblemFactChangeProcessed();

    /**
     * @param eventListener never null
     */
    void addEventListener(SolverEventListener<Solution_> eventListener);

    /**
     * @param eventListener never null
     */
    void removeEventListener(SolverEventListener<Solution_> eventListener);

    /**
     * Useful to reuse the {@link Score} calculation (for example in a UI)
     * and to explain the {@link Score} to the user
     * with the {@link ConstraintMatchTotal} and {@link Indictment} API.
     * @return never null
     * @deprecated in favor of {@link SolverFactory#getScoreDirectorFactory()}
     * Will be removed in 8.0.
     */
    @Deprecated
    ScoreDirectorFactory<Solution_> getScoreDirectorFactory();

}
