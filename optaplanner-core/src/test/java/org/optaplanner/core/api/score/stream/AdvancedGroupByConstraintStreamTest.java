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

package org.optaplanner.core.api.score.stream;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countBi;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countQuad;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countTri;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toMap;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toSet;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.asMap;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.asSet;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;

public class AdvancedGroupByConstraintStreamTest extends AbstractConstraintStreamTest {

    public AdvancedGroupByConstraintStreamTest(boolean constraintMatchEnabled,
            ConstraintStreamImplType constraintStreamImplType) {
        super(constraintMatchEnabled, constraintStreamImplType);
    }

    @TestTemplate
    public void collectedDowngradedAndFiltered() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntity entity = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector(
                (factory) -> factory.from(TestdataLavishEntity.class)
                        .groupBy(e -> e.getCode().substring(0, 1), count())
                        .groupBy(ImmutablePair::new)
                        .filter(pair -> !pair.left.equals("G"))
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, ImmutablePair::getRight));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatch(ImmutablePair.of("M", 1)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @TestTemplate
    public void collectedAndFiltered() {
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

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .groupBy(count())
                    .filter(count -> count == 10)
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, i -> i);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-10, 10));

        // Incremental
        Stream.of(entity1, entity2).forEach(entity -> {
            scoreDirector.beforeEntityRemoved(entity);
            solution.getEntityList().remove(entity);
            scoreDirector.afterEntityRemoved(entity);
        });
        assertScore(scoreDirector); // There is less than 10 entities, and therefore there are no penalties.
    }

    @TestTemplate
    public void collectedFilteredRecollected() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .groupBy(toSet())
                    .groupBy(sum(Set::size))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, count -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatchWithScore(-2, 2));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, 1));
    }

    @TestTemplate
    public void uniGroupByRecollected() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup)
                    .groupBy(toSet())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, Set::size);
        });

        TestdataLavishEntity entity1 = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, asSet(entity1.getEntityGroup(), entity2.getEntityGroup())));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity1);
        solution.getEntityList().remove(entity1);
        scoreDirector.afterEntityRemoved(entity1);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, asSet(entity2.getEntityGroup())));
    }

    @TestTemplate
    public void biGroupByRecollected() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 5);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    // Stream of all unique entity bi tuples that share a group
                    .groupBy((a, b) -> a.getEntityGroup(), countBi())
                    .groupBy(toMap((g, c) -> g, (g, c) -> c, Integer::sum))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1,
                        asMap(solution.getFirstEntityGroup(), 3, solution.getEntityGroupList().get(1), 1)));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1,
                        asMap(solution.getFirstEntityGroup(), 1, solution.getEntityGroupList().get(1), 1)));
    }

    @TestTemplate
    public void triGroupByRecollected() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 6);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .join(TestdataLavishEntity.class,
                            equal((a, b) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c) -> !Objects.equals(a, c) && !Objects.equals(b, c)))
                    // Stream of all unique entity tri tuples that share a group
                    .groupBy((a, b, c) -> a.getEntityGroup(), countTri())
                    .groupBy(toMap((g, c) -> g, (g, c) -> c, Integer::sum))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1,
                        asMap(solution.getFirstEntityGroup(), 3, solution.getEntityGroupList().get(1), 3)));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1,
                        asMap(solution.getEntityGroupList().get(1), 3)));
    }

    @TestTemplate
    public void quadGroupByRecollected() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 8);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .join(TestdataLavishEntity.class,
                            equal((a, b) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c) -> !Objects.equals(a, c) && !Objects.equals(b, c)))
                    .join(TestdataLavishEntity.class,
                            equal((a, b, c) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c, d) -> !Objects.equals(a, d) && !Objects.equals(b, d) && !Objects.equals(c, d)))
                    // Stream of all unique entity quad tuples that share a group
                    .groupBy((a, b, c, d) -> a.getEntityGroup(), countQuad())
                    .groupBy(toMap((g, c) -> g, (g, c) -> c, Integer::sum))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1,
                        asMap(solution.getFirstEntityGroup(), 12, solution.getEntityGroupList().get(1), 12)));

        // Incremental
        TestdataLavishEntity entity = solution.getFirstEntity();
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector,
                assertMatchWithScore(-1,
                        asMap(solution.getEntityGroupList().get(1), 12)));
    }

    @TestTemplate
    public void biGroupByRegrouped() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 4);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .groupBy((a, b) -> a.getEntityGroup())
                    .groupBy(Function.identity(), count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, count) -> count);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entity.getEntityGroup(), 1),
                assertMatchWithScore(-1, entity2.getEntityGroup(), 1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, entity2.getEntityGroup(), 1));
    }

    @TestTemplate
    public void triGroupByRegrouped() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 6);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .join(TestdataLavishEntity.class,
                            equal((a, b) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c) -> !Objects.equals(a, c) && !Objects.equals(b, c)))
                    // Stream of all unique entity tri tuples that share a group
                    .groupBy((a, b, c) -> a.getEntityGroup())
                    .groupBy(Function.identity(), count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, count) -> count);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entity.getEntityGroup(), 1),
                assertMatchWithScore(-1, entity2.getEntityGroup(), 1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, entity2.getEntityGroup(), 1));
    }

    @TestTemplate
    public void quadGroupByRegrouped() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 8);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .join(TestdataLavishEntity.class,
                            equal((a, b) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c) -> !Objects.equals(a, c) && !Objects.equals(b, c)))
                    .join(TestdataLavishEntity.class,
                            equal((a, b, c) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c, d) -> !Objects.equals(a, d) && !Objects.equals(b, d) && !Objects.equals(c, d)))
                    // Stream of all unique entity quad tuples that share a group
                    .groupBy((a, b, c, d) -> a.getEntityGroup())
                    .groupBy(Function.identity(), count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (group, count) -> count);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entity.getEntityGroup(), 1),
                assertMatchWithScore(-1, entity2.getEntityGroup(), 1));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, entity2.getEntityGroup(), 1));
    }

    @TestTemplate
    public void biGroupByRegroupedDouble() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 4);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .groupBy((a, b) -> a.getEntityGroup())
                    .groupBy(Function.identity(), count())
                    .groupBy((group, count) -> group.toString(), countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (groupName, count) -> count);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, new Object[] { entity.getEntityGroup().toString(), 1 }),
                assertMatchWithScore(-1, new Object[] { entity2.getEntityGroup().toString(), 1 }));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, new Object[] { entity2.getEntityGroup().toString(), 1 }));
    }

    @TestTemplate
    public void triGroupByRegroupedDouble() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 6);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .join(TestdataLavishEntity.class,
                            equal((a, b) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c) -> !Objects.equals(a, c) && !Objects.equals(b, c)))
                    // Stream of all unique entity tri tuples that share a group
                    .groupBy((a, b, c) -> a.getEntityGroup())
                    .groupBy(Function.identity(), count())
                    .groupBy((group, count) -> group.toString(), countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (groupName, count) -> count);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, new Object[] { entity.getEntityGroup().toString(), 1 }),
                assertMatchWithScore(-1, new Object[] { entity2.getEntityGroup().toString(), 1 }));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, new Object[] { entity2.getEntityGroup().toString(), 1 }));
    }

    @TestTemplate
    public void quadGroupByRegroupedDouble() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 8);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.fromUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
                    .join(TestdataLavishEntity.class,
                            equal((a, b) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c) -> !Objects.equals(a, c) && !Objects.equals(b, c)))
                    .join(TestdataLavishEntity.class,
                            equal((a, b, c) -> a.getEntityGroup(), TestdataLavishEntity::getEntityGroup),
                            filtering((a, b, c, d) -> !Objects.equals(a, d) && !Objects.equals(b, d) && !Objects.equals(c, d)))
                    // Stream of all unique entity quad tuples that share a group
                    .groupBy((a, b, c, d) -> a.getEntityGroup())
                    .groupBy(Function.identity(), count())
                    .groupBy((group, count) -> group.toString(), countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (groupName, count) -> count);
        });

        TestdataLavishEntity entity = solution.getFirstEntity();
        TestdataLavishEntity entity2 = solution.getEntityList().get(1);

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, new Object[] { entity.getEntityGroup().toString(), 1 }),
                assertMatchWithScore(-1, new Object[] { entity2.getEntityGroup().toString(), 1 }));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector, assertMatchWithScore(-1, new Object[] { entity2.getEntityGroup().toString(), 1 }));
    }

    @TestTemplate
    public void existsAfterGroupBy() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, count())
                    .ifExists(TestdataLavishEntityGroup.class, equal((groupA, count) -> groupA, Function.identity()))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (groupA, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, solution.getFirstEntityGroup(), 2),
                assertMatchWithScore(-2, entityGroup1, 2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup1);
        solution.getEntityGroupList().remove(entityGroup1);
        scoreDirector.afterProblemFactRemoved(entityGroup1);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, solution.getFirstEntityGroup(), 2));
    }

    @TestTemplate
    public void groupByAfterExists() {
        assumeDrools();
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(1, 1, 1, 1);
        TestdataLavishEntityGroup entityGroup1 = new TestdataLavishEntityGroup("MyEntityGroup");
        solution.getEntityGroupList().add(entityGroup1);
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup1, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((factory) -> {
            return factory.from(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal(TestdataLavishEntity::getEntityGroup, Function.identity()))
                    .groupBy(TestdataLavishEntity::getEntityGroup, count())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (groupA, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, solution.getFirstEntityGroup(), 2),
                assertMatchWithScore(-2, entityGroup1, 2));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup1);
        solution.getEntityGroupList().remove(entityGroup1);
        scoreDirector.afterProblemFactRemoved(entityGroup1);
        assertScore(scoreDirector,
                assertMatchWithScore(-2, solution.getFirstEntityGroup(), 2));
    }
}
