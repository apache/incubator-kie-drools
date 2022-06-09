package org.optaplanner.core.impl.exhaustivesearch.scope;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.AbstractNodeComparatorTest;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.ScoreFirstNodeComparator;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ExhaustiveSearchPhaseScopeTest extends AbstractNodeComparatorTest {

    @Test
    void testNodePruning() {
        ExhaustiveSearchPhaseScope<TestdataSolution> phase = new ExhaustiveSearchPhaseScope<>(new SolverScope<>());
        phase.setExpandableNodeQueue(new TreeSet<>(new ScoreFirstNodeComparator(true)));
        phase.addExpandableNode(buildNode(0, "0", 0, 0));
        phase.addExpandableNode(buildNode(0, "1", 0, 0));
        phase.addExpandableNode(buildNode(0, "2", 0, 0));
        phase.setBestPessimisticBound(SimpleScore.of(Integer.MIN_VALUE));
        phase.registerPessimisticBound(SimpleScore.of(1));
        assertThat(phase.getExpandableNodeQueue().size()).isEqualTo(1);
    }

}
