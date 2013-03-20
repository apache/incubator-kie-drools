/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.move.CompositeMove;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListener;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

@Deprecated
public class PlanningVariableWalker implements SolverPhaseLifecycleListener {
    
    private final PlanningEntityDescriptor planningEntityDescriptor;
    private List<PlanningValueWalker> planningValueWalkerList;

    private ScoreDirector scoreDirector;

    private Object planningEntity;

    public PlanningVariableWalker(PlanningEntityDescriptor planningEntityDescriptor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
    }

    public void setPlanningValueWalkerList(List<PlanningValueWalker> planningValueWalkerList) {
        this.planningValueWalkerList = planningValueWalkerList;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void solvingStarted(DefaultSolverScope solverScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.solvingStarted(solverScope);
        }
    }

    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.phaseStarted(phaseScope);
        }
        scoreDirector = phaseScope.getScoreDirector();
    }

    public void stepStarted(AbstractStepScope stepScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.stepStarted(stepScope);
        }
    }

    public void stepEnded(AbstractStepScope stepScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.stepEnded(stepScope);
        }
    }

    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.phaseEnded(phaseScope);
        }
        scoreDirector = null;
        planningEntity = null;
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.solvingEnded(solverScope);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void initWalk(Object planningEntity) {
        this.planningEntity = planningEntity;
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.initWalk(planningEntity);
        }
    }

    public boolean hasWalk() {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            if (planningValueWalker.hasWalk()) {
                return true;
            }
        }
        // All levels are maxed out
        return false;
    }

    public void walk() {
        // Find the level to increment (for example in 115999)
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            if (planningValueWalker.hasWalk()) {
                // Increment that level (for example 5 in 115999)
                planningValueWalker.walk();
                // Do not touch the higher levels (for example each 1 in 115999)
                break;
            } else {
                // Reset the lower levels (for example each 9 in 115999)
                planningValueWalker.resetWalk();
            }
        }
    }

    public void resetWalk() {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.resetWalk();
        }
    }

    // TODO refactor variableWalker to this
    public Iterator<Move> moveIterator(final Object planningEntity) {
        if (planningValueWalkerList.size() == 1) {
            PlanningValueWalker planningValueWalker = planningValueWalkerList.iterator().next();
            return planningValueWalker.moveIterator(planningEntity);
        } else {
            final List<Iterator<Move>> moveIteratorList = new ArrayList<Iterator<Move>>(planningValueWalkerList.size());
            final List<Move> composedMoveList = new ArrayList<Move>(planningValueWalkerList.size());
            boolean moveIteratorIsFirst = true;
            for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
                Iterator<Move> moveIterator = planningValueWalker.moveIterator(planningEntity);
                moveIteratorList.add(moveIterator);
                Move initialMove;
                if (moveIteratorIsFirst) {
                    // The first moveIterator 's next() call will be done by the new Iterator 's next() call
                    initialMove = null;
                    moveIteratorIsFirst = false;
                } else {
                    if (!moveIterator.hasNext()) {
                        // TODO the algorithms should be able to cope with that. Mind the use of .walkerList.get(j)
                        throw new IllegalStateException("The planning entity class ("
                                + planningEntityDescriptor.getPlanningEntityClass() + ") for planning variable ("
                                + planningValueWalker.getPlanningVariableDescriptor().getVariableName()
                                + ") has an empty planning value range for planning entity (" + planningEntity + ").");
                    }
                    initialMove = moveIterator.next();
                }
                composedMoveList.add(initialMove);
            }
            return new Iterator<Move>() {
                public boolean hasNext() {
                    for (Iterator<Move> moveIterator : moveIteratorList) {
                        if (moveIterator.hasNext()) {
                            return true;
                        }
                    }
                    // All levels are maxed out
                    return false;
                }

                public Move next() {
                    // Find the level to increment (for example in 115999)
                    for (int i = 0; i < moveIteratorList.size(); i++) {
                        Iterator<Move> moveIterator = moveIteratorList.get(i);
                        if (moveIterator.hasNext()) {
                            // Increment that level (for example 5 in 115999)
                            composedMoveList.set(i, moveIterator.next());
                            // Do not touch the higher levels (for example each 1 in 115999)
                            break;
                        } else {
                            // Reset a lower level (for example each 9 in 115999)
                            moveIterator = planningValueWalkerList.get(i).moveIterator(planningEntity);
                            moveIteratorList.set(i, moveIterator);
                            composedMoveList.set(i, moveIterator.next());
                        }
                    }
                    return new CompositeMove(new ArrayList<Move>(composedMoveList));
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

}
