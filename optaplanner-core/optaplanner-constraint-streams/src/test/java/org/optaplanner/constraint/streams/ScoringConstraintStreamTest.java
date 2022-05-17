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

package org.optaplanner.constraint.streams;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.api.score.stream.Joiners.equal;

import java.math.BigDecimal;

import org.junit.jupiter.api.TestTemplate;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;

class ScoringConstraintStreamTest extends AbstractConstraintStreamTest {

    public ScoringConstraintStreamTest(boolean constraintMatchEnabled, ConstraintStreamImplType constraintStreamImplType) {
        super(constraintMatchEnabled, constraintStreamImplType);
    }

    @TestTemplate
    void penalizeUniUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-7));
    }

    @TestTemplate
    void penalizeUni() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, entity -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-14));
    }

    @TestTemplate
    void penalizeUniLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .penalizeLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, entity -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-14));
    }

    @TestTemplate
    void penalizeUniBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] { factory.forEach(TestdataEntity.class)
                                .penalizeBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                        entity -> BigDecimal.valueOf(2)) });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-14)));
    }

    @TestTemplate
    void rewardUniUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(7));
    }

    @TestTemplate
    void rewardUni() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE, entity -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(14));
    }

    @TestTemplate
    void rewardUniLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .rewardLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, entity -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(14));
    }

    @TestTemplate
    void rewardUniBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .rewardBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                entity -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(14)));
    }

    @TestTemplate
    void impactPositiveUniUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(7));
    }

    @TestTemplate
    void impactPositiveUni() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, entity -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(14));
    }

    @TestTemplate
    void impactPositiveUniLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, entity -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(14));
    }

    @TestTemplate
    void impactPositiveUniBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                entity -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(14)));
    }

    @TestTemplate
    void impactNegativeUni() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEach(TestdataLavishEntity.class)
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, entity -> -2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-14));
    }

    @TestTemplate
    void impactNegativeUniLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEach(TestdataEntity.class)
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, entity -> -2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-14));
    }

    @TestTemplate
    void impactNegativeUniBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEach(TestdataEntity.class)
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                entity -> BigDecimal.valueOf(-2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-14)));
    }

    @TestTemplate
    void penalizeBiUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-21));
    }

    @TestTemplate
    void penalizeBi() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-42));
    }

    @TestTemplate
    void penalizeBiLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class)
                                .penalizeLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-42));
    }

    @TestTemplate
    void penalizeBiBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class)
                                        .penalizeBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-42)));
    }

    @TestTemplate
    void rewardBiUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(21));
    }

    @TestTemplate
    void rewardBi() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(42));
    }

    @TestTemplate
    void rewardBiLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class)
                                .rewardLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(42));
    }

    @TestTemplate
    void rewardBiBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class)
                                        .rewardBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(42)));
    }

    @TestTemplate
    void impactPositiveBiUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(21));
    }

    @TestTemplate
    void impactPositiveBi() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(42));
    }

    @TestTemplate
    void impactPositiveBiLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class)
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(42));
    }

    @TestTemplate
    void impactPositiveBiBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class)
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(42)));
    }

    @TestTemplate
    void impactNegativeBi() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class)
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2) -> -2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-42));
    }

    @TestTemplate
    void impactNegativeBiLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class)
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2) -> -2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-42));
    }

    @TestTemplate
    void impactNegativeBiBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class)
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2) -> BigDecimal.valueOf(-2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-42)));
    }

    @TestTemplate
    void penalizeTriUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-2));
    }

    @TestTemplate
    void penalizeTri() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
    }

    @TestTemplate
    void penalizeTriLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .penalizeLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
    }

    @TestTemplate
    void penalizeTriBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .penalizeBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
    }

    @TestTemplate
    void rewardTriUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
    }

    @TestTemplate
    void rewardTri() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
    }

    @TestTemplate
    void rewardTriLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .rewardLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
    }

    @TestTemplate
    void rewardTriBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .rewardBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
    }

    @TestTemplate
    void impactPositiveTriUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
    }

    @TestTemplate
    void impactPositiveTri() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
    }

    @TestTemplate
    void impactPositiveTriLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
    }

    @TestTemplate
    void impactPositiveTriBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
    }

    @TestTemplate
    void impactNegativeTri() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value) -> -2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
    }

    @TestTemplate
    void impactNegativeTriLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value) -> -2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
    }

    @TestTemplate
    void impactNegativeTriBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value) -> BigDecimal.valueOf(-2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
    }

    @TestTemplate
    void penalizeQuadUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-2));
    }

    @TestTemplate
    void penalizeQuad() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .penalize(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value, value2) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
    }

    @TestTemplate
    void penalizeQuadLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                .penalizeLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value, value2) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
    }

    @TestTemplate
    void penalizeQuadBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                        .penalizeBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value, value2) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
    }

    @TestTemplate
    void rewardQuadUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
    }

    @TestTemplate
    void rewardQuad() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .reward(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value, value2) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
    }

    @TestTemplate
    void rewardQuadLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                .rewardLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value, value2) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
    }

    @TestTemplate
    void rewardQuadBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                        .rewardBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value, value2) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
    }

    @TestTemplate
    void impactPositiveQuadUnweighted() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(2));
    }

    @TestTemplate
    void impactPositiveQuad() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value, value2) -> 2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(4));
    }

    @TestTemplate
    void impactPositiveQuadLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value, value2) -> 2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(4));
    }

    @TestTemplate
    void impactPositiveQuadBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value, value2) -> BigDecimal.valueOf(2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(4)));
    }

    @TestTemplate
    void impactNegativeQuad() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();

        InnerScoreDirector<TestdataLavishSolution, SimpleScore> scoreDirector = buildScoreDirector(
                factory -> factory.forEachUniquePair(TestdataLavishEntity.class, equal(TestdataLavishEntity::getValue))
                        .join(TestdataLavishValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                        .join(TestdataLavishValue.class, equal((entity, entity2, value) -> value, identity()))
                        .impact(TEST_CONSTRAINT_NAME, SimpleScore.ONE, (entity, entity2, value, value2) -> -2));

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleScore.of(-4));
    }

    @TestTemplate
    void impactNegativeQuadLong() {
        TestdataSimpleLongScoreSolution solution = TestdataSimpleLongScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleLongScoreSolution, SimpleLongScore> scoreDirector = buildScoreDirector(
                TestdataSimpleLongScoreSolution.buildSolutionDescriptor(),
                factory -> new Constraint[] {
                        factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                .impactLong(TEST_CONSTRAINT_NAME, SimpleLongScore.ONE, (entity, entity2, value, value2) -> -2L)
                });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleLongScore.of(-4));
    }

    @TestTemplate
    void impactNegativeQuadBigDecimal() {
        TestdataSimpleBigDecimalScoreSolution solution = TestdataSimpleBigDecimalScoreSolution.generateSolution();

        InnerScoreDirector<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> scoreDirector =
                buildScoreDirector(TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor(),
                        factory -> new Constraint[] {
                                factory.forEachUniquePair(TestdataEntity.class, equal(TestdataEntity::getValue))
                                        .join(TestdataValue.class, equal((entity, entity2) -> entity.getValue(), identity()))
                                        .join(TestdataValue.class, equal((entity, entity2, value) -> value, identity()))
                                        .impactBigDecimal(TEST_CONSTRAINT_NAME, SimpleBigDecimalScore.ONE,
                                                (entity, entity2, value, value2) -> BigDecimal.valueOf(-2))
                        });

        scoreDirector.setWorkingSolution(solution);
        scoreDirector.calculateScore();
        assertThat(scoreDirector.calculateScore()).isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(-4)));
    }

}
