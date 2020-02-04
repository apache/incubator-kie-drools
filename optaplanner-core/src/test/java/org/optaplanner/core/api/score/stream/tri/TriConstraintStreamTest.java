/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.tri;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.AbstractConstraintStreamTest;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
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

import static org.junit.Assert.assertEquals;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countTri;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

public class TriConstraintStreamTest extends AbstractConstraintStreamTest {

    public TriConstraintStreamTest(boolean constraintMatchEnabled, ConstraintStreamImplType constraintStreamImplType) {
        super(constraintMatchEnabled, constraintStreamImplType);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal(TestdataLavishEntity::getValue, Function.identity()))
                    .join(TestdataLavishExtra.class)
                    .filter((entity, value, extra) -> value.getCode().equals("MyValue 1")
                            && extra.getCode().equals("MyExtra 1"))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
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

    // ************************************************************************
    // Join
    // ************************************************************************

    @Test
    public void join_0() {
        assumeDrools();
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
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((a, b) -> Stream.of(entity1, entity2, entity3)
                            .filter(entity -> !Objects.equals(entity, a))
                            .filter(entity -> !Objects.equals(entity, b))
                            .map(TestdataObject::getCode)
                            .findFirst()
                            .orElseThrow(IllegalStateException::new), TestdataObject::getCode))
                    .join(TestdataLavishExtra.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
    public void join_1Equal() {
        assumeDrools();
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
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class, equal((a, b) -> Stream.of(entity1, entity2, entity3)
                            .filter(entity -> !Objects.equals(entity, a))
                            .filter(entity -> !Objects.equals(entity, b))
                            .map(TestdataObject::getCode)
                            .findFirst()
                            .orElseThrow(IllegalStateException::new), TestdataObject::getCode))
                    .join(TestdataLavishExtra.class, equal((e1, e2, e3) -> e1.getStringProperty(), TestdataLavishExtra::getStringProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
    public void join_2Equal() {
        assumeDrools();
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishValue.class, equal((e1, e2) -> e1.getValue(), Function.identity()))
                    .join(TestdataLavishExtra.class,
                            equal((e1, e2, value) -> e1.getStringProperty(), TestdataLavishExtra::getStringProperty),
                            equal((e1, e2, value) -> e1.getIntegerProperty(), TestdataLavishExtra::getIntegerProperty))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @Test
    public void ifExists_0Joiner0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishValueGroup.class)
                    .join(TestdataLavishEntityGroup.class)
                    .ifExists(TestdataLavishEntity.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                    !Objects.equals(entityC, entityB)))
                    .ifExists(TestdataLavishEntityGroup.class,
                            filtering((entityA, entityB, entityC, group) ->
                                    Objects.equals(group, entityA.getEntityGroup()) &&
                                            Objects.equals(group, entityB.getEntityGroup())))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                    !Objects.equals(entityC, entityB)))
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), Function.identity()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                    !Objects.equals(entityC, entityB)))
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), Function.identity()),
                            filtering((entityA, entityB, entityC, group) -> entityA.getCode().contains("MyEntity") ||
                                    group.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
    public void ifNotExists_0Joiner0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishValueGroup.class)
                    .join(TestdataLavishEntityGroup.class)
                    .ifNotExists(TestdataLavishEntity.class)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                    !Objects.equals(entityC, entityB)))
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            filtering((entityA, entityB, entityC, group) ->
                                    Objects.equals(group, entityA.getEntityGroup()) &&
                                            Objects.equals(group, entityB.getEntityGroup())))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                    !Objects.equals(entityC, entityB)))
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), Function.identity()))
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
                assertMatch(entity2, entity1, solution.getFirstEntity()));
    }

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            filtering((entityA, entityB, entityC) -> !Objects.equals(entityC, entityA) &&
                                    !Objects.equals(entityC, entityB)))
                    .ifNotExists(TestdataLavishEntityGroup.class,
                            equal((entityA, entityB, entityC) -> entityA.getEntityGroup(), Function.identity()),
                            filtering((entityA, entityB, entityC, group) -> entityA.getCode().contains("MyEntity") ||
                                    group.getCode().contains("MyEntity")))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

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

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Test
    public void groupBy_OMapping1Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), Function.identity()))
                    .groupBy(countTri())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, count -> count);
        });

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

    @Test
    public void groupBy_1Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), Function.identity()))
                    .groupBy((entity, group, value) -> value)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        TestdataLavishValue value1 = solution.getFirstValue();
        TestdataLavishValue value2 = solution.getValueList().get(1);

        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, value2),
                assertMatchWithScore(-1, value1));
    }

    @Test
    public void groupBy_1Mapping1Collector_count() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), Function.identity()))
                    .groupBy((entity, group, value) -> value, countTri())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, count) -> count);
        });

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

    @Test
    public void groupBy_2Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), Function.identity()))
                    .groupBy((entity, group, value) -> group, (entity, group, value) -> value)
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

    @Test
    public void groupBy_2Mapping1Collector_count() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), Function.identity()))
                    .groupBy((entity, group, value) -> group, (entity, group, value) -> value, countTri())
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

    @Test
    public void groupBy_2Mapping2Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntityGroup.class, equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .join(TestdataLavishValue.class, equal((entity, group) -> entity.getValue(), Function.identity()))
                    .groupBy((entity, group, value) -> group, (entity, group, value) -> value, countTri(), countTri())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE,
                            (group, value, count, sameCount) -> count + sameCount);
        });

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

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Test
    public void penalize_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .penalize("myConstraint1", SimpleScore.ONE),
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .penalize("myConstraint2", SimpleScore.ONE, (entity, value, extra) -> 20)
        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleScore.of(-42), scoreDirector.calculateScore());
    }

    @Test
    public void penalize_Long() {
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleLongScoreSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .penalize("myConstraint1", SimpleLongScore.ONE),
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .penalizeLong("myConstraint2", SimpleLongScore.ONE, (entity, value, extra) -> 20L)
        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleLongScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleLongScore.of(-42L), scoreDirector.calculateScore());
    }

    @Test
    public void penalize_BigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleBigDecimalScoreSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .penalize("myConstraint1", SimpleBigDecimalScore.ONE),
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .penalizeBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE, (entity, value, extra) -> new BigDecimal("0.2"))
        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("-2.4")), scoreDirector.calculateScore());
    }

    @Test
    public void reward_Int() {
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .reward("myConstraint1", SimpleScore.ONE),
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .reward("myConstraint2", SimpleScore.ONE, (entity, value, extra) -> 20)
        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleScore.of(42), scoreDirector.calculateScore());
    }

    @Test
    public void reward_Long() {
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleLongScoreSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .reward("myConstraint1", SimpleLongScore.ONE),
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .rewardLong("myConstraint2", SimpleLongScore.ONE, (entity, value, extra) -> 20L)
        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleLongScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleLongScore.of(42L), scoreDirector.calculateScore());
    }

    @Test
    public void reward_BigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleBigDecimalScoreSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .reward("myConstraint1", SimpleBigDecimalScore.ONE),
            factory.from(TestdataEntity.class)
                    .join(TestdataValue.class, equal(TestdataEntity::getValue, Function.identity()))
                    .join(TestdataValue.class)
                    .rewardBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE, (entity, value, extra) -> new BigDecimal("0.2"))
        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("2.4")), scoreDirector.calculateScore());
    }

    // ************************************************************************
    // Combinations
    // ************************************************************************


    @Test @Ignore("Not yet implemented") // TODO
    public void globalNodeOrder() {

    }

    @Test @Ignore("Not yet supported") // TODO
    public void nodeSharing() {

    }

}
