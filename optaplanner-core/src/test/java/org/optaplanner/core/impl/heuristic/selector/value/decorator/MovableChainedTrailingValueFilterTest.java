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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.pinned.chained.TestdataLegacyPinnedChainedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.chained.TestdataLegacyPinnedChainedSolution;
import org.optaplanner.core.impl.testdata.domain.pinned.chained.TestdataPinnedChainedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.chained.TestdataPinnedChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class MovableChainedTrailingValueFilterTest {

    @Test
    public void legacyPinnedChained() {
        GenuineVariableDescriptor variableDescriptor =
                TestdataLegacyPinnedChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataLegacyPinnedChainedEntity a1 = new TestdataLegacyPinnedChainedEntity("a1", a0, true);
        TestdataLegacyPinnedChainedEntity a2 = new TestdataLegacyPinnedChainedEntity("a2", a1, false);
        TestdataLegacyPinnedChainedEntity a3 = new TestdataLegacyPinnedChainedEntity("a3", a2, false);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataLegacyPinnedChainedEntity b1 = new TestdataLegacyPinnedChainedEntity("b1", b0, false);
        TestdataLegacyPinnedChainedEntity b2 = new TestdataLegacyPinnedChainedEntity("b2", b1, false);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataLegacyPinnedChainedEntity c1 = new TestdataLegacyPinnedChainedEntity("c1", c0, true);
        TestdataLegacyPinnedChainedEntity c2 = new TestdataLegacyPinnedChainedEntity("c2", c1, true);

        TestdataLegacyPinnedChainedSolution solution = new TestdataLegacyPinnedChainedSolution("solution");
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
    public void pinnedChained() {
        GenuineVariableDescriptor variableDescriptor = TestdataPinnedChainedEntity.buildVariableDescriptorForChainedObject();
        SolutionDescriptor solutionDescriptor = variableDescriptor.getEntityDescriptor().getSolutionDescriptor();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataPinnedChainedEntity a1 = new TestdataPinnedChainedEntity("a1", a0, true);
        TestdataPinnedChainedEntity a2 = new TestdataPinnedChainedEntity("a2", a1, false);
        TestdataPinnedChainedEntity a3 = new TestdataPinnedChainedEntity("a3", a2, false);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataPinnedChainedEntity b1 = new TestdataPinnedChainedEntity("b1", b0, false);
        TestdataPinnedChainedEntity b2 = new TestdataPinnedChainedEntity("b2", b1, false);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataPinnedChainedEntity c1 = new TestdataPinnedChainedEntity("c1", c0, true);
        TestdataPinnedChainedEntity c2 = new TestdataPinnedChainedEntity("c2", c1, true);

        TestdataPinnedChainedSolution solution = new TestdataPinnedChainedSolution("solution");
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
        VariableDescriptor variableDescriptor = TestdataPinnedChainedEntity.buildEntityDescriptor()
                .getVariableDescriptor("chainedObject");
        assertNotNull(((GenuineVariableDescriptor) variableDescriptor).getMovableChainedTrailingValueFilter());
    }

}
