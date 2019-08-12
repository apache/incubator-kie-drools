/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.uni;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.AbstractConstraintStreamTest;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValueGroup;

import static org.junit.Assert.*;
import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.common.Joiners.*;

public class UniConstraintStreamTest extends AbstractConstraintStreamTest {

    public UniConstraintStreamTest(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Test
    public void filter_problemFact() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishValueGroup valueGroup1 = new TestdataLavishValueGroup("MyValueGroup 1");
        solution.getValueGroupList().add(valueGroup1);
        TestdataLavishValueGroup valueGroup2 = new TestdataLavishValueGroup("MyValueGroup 2");
        solution.getValueGroupList().add(valueGroup2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishValueGroup.class)
                    .filter(valueGroup -> valueGroup.getCode().startsWith("MyValueGroup"))
                    .penalize();
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

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .filter(entity -> entity.getEntityGroup() == entityGroup)
                    .penalize();
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatch(entity1),
                assertMatch(entity2),
                assertMatch(entity3));
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Test
    public void join_0() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("MyValueGroup");
        solution.getValueGroupList().add(valueGroup);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);


        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishValueGroup.class)
                    .join(TestdataLavishEntityGroup.class)
                    .penalize();
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

    @Test
    public void join_1Equal() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            equal(TestdataLavishEntity::getEntityGroup))
                    .penalize();
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), solution.getFirstEntity()),
                assertMatch(solution.getFirstEntity(), entity2),
                assertMatch(entity1, entity1),
                assertMatch(entity2, solution.getFirstEntity()),
                assertMatch(entity2, entity2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity2);
        entity2.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity2);
        assertScore(scoreDirector,
                assertMatch(solution.getFirstEntity(), solution.getFirstEntity()),
                assertMatch(entity1, entity1),
                assertMatch(entity1, entity2),
                assertMatch(entity2, entity1),
                assertMatch(entity2, entity2));
    }

    @Test
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            equal(TestdataLavishEntity::getEntityGroup),
                            equal(TestdataLavishEntity::getIntegerProperty))
                    .penalize();
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

    @Test
    public void fromUniquePair_0() {
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.fromUniquePair(TestdataLavishEntity.class)
                    .penalize();
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatch(entityA, entityB),
                assertMatch(entityA, entityC),
                assertMatch(entityB, entityC));
    }

    @Test
    public void fromUniquePair_1Equals() {
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.fromUniquePair(TestdataLavishEntity.class,
                    equal(TestdataLavishEntity::getIntegerProperty)
            ).penalize();
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

    @Test @Ignore("TODO implement it") // TODO
    public void groupBy_1Mapping0Collector() {

    }

    @Test
    public void groupBy_1Mapping1Collector_count() {
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, count())
                    .penalize((entityGroup, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-8, solution.getFirstEntityGroup(), 8),
                assertMatchWithScore(-2, entityGroup1, 2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(entityGroup1);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-7, solution.getFirstEntityGroup(), 7),
                assertMatchWithScore(-3, entityGroup1, 3));
    }

    @Test
    public void groupBy_1Mapping1Collector_countDistinct() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, solution.getFirstValue());
        entity1.setStringProperty("A");
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        entity2.setStringProperty("A");
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", entityGroup1,
                solution.getFirstValue());
        entity3.setStringProperty("B");
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, countDistinct(TestdataLavishEntity::getStringProperty))
                    .penalize((entityGroup, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1),
                assertMatchWithScore(-2, entityGroup1, 2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setStringProperty("A");
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1),
                assertMatchWithScore(-1, entityGroup1, 1));

        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(solution.getFirstEntityGroup());
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, solution.getFirstEntityGroup(), 2),
                assertMatchWithScore(-1, entityGroup1, 1));
    }

    @Test @Ignore("TODO implement it") // TODO
    public void groupBy_2Mapping0Collector() {

    }

    @Test @Ignore("TODO implement it") // TODO
    public void groupBy_2Mapping1Collector_count() {
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue, count())
                    .penalize((entityGroup, value, count) -> count);
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
                TestdataSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleScore.of(1))
                    .from(TestdataEntity.class)
                    .penalize();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleScore.of(1))
                    .from(TestdataEntity.class)
                    .penalize(entity -> 20);
        });
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
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleLongScore.of(1))
                    .from(TestdataEntity.class)
                    .penalize();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleLongScore.of(1))
                    .from(TestdataEntity.class)
                    .penalizeLong(entity -> 20L);
        });
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
                TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleBigDecimalScore.of(BigDecimal.ONE))
                    .from(TestdataEntity.class)
                    .penalize();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleBigDecimalScore.of(BigDecimal.ONE))
                    .from(TestdataEntity.class)
                    .penalizeBigDecimal(entity -> new BigDecimal("0.2"));
        });
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
                TestdataSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleScore.of(1))
                    .from(TestdataEntity.class)
                    .reward();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleScore.of(1))
                    .from(TestdataEntity.class)
                    .reward(entity -> 20);
        });
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
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleLongScore.of(1))
                    .from(TestdataEntity.class)
                    .reward();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleLongScore.of(1))
                    .from(TestdataEntity.class)
                    .rewardLong(entity -> 20L);
        });
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
                TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleBigDecimalScore.of(BigDecimal.ONE))
                    .from(TestdataEntity.class)
                    .reward();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleBigDecimalScore.of(BigDecimal.ONE))
                    .from(TestdataEntity.class)
                    .rewardBigDecimal(entity -> new BigDecimal("0.2"));
        });
        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("2.4")), scoreDirector.calculateScore());
    }

    // ************************************************************************
    // Combinations
    // ************************************************************************

    @Test
    public void globalNodeOrder() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        entity1.setStringProperty("MyString1");
        solution.getEntityList().add(entity1);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector1 = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .filter(entity -> entity.getEntityGroup() == entityGroup)
                    .filter(entity -> entity.getStringProperty().equals("MyString1"))
                    .join(TestdataLavishEntity.class, equal(TestdataLavishEntity::getIntegerProperty))
                    .penalize();
        });

        // From scratch
        scoreDirector1.setWorkingSolution(solution);
        assertScore(scoreDirector1,
                assertMatch(entity1, solution.getFirstEntity()),
                assertMatch(entity1, entity1));

        InnerScoreDirector<TestdataLavishSolution> scoreDirector2 = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .join(constraint.from(TestdataLavishEntity.class)
                            .filter(entity -> entity.getEntityGroup() == entityGroup)
                            .filter(entity -> entity.getStringProperty().equals("MyString1")),
                            equal(TestdataLavishEntity::getIntegerProperty))
                    .penalize();
        });

        // From scratch
        scoreDirector2.setWorkingSolution(solution);
        assertScore(scoreDirector2,
                assertMatch(solution.getFirstEntity(), entity1),
                assertMatch(entity1, entity1));
    }

    @Test
    public void nodeSharing() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 3, 2);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(), solution.getFirstValue());
        entity1.setStringProperty("myProperty1");
        solution.getEntityList().add(entity1);

        AtomicLong monitorCount = new AtomicLong(0L);
        Predicate<TestdataLavishEntity> predicate = entity -> {
            monitorCount.getAndIncrement(); return true;
        };
        ConstraintStreamScoreDirectorFactory<TestdataLavishSolution> scoreDirectorFactory
                = new ConstraintStreamScoreDirectorFactory<>(
                TestdataLavishSolution.buildSolutionDescriptor(), (constraintFactory) -> {
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint1", SimpleScore.of(1))
                    .from(TestdataLavishEntity.class)
                    .filter(predicate)
                    .penalize();
            constraintFactory.newConstraintWithWeight("myPackage", "myConstraint2", SimpleScore.of(1))
                    .from(TestdataLavishEntity.class)
                    .filter(predicate)
                    .penalize();
        });
        InnerScoreDirector<TestdataLavishSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, constraintMatchEnabled);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertEquals(3, monitorCount.getAndSet(0L));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity1);
        entity1.setStringProperty("myProperty2");
        scoreDirector.afterProblemPropertyChanged(entity1);
        scoreDirector.calculateScore();
        assertEquals(1, monitorCount.get());
    }

}
