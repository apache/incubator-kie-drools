package org.optaplanner.core.impl.domain.variable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListAssignMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListSwapMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListUnassignMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.shadow_history.TestdataListEntityWithShadowHistory;
import org.optaplanner.core.impl.testdata.domain.list.shadow_history.TestdataListSolutionWithShadowHistory;
import org.optaplanner.core.impl.testdata.domain.list.shadow_history.TestdataListValueWithShadowHistory;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class ListVariableListenerTest {

    private final ListVariableDescriptor<TestdataListSolutionWithShadowHistory> variableDescriptor =
            TestdataListEntityWithShadowHistory.buildVariableDescriptorForValueList();

    private final InnerScoreDirector<TestdataListSolutionWithShadowHistory, SimpleScore> scoreDirector =
            PlannerTestUtils.mockScoreDirector(variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

    static TestdataListSolutionWithShadowHistory buildSolution(TestdataListEntityWithShadowHistory... entities) {
        List<TestdataListValueWithShadowHistory> values =
                Arrays.stream(entities).flatMap(e -> e.getValueList().stream()).collect(Collectors.toList());
        TestdataListSolutionWithShadowHistory solution = new TestdataListSolutionWithShadowHistory();
        solution.setEntityList(Arrays.asList(entities));
        solution.setValueList(values);
        return solution;
    }

    static void assertIndexHistory(TestdataListValueWithShadowHistory element, Integer... indexHistory) {
        assertThat(element.getIndex()).isEqualTo(indexHistory[indexHistory.length - 1]);
        assertThat(element.getIndexHistory()).containsExactly(indexHistory);
    }

    static void assertEntityHistory(TestdataListValueWithShadowHistory element,
            TestdataListEntityWithShadowHistory... entityHistory) {
        assertThat(element.getEntity()).isEqualTo(entityHistory[entityHistory.length - 1]);
        assertThat(element.getEntityHistory()).containsExactly(entityHistory);
    }

    void doChangeMove(
            TestdataListEntityWithShadowHistory sourceEntity, int sourceIndex,
            TestdataListEntityWithShadowHistory destinationEntity, int destinationIndex) {
        new ListChangeMove<>(variableDescriptor, sourceEntity, sourceIndex, destinationEntity, destinationIndex)
                .doMoveOnly(scoreDirector);
    }

    void doSwapMove(
            TestdataListEntityWithShadowHistory leftEntity, int leftIndex,
            TestdataListEntityWithShadowHistory rightEntity, int rightIndex) {
        new ListSwapMove<>(variableDescriptor, leftEntity, leftIndex, rightEntity, rightIndex)
                .doMoveOnly(scoreDirector);
    }

    @Test
    void addAndRemoveEntity() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListEntityWithShadowHistory ann = new TestdataListEntityWithShadowHistory("Ann", a, b, c);

        scoreDirector.setWorkingSolution(buildSolution(ann));

        // Add Ann.
        scoreDirector.beforeEntityAdded(ann);
        scoreDirector.afterEntityAdded(ann);
        scoreDirector.triggerVariableListeners();

        // Assert inverse entity.
        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann);
        assertEntityHistory(c, ann);

        // Assert index.
        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1);
        assertIndexHistory(c, 2);

        // Remove Ann.
        scoreDirector.beforeEntityRemoved(ann);
        scoreDirector.afterEntityRemoved(ann);
        scoreDirector.triggerVariableListeners();

        // Assert inverse entity.
        assertEntityHistory(a, ann, null);
        assertEntityHistory(b, ann, null);
        assertEntityHistory(c, ann, null);

        // Assert index.
        assertIndexHistory(a, 0, null);
        assertIndexHistory(b, 1, null);
        assertIndexHistory(c, 2, null);
    }

    @Test
    void addAndRemoveElement() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory x = new TestdataListValueWithShadowHistory("X");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c);

        scoreDirector.setWorkingSolution(buildSolution(ann));

        new ListAssignMove<>(variableDescriptor, x, ann, 2).doMoveOnly(scoreDirector);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann);
        assertEntityHistory(x, ann);
        assertEntityHistory(c, ann);

        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1);
        assertIndexHistory(x, 2);
        assertIndexHistory(c, 2, 3);

        new ListUnassignMove<>(variableDescriptor, ann, 1).doMoveOnly(scoreDirector);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann, null);
        assertEntityHistory(x, ann);
        assertEntityHistory(c, ann);

        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1, null);
        assertIndexHistory(x, 2, 1);
        assertIndexHistory(c, 2, 3, 2);
    }

    @Test
    @DisplayName("M1: Ann[3]→Ann[1]")
    void moveElementToLowerIndexSameEntity() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory d = new TestdataListValueWithShadowHistory("D");
        TestdataListValueWithShadowHistory e = new TestdataListValueWithShadowHistory("E");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c, d, e);

        scoreDirector.setWorkingSolution(buildSolution(ann));

        doChangeMove(ann, 3, ann, 1);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann);
        assertEntityHistory(c, ann);
        assertEntityHistory(d, ann);
        assertEntityHistory(e, ann);

        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1, 2);
        assertIndexHistory(c, 2, 3);
        assertIndexHistory(d, 3, 1);
        assertIndexHistory(e, 4);
    }

    @Test
    @DisplayName("M2: Ann[0]→Ann[2]")
    void moveElementToHigherIndexSameEntity() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory d = new TestdataListValueWithShadowHistory("D");
        TestdataListValueWithShadowHistory e = new TestdataListValueWithShadowHistory("E");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c, d, e);

        scoreDirector.setWorkingSolution(buildSolution(ann));

        doChangeMove(ann, 0, ann, 2);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann);
        assertEntityHistory(c, ann);
        assertEntityHistory(d, ann);
        assertEntityHistory(e, ann);

        assertIndexHistory(a, 0, 2);
        assertIndexHistory(b, 1, 0);
        assertIndexHistory(c, 2, 1);
        assertIndexHistory(d, 3);
        assertIndexHistory(e, 4);
    }

    @Test
    @DisplayName("M3: Ann[0]→Bob[1]")
    void moveElementToAnotherEntityChangeIndex() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory x = new TestdataListValueWithShadowHistory("X");
        TestdataListValueWithShadowHistory y = new TestdataListValueWithShadowHistory("Y");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c);
        TestdataListEntityWithShadowHistory bob = TestdataListEntityWithShadowHistory.createWithValues("Bob", x, y);

        scoreDirector.setWorkingSolution(buildSolution(ann, bob));

        new ListChangeMove<>(variableDescriptor, ann, 0, bob, 1).doMoveOnly(scoreDirector);

        assertEntityHistory(a, ann, bob);
        assertEntityHistory(b, ann);
        assertEntityHistory(c, ann);
        assertEntityHistory(x, bob);
        assertEntityHistory(y, bob);

        assertIndexHistory(a, 0, 1);
        assertIndexHistory(b, 1, 0);
        assertIndexHistory(c, 2, 1);
        assertIndexHistory(x, 0);
        assertIndexHistory(y, 1, 2);
    }

    @Test
    @DisplayName("M4: Ann[1]→Bob[1]")
    void moveElementToAnotherEntitySameIndex() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory x = new TestdataListValueWithShadowHistory("X");
        TestdataListValueWithShadowHistory y = new TestdataListValueWithShadowHistory("Y");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c);
        TestdataListEntityWithShadowHistory bob = TestdataListEntityWithShadowHistory.createWithValues("Bob", x, y);

        scoreDirector.setWorkingSolution(buildSolution(ann, bob));

        doChangeMove(ann, 1, bob, 1);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann, bob);
        assertEntityHistory(c, ann);
        assertEntityHistory(x, bob);
        assertEntityHistory(y, bob);

        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1);
        assertIndexHistory(c, 2, 1);
        assertIndexHistory(x, 0);
        assertIndexHistory(y, 1, 2);
    }

    @Test
    @DisplayName("S1: Ann[1]↔Ann[3]")
    void swapElementsSameEntity() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory d = new TestdataListValueWithShadowHistory("D");
        TestdataListValueWithShadowHistory e = new TestdataListValueWithShadowHistory("E");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c, d, e);

        scoreDirector.setWorkingSolution(buildSolution(ann));

        doSwapMove(ann, 1, ann, 3);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann);
        assertEntityHistory(c, ann);
        assertEntityHistory(d, ann);
        assertEntityHistory(e, ann);

        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1, 3);
        assertIndexHistory(c, 2);
        assertIndexHistory(d, 3, 1);
        assertIndexHistory(e, 4);
    }

    @Test
    @DisplayName("S2: Ann[0]↔Bob[1]")
    void swapElementsAnotherEntityChangeIndex() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory x = new TestdataListValueWithShadowHistory("X");
        TestdataListValueWithShadowHistory y = new TestdataListValueWithShadowHistory("Y");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c);
        TestdataListEntityWithShadowHistory bob = TestdataListEntityWithShadowHistory.createWithValues("Bob", x, y);

        scoreDirector.setWorkingSolution(buildSolution(ann, bob));

        doSwapMove(ann, 0, bob, 1);

        assertEntityHistory(a, ann, bob);
        assertEntityHistory(b, ann);
        assertEntityHistory(c, ann);
        assertEntityHistory(x, bob);
        assertEntityHistory(y, bob, ann);

        assertIndexHistory(a, 0, 1);
        assertIndexHistory(b, 1);
        assertIndexHistory(c, 2);
        assertIndexHistory(x, 0);
        assertIndexHistory(y, 1, 0);
    }

    @Test
    @DisplayName("S3: Ann[1]↔Bob[1]")
    void swapElementsAnotherEntitySameIndex() {
        TestdataListValueWithShadowHistory a = new TestdataListValueWithShadowHistory("A");
        TestdataListValueWithShadowHistory b = new TestdataListValueWithShadowHistory("B");
        TestdataListValueWithShadowHistory c = new TestdataListValueWithShadowHistory("C");
        TestdataListValueWithShadowHistory x = new TestdataListValueWithShadowHistory("X");
        TestdataListValueWithShadowHistory y = new TestdataListValueWithShadowHistory("Y");
        TestdataListEntityWithShadowHistory ann = TestdataListEntityWithShadowHistory.createWithValues("Ann", a, b, c);
        TestdataListEntityWithShadowHistory bob = TestdataListEntityWithShadowHistory.createWithValues("Bob", x, y);

        scoreDirector.setWorkingSolution(buildSolution(ann, bob));

        doSwapMove(ann, 1, bob, 1);

        assertEntityHistory(a, ann);
        assertEntityHistory(b, ann, bob);
        assertEntityHistory(c, ann);
        assertEntityHistory(x, bob);
        assertEntityHistory(y, bob, ann);

        assertIndexHistory(a, 0);
        assertIndexHistory(b, 1);
        assertIndexHistory(c, 2);
        assertIndexHistory(x, 0);
        assertIndexHistory(y, 1);
    }
}
