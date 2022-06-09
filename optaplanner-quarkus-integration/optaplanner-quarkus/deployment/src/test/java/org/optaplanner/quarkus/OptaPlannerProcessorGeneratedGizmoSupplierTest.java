package org.optaplanner.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedEntity;
import org.optaplanner.quarkus.gizmo.OptaPlannerGizmoBeanFactory;
import org.optaplanner.quarkus.testdata.gizmo.DummyVariableListener;

import io.quarkus.test.QuarkusUnitTest;

class OptaPlannerProcessorGeneratedGizmoSupplierTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.test.flat-class-path", "true")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("org/optaplanner/quarkus/gizmoSupplierTestSolverConfig.xml",
                            "solverConfig.xml")
                    .addClasses(
                            TestdataSolution.class,
                            TestdataEntity.class,
                            TestdataAnnotatedExtendedEntity.class,
                            DummyInterfaceEntity.class,
                            DummyAbstractEntity.class,
                            DummyVariableListener.class,
                            DummyChangeMoveFilter.class,
                            DummyConstraintProvider.class,
                            DummyEasyScoreCalculator.class,
                            DummyEntityFilter.class,
                            DummyIncrementalScoreCalculator.class,
                            DummyMoveIteratorFactory.class,
                            DummyMoveListFactory.class,
                            DummySolutionPartitioner.class,
                            DummyValueFilter.class));

    @Inject
    OptaPlannerGizmoBeanFactory gizmoBeanFactory;

    private void assertFactoryContains(Class<?> clazz) {
        assertThat(gizmoBeanFactory.newInstance(clazz)).isNotNull();
    }

    private void assertFactoryNotContains(Class<?> clazz) {
        assertThat(gizmoBeanFactory.newInstance(clazz)).isNull();
    }

    @Test
    void gizmoFactoryContainClassesReferencedInSolverConfig() {
        assertFactoryContains(DummyChangeMoveFilter.class);
        assertFactoryContains(DummyConstraintProvider.class);
        assertFactoryContains(DummyEasyScoreCalculator.class);
        assertFactoryContains(DummyEntityFilter.class);
        assertFactoryContains(DummyIncrementalScoreCalculator.class);
        assertFactoryContains(DummyMoveIteratorFactory.class);
        assertFactoryContains(DummyMoveListFactory.class);
        assertFactoryContains(DummySolutionPartitioner.class);
        assertFactoryContains(DummyValueFilter.class);
        assertFactoryContains(DummyVariableListener.class);

        assertFactoryNotContains(DummyInterfaceEntity.class);
        assertFactoryNotContains(DummyAbstractEntity.class);
    }

    /* Dummy classes below are referenced from the testSolverConfig.xml used in this test case. */

    @PlanningEntity
    public interface DummyInterfaceEntity {
        @CustomShadowVariable(
                sources = {
                        @PlanningVariableReference(entityClass = TestdataEntity.class,
                                variableName = "value")
                },
                variableListenerClass = DummyVariableListener.class)
        Integer getLength();
    }

    @PlanningEntity
    public static abstract class DummyAbstractEntity {
        @CustomShadowVariable(
                sources = {
                        @PlanningVariableReference(entityClass = TestdataEntity.class,
                                variableName = "value")
                },
                variableListenerClass = DummyVariableListener.class)
        abstract Integer getLength();
    }

    public static class DummySolutionPartitioner implements SolutionPartitioner<TestdataSolution> {
        @Override
        public List<TestdataSolution> splitWorkingSolution(ScoreDirector<TestdataSolution> scoreDirector,
                Integer runnablePartThreadLimit) {
            return null;
        }
    }

    public static class DummyEasyScoreCalculator
            implements EasyScoreCalculator<TestdataSolution, SimpleScore> {
        @Override
        public SimpleScore calculateScore(TestdataSolution testdataSolution) {
            return null;
        }
    }

    public static class DummyIncrementalScoreCalculator
            implements IncrementalScoreCalculator<TestdataSolution, SimpleScore> {
        @Override
        public void resetWorkingSolution(TestdataSolution workingSolution) {

        }

        @Override
        public void beforeEntityAdded(Object entity) {

        }

        @Override
        public void afterEntityAdded(Object entity) {

        }

        @Override
        public void beforeVariableChanged(Object entity, String variableName) {

        }

        @Override
        public void afterVariableChanged(Object entity, String variableName) {

        }

        @Override
        public void beforeEntityRemoved(Object entity) {

        }

        @Override
        public void afterEntityRemoved(Object entity) {

        }

        @Override
        public SimpleScore calculateScore() {
            return null;
        }
    }

    public static class DummyConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[0];
        }
    }

    public static class DummyValueFilter implements SelectionFilter<TestdataSolution, TestdataValue> {
        @Override
        public boolean accept(ScoreDirector<TestdataSolution> scoreDirector, TestdataValue selection) {
            return false;
        }
    }

    public static class DummyEntityFilter implements SelectionFilter<TestdataSolution, TestdataEntity> {
        @Override
        public boolean accept(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity selection) {
            return false;
        }
    }

    public static class DummyChangeMoveFilter
            implements SelectionFilter<TestdataSolution, ChangeMove<TestdataSolution>> {
        @Override
        public boolean accept(ScoreDirector<TestdataSolution> scoreDirector, ChangeMove<TestdataSolution> selection) {
            return false;
        }
    }

    public static class DummyMoveIteratorFactory implements MoveIteratorFactory<TestdataSolution, DummyMove> {
        @Override
        public long getSize(ScoreDirector<TestdataSolution> scoreDirector) {
            return 0;
        }

        @Override
        public Iterator<DummyMove> createOriginalMoveIterator(ScoreDirector<TestdataSolution> scoreDirector) {
            return null;
        }

        @Override
        public Iterator<DummyMove> createRandomMoveIterator(ScoreDirector<TestdataSolution> scoreDirector,
                Random workingRandom) {
            return null;
        }
    }

    public static class DummyMoveListFactory implements MoveListFactory<TestdataSolution> {
        @Override
        public List<? extends Move<TestdataSolution>> createMoveList(TestdataSolution testdataSolution) {
            return null;
        }
    }

}
