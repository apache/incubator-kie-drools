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

package org.optaplanner.core.impl.score.director;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

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
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(
                TestdataChainedEntity.class);
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("chainedObject");

        AbstractScoreDirectorFactory scoreDirectorFactory = mock(AbstractScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        AbstractScoreDirector scoreDirector = new AbstractScoreDirector<AbstractScoreDirectorFactory>(
                scoreDirectorFactory, false) {
            public boolean isConstraintMatchEnabled() {
                return false;
            }
            public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
                throw new IllegalStateException();
            }
            public Score calculateScore() {
                return SimpleScore.valueOf(-100);
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
        TestdataChainedEntity a1Clone = clonedStartingSolution.getChainedEntityList().get(0);
        assertCode("a1", a1Clone);
        TestdataChainedEntity a2Clone = clonedStartingSolution.getChainedEntityList().get(1);
        assertCode("a2", a2Clone);
        assertEquals("a1", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a0)).getCode());
        assertEquals("a2", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a1Clone)).getCode());
        assertEquals("a3", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a2Clone)).getCode());
        assertEquals("b1", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, b0)).getCode());
    }

}
