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

package org.optaplanner.core.api.score.stream.tri;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;

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
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishEntity;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishExtra;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishSolution;
import org.optaplanner.core.impl.testdata.domain.score.lavish.TestdataLavishValue;

import static org.junit.Assert.assertEquals;
import static org.optaplanner.core.api.score.stream.Joiners.equal;

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

    // TODO

    // ************************************************************************
    // Group by
    // ************************************************************************

    // TODO

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
