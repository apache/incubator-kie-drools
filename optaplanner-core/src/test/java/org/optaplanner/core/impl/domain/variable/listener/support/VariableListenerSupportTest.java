/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import org.junit.Test;
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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class VariableListenerSupportTest {

    @Test
    public void demandBasic() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataSolution> scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        TestdataSolution solution = new TestdataSolution();
        solution.setEntityList(Collections.<TestdataEntity>emptyList());
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.getSupplyManager()).thenReturn(mock(SupplyManager.class));
        VariableListenerSupport<TestdataSolution> variableListenerSupport = new VariableListenerSupport<>(scoreDirector);
        variableListenerSupport.linkVariableListeners();

        VariableDescriptor variableDescriptor = solutionDescriptor.getEntityDescriptorStrict(TestdataEntity.class)
                .getVariableDescriptor("value");

        SingletonInverseVariableSupply supply1
                = variableListenerSupport.demand(new SingletonInverseVariableDemand(variableDescriptor));
        SingletonInverseVariableSupply supply2
                = variableListenerSupport.demand(new SingletonInverseVariableDemand(variableDescriptor));
        assertSame(supply1, supply2);
    }

    @Test
    public void demandChained() {
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor
                = TestdataChainedSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        TestdataChainedSolution solution = new TestdataChainedSolution();
        solution.setChainedEntityList(Collections.<TestdataChainedEntity>emptyList());
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.getSupplyManager()).thenReturn(mock(SupplyManager.class));
        VariableListenerSupport<TestdataChainedSolution> variableListenerSupport
                = new VariableListenerSupport<>(scoreDirector);
        variableListenerSupport.linkVariableListeners();

        VariableDescriptor variableDescriptor = solutionDescriptor.getEntityDescriptorStrict(TestdataChainedEntity.class)
                .getVariableDescriptor("chainedObject");

        SingletonInverseVariableSupply supply1
                = variableListenerSupport.demand(new SingletonInverseVariableDemand(variableDescriptor));
        assertInstanceOf(ExternalizedSingletonInverseVariableSupply.class, supply1);
        SingletonInverseVariableSupply supply2
                = variableListenerSupport.demand(new SingletonInverseVariableDemand(variableDescriptor));
        assertSame(supply1, supply2);
    }

    @Test
    public void demandRichChained() {
        SolutionDescriptor<TestdataShadowingChainedSolution> solutionDescriptor
                = TestdataShadowingChainedSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataShadowingChainedSolution> scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        TestdataShadowingChainedSolution solution = new TestdataShadowingChainedSolution();
        solution.setChainedEntityList(Collections.<TestdataShadowingChainedEntity>emptyList());
        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        when(scoreDirector.getSupplyManager()).thenReturn(mock(SupplyManager.class));
        VariableListenerSupport<TestdataShadowingChainedSolution> variableListenerSupport
                = new VariableListenerSupport<>(scoreDirector);
        variableListenerSupport.linkVariableListeners();

        VariableDescriptor variableDescriptor = solutionDescriptor.getEntityDescriptorStrict(TestdataShadowingChainedEntity.class)
                .getVariableDescriptor("chainedObject");

        SingletonInverseVariableSupply supply1
                = variableListenerSupport.demand(new SingletonInverseVariableDemand(variableDescriptor));
        assertInstanceOf(SingletonInverseVariableListener.class, supply1);
        SingletonInverseVariableSupply supply2
                = variableListenerSupport.demand(new SingletonInverseVariableDemand(variableDescriptor));
        assertSame(supply1, supply2);
    }

}
