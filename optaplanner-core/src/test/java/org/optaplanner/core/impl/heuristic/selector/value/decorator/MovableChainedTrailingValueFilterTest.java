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

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Arrays;

import org.junit.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.immovable.chained.TestdataImmovableChainedEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.chained.TestdataImmovableChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class MovableChainedTrailingValueFilterTest {

    @Test
    public void immovableChained() {
        GenuineVariableDescriptor variableDescriptor = TestdataImmovableChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataImmovableChainedEntity a1 = new TestdataImmovableChainedEntity("a1", a0, true);
        TestdataImmovableChainedEntity a2 = new TestdataImmovableChainedEntity("a2", a1, false);
        TestdataImmovableChainedEntity a3 = new TestdataImmovableChainedEntity("a3", a2, false);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataImmovableChainedEntity b1 = new TestdataImmovableChainedEntity("b1", b0, false);
        TestdataImmovableChainedEntity b2 = new TestdataImmovableChainedEntity("b2", b1, false);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataImmovableChainedEntity c1 = new TestdataImmovableChainedEntity("c1", c0, true);
        TestdataImmovableChainedEntity c2 = new TestdataImmovableChainedEntity("c2", c1, true);

        TestdataImmovableChainedSolution solution = new TestdataImmovableChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0, c0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1, b2, c1, c2));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));

        MovableChainedTrailingValueFilter filter = new MovableChainedTrailingValueFilter(variableDescriptor);

        assertEquals(false, filter.accept(scoreDirector, a0));
        assertEquals(true, filter.accept(scoreDirector, a1));
        assertEquals(true, filter.accept(scoreDirector, a2));
        assertEquals(true, filter.accept(scoreDirector, a3));

        assertEquals(true, filter.accept(scoreDirector, b0));
        assertEquals(true, filter.accept(scoreDirector, b1));
        assertEquals(true, filter.accept(scoreDirector, b2));

        assertEquals(false, filter.accept(scoreDirector, c0));
        assertEquals(false, filter.accept(scoreDirector, c1));
        assertEquals(true, filter.accept(scoreDirector, c2));
    }

    @Test
    public void getMovableChainedTrailingValueFilter() {
        VariableDescriptor variableDescriptor = TestdataImmovableChainedEntity.buildEntityDescriptor()
                .getVariableDescriptor("chainedObject");
        assertNotNull(((GenuineVariableDescriptor) variableDescriptor).getMovableChainedTrailingValueFilter());
    }

}
