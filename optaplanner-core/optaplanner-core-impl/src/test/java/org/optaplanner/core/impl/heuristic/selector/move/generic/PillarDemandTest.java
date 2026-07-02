/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.SubPillarConfigPolicy;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.FromSolutionEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.FilteringEntitySelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class PillarDemandTest {

    @Test
    void equality() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EntityDescriptor<TestdataSolution> entityDescriptor = solutionDescriptor.findEntityDescriptor(TestdataEntity.class);
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList =
                entityDescriptor.getGenuineVariableDescriptorList();
        SubPillarConfigPolicy subPillarConfigPolicy = SubPillarConfigPolicy.withoutSubpillars();

        EntitySelector<TestdataSolution> entitySelector =
                new FromSolutionEntitySelector<>(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);
        SelectionFilter<TestdataSolution, Object> selectionFilter = (scoreDirector, selection) -> true;
        FilteringEntitySelector<TestdataSolution> filteringEntitySelector =
                new FilteringEntitySelector<>(entitySelector, List.of(selectionFilter));

        PillarDemand<TestdataSolution> pillarDemand =
                new PillarDemand<>(filteringEntitySelector, variableDescriptorList, subPillarConfigPolicy);
        Assertions.assertThat(pillarDemand).isEqualTo(pillarDemand);

        PillarDemand<TestdataSolution> samePillarDemand =
                new PillarDemand<>(filteringEntitySelector, variableDescriptorList, subPillarConfigPolicy);
        Assertions.assertThat(samePillarDemand).isEqualTo(pillarDemand);

        PillarDemand<TestdataSolution> samePillarDemandCopiedList =
                new PillarDemand<>(filteringEntitySelector, new ArrayList<>(variableDescriptorList), subPillarConfigPolicy);
        Assertions.assertThat(samePillarDemandCopiedList).isEqualTo(pillarDemand);

        EntitySelector<TestdataSolution> sameEntitySelector =
                new FilteringEntitySelector<>(entitySelector, List.of(selectionFilter));
        PillarDemand<TestdataSolution> samePillarDemandCopiedSelector =
                new PillarDemand<>(sameEntitySelector, new ArrayList<>(variableDescriptorList), subPillarConfigPolicy);
        Assertions.assertThat(samePillarDemandCopiedSelector).isEqualTo(pillarDemand);
    }

}
