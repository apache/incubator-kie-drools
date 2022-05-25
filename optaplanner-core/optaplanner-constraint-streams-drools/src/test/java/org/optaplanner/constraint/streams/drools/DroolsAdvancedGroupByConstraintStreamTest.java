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

package org.optaplanner.constraint.streams.drools;

import static org.assertj.core.api.Assertions.assertThatCode;
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

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.constraint.streams.common.AbstractAdvancedGroupByConstraintStreamTest;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntityGroup;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.util.Pair;

final class DroolsAdvancedGroupByConstraintStreamTest extends AbstractAdvancedGroupByConstraintStreamTest {

    public DroolsAdvancedGroupByConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new DroolsConstraintStreamImplSupport(constraintMatchEnabled));
    }

    @TestTemplate
    void collectedDowngradedAndFiltered() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 5, 1, 7);
        TestdataLavishEntity entity = new TestdataLavishEntity("MyEntity 1", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .groupBy(e -> e.getCode().substring(0, 1), count())
                        .groupBy(Pair::of)
                        .filter(pair -> !pair.getKey().equals("G"))
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, Pair::getValue));

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector, assertMatch(Pair.of("M", 1)));

        // Incremental
        scoreDirector.beforeEntityRemoved(entity);
        solution.getEntityList().remove(entity);
        scoreDirector.afterEntityRemoved(entity);
        assertScore(scoreDirector);
    }

    @TestTemplate
    void collectedAndFiltered() {
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
    void collectedFilteredRecollected() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
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
    void uniGroupByRecollected() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 2);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
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
    void biGroupByRecollected() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 5);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void triGroupByRecollected() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 6);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void quadGroupByRecollected() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 3, 2, 8);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void biGroupByRegrouped() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 4);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void triGroupByRegrouped() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 6);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void quadGroupByRegrouped() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 8);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void biGroupByRegroupedDouble() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 4);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void triGroupByRegroupedDouble() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 6);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void quadGroupByRegroupedDouble() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution(2, 2, 2, 8);

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getEntityGroup))
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
    void existsAfterGroupBy() {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
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
    void groupByAfterExists() {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
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

    @TestTemplate
    void groupByAfterExistsBi() {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class)
                    .ifExists(TestdataLavishEntityGroup.class,
                            equal((e1, e2) -> e1.getEntityGroup(), Function.identity()))
                    .groupBy((e1, e2) -> e1.getEntityGroup(), countBi())
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (groupA, count) -> count);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-3, solution.getFirstEntityGroup(), 3),
                assertMatchWithScore(-3, entityGroup1, 3));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entityGroup1);
        solution.getEntityGroupList().remove(entityGroup1);
        scoreDirector.afterProblemFactRemoved(entityGroup1);
        assertScore(scoreDirector,
                assertMatchWithScore(-3, solution.getFirstEntityGroup(), 3));
    }

    @TestTemplate
    void filteredFromUniquePair() {
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

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(factory -> {
            return factory.forEachUniquePair(TestdataLavishEntity.class,
                    Joiners.equal(TestdataLavishEntity::getEntityGroup),
                    Joiners.filtering((e1, e2) -> !e1.getCode().contains("My"))) // Filtering() caused PLANNER-2139.
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                assertMatchWithScore(-1, entity3, solution.getFirstEntity()));

        // Incremental
        scoreDirector.beforeProblemFactRemoved(entity3);
        solution.getEntityList().remove(entity3);
        scoreDirector.afterProblemFactRemoved(entity3);
        assertScore(scoreDirector);
    }

    @TestTemplate
    void groupByThenJoinThenGroupBy() { // PLANNER-2270
        assertThatCode(() -> buildScoreDirector(factory -> {
            return factory.forEach(TestdataLavishEntity.class)
                    .groupBy(TestdataLavishEntity::getEntityGroup, TestdataLavishEntity::getValue)
                    .join(TestdataLavishEntity.class)
                    .groupBy((group, value, entity) -> group,
                            (group, value, entity) -> entity,
                            sum((group, count, entity) -> 1))
                    .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE);
        })).doesNotThrowAnyException();
    }

}
