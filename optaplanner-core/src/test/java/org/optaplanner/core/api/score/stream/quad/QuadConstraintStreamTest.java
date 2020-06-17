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

package org.optaplanner.core.api.score.stream.quad;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countQuad;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.AbstractConstraintStreamTest;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
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

public class QuadConstraintStreamTest extends AbstractConstraintStreamTest {

    public QuadConstraintStreamTest(boolean constraintMatchEnabled, ConstraintStreamImplType constraintStreamImplType) {
        super(constraintMatchEnabled, constraintStreamImplType);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @TestTemplate
    public void filter_entity() {
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
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(), value1);
        solution.getEntityList().add(entity3);
        TestdataLavishExtra extra1 = new TestdataLavishExtra("MyExtra 1");
        solution.getExtraList().add(extra1);
        TestdataLavishExtra extra2 = new TestdataLavishExtra("MyExtra 2");
        solution.getExtraList().add(extra2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

    @TestTemplate
    public void filterConsecutive() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(5, 5);
        TestdataLavishEntity entity1 = solution.getEntityList().get(0);
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);
        TestdataLavishEntity entity3 = solution.getEntityList().get(2);
        TestdataLavishEntity entity4 = solution.getEntityList().get(3);
        TestdataLavishEntity entity5 = solution.getEntityList().get(4);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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
    // Join
    // ************************************************************************

    // TODO

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @TestTemplate
    public void ifExists_0Joiner0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void ifExists_0Join1Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getValueList().get(1));
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

    @TestTemplate
    public void ifNotExists_0Joiner0Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void ifNotExists_0Join1Filter() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getValueList().get(1));
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class)
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

    // ************************************************************************
    // Group by
    // ************************************************************************

    @TestTemplate
    public void groupBy_OMapping1Collector() {
        assumeDrools();
        /*
         * E1 has G1 and V1
         * E2 has G2 and V2
         * E3 has G1 and V1
         */
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void groupBy_1Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void groupBy_1Mapping1Collector_count() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void groupBy_2Mapping0Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void groupBy_2Mapping1Collector_count() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    @TestTemplate
    public void groupBy_2Mapping2Collector() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 2, 2, 3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
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

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @TestTemplate
    public void penalize_Int() {
        assumeDrools();
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSolution.buildSolutionDescriptor(), (factory) -> {
                            QuadConstraintStream<TestdataEntity, TestdataEntity, TestdataValue, TestdataValue> base = factory
                                    .fromUniquePair(TestdataEntity.class)
                                    .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                    .join(TestdataValue.class);
                            return new Constraint[] {
                                    base.penalize("myConstraint1", SimpleScore.ONE),
                                    base.penalize("myConstraint2", SimpleScore.ONE, (entity1, entity2, value, extra) -> 20)
                            };
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false,
                constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-21));
    }

    @TestTemplate
    public void penalize_Long() {
        assumeDrools();
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleLongScoreSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                        (factory) -> {
                            QuadConstraintStream<TestdataEntity, TestdataEntity, TestdataValue, TestdataValue> base = factory
                                    .fromUniquePair(TestdataEntity.class)
                                    .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                    .join(TestdataValue.class);
                            return new Constraint[] {
                                    base.penalize("myConstraint1", SimpleLongScore.ONE),
                                    base.penalizeLong("myConstraint2", SimpleLongScore.ONE,
                                            (entity1, entity2, value, extra) -> 20L)
                            };
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleLongScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(
                false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-21L));
    }

    @TestTemplate
    public void penalize_BigDecimal() {
        assumeDrools();
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleBigDecimalScoreSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), (factory) -> {
                            QuadConstraintStream<TestdataEntity, TestdataEntity, TestdataValue, TestdataValue> base = factory
                                    .fromUniquePair(TestdataEntity.class)
                                    .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                    .join(TestdataValue.class);
                            return new Constraint[] {
                                    base.penalize("myConstraint1", SimpleBigDecimalScore.ONE),
                                    base.penalizeBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                            (entity1, entity2, value, extra) -> new BigDecimal("0.2"))
                            };
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(
                false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-1.2")));
    }

    @TestTemplate
    public void penalize_negative() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 2);

        String constraintName = "myConstraint";
        ConstraintStreamScoreDirectorFactory<TestdataLavishSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataLavishSolution.buildSolutionDescriptor(),
                        (factory) -> new Constraint[] {
                                factory.fromUniquePair(TestdataLavishEntity.class)
                                        .join(TestdataLavishEntityGroup.class)
                                        .join(TestdataLavishValue.class)
                                        .penalize(constraintName, SimpleScore.ONE, (entityA, entityB, group, value) -> -1)
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false,
                constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

    @TestTemplate
    public void reward_Int() {
        assumeDrools();
        TestdataSolution solution = new TestdataSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
                                factory.fromUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .reward("myConstraint1", SimpleScore.ONE),
                                factory.fromUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .reward("myConstraint2", SimpleScore.ONE, (entity1, entity2, value, extra) -> 20)
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false,
                constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(21));
    }

    @TestTemplate
    public void reward_Long() {
        assumeDrools();
        TestdataSimpleLongScoreSolution solution = new TestdataSimpleLongScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleLongScoreSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSimpleLongScoreSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
                                factory.fromUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .reward("myConstraint1", SimpleLongScore.ONE),
                                factory.fromUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .rewardLong("myConstraint2", SimpleLongScore.ONE,
                                                (entity1, entity2, value, extra) -> 20L)
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleLongScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false,
                constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(21L));
    }

    @TestTemplate
    public void reward_BigDecimal() {
        assumeDrools();
        TestdataSimpleBigDecimalScoreSolution solution = new TestdataSimpleBigDecimalScoreSolution();
        TestdataValue v1 = new TestdataValue("v1");
        solution.setValueList(Arrays.asList(v1));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v1);
        solution.setEntityList(Arrays.asList(e1, e2));

        ConstraintStreamScoreDirectorFactory<TestdataSimpleBigDecimalScoreSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), (factory) -> new Constraint[] {
                                factory.fromUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .reward("myConstraint1", SimpleBigDecimalScore.ONE),
                                factory.fromUniquePair(TestdataEntity.class)
                                        .join(TestdataValue.class, equal((entity1, entity2) -> e1.getValue(), identity()))
                                        .join(TestdataValue.class)
                                        .rewardBigDecimal("myConstraint2", SimpleBigDecimalScore.ONE,
                                                (entity1, entity2, value, extra) -> new BigDecimal("0.2"))
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false,
                constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThat(scoreDirector.calculateScore())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("1.2")));
    }

    @TestTemplate
    public void reward_negative() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 2);

        String constraintName = "myConstraint";
        ConstraintStreamScoreDirectorFactory<TestdataLavishSolution> scoreDirectorFactory =
                new ConstraintStreamScoreDirectorFactory<>(
                        TestdataLavishSolution.buildSolutionDescriptor(),
                        (factory) -> new Constraint[] {
                                factory.fromUniquePair(TestdataLavishEntity.class)
                                        .join(TestdataLavishEntityGroup.class)
                                        .join(TestdataLavishValue.class)
                                        .reward(constraintName, SimpleScore.ONE, (entityA, entityB, group, value) -> -1)
                        }, constraintStreamImplType);
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false,
                constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertThatThrownBy(scoreDirector::calculateScore).hasMessageContaining(constraintName);
    }

    // ************************************************************************
    // Combinations
    // ************************************************************************

    @TestTemplate
    @Disabled("Not yet implemented") // TODO
    public void globalNodeOrder() {

    }

    @TestTemplate
    @Disabled("Not yet supported") // TODO
    public void nodeSharing() {

    }
}
