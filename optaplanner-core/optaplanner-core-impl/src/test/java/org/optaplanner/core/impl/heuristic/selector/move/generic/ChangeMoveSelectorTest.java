package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.doInsideStep;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockEntitySelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockValueSelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class ChangeMoveSelectorTest {

    @Test
    void original() {
        TestdataEntity a = new TestdataEntity("a");
        TestdataEntity b = new TestdataEntity("b");
        TestdataEntity c = new TestdataEntity("c");
        TestdataEntity d = new TestdataEntity("d");
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        EntitySelector<TestdataSolution> entitySelector = mockEntitySelector(TestdataEntity.class, a, b, c, d);
        ValueSelector<TestdataSolution> valueSelector = mockValueSelector(TestdataEntity.class, "value", v1, v2, v3);

        ChangeMoveSelector<TestdataSolution> moveSelector = new ChangeMoveSelector<>(entitySelector, valueSelector, false);

        SolverScope<TestdataSolution> solverScope = solvingStarted(moveSelector);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeA, selector -> assertAllCodesOfMoveSelector(selector,
                "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3"));

        // Step A(1): initialize two entities: a->1, b->3.
        when(valueSelector.getVariableDescriptor().getValue(a)).thenReturn(v1);
        when(valueSelector.getVariableDescriptor().getValue(b)).thenReturn(v3);

        doInsideStep(moveSelector, phaseScopeA, selector -> assertAllCodesOfMoveSelector(selector,
                "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3"));

        // Step A(2): initialize two entities: c->2, d->1.
        when(valueSelector.getVariableDescriptor().getValue(c)).thenReturn(v2);
        when(valueSelector.getVariableDescriptor().getValue(d)).thenReturn(v1);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope<TestdataSolution> phaseScopeB = phaseStarted(moveSelector, solverScope);

        // All entities were initialized in the previous phase. As a result, some moves are now not doable.
        // Nevertheless, the move selector must produce all possible moves, including those that are not doable at this point
        // because if the selector is cached, it might only be called once per phase (for example). Moves that are not doable
        // at the time they are created might become doable later, so they must be included in the cache.

        doInsideStep(moveSelector, phaseScopeB, selector -> assertAllCodesOfMoveSelector(selector,
                "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3"));
        doInsideStep(moveSelector, phaseScopeB, selector -> assertAllCodesOfMoveSelector(selector,
                "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3"));
        doInsideStep(moveSelector, phaseScopeB, selector -> assertAllCodesOfMoveSelector(selector,
                "a->1", "a->2", "a->3", "b->1", "b->2", "b->3", "c->1", "c->2", "c->3", "d->1", "d->2", "d->3"));

        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

    @Test
    void emptyEntitySelectorOriginal() {
        EntitySelector<TestdataSolution> entitySelector = mockEntitySelector(TestdataEntity.class);
        ValueSelector<TestdataSolution> valueSelector = mockValueSelector(TestdataEntity.class, "value",
                new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));

        ChangeMoveSelector<TestdataSolution> moveSelector = new ChangeMoveSelector<>(entitySelector, valueSelector, false);

        SolverScope<TestdataSolution> solverScope = solvingStarted(moveSelector, null, null);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeA, PlannerAssert::assertAllCodesOfMoveSelector);
        doInsideStep(moveSelector, phaseScopeA, PlannerAssert::assertAllCodesOfMoveSelector);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope<TestdataSolution> phaseScopeB = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeB, PlannerAssert::assertAllCodesOfMoveSelector);
        doInsideStep(moveSelector, phaseScopeB, PlannerAssert::assertAllCodesOfMoveSelector);
        doInsideStep(moveSelector, phaseScopeB, PlannerAssert::assertAllCodesOfMoveSelector);

        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

    @Test
    void emptyValueSelectorOriginal() {
        EntitySelector<TestdataSolution> entitySelector = mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));
        ValueSelector<TestdataSolution> valueSelector = mockValueSelector(TestdataEntity.class, "value");

        ChangeMoveSelector<TestdataSolution> moveSelector = new ChangeMoveSelector<>(entitySelector, valueSelector, false);

        SolverScope<TestdataSolution> solverScope = solvingStarted(moveSelector);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeA, PlannerAssert::assertAllCodesOfMoveSelector);
        doInsideStep(moveSelector, phaseScopeA, PlannerAssert::assertAllCodesOfMoveSelector);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope<TestdataSolution> phaseScopeB = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeB, PlannerAssert::assertAllCodesOfMoveSelector);
        doInsideStep(moveSelector, phaseScopeB, PlannerAssert::assertAllCodesOfMoveSelector);
        doInsideStep(moveSelector, phaseScopeB, PlannerAssert::assertAllCodesOfMoveSelector);

        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

    @Test
    void randomSelection() {
        TestdataEntity a = new TestdataEntity("a");
        TestdataEntity b = new TestdataEntity("b");
        TestdataEntity c = new TestdataEntity("c");
        TestdataEntity d = new TestdataEntity("d");
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        EntitySelector<TestdataSolution> entitySelector = mockEntitySelector(TestdataEntity.class, a, b, c, d);
        ValueSelector<TestdataSolution> valueSelector = mockValueSelector(TestdataEntity.class, "value", v1, v2, v3);

        ChangeMoveSelector<TestdataSolution> moveSelector = new ChangeMoveSelector<>(entitySelector, valueSelector, true);

        SolverScope<TestdataSolution> solverScope = solvingStarted(moveSelector);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeA, selector -> assertCodesOfNeverEndingMoveSelector(selector,
                "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1"));

        // Step A(1): initialize two entities: a->1, b->3.
        when(valueSelector.getVariableDescriptor().getValue(a)).thenReturn(v1);
        when(valueSelector.getVariableDescriptor().getValue(b)).thenReturn(v3);

        doInsideStep(moveSelector, phaseScopeA, selector -> assertCodesOfNeverEndingMoveSelector(selector,
                "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1"));

        // Step A(2): initialize two entities: c->2, d->1.
        when(valueSelector.getVariableDescriptor().getValue(c)).thenReturn(v2);
        when(valueSelector.getVariableDescriptor().getValue(d)).thenReturn(v1);

        moveSelector.phaseEnded(phaseScopeA);

        // All entities were initialized in the previous phase. As a result, some moves are now not doable.
        // Nevertheless, the move selector must produce all possible moves, including those that are not doable at this point
        // because if the selector is cached, it might only be called once per phase (for example). Moves that are not doable
        // at the time they are created might become doable later, so they must be included in the cache.

        AbstractPhaseScope<TestdataSolution> phaseScopeB = phaseStarted(moveSelector, solverScope);

        doInsideStep(moveSelector, phaseScopeB, selector -> assertCodesOfNeverEndingMoveSelector(selector,
                "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1"));
        doInsideStep(moveSelector, phaseScopeB, selector -> assertCodesOfNeverEndingMoveSelector(selector,
                "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1"));
        doInsideStep(moveSelector, phaseScopeB, selector -> assertCodesOfNeverEndingMoveSelector(selector,
                "a->1", "b->1", "c->1", "d->1", "a->1", "b->1", "c->1", "d->1"));

        moveSelector.phaseEnded(phaseScopeB);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 5);
        verifyPhaseLifecycle(valueSelector, 1, 2, 5);
    }

}
