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

package org.optaplanner.core.impl.domain.variable.listener.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.ExternalizedSingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableListener;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.order.TestdataShadowVariableOrderEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.order.TestdataShadowVariableOrderSolution;

class VariableListenerSupportTest {

    @Test
    void demandBasic() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        TestdataSolution solution = new TestdataSolution();
        solution.setEntityList(Collections.emptyList());
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.getSupplyManager()).thenReturn(mock(SupplyManager.class));
        VariableListenerSupport<TestdataSolution> variableListenerSupport = VariableListenerSupport.create(scoreDirector);
        variableListenerSupport.linkVariableListeners();

        VariableDescriptor<TestdataSolution> variableDescriptor =
                solutionDescriptor.getEntityDescriptorStrict(TestdataEntity.class)
                        .getVariableDescriptor("value");

        SingletonInverseVariableSupply supply1 = variableListenerSupport
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        SingletonInverseVariableSupply supply2 = variableListenerSupport
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        assertThat(supply2).isSameAs(supply1);
    }

    @Test
    void demandChained() {
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        TestdataChainedSolution solution = new TestdataChainedSolution();
        solution.setChainedEntityList(Collections.emptyList());
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.getSupplyManager()).thenReturn(mock(SupplyManager.class));
        VariableListenerSupport<TestdataChainedSolution> variableListenerSupport =
                VariableListenerSupport.create(scoreDirector);
        variableListenerSupport.linkVariableListeners();

        VariableDescriptor<TestdataChainedSolution> variableDescriptor =
                solutionDescriptor.getEntityDescriptorStrict(TestdataChainedEntity.class)
                        .getVariableDescriptor("chainedObject");

        SingletonInverseVariableSupply supply1 = variableListenerSupport
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        assertThat(supply1)
                .isInstanceOf(ExternalizedSingletonInverseVariableSupply.class);
        SingletonInverseVariableSupply supply2 = variableListenerSupport
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        assertThat(supply2).isSameAs(supply1);
    }

    @Test
    void demandRichChained() {
        SolutionDescriptor<TestdataShadowingChainedSolution> solutionDescriptor =
                TestdataShadowingChainedSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataShadowingChainedSolution, SimpleScore> scoreDirector =
                mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        TestdataShadowingChainedSolution solution = new TestdataShadowingChainedSolution();
        solution.setChainedEntityList(Collections.emptyList());
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.getSupplyManager()).thenReturn(mock(SupplyManager.class));
        VariableListenerSupport<TestdataShadowingChainedSolution> variableListenerSupport =
                VariableListenerSupport.create(scoreDirector);
        variableListenerSupport.linkVariableListeners();

        VariableDescriptor<TestdataShadowingChainedSolution> variableDescriptor = solutionDescriptor
                .getEntityDescriptorStrict(TestdataShadowingChainedEntity.class)
                .getVariableDescriptor("chainedObject");

        SingletonInverseVariableSupply supply1 = variableListenerSupport
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        assertThat(supply1)
                .isInstanceOf(SingletonInverseVariableListener.class);
        SingletonInverseVariableSupply supply2 = variableListenerSupport
                .demand(new SingletonInverseVariableDemand<>(variableDescriptor));
        assertThat(supply2).isSameAs(supply1);
    }

    @Test
    void shadowVariableListenerOrder() {
        EntityDescriptor<TestdataShadowVariableOrderSolution> entityDescriptor =
                TestdataShadowVariableOrderEntity.buildEntityDescriptor();
        SolutionDescriptor<TestdataShadowVariableOrderSolution> solutionDescriptor = entityDescriptor.getSolutionDescriptor();
        InnerScoreDirector<TestdataShadowVariableOrderSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);

        NotifiableRegistry<TestdataShadowVariableOrderSolution> registry = new NotifiableRegistry<>(solutionDescriptor);
        VariableListenerSupport<TestdataShadowVariableOrderSolution> variableListenerSupport =
                new VariableListenerSupport<>(scoreDirector, registry, new HashMap<>());

        variableListenerSupport.linkVariableListeners();

        assertThat(registry.getAll())
                .map(VariableListenerNotifiable::toString)
                .containsExactly(
                        "(0) C",
                        "(1) D",
                        "(2) E",
                        "(3) FG");

        assertThat(registry.get(entityDescriptor))
                .map(VariableListenerNotifiable::toString)
                .containsExactly(
                        "(0) C",
                        "(1) D",
                        "(2) E",
                        "(3) FG");

        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x6A")))
                .map(VariableListenerNotifiable::toString)
                .containsExactly("(0) C");
        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x5B")))
                .map(VariableListenerNotifiable::toString)
                .containsExactly("(2) E");
        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x3C")))
                .map(VariableListenerNotifiable::toString)
                .containsExactly("(1) D", "(2) E");
        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x1D")))
                .isEmpty();
        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x2E")))
                .map(VariableListenerNotifiable::toString)
                .containsExactly("(3) FG");
        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x4F")))
                .isEmpty();
        assertThat(registry.get(entityDescriptor.getVariableDescriptor("x0G")))
                .isEmpty();
    }
}
