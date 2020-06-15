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

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.pinned.chained.TestdataPinnedChainedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.chained.TestdataPinnedChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class MovableChainedTrailingValueFilterTest {

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

        assertThat(filter.accept(scoreDirector, a0)).isFalse();
        assertThat(filter.accept(scoreDirector, a1)).isTrue();
        assertThat(filter.accept(scoreDirector, a2)).isTrue();
        assertThat(filter.accept(scoreDirector, a3)).isTrue();

        assertThat(filter.accept(scoreDirector, b0)).isTrue();
        assertThat(filter.accept(scoreDirector, b1)).isTrue();
        assertThat(filter.accept(scoreDirector, b2)).isTrue();

        assertThat(filter.accept(scoreDirector, c0)).isFalse();
        assertThat(filter.accept(scoreDirector, c1)).isFalse();
        assertThat(filter.accept(scoreDirector, c2)).isTrue();
    }

    @Test
    public void getMovableChainedTrailingValueFilter() {
        VariableDescriptor variableDescriptor = TestdataPinnedChainedEntity.buildEntityDescriptor()
                .getVariableDescriptor("chainedObject");
        assertThat(((GenuineVariableDescriptor) variableDescriptor).getMovableChainedTrailingValueFilter()).isNotNull();
    }

}
