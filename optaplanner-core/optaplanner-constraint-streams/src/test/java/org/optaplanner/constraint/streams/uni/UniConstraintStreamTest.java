/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.uni;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countDistinct;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.max;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.min;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toSet;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.constraint.streams.AbstractConstraintStreamTest;
import org.optaplanner.constraint.streams.ConstraintStreamFunctionalTest;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableEntity;
import org.optaplanner.core.impl.testdata.domain.nullable.TestdataNullableSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValueGroup;

class UniConstraintStreamTest extends AbstractConstraintStreamTest implements ConstraintStreamFunctionalTest {

    public UniConstraintStreamTest(boolean constraintMatchEnabled, ConstraintStreamImplType constraintStreamImplType) {
        super(constraintMatchEnabled, constraintStreamImplType);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @TestTemplate
    public void filter_problemFact() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishValueGroup valueGroup1 = new TestdataLavishValueGroup("MyValueGroup 1");
        solution.getValueGroupList().add(valueGroup1);
        TestdataLavishValueGroup valueGroup2 = new TestdataLavishValueGroup("MyValueGroup 2");
        solution.getValueGroupList().add(valueGroup2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .filter(valueGroup -> valueGroup.getCode().startsWith("MyValueGroup"))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(valueGroup1),
                assertMatch(valueGroup2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(valueGroup1);
        valueGroup1.setCode("Other code");
        scoreDirector.afterProblemPropertyChanged(valueGroup1);
        assertScore(scoreDirector,
                assertMatch(valueGroup2));
    }

    @Override
    @TestTemplate
    public void filter_entity() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .filter(entity -> entity.getEntityGroup() == entityGroup)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));

        // Incrementally update
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2),
                assertMatch(entity3));

        // Remove entity
        scoreDirector.beforeEntityRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterEntityRemoved(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));

        // Add it back again, to make sure it was properly removed before
        scoreDirector.beforeEntityAdded(entity3);
        solution.getEntityList().add(entity3);
        scoreDirector.afterEntityAdded(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2),
                assertMatch(entity3));
    }

    @Override
    @TestTemplate
    public void filter_consecutive() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(4, 4);
        TestdataLavishEntity entity1 = solution.getEntityList().get(0);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntity entity4 = solution.getEntityList().get(3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .filter(entity -> !Objects.equals(entity, entity1))
                    .filter(entity -> !Objects.equals(entity, entity2))
                    .filter(entity -> !Objects.equals(entity, entity3))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatch(entity4));

        // Remove entity
        scoreDirector.beforeEntityRemoved(entity4);
        solution.getEntityList().remove(entity4);
        scoreDirector.afterEntityRemoved(entity4);
        assertScore(scoreDirector);
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @TestTemplate
    public void join_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .join(Integer.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Integer.class.getCanonicalName())
                .hasMessageContaining("assignable from");
    }

    @Override
    @TestTemplate
    public void join_0() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .join(TestdataLavishEntityGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstValueGroup(), solution.getFirstEntityGroup()),
                assertMatch(solution.getFirstValueGroup(), entityGroup),
                assertMatch(valueGroup, solution.getFirstEntityGroup()),
                assertMatch(valueGroup, entityGroup));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstValueGroup(), solution.getFirstEntityGroup()),
                assertMatch(valueGroup, solution.getFirstEntityGroup()));
    }

    @Override
    @TestTemplate
    public void join_1Equal() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                value2);
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                value1);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            equal(TestdataLavishEntity::getValue))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1, entity1),
                assertMatch(entity1, entity3),
                assertMatch(entity2, entity2),
                assertMatch(entity3, entity1),
                assertMatch(entity3, entity3));

        // Incremental
        scoreDirector.beforeVariableChanged(entity3, "value");
        entity3.setValue(value2);
        scoreDirector.afterVariableChanged(entity3, "value");
        assertScore(scoreDirector,
                assertMatch(entity1, entity1),
                assertMatch(entity2, entity2),
                assertMatch(entity2, entity3),
                assertMatch(entity3, entity2),
                assertMatch(entity3, entity3));

        // Incremental for which the first change matches a join that doesn't survive the second change
        scoreDirector.beforeVariableChanged(entity1, "value");
        entity1.setValue(value2);
        scoreDirector.afterVariableChanged(entity1, "value");
        scoreDirector.beforeVariableChanged(entity3, "value");
        entity3.setValue(value1);
        scoreDirector.afterVariableChanged(entity3, "value");
        assertScore(scoreDirector,
                assertMatch(entity1, entity1),
                assertMatch(entity2, entity2),
                assertMatch(entity1, entity2),
                assertMatch(entity2, entity1),
                assertMatch(entity3, entity3));
    }

    /**
     * A join must not presume that left inserts/retracts always happen before right inserts/retracts,
     * if node sharing is active.
     * This test triggers a right insert/retract before a left insert/retract.
     */
    @TestTemplate
    public void join_1_mirrored() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = new TestdataLavishValue("MyValue 2", solution.getFirstValueGroup());
        solution.getValueList().add(value2);
        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(), value2);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataLavishSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        // A.join(B)
                        factory.forEach(TestdataLavishEntity.class)
                                .join(TestdataLavishValue.class,
                                        equal(TestdataLavishEntity::getValue, value -> value))
                                .penalize("testConstraint1", SimpleScore.ONE),
                        // B.join(A)
                        factory.forEach(TestdataLavishValue.class)
                                .join(TestdataLavishEntity.class,
                                        equal(value -> value, TestdataLavishEntity::getValue))
                                .penalize("testConstraint2", SimpleScore.ONE),
                });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch("testConstraint1", entity1, value1),
                assertMatch("testConstraint2", value1, entity1),
                assertMatch("testConstraint1", entity2, value2),
                assertMatch("testConstraint2", value2, entity2));

        // Incremental
        scoreDirector.beforeVariableChanged(entity2, "value");
        entity2.setValue(value1);
        scoreDirector.afterVariableChanged(entity2, "value");
        assertScore(scoreDirector,
                assertMatch("testConstraint1", entity1, value1),
                assertMatch("testConstraint2", value1, entity1),
                assertMatch("testConstraint1", entity2, value1),
                assertMatch("testConstraint2", value1, entity2));
    }

    @Override
    @TestTemplate
    public void join_2Equal() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        entity1.setIntegerProperty(7);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup, solution.getFirstValue());
        entity2.setIntegerProperty(7);
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", entityGroup, solution.getFirstValue());
        entity3.setIntegerProperty(8);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            equal(TestdataLavishEntity::getEntityGroup),
                            equal(TestdataLavishEntity::getIntegerProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), solution.getFirstEntity()),
                assertMatch(entity1, entity1),
                assertMatch(entity1, entity2),
                assertMatch(entity2, entity1),
                assertMatch(entity2, entity2),
                assertMatch(entity3, entity3));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity1);
        entity1.setIntegerProperty(8);
        scoreDirector.afterProblemPropertyChanged(entity1);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), solution.getFirstEntity()),
                assertMatch(entity1, entity1),
                assertMatch(entity1, entity3),
                assertMatch(entity2, entity2),
                assertMatch(entity3, entity1),
                assertMatch(entity3, entity3));
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @Override
    @TestTemplate
    public void ifExists_unknownClass() {
        assumeDrools();
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .ifExists(Integer.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Integer.class.getCanonicalName())
                .hasMessageContaining("assignable from");
    }

    @Override
    @TestTemplate
    public void ifExists_0Joiner0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .ifExists(TestdataLavishEntityGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstValueGroup()),
                assertMatch(valueGroup));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstValueGroup()),
                assertMatch(valueGroup));
    }

    @Override
    @TestTemplate
    public void ifExists_0Join1Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class,
                            filtering((entity, group) -> Objects.equals(entity.getEntityGroup(), group)))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity1),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity2));
    }

    @Override
    @TestTemplate
    public void ifExists_1Join0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity1),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity2));
    }

    @Override
    @TestTemplate
    public void ifExists_1Join1Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal(TestdataLavishEntity::getEntityGroup, Function.identity()),
                            filtering((entity, group) -> entity.getCode().contains("MyEntity")
                                    || group.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(entity2));
    }

    @TestTemplate
    public void ifExistsOther_1Join0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifExistsOther(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));
    }

    @Override
    @TestTemplate
    public void ifExistsDoesNotIncludeNullVars() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(), null);
        solution.getEntityList().add(entity3);

        // both forEach() and ifExists() will skip entity3, as it is not initialized.
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifExistsOther(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));
    }

    @Override
    @TestTemplate
    @Deprecated(forRemoval = true)
    public void ifExistsIncludesNullVarsWithFrom() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(), null);
        solution.getEntityList().add(entity3);

        // from() will skip entity3, as it is not initialized.
        // ifExists() will still catch it, as it ignores that check.
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.from(TestdataLavishEntity.class)
                    .ifExistsOther(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity1),
                assertMatch(entity2));
    }

    @Override
    @TestTemplate
    public void ifNotExists_unknownClass() {
        assumeDrools();
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .ifNotExists(Integer.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Integer.class.getCanonicalName())
                .hasMessageContaining("assignable from");
    }

    @Override
    @TestTemplate
    public void ifNotExists_0Joiner0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishValueGroup.class)
                    .ifNotExists(TestdataLavishEntityGroup.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector);

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector);
    }

    @Override
    @TestTemplate
    public void ifNotExists_0Join1Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            filtering((entity, group) -> Objects.equals(entity.getEntityGroup(), group)))
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
                assertMatch(entity1));
    }

    @Override
    @TestTemplate
    public void ifNotExists_1Join0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
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
                assertMatch(entity1));
    }

    @Override
    @TestTemplate
    public void ifNotExists_1Join1Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal(TestdataLavishEntity::getEntityGroup, Function.identity()),
                            filtering((entity, group) -> entity.getCode().contains("MyEntity")
                                    || group.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup);
        solution.getEntityGroupList().remove(entityGroup);
        scoreDirector.afterProblemFactRemoved(entityGroup);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity1));
    }

    @Override
    @TestTemplate
    public void ifNotExistsDoesNotIncludeNullVars() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(), null);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifNotExistsOther(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()),
                assertMatch(entity1));
    }

    @Override
    @TestTemplate
    @Deprecated(forRemoval = true)
    public void ifNotExistsIncludesNullVarsWithFrom() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("Entity with null var", solution.getFirstEntityGroup(), null);
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.from(TestdataLavishEntity.class)
                    .ifNotExistsOther(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()));
    }

    @TestTemplate
    public void ifNotExistsOther_1Join0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .ifNotExistsOther(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity()));
    }

    // ************************************************************************
    // For Each
    // ************************************************************************

    @TestTemplate
    public void forEach_unknownClass() {
        assertThatThrownBy(() -> buildScoreDirector(factory -> {
            return factory.forEach(Integer.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Integer.class.getCanonicalName())
                .hasMessageContaining("assignable from");
    }

    @TestTemplate
    public void forEach_polymorphism() {
        TestdataSolution solution = new TestdataUnannotatedExtendedSolution();
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        solution.setValueList(List.of(v1, v2));
        TestdataUnannotatedExtendedEntity cat = new TestdataUnannotatedExtendedEntity("Cat", v1);
        TestdataEntity animal = new TestdataEntity("Animal", v1);
        TestdataUnannotatedExtendedEntity dog = new TestdataUnannotatedExtendedEntity("Dog", v1);
        solution.setEntityList(List.of(cat, animal, dog));

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .penalize("superclassConstraint", SimpleScore.ONE),
                        factory.forEach(TestdataUnannotatedExtendedEntity.class)
                                .penalize("subclassConstraint", SimpleScore.ONE)
                });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch("superclassConstraint", cat),
                assertMatch("superclassConstraint", animal),
                assertMatch("superclassConstraint", dog),
                assertMatch("subclassConstraint", cat),
                assertMatch("subclassConstraint", dog));
    }

    @TestTemplate
    public void forEachUniquePair_0() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 0);
        TestdataLavishEntity entityB = new TestdataLavishEntity("B", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entityB);
        TestdataLavishEntity entityA = new TestdataLavishEntity("A", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entityA);
        TestdataLavishEntity entityC = new TestdataLavishEntity("C", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entityC);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entityA, entityB),
                assertMatch(entityA, entityC),
                assertMatch(entityB, entityC));
    }

    @TestTemplate
    public void forEachUniquePair_1Equals() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 0);
        TestdataLavishEntity entityB = new TestdataLavishEntity("B", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        entityB.setIntegerProperty(2);
        solution.getEntityList().add(entityB);
        TestdataLavishEntity entityA = new TestdataLavishEntity("A", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        entityA.setIntegerProperty(2);
        solution.getEntityList().add(entityA);
        TestdataLavishEntity entityC = new TestdataLavishEntity("C", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        entityC.setIntegerProperty(10);
        solution.getEntityList().add(entityC);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getIntegerProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entityA, entityB));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entityB);
        entityB.setIntegerProperty(10);
        scoreDirector.afterProblemPropertyChanged(entityB);
        assertScore(scoreDirector,
                assertMatch(entityB, entityC));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @TestTemplate
    public void groupBy_1Mapping0Collect_filtered() {
        assumeDrools();
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
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup)
                    .filter(entityGroup -> Objects.equals(entityGroup, entityGroup1))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-1, entityGroup1));
    }

    @TestTemplate
    public void groupBy_1Mapping1Collect_filtered() {
        assumeDrools();
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
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, count())
                    .filter((entityGroup, count) -> count > 1)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entityGroup1, 2),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 8));
    }

    @TestTemplate
    public void groupBy_joinedAndFiltered() {
        assumeDrools();
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
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup)
                    .join(TestdataLavishEntity.class, equal(Function.identity(), TestdataLavishEntity::getEntityGroup))
                    .filter((group, entity) -> group.equals(entityGroup1))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entityGroup1, entity1),
                assertMatchWithScore(-1, entityGroup1, entity2));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping0Collector() {
        assumeDrools();
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
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup)
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
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 2),
                assertMatchWithScore(-1, solution.getEntityGroupList().get(1), 1));

        // Incremental
        Stream.of(solution.getEntityList().get(0), solution.getEntityList().get(1))
                .forEach(entity -> {
                    scoreDirector.beforeEntityRemoved(entity);
                    solution.getEntityList().remove(entity);
                    scoreDirector.afterEntityRemoved(entity);
                });
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping2Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup,
                            count(),
                            toSet())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 2, asSet(entity1, entity3)),
                assertMatchWithScore(-1, solution.getEntityGroupList().get(1), 1, Collections.singleton(entity2)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1, Collections.singleton(entity3)),
                assertMatchWithScore(-1, solution.getEntityGroupList().get(1), 1, Collections.singleton(entity2)));
    }

    @Override
    @TestTemplate
    public void groupBy_1Mapping3Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup,
                            count(),
                            countDistinct(),
                            toSet())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 2, 2, asSet(entity1, entity3)),
                assertMatchWithScore(-1, solution.getEntityGroupList().get(1), 1, 1, Collections.singleton(entity2)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1, 1, Collections.singleton(entity3)),
                assertMatchWithScore(-1, solution.getEntityGroupList().get(1), 1, 1, Collections.singleton(entity2)));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping1Collector() {
        assumeDrools();
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
        // Insert the same entity twice, make sure it doesn't matter.
        // This will exercise code in Drools that removes duplicates.
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-10, 10));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterEntityRemoved(entity3);
        assertScore(scoreDirector, assertMatchWithScore(-9, 9));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping2Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(count(),
                            countDistinct())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-1, 3, 3));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector, assertMatchWithScore(-1, 2, 2));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping3Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(count(),
                            min(TestdataLavishEntity::getIntegerProperty),
                            max(TestdataLavishEntity::getIntegerProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        entity1.setIntegerProperty(0);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        entity2.setIntegerProperty(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        entity3.setIntegerProperty(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 3, 0, 2));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 2, 1, 2));
    }

    @Override
    @TestTemplate
    public void groupBy_0Mapping4Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 3);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(count(),
                            min(TestdataLavishEntity::getIntegerProperty),
                            max(TestdataLavishEntity::getIntegerProperty),
                            toSet())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        entity1.setIntegerProperty(0);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        entity2.setIntegerProperty(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        entity3.setIntegerProperty(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 3, 0, 2, asSet(entity1, entity2, entity3)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, 2, 1, 2, asSet(entity2, entity3)));
    }

    @TestTemplate
    public void groupBy_1Mapping1Collector_groupingOnPrimitives() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, solution.getFirstValue());
        entity1.setIntegerProperty(1);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        entity2.setIntegerProperty(2);
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        entity3.setIntegerProperty(3);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getIntegerProperty, count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (integerProperty, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-8, 1, 8),
                assertMatchWithScore(-1, 2, 1),
                assertMatchWithScore(-1, 3, 1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterEntityRemoved(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-8, 1, 8),
                assertMatchWithScore(-1, 2, 1));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishValue secondValue = solution.getValueList().get(1);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", entityGroup1, secondValue);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entityGroup1, solution.getFirstValue()),
                assertMatchWithScore(-1, entityGroup1, secondValue),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(0)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(1)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(2)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(3)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(4)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterEntityRemoved(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entityGroup1, solution.getFirstValue()),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(0)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(1)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(2)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(3)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(4)));

        // Ensure that the first match is still there when entity2, as it still has entity1
        scoreDirector.beforeEntityRemoved(entity2);
        solution.getEntityList().remove(entity2);
        scoreDirector.afterEntityRemoved(entity2);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entityGroup1, solution.getFirstValue()),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(0)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(1)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(2)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(3)),
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), solution.getValueList().get(4)));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping1Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 7);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, value1);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", entityGroup1, value1);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue, count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entityGroup, value, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-7, solution.getFirstEntityGroup(), solution.getFirstValue(), 7),
                assertMatchWithScore(-2, entityGroup1, value1, 2),
                assertMatchWithScore(-1, entityGroup1, solution.getFirstValue(), 1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(solution.getFirstEntityGroup());
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatchWithScore(-8, solution.getFirstEntityGroup(), solution.getFirstValue(), 8),
                assertMatchWithScore(-2, entityGroup1, value1, 2));
    }

    @Override
    @TestTemplate
    public void groupBy_2Mapping2Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 7);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishValue value1 = new TestdataLavishValue("MyValue", solution.getFirstValueGroup());
        solution.getValueList().add(value1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, value1);
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", entityGroup1, value1);
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue, count(), count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entityGroup, value, count, sameCount) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-7, solution.getFirstEntityGroup(), solution.getFirstValue(), 7, 7),
                assertMatchWithScore(-2, entityGroup1, value1, 2, 2),
                assertMatchWithScore(-1, entityGroup1, solution.getFirstValue(), 1, 1));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(solution.getFirstEntityGroup());
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatchWithScore(-8, solution.getFirstEntityGroup(), solution.getFirstValue(), 8, 8),
                assertMatchWithScore(-2, entityGroup1, value1, 2, 2));
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 5);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue,
                            TestdataLavishEntity::getCode)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntity entity4 = solution.getEntityList().get(3);
        TestdataLavishEntity entity5 = solution.getEntityList().get(4);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);
        TestdataLavishValue value3 = solution.getValueList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, value1, entity1.getCode()),
                assertMatchWithScore(-1, group2, value2, entity2.getCode()),
                assertMatchWithScore(-1, group1, value3, entity3.getCode()),
                assertMatchWithScore(-1, group2, value1, entity4.getCode()),
                assertMatchWithScore(-1, group1, value2, entity5.getCode()));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2, entity2.getCode()),
                assertMatchWithScore(-1, group1, value3, entity3.getCode()),
                assertMatchWithScore(-1, group2, value1, entity4.getCode()),
                assertMatchWithScore(-1, group1, value2, entity5.getCode()));
    }

    @Override
    @TestTemplate
    public void groupBy_3Mapping1Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 5);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue,
                            TestdataLavishEntity::getCode, ConstraintCollectors.toSet())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntity entity4 = solution.getEntityList().get(3);
        TestdataLavishEntity entity5 = solution.getEntityList().get(4);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);
        TestdataLavishValue value3 = solution.getValueList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group1, value1, entity1.getCode(), Collections.singleton(entity1)),
                assertMatchWithScore(-1, group2, value2, entity2.getCode(), Collections.singleton(entity2)),
                assertMatchWithScore(-1, group1, value3, entity3.getCode(), Collections.singleton(entity3)),
                assertMatchWithScore(-1, group2, value1, entity4.getCode(), Collections.singleton(entity4)),
                assertMatchWithScore(-1, group1, value2, entity5.getCode(), Collections.singleton(entity5)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, group2, value2, entity2.getCode(), Collections.singleton(entity2)),
                assertMatchWithScore(-1, group1, value3, entity3.getCode(), Collections.singleton(entity3)),
                assertMatchWithScore(-1, group2, value1, entity4.getCode(), Collections.singleton(entity4)),
                assertMatchWithScore(-1, group1, value2, entity5.getCode(), Collections.singleton(entity5)));
    }

    @Override
    @TestTemplate
    public void groupBy_4Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 5);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(Function.identity(), TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue,
                            TestdataLavishEntity::getCode)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntity entity4 = solution.getEntityList().get(3);
        TestdataLavishEntity entity5 = solution.getEntityList().get(4);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);
        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);
        TestdataLavishValue value3 = solution.getValueList().get(2);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entity1, group1, value1, entity1.getCode()),
                assertMatchWithScore(-1, entity2, group2, value2, entity2.getCode()),
                assertMatchWithScore(-1, entity3, group1, value3, entity3.getCode()),
                assertMatchWithScore(-1, entity4, group2, value1, entity4.getCode()),
                assertMatchWithScore(-1, entity5, group1, value2, entity5.getCode()));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entity2, group2, value2, entity2.getCode()),
                assertMatchWithScore(-1, entity3, group1, value3, entity3.getCode()),
                assertMatchWithScore(-1, entity4, group2, value1, entity4.getCode()),
                assertMatchWithScore(-1, entity5, group1, value2, entity5.getCode()));
    }

    // ************************************************************************
    // Map/flatten/distinct
    // ************************************************************************

    @Override
    @TestTemplate
    public void distinct() { // On a distinct stream, this is a no-op.
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));
    }

    @Override
    @TestTemplate
    public void mapWithDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .map(TestdataLavishEntity::getEntityGroup) // Two entities, just one group => duplicates.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group = solution.getFirstEntityGroup();

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group),
                assertMatch(group));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group));
    }

    @Override
    @TestTemplate
    public void mapWithoutDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .map(TestdataLavishEntity::getEntityGroup) // Two entities, two groups => no duplicates.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group2));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group2));
    }

    @Override
    @TestTemplate
    public void mapAndDistinctWithDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .map(TestdataLavishEntity::getEntityGroup) // Two entities, just one group => duplicates.
                    .distinct() // Duplicate copies removed here.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group = solution.getFirstEntityGroup();

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group));
    }

    @Override
    @TestTemplate
    public void mapAndDistinctWithoutDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .map(TestdataLavishEntity::getEntityGroup) // Two entities, two groups => no duplicates.
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group2));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group2));
    }

    @Override
    @TestTemplate
    public void flattenLastWithDuplicates() {
        assumeDrools();

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .flattenLast(entity -> Arrays.asList(group1, group1, group2))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group1),
                assertMatch(group2),
                assertMatch(group1),
                assertMatch(group1),
                assertMatch(group2));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group1),
                assertMatch(group2));
    }

    @Override
    @TestTemplate
    public void flattenLastWithoutDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .flattenLast(entity -> Collections.singletonList(entity.getEntityGroup()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group2));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group2));
    }

    @Override
    @TestTemplate
    public void flattenLastAndDistinctWithDuplicates() {
        assumeDrools();

        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .flattenLast(entity -> Arrays.asList(group1, group1, group2))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group2));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group2));
    }

    @Override
    @TestTemplate
    public void flattenLastAndDistinctWithoutDuplicates() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 2, 2);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .flattenLast(entity -> Collections.singletonList(entity.getEntityGroup()))
                    .distinct()
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishEntityGroup group1 = solution.getFirstEntityGroup();
        TestdataLavishEntityGroup group2 = solution.getEntityGroupList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(group1),
                assertMatch(group2));

        TestdataLavishEntity entity = solution.getFirstEntity();

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatch(group2));
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    @TestTemplate
    public void penalize_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .penalize("myConstraint1", SimpleScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .penalize("myConstraint2", SimpleScore.ONE, entity -> 20)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-42));
    }

    @Override
    @TestTemplate
    public void penalize_Long() {
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .penalize("myConstraint1", SimpleLongScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .penalizeLong("myConstraint2", SimpleLongScore.ONE, entity -> 20L)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-42L));
    }

    @Override
    @TestTemplate
    public void penalize_BigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(
                        TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .penalize("myConstraint1", SimpleBigDecimalScore.ONE),
                                factory.forEach(TestdataEntity.class)
                                        .penalizeBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                                entity -> new BigDecimal("0.2"))
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-2.4")));
    }

    @Override
    @TestTemplate
    public void penalize_negative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        String constraintName = "myConstraint1";
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .penalize(constraintName, SimpleScore.ONE, e -> -1));

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

    @Override
    @TestTemplate
    public void reward_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .reward("myConstraint1", SimpleScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .reward("myConstraint2", SimpleScore.ONE, entity -> 20)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(42));
    }

    @Override
    @TestTemplate
    public void reward_Long() {
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .reward("myConstraint1", SimpleLongScore.ONE),
                        factory.forEach(TestdataEntity.class)
                                .rewardLong("myConstraint2", SimpleLongScore.ONE, entity -> 20L)
                });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(42L));
    }

    @Override
    @TestTemplate
    public void reward_BigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(
                        TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .reward("myConstraint1", SimpleBigDecimalScore.ONE),
                                factory.forEach(TestdataEntity.class)
                                        .rewardBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                                entity -> new BigDecimal("0.2"))
                        });

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("2.4")));
    }

    @Override
    @TestTemplate
    public void reward_negative() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        String constraintName = "myConstraint1";
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .reward(constraintName, SimpleScore.ONE, e -> -1));

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

    // ************************************************************************
    // Combinations
    // ************************************************************************

    @TestTemplate
    public void duplicateConstraintId() {
        ConstraintProvider constraintProvider = factory -> new Constraint[] {
                factory.forEach(TestdataLavishEntity.class).penalize("duplicateConstraintName", SimpleScore.ONE),
                factory.forEach(TestdataLavishEntity.class).penalize("duplicateConstraintName", SimpleScore.ONE)
        };
        assertThatIllegalStateException().isThrownBy(() -> buildScoreDirector(
                TestdataLavishSolution.buildSolutionDescriptor(),
                constraintProvider));
    }

    @TestTemplate
    public void zeroConstraintWeightDisabled() {
        /*
         * This may never be possible for Drools. If implemented, please remember that some rules may be shared by
         * different scoring streams, and therefore the rules can only be disabled when all the relevant scoring streams
         * have their weights set to zero.
         */
        assumeBavet();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        entity1.setStringProperty("myProperty1");
        solution.getEntityList().add(entity1);

        AtomicLong zeroWeightMonitorCount = new AtomicLong(0L);
        AtomicLong oneWeightMonitorCount = new AtomicLong(0L);
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataLavishSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataLavishEntity.class)
                                .filter(entity -> {
                                    zeroWeightMonitorCount.getAndIncrement();
                                    return true;
                                })
                                .penalize("myConstraint1", SimpleScore.ZERO),
                        factory.forEach(TestdataLavishEntity.class)
                                .filter(entity -> {
                                    oneWeightMonitorCount.getAndIncrement();
                                    return true;
                                })
                                .penalize("myConstraint2", SimpleScore.ONE)
                });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(zeroWeightMonitorCount.getAndSet(0L)).isEqualTo(0);
        assertThat(oneWeightMonitorCount.getAndSet(0L)).isEqualTo(3);

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity1);
        entity1.setStringProperty("myProperty2");
        scoreDirector.afterProblemPropertyChanged(entity1);
        scoreDirector.calculateScore();
        assertThat(zeroWeightMonitorCount.get()).isEqualTo(0);
        assertThat(oneWeightMonitorCount.get()).isEqualTo(1);
    }

    @TestTemplate
    public void nodeSharing() {
        assumeBavet();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        entity1.setStringProperty("myProperty1");
        solution.getEntityList().add(entity1);

        AtomicLong monitorCount = new AtomicLong(0L);
        Predicate<TestdataLavishEntity> predicate = entity -> {
            monitorCount.getAndIncrement();
            return true;
        };
        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataLavishSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataLavishEntity.class)
                                .filter(predicate)
                                .penalize("myConstraint1", SimpleScore.ONE),
                        factory.forEach(TestdataLavishEntity.class)
                                .filter(predicate)
                                .penalize("myConstraint2", SimpleScore.ONE)
                });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(monitorCount.getAndSet(0L)).isEqualTo(3);

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity1);
        entity1.setStringProperty("myProperty2");
        scoreDirector.afterProblemPropertyChanged(entity1);
        scoreDirector.calculateScore();
        assertThat(monitorCount.get()).isEqualTo(1);
    }

    @TestTemplate
    public void reuseFilteredStream() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 2);
        TestdataLavishEntity entity1 = solution.getEntityList().get(0);

        Predicate<TestdataLavishEntity> predicate = entity -> Objects.equals(entity, entity1);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataLavishSolution.buildSolutionDescriptor(),
                factory -> {
                    UniConstraintStream<TestdataLavishEntity> base = factory.forEach(TestdataLavishEntity.class)
                            .filter(predicate);
                    return new Constraint[] {
                            base.penalize("myConstraint1", SimpleScore.ONE),
                            base.penalize("myConstraint2", SimpleScore.ONE)
                    };
                });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch("myConstraint1", entity1),
                assertMatch("myConstraint2", entity1));
    }

    @TestTemplate
    public void reuseGroupedStream() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 2);
        TestdataLavishEntity entity1 = solution.getEntityList().get(0);
        TestdataLavishEntityGroup entityGroup1 = solution.getFirstEntityGroup();
        entity1.setEntityGroup(entityGroup1);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntityGroup entityGroup2 = new TestdataLavishEntityGroup("Some Other Group");
        entity2.setEntityGroup(entityGroup2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataLavishSolution.buildSolutionDescriptor(),
                factory -> {
                    UniConstraintStream<TestdataLavishEntityGroup> base = factory.forEach(TestdataLavishEntity.class)
                            .groupBy(TestdataLavishEntity::getEntityGroup);
                    return new Constraint[] {
                            base.penalize("myConstraint1", SimpleScore.ONE),
                            base.filter(e -> !Objects.equals(e, entityGroup1))
                                    .penalize("myConstraint2", SimpleScore.ONE)
                    };
                });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch("myConstraint1", entityGroup1),
                assertMatch("myConstraint1", entityGroup2),
                assertMatch("myConstraint2", entityGroup2));
    }

    // ************************************************************************
    // from() (deprecated)
    // ************************************************************************

    @TestTemplate
    @Deprecated(forRemoval = true)
    public void fromIncludesNullWhenNullable() {
        TestdataNullableSolution solution = TestdataNullableSolution.generateSolution();
        TestdataNullableEntity entityWithNull = solution.getEntityList().get(0);
        TestdataNullableEntity entityWithValue = solution.getEntityList().get(1);

        InnerScoreDirector<TestdataNullableSolution, SimpleScore> scoreDirector = buildScoreDirector(
                TestdataNullableSolution.buildSolutionDescriptor(),
                constraintFactory -> new Constraint[] {
                        constraintFactory.from(TestdataNullableEntity.class)
                                .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE)
                });

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entityWithNull),
                assertMatch(entityWithValue));
    }

}
