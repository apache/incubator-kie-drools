package org.optaplanner.constraint.streams.common.tri;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countDistinct;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countTri;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.max;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.min;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toSet;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamTest;
import org.optaplanner.constraint.streams.common.ConstraintStreamFunctionalTest;
import org.optaplanner.constraint.streams.common.ConstraintStreamImplSupport;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintJustification;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishExtra;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValueGroup;

public abstract class AbstractTriConstraintStreamTest
        extends AbstractConstraintStreamTest
        implements ConstraintStreamFunctionalTest {

    protected AbstractTriConstraintStreamTest(ConstraintStreamImplSupport implSupport) {
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
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal(TestdataLavishEntity::getValue, identity()))
                    .join(TestdataLavishExtra.class)
                    .filter((entity, value, extra) -> value.getCode().equals("MyValue 1")
                            && extra.getCode().equals("MyExtra 1"))
                    .penalize(SimpleScore.ONE)
                    .asConstraint(TEST_CONSTRAINT_NAME);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity3, value1, extra1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setValue(value2);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setValue(value1);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity2, value1, extra1));
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                equal((a, b) -> a, identity()),
                                filtering((entityA, entityB, entityC) -> !Objects.equals(entityA, entity1)))
                        .filter((entityA, entityB, entityC) -> !Objects.equals(entityA, entity1))
                        .filter((entityA, entityB, entityC) -> !Objects.equals(entityA, entity2))
                        .filter((entityA, entityB, entityC) -> !Objects.equals(entityA, entity3))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatch(entity4, entity5, entity4));

        // Remove entity
        scoreDirector.beforeEntityRemoved(entity4);
        solution.getEntityList().remove(entity4);
        scoreDirector.afterEntityRemoved(entity4);
        assertScore(scoreDirector);
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    @TestTemplate
    public void join_0() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 0, 1, 0);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue 1", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        solution.getValueList().add(value2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(), value1);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(), value2);
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(), value2);
        solution.getEntityList().add(entity3);
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        solution.getExtraList().add(extra2);
        TestdataLavishExtra extra3 = new TestdataLavishExtra("MyExtra 3");
        solution.getExtraList().add(extra3);

        // pick three distinct entities and join them with all extra values
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((a, b) -> Stream.of(entity1, entity2, entity3)
                                .filter(entity -> !Objects.equals(entity, a))
                                .filter(entity -> !Objects.equals(entity, b))
                                .map(TestdataObject::getCode)
                                .findFirst()
                                .orElseThrow(IllegalStateException::new), TestdataObject::getCode))
                        .join(TestdataLavishExtra.class)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3, extra1),
                assertMatch(entity1, entity2, entity3, extra2),
                assertMatch(entity1, entity2, entity3, extra3),
                assertMatch(entity1, entity3, entity2, extra1),
                assertMatch(entity1, entity3, entity2, extra2),
                assertMatch(entity1, entity3, entity2, extra3),
                assertMatch(entity2, entity3, entity1, extra1),
                assertMatch(entity2, entity3, entity1, extra2),
                assertMatch(entity2, entity3, entity1, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void join_1Equal() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 0, 1, 0);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue 1", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        solution.getValueList().add(value2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(), value1);
        entity1.setStringProperty("MyString");
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(), value2);
        entity2.setStringProperty(null);
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(), value2);
        entity3.setStringProperty(null);
        solution.getEntityList().add(entity3);
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        extra1.setStringProperty("MyString");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        extra2.setStringProperty(null);
        solution.getExtraList().add(extra2);
        TestdataLavishExtra extra3 = new TestdataLavishExtra("MyExtra 3");
        extra3.setStringProperty("MyString");
        solution.getExtraList().add(extra3);

        // pick three distinct entities and join them with an extra value that matches that of the first entity
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((a, b) -> Stream.of(entity1, entity2, entity3)
                                .filter(entity -> !Objects.equals(entity, a))
                                .filter(entity -> !Objects.equals(entity, b))
                                .map(TestdataObject::getCode)
                                .findFirst()
                                .orElseThrow(IllegalStateException::new), TestdataObject::getCode))
                        .join(TestdataLavishExtra.class,
                                equal((e1, e2, e3) -> e1.getStringProperty(), TestdataLavishExtra::getStringProperty))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3, extra1),
                assertMatch(entity1, entity2, entity3, extra3),
                assertMatch(entity1, entity3, entity2, extra1),
                assertMatch(entity1, entity3, entity2, extra3),
                assertMatch(entity2, entity3, entity1, extra2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void join_2Equal() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 0, 1, 0);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue 1", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        solution.getValueList().add(value2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(), value1);
        entity1.setStringProperty("MyString");
        entity1.setIntegerProperty(7);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(), value2);
        entity2.setStringProperty(null);
        entity2.setIntegerProperty(8);
        solution.getEntityList().add(entity2);
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        extra1.setStringProperty("MyString");
        extra1.setIntegerProperty(8);
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        extra2.setStringProperty(null);
        extra2.setIntegerProperty(7);
        solution.getExtraList().add(extra2);
        TestdataLavishExtra extra3 = new TestdataLavishExtra("MyExtra 3");
        extra3.setStringProperty("MyString");
        extra3.setIntegerProperty(7);
        solution.getExtraList().add(extra3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishValue.class, equal((e1, e2) -> e1.getValue(), identity()))
                        .join(TestdataLavishExtra.class,
                                equal((e1, e2, value) -> e1.getStringProperty(), TestdataLavishExtra::getStringProperty),
                                equal((e1, e2, value) -> e1.getIntegerProperty(), TestdataLavishExtra::getIntegerProperty))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, value1, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void joinAfterGroupBy() {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .groupBy(countDistinct(TestdataLavishEntity::getValue),
                                countDistinct(TestdataLavishEntity::getValue),
                                countDistinct(TestdataLavishEntity::getValue))
                        .join(TestdataLavishExtra.class)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(1, 1, 1, extra1),
                assertMatch(1, 1, 1, extra2));

        // Incremental
        scoreDirector.beforeVariableChanged(entity2, "value");
        entity2.setValue(value2);
        scoreDirector.afterVariableChanged(entity2, "value");
        assertScore(scoreDirector,
                assertMatch(2, 2, 2, extra1),
                assertMatch(2, 2, 2, extra2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(1, 1, 1, extra1),
                assertMatch(1, 1, 1, extra2));
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @Override
    @TestTemplate
    public void ifExists_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishValueGroup.class)
                .join(TestdataLavishEntityGroup.class)
                .ifExists(Integer.class)
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME)))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(Integer.class.getCanonicalName())
                        .hasMessageContaining("assignable from");
    }

    @Override
    @TestTemplate
    public void ifExists_0Joiner0Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishValueGroup.class)
                        .join(TestdataLavishEntityGroup.class)
                        .ifExists(TestdataLavishEntity.class)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(valueGroup, solution.getFirstValueGroup(), solution.getFirstEntityGroup()));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityGroupList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifExists_0Join1Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> factory
                .forEachUniquePair(TestdataLavishEntity.class)
                .join(TestdataLavishEntity.class,
                        filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                !Objects.equals(entityC, entityB)))
                .ifExists(TestdataLavishEntityGroup.class,
                        filtering((entityA, entityB, entityC, group) -> Objects.equals(group, entityA.getEntityGroup()) &&
                                Objects.equals(group, entityB.getEntityGroup())))
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, solution.getFirstEntity()));

        // Incremental
        TestdataLavishEntityGroup toRemove = solution.getFirstEntityGroup();
        scoreDirector.beforeProblemFactRemoved(toRemove);
        solution.getEntityGroupList().remove(toRemove);
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                        !Objects.equals(entityC, entityB)))
                        .ifExists(TestdataLavishEntityGroup.class,
                                equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), identity()))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()),
                assertMatch(entity1, entity2, solution.getFirstEntity()),
                assertMatch(solution.getFirstEntity(), entity1, entity2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()),
                assertMatch(entity1, entity2, solution.getFirstEntity()));
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                        !Objects.equals(entityC, entityB)))
                        .ifExists(TestdataLavishEntityGroup.class,
                                equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), identity()),
                                filtering((entityA, entityB, entityC, group) -> entityA.getCode().contains("MyEntity") ||
                                        group.getCode().contains("MyEntity")))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity2, entity1));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifExistsDoesNotIncludeNullVars() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(),
                null);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> entityA != entityC && entityB != entityC))
                        .ifExists(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC, entityD) -> entityA != entityD && entityB != entityD
                                        && entityC != entityD))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    @Deprecated(forRemoval = true)
    public void ifExistsIncludesNullVarsWithFrom() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(),
                null);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.fromUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> entityA != entityC && entityB != entityC))
                        .ifExists(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC, entityD) -> entityA != entityD && entityB != entityD
                                        && entityC != entityD))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1, entity2),
                assertMatch(solution.getFirstEntity(), entity2, entity1),
                assertMatch(entity1, entity2, solution.getFirstEntity()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterProblemFactRemoved(entity3);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifNotExists_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishValueGroup.class)
                .join(TestdataLavishEntityGroup.class)
                .ifNotExists(Integer.class)
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME)))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(Integer.class.getCanonicalName())
                        .hasMessageContaining("assignable from");
    }

    @Override
    @TestTemplate
    public void ifNotExists_0Joiner0Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishValueGroup.class)
                        .join(TestdataLavishEntityGroup.class)
                        .ifNotExists(TestdataLavishEntity.class)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityGroupList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(valueGroup, solution.getFirstValueGroup(), solution.getFirstEntityGroup()));
    }

    @Override
    @TestTemplate
    public void ifNotExists_0Join1Filter() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> factory
                .forEachUniquePair(TestdataLavishEntity.class)
                .join(TestdataLavishEntity.class,
                        filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                !Objects.equals(entityC, entityB)))
                .ifNotExists(TestdataLavishEntityGroup.class,
                        filtering((entityA, entityB, entityC, group) -> Objects.equals(group, entityA.getEntityGroup()) &&
                                Objects.equals(group, entityB.getEntityGroup())))
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()),
                assertMatch(solution.getFirstEntity(), entity1, entity2));

        // Incremental
        TestdataLavishEntityGroup toRemove = solution.getFirstEntityGroup();
        scoreDirector.beforeProblemFactRemoved(toRemove);
        solution.getEntityGroupList().remove(toRemove);
        scoreDirector.afterProblemFactRemoved(toRemove);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()),
                assertMatch(solution.getFirstEntity(), entity2, entity1),
                assertMatch(entity1, entity2, solution.getFirstEntity()));
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                        !Objects.equals(entityC, entityB)))
                        .ifNotExists(TestdataLavishEntityGroup.class,
                                equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), identity()))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()));
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                        !Objects.equals(entityC, entityB)))
                        .ifNotExists(TestdataLavishEntityGroup.class,
                                equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), identity()),
                                filtering((entityA, entityB, entityC, group) -> entityA.getCode().contains("MyEntity") ||
                                        group.getCode().contains("MyEntity")))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()),
                assertMatch(entity1, entity2, solution.getFirstEntity()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(entity2, entity1, solution.getFirstEntity()),
                assertMatch(solution.getFirstEntity(), entity2, entity1),
                assertMatch(entity1, entity2, solution.getFirstEntity()));
    }

    @Override
    @TestTemplate
    public void ifNotExistsDoesNotIncludeNullVars() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(),
                null);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> entityA != entityC && entityB != entityC))
                        .ifNotExists(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC, entityD) -> entityA != entityD && entityB != entityD
                                        && entityC != entityD))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1, entity2),
                assertMatch(solution.getFirstEntity(), entity2, entity1),
                assertMatch(entity1, entity2, solution.getFirstEntity()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterProblemFactRemoved(entity3);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1, entity2),
                assertMatch(solution.getFirstEntity(), entity2, entity1),
                assertMatch(entity1, entity2, solution.getFirstEntity()));
    }

    @Override
    @TestTemplate
    @Deprecated(forRemoval = true)
    public void ifNotExistsIncludesNullVarsWithFrom() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(),
                null);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.fromUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC) -> entityA != entityC && entityB != entityC))
                        .ifNotExists(TestdataLavishEntity.class,
                                filtering((entityA, entityB, entityC, entityD) -> entityA != entityD && entityB != entityD
                                        && entityC != entityD))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterProblemFactRemoved(entity3);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1, entity2),
                assertMatch(solution.getFirstEntity(), entity2, entity1),
                assertMatch(entity1, entity2, solution.getFirstEntity()));
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .groupBy(countDistinct(TestdataLavishEntity::getValue),
                                countDistinct(TestdataLavishEntity::getValue),
                                countDistinct(TestdataLavishEntity::getValue))
                        .ifExists(TestdataLavishExtra.class)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(1, 1, 1));

        // Incremental
        scoreDirector.beforeVariableChanged(entity2, "value");
        entity2.setValue(value2);
        scoreDirector.afterVariableChanged(entity2, "value");
        assertScore(scoreDirector,
                assertMatch(2, 2, 2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(1, 1, 1));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    @TestTemplate
    public void groupBy_0Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                        .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                        .groupBy(countTri())
                        .penalize(SimpleScore.ONE, count -> count)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-3, 3));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, 2));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((e1, e2) -> e1, Function.identity()))
                        .groupBy(countTri(),
                                countDistinct((e, e2, e3) -> e))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((e1, e2) -> e1, Function.identity()))
                        .groupBy(countTri(),
                                min((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3) -> e
                                        .getLongProperty()),
                                max((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3) -> e
                                        .getLongProperty()))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((e1, e2) -> e1, Function.identity()))
                        .groupBy(countTri(),
                                min((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3) -> e
                                        .getLongProperty()),
                                max((TestdataLavishEntity e, TestdataLavishEntity e2, TestdataLavishEntity e3) -> e
                                        .getLongProperty()),
                                toSet((e, e2, e3) -> e))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                        .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                        .groupBy((entity, group, value) -> value)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                        .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                        .groupBy((entity, group, value) -> value, countTri())
                        .penalize(SimpleScore.ONE, (group, count) -> count)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, value2, 1),
                assertMatchWithScore(-2, value1, 2));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((entityA, entityB) -> entityA, Function.identity()))
                        .groupBy((entityA, entityB, entityC) -> entityA.toString(),
                                countTri(),
                                toSet((entityA, entityB, entityC) -> entityA))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, equal((entityA, entityB) -> entityA, Function.identity()))
                        .groupBy((entityA, entityB, entityC) -> entityA.toString(),
                                min((TestdataLavishEntity entityA, TestdataLavishEntity entityB,
                                        TestdataLavishEntity entityC) -> entityA.getLongProperty()),
                                max((TestdataLavishEntity entityA, TestdataLavishEntity entityB,
                                        TestdataLavishEntity entityC) -> entityA.getLongProperty()),
                                toSet((entityA, entityB, entityC) -> entityA))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                        .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                        .groupBy((entity, group, value) -> group, (entity, group, value) -> value)
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                        .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                        .groupBy((entity, group, value) -> group, (entity, group, value) -> value, countTri())
                        .penalize(SimpleScore.ONE, (group, value, count) -> count)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2, 1),
                assertMatchWithScore(-2, group1, value1, 2));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, identity()))
                        .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), identity()))
                        .groupBy((entity, group, value) -> group, (entity, group, value) -> value, countTri(), countTri())
                        .penalize(SimpleScore.ONE,
                                (group, value, count, sameCount) -> count + sameCount)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, group2, value2, 1, 1),
                assertMatchWithScore(-4, group1, value1, 2, 2));

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
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> factory
                .forEachUniquePair(TestdataLavishEntity.class)
                .join(TestdataLavishEntity.class,
                        filtering((a, b, c) -> a != c && b != c))
                .groupBy((a, b, c) -> a.getEntityGroup(), (a, b, c) -> b.getEntityGroup(), (a, b, c) -> c.getEntityGroup())
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2, group3),
                assertMatchWithScore(-1, group1, group3, group2),
                assertMatchWithScore(-1, group2, group3, group1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> factory
                .forEachUniquePair(TestdataLavishEntity.class)
                .join(TestdataLavishEntity.class,
                        filtering((a, b, c) -> a != c && b != c))
                .groupBy((a, b, c) -> a.getEntityGroup(), (a, b, c) -> b.getEntityGroup(), (a, b, c) -> c.getEntityGroup(),
                        ConstraintCollectors.countTri())
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2, group3, 1),
                assertMatchWithScore(-1, group1, group3, group2, 1),
                assertMatchWithScore(-1, group2, group3, group1, 1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void groupBy_4Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> factory
                .forEachUniquePair(TestdataLavishEntity.class)
                .join(TestdataLavishEntity.class,
                        filtering((a, b, c) -> a != c && b != c))
                .groupBy((a, b, c) -> a.getEntityGroup(), (a, b, c) -> b.getEntityGroup(), (a, b, c) -> c.getEntityGroup(),
                        (a, b, c) -> a.getValue())
                .penalize(SimpleScore.ONE)
                .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);
        TestdataLavishValue value1 = solution.getValueList().get(0);
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2, group3, value1),
                assertMatchWithScore(-1, group1, group3, group2, value2),
                assertMatchWithScore(-1, group2, group3, group1, value1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    // ************************************************************************
    // Map/flatten/distinct
    // ************************************************************************

    @Override
    @TestTemplate
    public void distinct() { // On a distinct stream, this is a no-op.
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .distinct()
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, entity3),
                assertMatch(entity1, entity3, entity2),
                assertMatch(entity2, entity3, entity1));
    }

    @Override
    @TestTemplate
    public void mapWithDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .map((a, b, c) -> asSet(a.getEntityGroup(), b.getEntityGroup(), c.getEntityGroup()))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1, group2)),
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
    public void mapWithoutDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .map((a, b, c) -> asSet(a.getEntityGroup(), b.getEntityGroup()))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1, group3)),
                assertMatch(asSet(group2, group3)));

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
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .map((a, b, c) -> asSet(a.getEntityGroup(), b.getEntityGroup(), c.getEntityGroup()))
                        .distinct()
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

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
    public void mapAndDistinctWithoutDuplicates() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .map((a, b, c) -> asSet(a.getEntityGroup(), b.getEntityGroup()))
                        .distinct()
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1, group3)),
                assertMatch(asSet(group2, group3)));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .flattenLast(c -> asList(c.getEntityGroup(), group1, group2))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, group1),
                assertMatch(entity1, entity2, group1),
                assertMatch(entity1, entity2, group2),
                assertMatch(entity1, entity3, group1),
                assertMatch(entity1, entity3, group2),
                assertMatch(entity1, entity3, group2),
                assertMatch(entity2, entity3, group1),
                assertMatch(entity2, entity3, group1),
                assertMatch(entity2, entity3, group2));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .flattenLast(c -> asSet(c.getEntityGroup(), c.getEntityGroup() == group1 ? group2 : group1))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, group1),
                assertMatch(entity1, entity2, group2),
                assertMatch(entity1, entity3, group1),
                assertMatch(entity1, entity3, group2),
                assertMatch(entity2, entity3, group1),
                assertMatch(entity2, entity3, group2));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .flattenLast(c -> asList(c.getEntityGroup(), group1, group2))
                        .distinct()
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, group1),
                assertMatch(entity1, entity2, group2),
                assertMatch(entity1, entity3, group1),
                assertMatch(entity1, entity3, group2),
                assertMatch(entity2, entity3, group1),
                assertMatch(entity2, entity3, group2));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector =
                buildScoreDirector(factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .join(TestdataLavishEntity.class, Joiners.filtering((a, b, c) -> a != c && b != c))
                        .flattenLast(c -> asSet(c.getEntityGroup(), c.getEntityGroup() == group1 ? group2 : group1))
                        .distinct()
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2, group1),
                assertMatch(entity1, entity2, group2),
                assertMatch(entity1, entity3, group1),
                assertMatch(entity1, entity3, group2),
                assertMatch(entity2, entity3, group1),
                assertMatch(entity2, entity3, group2));

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
    public void penalizeUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .penalize(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-2));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    private <Score_ extends Score<Score_>, Solution_, Entity_, Value_> void assertDefaultJustifications(
            InnerScoreDirector<Solution_, Score_> scoreDirector, List<Entity_> entityList, List<Value_> valueList) {
        if (!implSupport.isConstreamMatchEnabled())
            return;

        assertThat(scoreDirector.getIndictmentMap())
                .containsOnlyKeys(entityList.get(0),
                        entityList.get(1),
                        entityList.get(5),
                        entityList.get(6),
                        valueList.get(0),
                        valueList.get(1));

        String constraintFqn =
                ConstraintMatchTotal.composeConstraintId(scoreDirector.getSolutionDescriptor()
                        .getSolutionClass().getPackageName(), TEST_CONSTRAINT_NAME);
        Map<String, ConstraintMatchTotal<Score_>> constraintMatchTotalMap = scoreDirector.getConstraintMatchTotalMap();
        assertThat(constraintMatchTotalMap)
                .containsOnlyKeys(constraintFqn);
        ConstraintMatchTotal<Score_> constraintMatchTotal = constraintMatchTotalMap.get(constraintFqn);
        assertThat(constraintMatchTotal.getConstraintMatchSet())
                .hasSize(2);
        List<ConstraintMatch<Score_>> constraintMatchList = new ArrayList<>(constraintMatchTotal.getConstraintMatchSet());
        for (int i = 0; i < 2; i++) {
            ConstraintMatch<Score_> constraintMatch = constraintMatchList.get(i);
            assertSoftly(softly -> {
                ConstraintJustification justification = constraintMatch.getJustification();
                softly.assertThat(justification)
                        .isInstanceOf(DefaultConstraintJustification.class);
                DefaultConstraintJustification castJustification =
                        (DefaultConstraintJustification) justification;
                softly.assertThat(castJustification.getFacts())
                        .hasSize(3);
                softly.assertThat(constraintMatch.getIndictedObjectList())
                        .hasSize(3);
            });
        }
    }

    @Override
    @TestTemplate
    public void penalize() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .penalize(SimpleScore.ONE, (entity, entity2, value) -> 2)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void penalizeLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .penalizeLong(SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void penalizeBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .penalizeBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .reward(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void reward() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .reward(SimpleScore.ONE, (entity, entity2, value) -> 2)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .rewardLong(SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .rewardBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(SimpleScore.ONE)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositive() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(SimpleScore.ONE, (entity, entity2, value) -> 2)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .impactLong(SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .impactBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactNegative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(SimpleScore.ONE, (entity, entity2, value) -> -2)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactNegativeLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .impactLong(SimpleLongScore.ONE, (entity, entity2, value) -> -2L)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactNegativeBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .impactBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(-2))
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
        assertDefaultJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void penalizeUnweightedCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .penalize(SimpleScore.ONE)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-2));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    private <Score_ extends Score<Score_>, Solution_, Entity_, Value_> void assertCustomJustifications(
            InnerScoreDirector<Solution_, Score_> scoreDirector, List<Entity_> entityList, List<Value_> valueList) {
        if (!implSupport.isConstreamMatchEnabled())
            return;

        assertThat(scoreDirector.getIndictmentMap())
                .containsOnlyKeys(entityList.get(0),
                        entityList.get(1),
                        entityList.get(5),
                        entityList.get(6),
                        valueList.get(0),
                        valueList.get(1));

        String constraintFqn =
                ConstraintMatchTotal.composeConstraintId(scoreDirector.getSolutionDescriptor()
                        .getSolutionClass().getPackageName(), TEST_CONSTRAINT_NAME);
        Map<String, ConstraintMatchTotal<Score_>> constraintMatchTotalMap = scoreDirector.getConstraintMatchTotalMap();
        assertThat(constraintMatchTotalMap)
                .containsOnlyKeys(constraintFqn);
        ConstraintMatchTotal<Score_> constraintMatchTotal = constraintMatchTotalMap.get(constraintFqn);
        assertThat(constraintMatchTotal.getConstraintMatchSet())
                .hasSize(2);
        List<ConstraintMatch<Score_>> constraintMatchList = new ArrayList<>(constraintMatchTotal.getConstraintMatchSet());
        for (int i = 0; i < 2; i++) {
            ConstraintMatch<Score_> constraintMatch = constraintMatchList.get(i);
            assertSoftly(softly -> {
                ConstraintJustification justification = constraintMatch.getJustification();
                softly.assertThat(justification)
                        .isInstanceOf(TestConstraintJustification.class);
                TestConstraintJustification<Score_> castJustification =
                        (TestConstraintJustification<Score_>) justification;
                softly.assertThat(castJustification.getFacts())
                        .hasSize(3);
                softly.assertThat(constraintMatch.getIndictedObjectList())
                        .hasSize(3);
            });
        }
    }

    @Override
    @TestTemplate
    public void penalizeCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .penalize(SimpleScore.ONE, (entity, entity2, value) -> 2)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void penalizeLongCustomJustifications() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .penalizeLong(SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                                .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                .indictWith(Set::of)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void penalizeBigDecimalCustomJustifications() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .penalizeBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                        .indictWith(Set::of)
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardUnweightedCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .reward(SimpleScore.ONE)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .reward(SimpleScore.ONE, (entity, entity2, value) -> 2)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardLongCustomJustifications() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .rewardLong(SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                                .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                .indictWith(Set::of)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void rewardBigDecimalCustomJustifications() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .rewardBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                        .indictWith(Set::of)
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveUnweightedCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(SimpleScore.ONE)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(SimpleScore.ONE, (entity, entity2, value) -> 2)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveLongCustomJustifications() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .impactLong(SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                                .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                .indictWith(Set::of)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactPositiveBigDecimalCustomJustifications() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .impactBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                        .indictWith(Set::of)
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactNegativeCustomJustifications() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(SimpleScore.ONE, (entity, entity2, value) -> -2)
                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                        .indictWith(Set::of)
                        .asConstraint(TEST_CONSTRAINT_NAME));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactNegativeLongCustomJustifications() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .impactLong(SimpleLongScore.ONE, (entity, entity2, value) -> -2L)
                                .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                .indictWith(Set::of)
                                .asConstraint(TEST_CONSTRAINT_NAME)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

    @Override
    @TestTemplate
    public void impactNegativeBigDecimalCustomJustifications() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .impactBigDecimal(SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(-2))
                                        .justifyWith((a, b, c, score) -> new TestConstraintJustification<>(score, a, b, c))
                                        .indictWith(Set::of)
                                        .asConstraint(TEST_CONSTRAINT_NAME)
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
        assertCustomJustifications(scoreDirector, solution.getEntityList(), solution.getValueList());
    }

}
