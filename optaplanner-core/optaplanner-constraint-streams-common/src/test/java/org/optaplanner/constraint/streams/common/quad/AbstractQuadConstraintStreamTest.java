package org.optaplanner.constraint.streams.common.quad;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countDistinct;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countQuad;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.max;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.min;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toSet;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamTest;
import org.optaplanner.constraint.streams.common.ConstraintStreamFunctionalTest;
import org.optaplanner.constraint.streams.common.ConstraintStreamImplSupport;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishExtra;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValueGroup;

public abstract class AbstractQuadConstraintStreamTest
        extends AbstractConstraintStreamTest
        implements ConstraintStreamFunctionalTest {

    protected AbstractQuadConstraintStreamTest(ConstraintStreamImplSupport implSupport) {
        super(implSupport);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    @TestTemplate
    public void filter_entity() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 0, 1, 0);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue 1", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        solution.getValueList().add(value2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(), value1);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(), value2);
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(), value1);
        solution.getEntityList().add(entity3);
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        solution.getExtraList().add(extra2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal((e1, e2) -> e1.getValue(), identity()))
                    .join(TestdataLavishExtra.class)
                    .filter((e1, e2, value, extra) -> value.getCode().equals("MyValue 1")
                            && extra.getCode().equals("MyExtra 1"))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, value1, extra1),
                assertMatch(entity1, entity3, value1, extra1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setValue(value2);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, value1, extra1),
                assertMatch(entity1, entity3, value1, extra1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setValue(value1);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, value1, extra1),
                assertMatch(entity1, entity3, value1, extra1),
                assertMatch(entity2, entity3, value1, extra1));
    }

    @Override
    @TestTemplate
    public void filter_consecutive() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(5, 5);
        TestdataLavishEntity entity1 = solution.getEntityList().get(0);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntity entity4 = solution.getEntityList().get(3);
        TestdataLavishEntity entity5 = solution.getEntityList().get(4);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((a, b) -> a, identity()))
                    .join(TestdataLavishEntity.class,
                            equal((a, b, c) -> a, identity()),
                            filtering((entityA, entityB, entityC, entityD) -> !Objects.equals(entityA, entity1)))
                    .filter((entityA, entityB, entityC, entityD) -> !Objects.equals(entityA, entity2))
                    .filter((entityA, entityB, entityC, entityD) -> !Objects.equals(entityA, entity3))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatch(entity4, entity5, entity4, entity4));

        // Remove entity
        scoreDirector.beforeEntityRemoved(entity4);
        solution.getEntityList().remove(entity4);
        scoreDirector.afterEntityRemoved(entity4);
        assertScore(scoreDirector);
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @Override
    @TestTemplate
    public void ifExists_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .ifExists(Integer.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Integer.class.getCanonicalName())
                .hasMessageContaining("assignable from");

    }

    @Override
    @TestTemplate
    public void ifExists_0Joiner0Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .ifExists(TestdataLavishValueGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        TestdataLavishValueGroup valueGroup = solution.getFirstValueGroup();
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue(),
                        solution.getFirstEntity()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(valueGroup);
        solution.getValueGroupList().remove(valueGroup);
        scoreDirector.afterProblemFactRemoved(valueGroup);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifExists_0Join1Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getValueList().get(1));
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal((entityA, entityB) -> entityA.getEntityGroup(), identity()))
                    .join(TestdataLavishValue.class, equal((entityA, entityB, group) -> entityA.getValue(), identity()))
                    .ifExists(TestdataLavishValueGroup.class,
                            filtering((entityA, entityB, entityAGroup, value, valueGroup) -> Objects
                                    .equals(value.getValueGroup(), valueGroup)))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, entityGroup, solution.getFirstValue()),
                assertMatch(entity2, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()),
                assertMatch(entity1, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()));

        // Incremental
        TestdataLavishValueGroup toRemove = solution.getFirstValueGroup();
        scoreDirector.beforeProblemFactRemoved(toRemove);
        solution.getValueGroupList().remove(toRemove);
        scoreDirector.afterProblemFactRemoved(toRemove);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifExists_1Join0Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal((entityA, entityB) -> entityA.getEntityGroup(), identity()))
                    .join(TestdataLavishValue.class, equal((entityA, entityB, group) -> entityA.getValue(), identity()))
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, groupA, valueA) -> entityA.getEntityGroup(), identity()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, entityGroup, solution.getFirstValue()),
                assertMatch(entity2, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()),
                assertMatch(entity1, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(entity2, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()),
                assertMatch(entity1, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()));
    }

    @Override
    @TestTemplate
    public void ifExists_1Join1Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal((entityA, entityB) -> entityA.getEntityGroup(), identity()))
                    .join(TestdataLavishValue.class, equal((entityA, entityB, group) -> entityA.getValue(), identity()))
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, groupA, valueA) -> entityA.getEntityGroup(), identity()),
                            filtering((entityA, entityB, groupA, valueA, groupB) -> entityA.getCode().contains("MyEntity")
                                    || groupA.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, entityGroup, solution.getFirstValue()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    @Disabled("Would cause too many matches to meaningfully assert; cost-benefit ratio is wrong here.")
    public void ifExistsDoesNotIncludeNullVars() {

    }

    @Override
    @TestTemplate
    @Deprecated(forRemoval = true)
    @Disabled("Would cause too many matches to meaningfully assert; cost-benefit ratio is wrong here.")
    public void ifExistsIncludesNullVarsWithFrom() {
    }

    @Override
    @TestTemplate
    public void ifNotExists_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .ifNotExists(Integer.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Integer.class.getCanonicalName())
                .hasMessageContaining("assignable from");

    }

    @Override
    @TestTemplate
    public void ifNotExists_0Joiner0Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .ifNotExists(TestdataLavishValueGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        TestdataLavishValueGroup valueGroup = solution.getFirstValueGroup();
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        scoreDirector.beforeProblemFactRemoved(valueGroup);
        solution.getValueGroupList().remove(valueGroup);
        scoreDirector.afterProblemFactRemoved(valueGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue(),
                        solution.getFirstEntity()));
    }

    @Override
    @TestTemplate
    public void ifNotExists_0Join1Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getValueList().get(1));
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal((entityA, entityB) -> entityA.getEntityGroup(), identity()))
                    .join(TestdataLavishValue.class, equal((entityA, entityB, group) -> entityA.getValue(), identity()))
                    .ifNotExists(TestdataLavishValueGroup.class,
                            filtering((entityA, entityB, entityAGroup, value, valueGroup) -> Objects
                                    .equals(value.getValueGroup(), valueGroup)))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        TestdataLavishValueGroup toRemove = solution.getFirstValueGroup();
        scoreDirector.beforeProblemFactRemoved(toRemove);
        solution.getValueGroupList().remove(toRemove);
        scoreDirector.afterProblemFactRemoved(toRemove);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, entityGroup, solution.getFirstValue()),
                assertMatch(entity2, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()),
                assertMatch(entity1, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()));
    }

    @Override
    @TestTemplate
    public void ifNotExists_1Join0Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal((entityA, entityB) -> entityA.getEntityGroup(), identity()))
                    .join(TestdataLavishValue.class, equal((entityA, entityB, group) -> entityA.getValue(), identity()))
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, groupA, valueA) -> entityB.getEntityGroup(), identity()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(entity1, solution.getFirstEntity(), solution.getFirstEntityGroup(), solution.getFirstValue()));
    }

    @Override
    @TestTemplate
    public void ifNotExists_1Join1Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal((entityA, entityB) -> entityA.getEntityGroup(), identity()))
                    .join(TestdataLavishValue.class, equal((entityA, entityB, group) -> entityA.getValue(), identity()))
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, groupA, valueA) -> entityA.getEntityGroup(), identity()),
                            filtering((entityA, entityB, groupA, valueA,
                                    groupB) -> !(entityA.getCode().contains("MyEntity")
                                            && groupB.getCode().contains("MyEntity"))))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, entityGroup, solution.getFirstValue()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    @Disabled("Would cause too many matches to meaningfully assert; cost-benefit ratio is wrong here.")
    public void ifNotExistsDoesNotIncludeNullVars() {

    }

    @Override
    @TestTemplate
    @Deprecated(forRemoval = true)
    @Disabled("Would cause too many matches to meaningfully assert; cost-benefit ratio is wrong here.")
    public void ifNotExistsIncludesNullVarsWithFrom() {

    }

    @Override
    @TestTemplate
    public void ifExistsAfterGroupBy() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 0, 1, 0);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue 1", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        solution.getValueList().add(value2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(), value1);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(), value1);
        solution.getEntityList().add(entity2);
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        solution.getExtraList().add(extra2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(countDistinct(TestdataLavishEntity::getValue),
                            countDistinct(TestdataLavishEntity::getValue),
                            countDistinct(TestdataLavishEntity::getValue),
                            countDistinct(TestdataLavishEntity::getValue))
                    .ifExists(TestdataLavishExtra.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(1, 1, 1, 1));

        // Incremental
        scoreDirector.beforeVariableChanged(entity2, "value");
        entity2.setValue(value2);
        scoreDirector.afterVariableChanged(entity2, "value");
        assertScore(scoreDirector,
                assertMatch(2, 2, 2, 2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(1, 1, 1, 1));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    @TestTemplate
    public void groupBy_0Mapping1Collector() {
        /*
         * E1 has G1 and V1
         * E2 has G2 and V2
         * E3 has G1 and V1
         */
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy(countQuad())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, count -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-5, 5)); // E1 G1 V1 E1, E1 G1 V1 E3, E2 G2 V2 E2, E3 G1 V1 E1, E3 G1 V1 E3

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, 2)); // E2 G2 V2 E2, E3 G1 V1 E3
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((e1, e2) -> e1, Function.identity()))
                    .join(TestdataLavishEntity.class, equal((e1, e2, e3) -> e2, Function.identity()))
                    .groupBy(countQuad(),
                            countDistinct((e, e2, e3, e4) -> e))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-1, 3, 2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector, assertMatchWithScore(-1, 1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping3Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((e1, e2) -> e1, Function.identity()))
                    .join(TestdataLavishEntity.class, equal((e1, e2, e3) -> e2, Function.identity()))
                    .groupBy(countQuad(),
                            min((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3,
                                    TestdataLavishEntity e4) -> e.getLongProperty()),
                            max((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3,
                                    TestdataLavishEntity e4) -> e.getLongProperty()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        entity1.setLongProperty(0L);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        entity2.setLongProperty(1L);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        entity3.setLongProperty(2L);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 3, 0L, 1L));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 1, 1L, 1L));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping4Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((e1, e2) -> e1, Function.identity()))
                    .join(TestdataLavishEntity.class, equal((e1, e2, e3) -> e2, Function.identity()))
                    .groupBy(countQuad(),
                            min((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3,
                                    TestdataLavishEntity e4) -> e.getLongProperty()),
                            max((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3,
                                    TestdataLavishEntity e4) -> e.getLongProperty()),
                            toSet((e, e2, e3, e4) -> e))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        entity1.setLongProperty(0L);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        entity2.setLongProperty(1L);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        entity3.setLongProperty(2L);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 3, 0L, 1L, asSet(entity1, entity2)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 1, 1L, 1L, asSet(entity2)));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> value)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, value2),
                assertMatchWithScore(-1, value1));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> value, countQuad())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, count) -> count);
        });

        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, value2, 1),
                assertMatchWithScore(-4, value1, 4));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, value2, 1),
                assertMatchWithScore(-1, value1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((entityA, entityB) -> entityA, Function.identity()))
                    .join(TestdataLavishEntity.class, equal((entityA, entityB, entityC) -> entityB, Function.identity()))
                    .groupBy((entityA, entityB, entityC, entityD) -> entityA.toString(),
                            countQuad(),
                            toSet((entityA, entityB, entityC, entityD) -> entityA))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, entity1.toString(), 2, singleton(entity1)),
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, entity2.toString(), 1, singleton(entity2)));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, entity2.toString(), 1, singleton(entity2)));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping3Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((entityA, entityB) -> entityA, Function.identity()))
                    .join(TestdataLavishEntity.class, equal((entityA, entityB, entityC) -> entityB, Function.identity()))
                    .groupBy((entityA, entityB, entityC, entityD) -> entityA.toString(),
                            min((TestdataLavishEntity entityA, TestdataLavishEntity entityB,
                                    TestdataLavishEntity entityC, TestdataLavishEntity entityD) -> entityA.getLongProperty()),
                            max((TestdataLavishEntity entityA, TestdataLavishEntity entityB,
                                    TestdataLavishEntity entityC, TestdataLavishEntity entityD) -> entityA.getLongProperty()),
                            toSet((entityA, entityB, entityC, entityD) -> entityA))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        entity1.setLongProperty(Long.MAX_VALUE);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        entity2.setLongProperty(Long.MIN_VALUE);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, entity1.toString(), Long.MAX_VALUE, Long.MAX_VALUE,
                        singleton(entity1)),
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, entity2.toString(), Long.MIN_VALUE, Long.MIN_VALUE,
                        singleton(entity2)));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, entity2.toString(), Long.MIN_VALUE, Long.MIN_VALUE,
                        singleton(entity2)));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> group, (entity1, group, value, entity2) -> value)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2),
                assertMatchWithScore(-1, group1, value1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2),
                assertMatchWithScore(-1, group1, value1));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> group, (entity1, group, value, entity2) -> value,
                            countQuad())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, value, count) -> count);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2, 1),
                assertMatchWithScore(-4, group1, value1, 4));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2, 1),
                assertMatchWithScore(-1, group1, value1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> group, (entity1, group, value, entity2) -> value,
                            countQuad(), countQuad())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, value, count, sameCount) -> count + sameCount);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, group2, value2, 1, 1),
                assertMatchWithScore(-8, group1, value1, 4, 4));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, group2, value2, 1, 1),
                assertMatchWithScore(-2, group1, value1, 1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> entity1.getEntityGroup(),
                            (entity1, group, value, entity2) -> entity2.getEntityGroup(),
                            (entity1, group, value, entity2) -> value)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group2, value2),
                assertMatchWithScore(-1, group1, group1, value1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2, value2),
                assertMatchWithScore(-1, group1, value1, value1));
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> entity1.getEntityGroup(),
                            (entity1, group, value, entity2) -> entity2.getEntityGroup(),
                            (entity1, group, value, entity2) -> group, ConstraintCollectors.countQuad())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group2, group2, 1),
                assertMatchWithScore(-1, group1, group1, group1, 4));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group2, group2, 1),
                assertMatchWithScore(-1, group1, group1, group1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_4Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                    .join(TestdataLavishEntity.class, equal((entity, group, value) -> group,
                            TestdataLavishEntity::getEntityGroup))
                    .groupBy((entity1, group, value, entity2) -> entity1.getEntityGroup(),
                            (entity1, group, value, entity2) -> entity2.getEntityGroup(),
                            (entity1, group, value, entity2) -> group,
                            (entity1, group, value, entity2) -> value)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group2, group2, value2),
                assertMatchWithScore(-1, group1, group1, group1, value1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group2, group2, value2),
                assertMatchWithScore(-1, group1, group1, group1, value1));
    }

    // ************************************************************************
    // Map/flatten/distinct
    // ************************************************************************

    @Override
    @TestTemplate
    public void distinct() { // On a distinct stream, this is a no-op.
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2) -> entity1.getEntityGroup(), Function.identity()))
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2, group) -> entity2.getEntityGroup(), Function.identity()))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, group1, group2),
                assertMatch(entity1, entity3, group1, group1),
                assertMatch(entity2, entity3, group2, group1));
    }

    @Override
    @TestTemplate
    public void mapWithDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2) -> entity1.getEntityGroup(), Function.identity()))
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2, group) -> entity2.getEntityGroup(), Function.identity()))
                    .map((entity1, entity2, group1, group2) -> asSet(group1, group2))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)));
    }

    @Override
    @TestTemplate
    public void mapWithoutDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2) -> entity1.getEntityGroup(), Function.identity()))
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2, group) -> entity2.getEntityGroup(), Function.identity()))
                    .map((entity1, entity2, group1, group2) -> asSet(group1, group2))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void mapAndDistinctWithDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2) -> entity1.getEntityGroup(), Function.identity()))
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2, group) -> entity2.getEntityGroup(), Function.identity()))
                    .map((entity1, entity2, group1, group2) -> asSet(group1, group2))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)));
    }

    @Override
    @TestTemplate
    public void mapAndDistinctWithoutDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2) -> entity1.getEntityGroup(), Function.identity()))
                    .join(TestdataLavishEntityGroup.class,
                            Joiners.equal((entity1, entity2, group) -> entity2.getEntityGroup(), Function.identity()))
                    .map((entity1, entity2, group1, group2) -> asSet(group1, group2))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void flattenLastWithDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, filtering((a, b, c) -> a != c && b != c))
                    .join(TestdataLavishEntity.class, filtering((a, b, c, d) -> a != d && b != d))
                    .flattenLast((d) -> asList(group1, group1, group2))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3, group1),
                assertMatch(entity1, entity2, entity3, group1),
                assertMatch(entity1, entity2, entity3, group2),
                assertMatch(entity1, entity3, entity2, group1),
                assertMatch(entity1, entity3, entity2, group1),
                assertMatch(entity1, entity3, entity2, group2),
                assertMatch(entity2, entity3, entity1, group1),
                assertMatch(entity2, entity3, entity1, group1),
                assertMatch(entity2, entity3, entity1, group2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void flattenLastWithoutDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, filtering((a, b, c) -> a != c && b != c))
                    .join(TestdataLavishEntity.class, filtering((a, b, c, d) -> a != d && b != d))
                    .flattenLast((d) -> asList(group1, group2))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3, group1),
                assertMatch(entity1, entity2, entity3, group2),
                assertMatch(entity1, entity3, entity2, group1),
                assertMatch(entity1, entity3, entity2, group2),
                assertMatch(entity2, entity3, entity1, group1),
                assertMatch(entity2, entity3, entity1, group2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void flattenLastAndDistinctWithDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, filtering((a, b, c) -> a != c && b != c))
                    .join(TestdataLavishEntity.class, filtering((a, b, c, d) -> a != d && b != d))
                    .flattenLast((d) -> asList(group1, group1, group2))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3, group1),
                assertMatch(entity1, entity2, entity3, group2),
                assertMatch(entity1, entity3, entity2, group1),
                assertMatch(entity1, entity3, entity2, group2),
                assertMatch(entity2, entity3, entity1, group1),
                assertMatch(entity2, entity3, entity1, group2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void flattenLastAndDistinctWithoutDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, filtering((a, b, c) -> a != c && b != c))
                    .join(TestdataLavishEntity.class, filtering((a, b, c, d) -> a != d && b != d))
                    .flattenLast((d) -> asList(group1, group2))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3, group1),
                assertMatch(entity1, entity2, entity3, group2),
                assertMatch(entity1, entity3, entity2, group1),
                assertMatch(entity1, entity3, entity2, group2),
                assertMatch(entity2, entity3, entity1, group1),
                assertMatch(entity2, entity3, entity1, group2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    @TestTemplate
    public void penalize_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(singletonList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataSolution.buildSolutionDescriptor(),
                factory -> {
                    QuadConstraintStream<TestdataEntity, TestdataEntity, TestdataValue, TestdataValue> base = factory
                            .forEachUniquePair(TestdataEntity.class)
                            .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                            .join(TestdataValue.class);
                    return new Constraint[] {
                            base.penalize("myConstraint1", SimpleScore.ONE),
                            base.penalize("myConstraint2", SimpleScore.ONE, (entity1, entity2, value, extra) -> 20)
                    };
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-21));
    }

    @Override
    @TestTemplate
    public void penalize_Long() {
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> {
                    QuadConstraintStream<TestdataEntity, TestdataEntity, TestdataValue, TestdataValue> base = factory
                            .forEachUniquePair(TestdataEntity.class)
                            .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                            .join(TestdataValue.class);
                    return new Constraint[] {
                            base.penalize("myConstraint1", SimpleLongScore.ONE),
                            base.penalizeLong("myConstraint2", SimpleLongScore.ONE,
                                    (entity1, entity2, value, extra) -> 20L)
                    };
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-21L));
    }

    @Override
    @TestTemplate
    public void penalize_BigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(
                        TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> {
                            QuadConstraintStream<TestdataEntity, TestdataEntity, TestdataValue, TestdataValue> base = factory
                                    .forEachUniquePair(TestdataEntity.class)
                                    .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                    .join(TestdataValue.class);
                            return new Constraint[] {
                                    base.penalize("myConstraint1", SimpleBigDecimalScore.ONE),
                                    base.penalizeBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                            (entity1, entity2, value, extra) -> new BigDecimal("0.2"))
                            };
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-1.2")));
    }

    @Override
    @TestTemplate
    public void penalize_negative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 2);

        String constraintName = "myConstraint";
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class)
                        .join(TestdataLavishValue.class)
                        .penalize(constraintName, SimpleScore.ONE, (entityA, entityB, group, value) -> -1));

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

    @Override
    @TestTemplate
    public void reward_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class)
                                .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                .join(TestdataValue.class)
                                .reward("myConstraint1", SimpleScore.ONE),
                        factory.forEachUniquePair(TestdataEntity.class)
                                .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                .join(TestdataValue.class)
                                .reward("myConstraint2", SimpleScore.ONE, (entity1, entity2, value, extra) -> 20)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(21));
    }

    @Override
    @TestTemplate
    public void reward_Long() {
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class)
                                .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                .join(TestdataValue.class)
                                .reward("myConstraint1", SimpleLongScore.ONE),
                        factory.forEachUniquePair(TestdataEntity.class)
                                .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                .join(TestdataValue.class)
                                .rewardLong("myConstraint2", SimpleLongScore.ONE,
                                        (entity1, entity2, value, extra) -> 20L)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(21L));
    }

    @Override
    @TestTemplate
    public void reward_BigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(
                        TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .reward("myConstraint1", SimpleBigDecimalScore.ONE),
                                factory.forEachUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .rewardBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                                (entity1, entity2, value, extra) -> new BigDecimal("0.2"))
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("1.2")));
    }

    @Override
    @TestTemplate
    public void reward_negative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 2);

        String constraintName = "myConstraint";
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class)
                        .join(TestdataLavishValue.class)
                        .reward(constraintName, SimpleScore.ONE, (entityA, entityB, group, value) -> -1));

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

}
