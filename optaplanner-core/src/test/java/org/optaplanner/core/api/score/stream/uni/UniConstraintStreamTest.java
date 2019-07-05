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

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.score.stream.AbstractConstraintStreamTest;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishEntity;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishEntityGroup;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishSolution;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishValueGroup;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

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
    public void join_1Equal() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 1);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .join(TestdataLavishEntity.class,
                            equalTo(TestdataLavishEntity::getEntityGroup))
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
                            equalTo(TestdataLavishEntity::getEntityGroup),
                            equalTo(TestdataLavishEntity::getIntegerProperty))
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

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Test @Ignore("TODO implement it") // TODO
    public void groupBy_1Mapping0Collector() {

    }

    @Test
    public void groupBy_1Mapping1Collector_count() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, count())
                    .penalizeInt((g, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-8, solution.getFirstEntityGroup(), 8),
                assertMatchWithScore(-2, entityGroup, 2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-7, solution.getFirstEntityGroup(), 7),
                assertMatchWithScore(-3, entityGroup, 3));
    }

    @Test
    public void groupBy_1Mapping1Collector_countDistinct() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        entity1.setStringProperty("A");
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup, solution.getFirstValue());
        entity2.setStringProperty("A");
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", entityGroup,
                solution.getFirstValue());
        entity3.setStringProperty("B");
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, countDistinct(TestdataLavishEntity::getStringProperty))
                    .penalizeInt((g, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1),
                assertMatchWithScore(-2, entityGroup, 2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setStringProperty("A");
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, solution.getFirstEntityGroup(), 1),
                assertMatchWithScore(-1, entityGroup, 1));

        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(solution.getFirstEntityGroup());
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, solution.getFirstEntityGroup(), 2),
                assertMatchWithScore(-1, entityGroup, 1));
    }

    @Test @Ignore("TODO implement it") // TODO
    public void groupBy_2Mapping0Collector() {

    }

    @Test @Ignore("TODO implement it") // TODO
    public void groupBy_2Mapping1Collector() {

    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    // TODO

}
