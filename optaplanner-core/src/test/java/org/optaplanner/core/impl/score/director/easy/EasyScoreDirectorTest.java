/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.easy;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.shadow.corrupted.TestdataCorruptedShadowedEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.corrupted.TestdataCorruptedShadowedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EasyScoreDirectorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constraintMatchTotalsUnsupported() {
        EasyScoreDirector<Object> director
                = new EasyScoreDirector<>(mockEasyScoreDirectorFactory(), false, true, null);
        assertFalse(director.isConstraintMatchEnabled());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("not supported");
        director.getConstraintMatchTotals();
    }

    @SuppressWarnings("unchecked")
    private EasyScoreDirectorFactory<Object> mockEasyScoreDirectorFactory() {
        EasyScoreDirectorFactory<Object> factory = mock(EasyScoreDirectorFactory.class);
        when(factory.getSolutionDescriptor()).thenReturn(mock(SolutionDescriptor.class));
        return factory;
    }

    @Test
    public void shadowVariableCorruption() {
        EasyScoreDirectorFactory<TestdataCorruptedShadowedSolution> scoreDirectorFactory =
                new EasyScoreDirectorFactory<>(TestdataCorruptedShadowedSolution.buildSolutionDescriptor(),
                        (EasyScoreCalculator<TestdataCorruptedShadowedSolution>) (solution_) -> SimpleScore.of(0));
        scoreDirectorFactory.setInitializingScoreTrend(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1));
        EasyScoreDirector<TestdataCorruptedShadowedSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector(false, false);

        TestdataCorruptedShadowedSolution solution = new TestdataCorruptedShadowedSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        solution.setValueList(Arrays.asList(v1, v2));
        TestdataCorruptedShadowedEntity e1 = new TestdataCorruptedShadowedEntity("e1");
        TestdataCorruptedShadowedEntity e2 = new TestdataCorruptedShadowedEntity("e2");
        solution.setEntityList(Arrays.asList(e1, e2));
        scoreDirector.setWorkingSolution(solution);

        scoreDirector.assertShadowVariablesAreNotStale(SimpleScore.ofUninitialized(-2, 0), "NoChange");
        scoreDirector.beforeVariableChanged(e1, "value");
        e1.setValue(v1);
        scoreDirector.afterVariableChanged(e1, "value");
        scoreDirector.beforeVariableChanged(e2, "value");
        e2.setValue(v1);
        scoreDirector.afterVariableChanged(e2, "value");
        scoreDirector.triggerVariableListeners();
        // TODO After upgrade to JUnit 5, clean this up
        try {
            scoreDirector.assertShadowVariablesAreNotStale(SimpleScore.ofUninitialized(0, 0), "FirstChange");
            fail("IllegalStateException wasn't thrown.");
        } catch (IllegalStateException e) {
            // ok
        }
    }

}
