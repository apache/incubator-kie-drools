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
package org.optaplanner.core.impl.domain.solution.descriptor;

import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.abstractsolution.TestdataExtendedAbstractSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataReadMethodProblemFactCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicatePlanningEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicateProblemFactCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataNoProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataProblemFactIsPlanningEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataProblemFactCollectionPropertyWithArgumentSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class SolutionDescriptorTest {

    // ************************************************************************
    // Problem fact and planning entity properties
    // ************************************************************************

    @Test
    public void problemFactProperty() {
        SolutionDescriptor<TestdataProblemFactPropertySolution> solutionDescriptor
                = TestdataProblemFactPropertySolution.buildSolutionDescriptor();
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactMemberAccessorMap(),
                "extraObject");
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactCollectionMemberAccessorMap(),
                "valueList", "otherProblemFactList");
    }

    @Test
    public void readMethodProblemFactCollectionProperty() {
        SolutionDescriptor<TestdataReadMethodProblemFactCollectionPropertySolution> solutionDescriptor
                = TestdataReadMethodProblemFactCollectionPropertySolution.buildSolutionDescriptor();
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactMemberAccessorMap());
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactCollectionMemberAccessorMap(),
                "valueList", "createProblemFacts");
    }

    @Test(expected = IllegalStateException.class)
    public void problemFactCollectionPropertyWithArgument() {
        TestdataProblemFactCollectionPropertyWithArgumentSolution.buildSolutionDescriptor();
    }

    @Test(expected = IllegalStateException.class)
    public void duplicateProblemFactCollectionProperty() {
        TestdataDuplicateProblemFactCollectionPropertySolution.buildSolutionDescriptor();
    }

    @Test(expected = IllegalStateException.class)
    public void duplicatePlanningEntityCollectionProperty() {
        TestdataDuplicatePlanningEntityCollectionPropertySolution.buildSolutionDescriptor();
    }

    @Test(expected = IllegalStateException.class)
    public void problemFactIsPlanningEntityCollectionProperty() {
        TestdataProblemFactIsPlanningEntityCollectionPropertySolution.buildSolutionDescriptor();
    }

    @Test
    public void noProblemFactPropertyWithEasyScoreCalculation() {
        SolverFactory<TestdataNoProblemFactPropertySolution> solverFactory
                = PlannerTestUtils.buildSolverFactoryWithEasyScoreDirector(
                        TestdataNoProblemFactPropertySolution.class, TestdataEntity.class);
        solverFactory.buildSolver();
    }

    @Test(expected = IllegalStateException.class)
    public void noProblemFactPropertyWithDroolsScoreCalculation() {
        SolverFactory<TestdataNoProblemFactPropertySolution> solverFactory
                = PlannerTestUtils.buildSolverFactoryWithDroolsScoreDirector(
                        TestdataNoProblemFactPropertySolution.class, TestdataEntity.class);
        solverFactory.buildSolver();
    }

    @Test
    public void extended() {
        SolutionDescriptor<TestdataAnnotatedExtendedSolution> solutionDescriptor
                = TestdataAnnotatedExtendedSolution.buildExtendedSolutionDescriptor();
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactMemberAccessorMap());
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactCollectionMemberAccessorMap(),
                "valueList", "subValueList");
        assertMapContainsKeysExactly(solutionDescriptor.getEntityMemberAccessorMap());
        assertMapContainsKeysExactly(solutionDescriptor.getEntityCollectionMemberAccessorMap(),
                "entityList", "subEntityList");
    }

    // ************************************************************************
    // Others
    // ************************************************************************

    @Test
    public void extendedAbstractSolution() {
        SolutionDescriptor<TestdataExtendedAbstractSolution> solutionDescriptor
                = TestdataExtendedAbstractSolution.buildSolutionDescriptor();
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactMemberAccessorMap());
        assertMapContainsKeysExactly(solutionDescriptor.getProblemFactCollectionMemberAccessorMap(),
                "problemFactList");
        assertMapContainsKeysExactly(solutionDescriptor.getEntityMemberAccessorMap());
        assertMapContainsKeysExactly(solutionDescriptor.getEntityCollectionMemberAccessorMap(),
                "entityList");
    }

}
