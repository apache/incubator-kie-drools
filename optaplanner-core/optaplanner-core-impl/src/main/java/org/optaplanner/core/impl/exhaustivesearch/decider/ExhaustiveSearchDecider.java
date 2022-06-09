package org.optaplanner.core.impl.exhaustivesearch.decider;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.event.ExhaustiveSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.ManualEntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExhaustiveSearchDecider<Solution_> implements ExhaustiveSearchPhaseLifecycleListener<Solution_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExhaustiveSearchDecider.class);

    protected final String logIndentation;
    protected final BestSolutionRecaller<Solution_> bestSolutionRecaller;
    protected final Termination<Solution_> termination;
    protected final ManualEntityMimicRecorder<Solution_> manualEntityMimicRecorder;
    protected final MoveSelector<Solution_> moveSelector;
    protected final boolean scoreBounderEnabled;
    protected final ScoreBounder scoreBounder;

    protected boolean assertMoveScoreFromScratch = false;
    protected boolean assertExpectedUndoMoveScore = false;

    public ExhaustiveSearchDecider(String logIndentation, BestSolutionRecaller<Solution_> bestSolutionRecaller,
            Termination<Solution_> termination, ManualEntityMimicRecorder<Solution_> manualEntityMimicRecorder,
            MoveSelector<Solution_> moveSelector, boolean scoreBounderEnabled, ScoreBounder scoreBounder) {
        this.logIndentation = logIndentation;
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.termination = termination;
        this.manualEntityMimicRecorder = manualEntityMimicRecorder;
        this.moveSelector = moveSelector;
        this.scoreBounderEnabled = scoreBounderEnabled;
        this.scoreBounder = scoreBounder;
    }

    public ManualEntityMimicRecorder<Solution_> getManualEntityMimicRecorder() {
        return manualEntityMimicRecorder;
    }

    public MoveSelector<Solution_> getMoveSelector() {
        return moveSelector;
    }

    public boolean isScoreBounderEnabled() {
        return scoreBounderEnabled;
    }

    public ScoreBounder getScoreBounder() {
        return scoreBounder;
    }

    public void setAssertMoveScoreFromScratch(boolean assertMoveScoreFromScratch) {
        this.assertMoveScoreFromScratch = assertMoveScoreFromScratch;
    }

    public void setAssertExpectedUndoMoveScore(boolean assertExpectedUndoMoveScore) {
        this.assertExpectedUndoMoveScore = assertExpectedUndoMoveScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        moveSelector.solvingStarted(solverScope);
    }

    @Override
    public void phaseStarted(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        moveSelector.phaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(ExhaustiveSearchStepScope<Solution_> stepScope) {
        moveSelector.stepStarted(stepScope);
    }

    @Override
    public void stepEnded(ExhaustiveSearchStepScope<Solution_> stepScope) {
        moveSelector.stepEnded(stepScope);
    }

    @Override
    public void phaseEnded(ExhaustiveSearchPhaseScope<Solution_> phaseScope) {
        moveSelector.phaseEnded(phaseScope);
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        moveSelector.solvingEnded(solverScope);
    }

    public void expandNode(ExhaustiveSearchStepScope<Solution_> stepScope) {
        ExhaustiveSearchNode expandingNode = stepScope.getExpandingNode();
        manualEntityMimicRecorder.setRecordedEntity(expandingNode.getEntity());
        stepScope.setBestScoreImproved(false);

        int moveIndex = 0;
        ExhaustiveSearchLayer moveLayer = stepScope.getPhaseScope().getLayerList().get(expandingNode.getDepth() + 1);
        for (Move<?> move : moveSelector) {
            ExhaustiveSearchNode moveNode = new ExhaustiveSearchNode(moveLayer, expandingNode);
            moveIndex++;
            moveNode.setMove(move);
            // Do not filter out pointless moves, because the original value of the entity(s) is irrelevant.
            // If the original value is null and the variable is nullable, the move to null must be done too.
            doMove(stepScope, moveNode);
            // TODO in the lowest level (and only in that level) QuitEarly can be useful
            // No QuitEarly because lower layers might be promising
            stepScope.getPhaseScope().getSolverScope().checkYielding();
            if (termination.isPhaseTerminated(stepScope.getPhaseScope())) {
                break;
            }
        }
        stepScope.setSelectedMoveCount((long) moveIndex);
    }

    private <Score_ extends Score<Score_>> void doMove(ExhaustiveSearchStepScope<Solution_> stepScope,
            ExhaustiveSearchNode moveNode) {
        InnerScoreDirector<Solution_, Score_> scoreDirector = stepScope.getScoreDirector();
        // TODO reuse scoreDirector.doAndProcessMove() unless it's an expandableNode
        Move<Solution_> move = moveNode.getMove();
        Move<Solution_> undoMove = move.doMove(scoreDirector);
        moveNode.setUndoMove(undoMove);
        processMove(stepScope, moveNode);
        undoMove.doMoveOnly(scoreDirector);
        if (assertExpectedUndoMoveScore) {
            // In BRUTE_FORCE a stepScore can be null because it was not calculated
            if (stepScope.getStartingStepScore() != null) {
                scoreDirector.assertExpectedUndoMoveScore(move, (Score_) stepScope.getStartingStepScore());
            }
        }
        LOGGER.trace("{}        Move treeId ({}), score ({}), expandable ({}), move ({}).",
                logIndentation,
                moveNode.getTreeId(), moveNode.getScore(), moveNode.isExpandable(), moveNode.getMove());
    }

    private <Score_ extends Score<Score_>> void processMove(ExhaustiveSearchStepScope<Solution_> stepScope,
            ExhaustiveSearchNode moveNode) {
        ExhaustiveSearchPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
        boolean lastLayer = moveNode.isLastLayer();
        if (!scoreBounderEnabled) {
            if (lastLayer) {
                Score_ score = phaseScope.calculateScore();
                moveNode.setScore(score);
                if (assertMoveScoreFromScratch) {
                    phaseScope.assertWorkingScoreFromScratch(score, moveNode.getMove());
                }
                bestSolutionRecaller.processWorkingSolutionDuringMove(score, stepScope);
            } else {
                phaseScope.addExpandableNode(moveNode);
            }
        } else {
            Score_ score = phaseScope.calculateScore();
            moveNode.setScore(score);
            if (assertMoveScoreFromScratch) {
                phaseScope.assertWorkingScoreFromScratch(score, moveNode.getMove());
            }
            if (lastLayer) {
                // There is no point in bounding a fully initialized score
                phaseScope.registerPessimisticBound(score);
                bestSolutionRecaller.processWorkingSolutionDuringMove(score, stepScope);
            } else {
                InnerScoreDirector<Solution_, Score_> scoreDirector = phaseScope.getScoreDirector();
                Score_ optimisticBound = (Score_) scoreBounder.calculateOptimisticBound(scoreDirector, score);
                moveNode.setOptimisticBound(optimisticBound);
                Score_ bestPessimisticBound = (Score_) phaseScope.getBestPessimisticBound();
                if (optimisticBound.compareTo(bestPessimisticBound) > 0) {
                    // It's still worth investigating this node further (no need to prune it)
                    phaseScope.addExpandableNode(moveNode);
                    Score_ pessimisticBound = (Score_) scoreBounder.calculatePessimisticBound(scoreDirector, score);
                    phaseScope.registerPessimisticBound(pessimisticBound);
                }
            }
        }
    }

}
