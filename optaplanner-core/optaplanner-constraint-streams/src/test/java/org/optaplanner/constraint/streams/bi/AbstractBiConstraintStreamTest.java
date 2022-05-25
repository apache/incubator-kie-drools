/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bi;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countBi;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countDistinct;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.max;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.min;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toSet;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.constraint.streams.AbstractConstraintStreamTest;
import org.optaplanner.constraint.streams.ConstraintStreamFunctionalTest;
import org.optaplanner.constraint.streams.ConstraintStreamImplSupport;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
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

public abstract class AbstractBiConstraintStreamTest extends AbstractConstraintStreamTest
        implements ConstraintStreamFunctionalTest {

    protected AbstractBiConstraintStreamTest(ConstraintStreamImplSupport implSupport) {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal(TestdataLavishEntity::getValue, Function.identity()))
                    .filter((entity, value) -> value.getCode().equals("MyValue 1"))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1),
                assertMatch(entity3, value1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setValue(value2);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1, value1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setValue(value1);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1),
                assertMatch(entity2, value1));
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
            return factory.forEachUniquePair(TestdataLavishEntity.class,
                    filtering((entityA, entityB) -> !Objects.equals(entityA, entity1)))
                    .filter((entityA, entityB) -> !Objects.equals(entityA, entity2))
                    .filter((entityA, entityB) -> !Objects.equals(entityA, entity3))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatch(entity4, entity5));

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
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        solution.getExtraList().add(extra2);
        TestdataLavishExtra extra3 = new TestdataLavishExtra("MyExtra 3");
        solution.getExtraList().add(extra3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal(TestdataLavishEntity::getValue, Function.identity()))
                    .join(TestdataLavishExtra.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity1, value1, extra2),
                assertMatch(entity1, value1, extra3),
                assertMatch(entity2, value2, extra1),
                assertMatch(entity2, value2, extra2),
                assertMatch(entity2, value2, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity1, value1, extra2),
                assertMatch(entity1, value1, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
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
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        extra1.setStringProperty("MyString");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        extra2.setStringProperty(null);
        solution.getExtraList().add(extra2);
        TestdataLavishExtra extra3 = new TestdataLavishExtra("MyExtra 3");
        extra3.setStringProperty("MyString");
        solution.getExtraList().add(extra3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal(TestdataLavishEntity::getValue, Function.identity()))
                    .join(TestdataLavishExtra.class,
                            equal((entity, value) -> entity.getStringProperty(), TestdataLavishExtra::getStringProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity1, value1, extra3),
                assertMatch(entity2, value2, extra2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity1, value1, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @TestTemplate
    public void join_1Filtering() {
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
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        extra1.setStringProperty("MyString");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        extra2.setStringProperty(null);
        solution.getExtraList().add(extra2);
        TestdataLavishExtra extra3 = new TestdataLavishExtra("MyExtra 3");
        extra3.setStringProperty("MyString");
        solution.getExtraList().add(extra3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, filtering((entity, value) -> Objects.equals(entity.getValue(), value)))
                    .join(TestdataLavishExtra.class,
                            equal((entity, value) -> entity.getStringProperty(), TestdataLavishExtra::getStringProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity1, value1, extra3),
                assertMatch(entity2, value2, extra2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra1),
                assertMatch(entity1, value1, extra3));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal(TestdataLavishEntity::getValue, Function.identity()))
                    .join(TestdataLavishExtra.class,
                            equal((entity, value) -> entity.getStringProperty(), TestdataLavishExtra::getStringProperty),
                            equal((entity, value) -> entity.getIntegerProperty(), TestdataLavishExtra::getIntegerProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1, extra3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    @TestTemplate
    public void join_filtering_comesLast() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, filtering((a, b) -> false),
                            equal(TestdataLavishEntity::getValue, Function.identity()))
                    .join(TestdataLavishExtra.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalStateException.class);
    }

    @TestTemplate
    public void join_mixedEqualsAndFiltering() {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class,
                            equal(TestdataLavishEntity::getValue, Function.identity()),
                            filtering((entity, value) -> value.getCode().contains("1")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, value1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1, value1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector);
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @Override
    @TestTemplate
    public void ifExists_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
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
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishValueGroup.class)
                    .ifExists(TestdataLavishEntityGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(valueGroup, solution.getFirstValueGroup()));

        // Incremental
        TestdataLavishEntityGroup entityGroup = solution.getFirstEntityGroup();
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class,
                            filtering((entityA, entityB, group) -> Objects.equals(group, entityA.getEntityGroup()) &&
                                    Objects.equals(group, entityB.getEntityGroup())))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity2));

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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB) -> entityA.getEntityGroup(), Function.identity()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(solution.getFirstEntity(), entity2),
                assertMatch(entity1, entity2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(solution.getFirstEntity(), entity2));
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
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB) -> entityA.getEntityGroup(), Function.identity()),
                            filtering((entityA, entityB, group) -> entityA.getCode().contains("MyEntity") ||
                                    group.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2));

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
                null);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntity.class,
                            filtering((a, b, c) -> a != c && b != c))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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
                null);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntity.class,
                            filtering((a, b, c) -> a != c && b != c))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterProblemFactRemoved(entity2);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifNotExists_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
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
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishValueGroup.class)
                    .ifNotExists(TestdataLavishEntityGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        TestdataLavishEntityGroup entityGroup = solution.getFirstEntityGroup();
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(valueGroup, solution.getFirstValueGroup()));
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            filtering((entityA, entityB, group) -> Objects.equals(group, entityA.getEntityGroup()) &&
                                    Objects.equals(group, entityB.getEntityGroup())))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(entity1, entity2));

        // Incremental
        TestdataLavishEntityGroup toRemove = solution.getFirstEntityGroup();
        scoreDirector.beforeProblemFactRemoved(toRemove);
        solution.getEntityGroupList().remove(toRemove);
        scoreDirector.afterProblemFactRemoved(toRemove);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(solution.getFirstEntity(), entity2),
                assertMatch(entity1, entity2));
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
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB) -> entityA.getEntityGroup(), Function.identity()))
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
                assertMatch(entity1, entity2));
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
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB) -> entityA.getEntityGroup(), Function.identity()),
                            filtering((entityA, entityB, group) -> entityA.getCode().contains("MyEntity") ||
                                    group.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(solution.getFirstEntity(), entity2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(solution.getFirstEntity(), entity2),
                assertMatch(entity1, entity2));
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
                null);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .ifNotExists(TestdataLavishEntity.class,
                            filtering((a, b, c) -> a != c && b != c))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterProblemFactRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1));
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
                null);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .ifNotExists(TestdataLavishEntity.class,
                            filtering((a, b, c) -> a != c && b != c))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterProblemFactRemoved(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), entity1));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    @TestTemplate
    public void groupBy_1Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .groupBy((entityA, entityB) -> entityA.getEntityGroup())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup()),
                assertMatchWithScore(-1, entityGroup1));

        // Incremental
        Stream.of(entity1, entity2).forEach(entity -> {
            scoreDirector.beforeEntityRemoved(entity);
            solution.getEntityList().remove(entity);
            scoreDirector.afterEntityRemoved(entity);
        });
        assertScore(scoreDirector, assertMatchWithScore(-1, solution.getFirstEntityGroup()));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 7);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((entityA, entityB) -> entityA.toString(), countBi())
                    .filter((entity, count) -> count > 4)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, solution.getFirstEntity().toString(), 6),
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, solution.getEntityList().get(1).toString(), 5));

        // Incremental; we have a new first entity, and less entities in total.
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, TEST_CONSTRAINT_NAME, solution.getFirstEntity().toString(), 5));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((entityA, entityB) -> entityA.toString(),
                            countBi(),
                            toSet((entityA, entityB) -> entityA))
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
                    .groupBy((entityA, entityB) -> entityA.toString(),
                            min((TestdataLavishEntity entityA, TestdataLavishEntity entityB) -> entityA.getLongProperty()),
                            max((TestdataLavishEntity entityA, TestdataLavishEntity entityB) -> entityA.getLongProperty()),
                            toSet((entityA, entityB) -> entityA))
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
    public void groupBy_0Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 2, 3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy(countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-3, 3));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy(countBi(),
                            countDistinct((e, e2) -> e))
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
                    .groupBy(countBi(),
                            min((TestdataLavishEntity e, TestdataLavishEntity e2) -> e.getLongProperty()),
                            max((TestdataLavishEntity e, TestdataLavishEntity e2) -> e.getLongProperty()))
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
                    .groupBy(countBi(),
                            min((TestdataLavishEntity e, TestdataLavishEntity e2) -> e.getLongProperty()),
                            max((TestdataLavishEntity e, TestdataLavishEntity e2) -> e.getLongProperty()),
                            toSet((e, e2) -> e))
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
    public void groupBy_2Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((a, b) -> a.getEntityGroup(), (a, b) -> b.getEntityGroup())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2),
                assertMatchWithScore(-1, group1, group3),
                assertMatchWithScore(-1, group2, group3));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group3));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 4);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((a, b) -> a.getEntityGroup(), (a, b) -> b.getEntityGroup(), countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entityGroup1, entityGroup2, count) -> count);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group1, 1),
                assertMatchWithScore(-3, group1, group2, 3),
                assertMatchWithScore(-1, group2, group1, 1),
                assertMatchWithScore(-1, group2, group2, 1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group2, 1),
                assertMatchWithScore(-1, group1, group2, 1),
                assertMatchWithScore(-1, group2, group1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping2Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 4);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((a, b) -> a.getEntityGroup(), (a, b) -> b.getEntityGroup(), countBi(), countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE,
                            (entityGroup1, entityGroup2, count, sameCount) -> count + sameCount);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, group1, group1, 1, 1),
                assertMatchWithScore(-6, group1, group2, 3, 3),
                assertMatchWithScore(-2, group2, group1, 1, 1),
                assertMatchWithScore(-2, group2, group2, 1, 1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, group2, group2, 1, 1),
                assertMatchWithScore(-2, group1, group2, 1, 1),
                assertMatchWithScore(-2, group2, group1, 1, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((a, b) -> a.getEntityGroup(), (a, b) -> b.getEntityGroup(), (a, b) -> a.getValue())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);
        TestdataLavishValue value1 = solution.getValueList().get(0);
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2, value1),
                assertMatchWithScore(-1, group1, group3, value1),
                assertMatchWithScore(-1, group2, group3, value2));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group3, value2));
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping1Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((a, b) -> a.getEntityGroup(), (a, b) -> b.getEntityGroup(), (a, b) -> a.getValue(),
                            ConstraintCollectors.countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);
        TestdataLavishValue value1 = solution.getValueList().get(0);
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2, value1, 1),
                assertMatchWithScore(-1, group1, group3, value1, 1),
                assertMatchWithScore(-1, group2, group3, value2, 1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group3, value2, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_4Mapping0Collector() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .groupBy((a, b) -> a.getEntityGroup(), (a, b) -> b.getEntityGroup(), (a, b) -> a.getValue(),
                            (a, b) -> b.getValue())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getEntityGroupList().get(0);
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishEntityGroup group3 = solution.getEntityGroupList().get(2);
        TestdataLavishValue value1 = solution.getValueList().get(0);
        TestdataLavishValue value2 = solution.getValueList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, group2, value1, value2),
                assertMatchWithScore(-1, group1, group3, value1, value1),
                assertMatchWithScore(-1, group2, group3, value2, value1));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, group3, value2, value1));
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
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity2),
                assertMatch(entity1, entity3),
                assertMatch(entity2, entity3));
    }

    @Override
    @TestTemplate
    public void mapWithDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .map((a, b) -> asSet(a.getEntityGroup(), b.getEntityGroup())) // 3 entities, 2 groups => duplicates.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1)));

        TestdataLavishEntity entity = solution.getFirstEntity();

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
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .map((a, b) -> asSet(a.getEntityGroup(), b.getEntityGroup())) // 3 entities, 3 groups => no duplicates.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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
        assertScore(scoreDirector,
                assertMatch(asSet(group2, group3)));
    }

    @Override
    @TestTemplate
    public void mapAndDistinctWithDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .map((a, b) -> asSet(a.getEntityGroup(), b.getEntityGroup())) // 3 entities, 2 groups => duplicates.
                    .distinct() // Duplicate copies removed here.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(asSet(group1, group2)),
                assertMatch(asSet(group1)));

        TestdataLavishEntity entity = solution.getFirstEntity();

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
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 3, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .map((a, b) -> asSet(a.getEntityGroup(), b.getEntityGroup())) // 3 entities, 3 groups => no duplicates.
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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
        assertScore(scoreDirector,
                assertMatch(asSet(group2, group3)));
    }

    @Override
    @TestTemplate
    public void flattenLastWithDuplicates() {
        assumeDrools();

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .flattenLast(b -> asList(b.getEntityGroup(), group1, group2))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, group1),
                assertMatch(entity1, group1),
                assertMatch(entity1, group2),
                assertMatch(entity2, group2),
                assertMatch(entity2, group1),
                assertMatch(entity2, group2),
                assertMatch(entity1, group1),
                assertMatch(entity1, group1),
                assertMatch(entity1, group2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatch(entity2, group2),
                assertMatch(entity2, group1),
                assertMatch(entity2, group2));
    }

    @Override
    @TestTemplate
    public void flattenLastWithoutDuplicates() {
        assumeDrools();

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .flattenLast(b -> singleton(b.getEntityGroup()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, group1),
                assertMatch(entity1, group2),
                assertMatch(entity1, group1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatch(entity2, group1));
    }

    @Override
    @TestTemplate
    public void flattenLastAndDistinctWithDuplicates() {
        assumeDrools();

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .flattenLast(b -> asList(b.getEntityGroup(), group1, group2))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, group1),
                assertMatch(entity1, group2),
                assertMatch(entity2, group2),
                assertMatch(entity2, group1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatch(entity2, group1),
                assertMatch(entity2, group2));
    }

    @Override
    @TestTemplate
    public void flattenLastAndDistinctWithoutDuplicates() {
        assumeDrools();

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .flattenLast(b -> singleton(b.getEntityGroup()))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity2, group1),
                assertMatch(entity1, group2),
                assertMatch(entity1, group1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatch(entity2, group1));
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    @TestTemplate
    public void penalize_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(asList(e1, e2));

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                .penalize("myConstraint1", SimpleScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                .penalize("myConstraint2", SimpleScore.ONE, (entity, value) -> 20)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-42));
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

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector =
                buildScoreDirector(
                        TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                        .penalize("myConstraint1", SimpleLongScore.ONE),
                                factory.forEach(TestdataEntity.class)
                                        .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                        .penalizeLong("myConstraint2", SimpleLongScore.ONE, (entity, value) -> 20L)
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-42L));
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
                        factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                        .penalize("myConstraint1", SimpleBigDecimalScore.ONE),
                                factory.forEach(TestdataEntity.class)
                                        .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                        .penalizeBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                                (entity, value) -> new BigDecimal("0.2"))
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-2.4")));
    }

    @Override
    @TestTemplate
    public void penalize_negative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        String constraintName = "myConstraint";
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishValue.class)
                        .penalize(constraintName, SimpleScore.ONE, (entity, value) -> -1));

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
                        factory.forEach(TestdataEntity.class)
                                .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                .reward("myConstraint1", SimpleScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                .reward("myConstraint2", SimpleScore.ONE, (entity, value) -> 20)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(42));
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
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(), factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                .reward("myConstraint1", SimpleLongScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                .rewardLong("myConstraint2", SimpleLongScore.ONE, (entity, value) -> 20L)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(42L));
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
                                factory.forEach(TestdataEntity.class)
                                        .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                        .reward("myConstraint1", SimpleBigDecimalScore.ONE),
                                factory.forEach(TestdataEntity.class)
                                        .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                                        .rewardBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                                (entity, value) -> new BigDecimal("0.2"))
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("2.4")));
    }

    @Override
    @TestTemplate
    public void reward_negative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        String constraintName = "myConstraint";
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .join(TestdataLavishValue.class)
                        .reward(constraintName, SimpleScore.ONE, (entity, value) -> -1));

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

    // ************************************************************************
    // Combinations
    // ************************************************************************

    @TestTemplate
    public void joinerEqualsAndSameness() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 1, 2);
        // The two bigDecimals are not the same, but they equals()
        String decimal = "0.01";
        BigDecimal bigDecimal1 = new BigDecimal(decimal);
        BigDecimal bigDecimal2 = new BigDecimal(decimal);
        TestdataLavishEntity entity1 = solution.getEntityList().get(0);
        entity1.setBigDecimalProperty(bigDecimal1);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        entity2.setBigDecimalProperty(bigDecimal2);
        // Entity 3's BigDecimal property is the same as Entity 1's and equals() Entity 2's.
        TestdataLavishEntity entity3 = new TestdataLavishEntity("My Entity 0", solution.getFirstEntityGroup(),
                entity1.getValue());
        entity3.setBigDecimalProperty(bigDecimal1);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal(TestdataLavishEntity::getBigDecimalProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                // Each entity's BigDecimal property is joined to itself.
                assertMatch(entity1, entity1),
                assertMatch(entity2, entity2),
                assertMatch(entity3, entity3),
                // Each entity's BigDecimal property is joined to each other entity's.
                assertMatch(entity1, entity2),
                assertMatch(entity1, entity3),
                assertMatch(entity2, entity1),
                assertMatch(entity2, entity3),
                assertMatch(entity3, entity1),
                assertMatch(entity3, entity2));
    }

}
