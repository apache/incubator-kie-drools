/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EasyScoreDirectorFactoryTest {

    @Test
    public void buildScoreDirector() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EasyScoreCalculator<TestdataSolution> scoreCalculator = mock(EasyScoreCalculator.class);
        when(scoreCalculator.calculateScore(any(TestdataSolution.class)))
                .thenAnswer(invocation -> SimpleScore.of(-10));
        EasyScoreDirectorFactory<TestdataSolution> directorFactory = new EasyScoreDirectorFactory<>(
                solutionDescriptor, scoreCalculator);

        EasyScoreDirector<TestdataSolution> director = directorFactory.buildScoreDirector(false, false);
        TestdataSolution solution = new TestdataSolution();
        solution.setValueList(Collections.emptyList());
        solution.setEntityList(Collections.emptyList());
        director.setWorkingSolution(solution);
        assertEquals(SimpleScore.ofUninitialized(0, -10), director.calculateScore());
    }

}
