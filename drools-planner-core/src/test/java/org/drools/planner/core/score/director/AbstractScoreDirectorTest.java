/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.score.director;

import java.util.Arrays;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.drools.planner.core.testdata.domain.TestdataChainedSolution;
import org.drools.planner.core.testdata.domain.TestdataObject;
import org.drools.planner.core.testdata.domain.TestdataSolution;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractScoreDirectorTest {

    @Test
    public void getTrailingEntityMap() {
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        SolutionDescriptor solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        PlanningEntityDescriptor entityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(
                TestdataChainedEntity.class);
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");

        AbstractScoreDirectorFactory scoreDirectorFactory = mock(AbstractScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        AbstractScoreDirector scoreDirector = new AbstractScoreDirector<AbstractScoreDirectorFactory>(
                scoreDirectorFactory) {
            public Score calculateScore() {
                return DefaultSimpleScore.valueOf(-100);
            }
        };
        FieldAccessingSolutionCloner<TestdataChainedSolution> cloner
                = new FieldAccessingSolutionCloner<TestdataChainedSolution>(solutionDescriptor);
        TestdataChainedSolution clonedStartingSolution = cloner.cloneSolution(solution);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(a1, scoreDirector.getTrailingEntity(variableDescriptor, a0));
        assertEquals(a2, scoreDirector.getTrailingEntity(variableDescriptor, a1));
        assertEquals(a3, scoreDirector.getTrailingEntity(variableDescriptor, a2));
        assertEquals(b1, scoreDirector.getTrailingEntity(variableDescriptor, b0));
        
        scoreDirector.beforeVariableChanged(a3, "chainedObject");
        a3.setChainedObject(b1);
        scoreDirector.afterVariableChanged(a3, "chainedObject");
        assertEquals(a1, scoreDirector.getTrailingEntity(variableDescriptor, a0));
        assertEquals(a2, scoreDirector.getTrailingEntity(variableDescriptor, a1));
        assertEquals(b1, scoreDirector.getTrailingEntity(variableDescriptor, b0));
        assertEquals(a3, scoreDirector.getTrailingEntity(variableDescriptor, b1));

        scoreDirector.setWorkingSolution(clonedStartingSolution);
        assertEquals("a1", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a0)).getCode());
        assertEquals("a2", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a1)).getCode());
        assertEquals("a3", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a2)).getCode());
        assertEquals("b1", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, b0)).getCode());
    }

}
