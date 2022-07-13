package org.optaplanner.constraint.streams.bavet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.uni.ForEachUniNode;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public final class BavetConstraintSessionFactory<Solution_, Score_ extends Score<Score_>> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final List<BavetConstraint<Solution_>> constraintList;

    public BavetConstraintSessionFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            List<BavetConstraint<Solution_>> constraintList) {
        this.solutionDescriptor = solutionDescriptor;
        this.constraintList = constraintList;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetConstraintSession<Score_> buildSession(boolean constraintMatchEnabled,
            Solution_ workingSolution) {
        ScoreDefinition<Score_> scoreDefinition = solutionDescriptor.getScoreDefinition();
        AbstractScoreInliner<Score_> scoreInliner = AbstractScoreInliner.buildScoreInliner(scoreDefinition,
                constraintMatchEnabled);

        Score_ zeroScore = scoreDefinition.getZeroScore();
        Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet = new LinkedHashSet<>();
        Map<Constraint, Score_> constraintWeightMap = new HashMap<>(constraintList.size());
        for (BavetConstraint<Solution_> constraint : constraintList) {
            Score_ constraintWeight = constraint.extractConstraintWeight(workingSolution);
            // Filter out nodes that only lead to constraints with zero weight.
            // Note: Node sharing happens earlier, in BavetConstraintFactory#share(Stream_).
            if (!constraintWeight.equals(zeroScore)) {
                // Relies on BavetConstraintFactory#share(Stream_) occurring for all constraint stream instances
                // to ensure there are no 2 equal ConstraintStream instances (with different child stream lists).
                constraint.collectActiveConstraintStreams(constraintStreamSet);
                constraintWeightMap.put(constraint, constraintWeight);
            }
        }
        NodeBuildHelper<Score_> buildHelper = new NodeBuildHelper<>(constraintStreamSet, constraintWeightMap, scoreInliner);
        // Build constraintStreamSet in reverse order to create downstream nodes first
        // so every node only has final variables (some of which have downstream node method references).
        List<BavetAbstractConstraintStream<Solution_>> reversedConstraintStreamList = new ArrayList<>(constraintStreamSet);
        Collections.reverse(reversedConstraintStreamList);
        for (BavetAbstractConstraintStream<Solution_> constraintStream : reversedConstraintStreamList) {
            constraintStream.buildNode(buildHelper);
        }
        List<AbstractNode> nodeList = buildHelper.destroyAndGetNodeList();
        Map<Class<?>, ForEachUniNode<Object>> declaredClassToNodeMap = new LinkedHashMap<>();
        for (AbstractNode node : nodeList) {
            if (node instanceof ForEachUniNode) {
                ForEachUniNode<Object> forEachUniNode = (ForEachUniNode<Object>) node;
                ForEachUniNode<Object> old = declaredClassToNodeMap.put(forEachUniNode.getForEachClass(), forEachUniNode);
                if (old != null) {
                    throw new IllegalStateException("Impossible state: For class (" + forEachUniNode.getForEachClass()
                            + ") there are 2 nodes (" + forEachUniNode + ", " + old + ").");
                }
            }
        }
        return new BavetConstraintSession<>(scoreInliner, declaredClassToNodeMap, nodeList.toArray(new AbstractNode[0]));
    }

}
