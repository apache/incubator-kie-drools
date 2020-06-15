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
package org.optaplanner.core.impl.domain.solution.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfCollection;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataArrayBasedSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataSetBasedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.reflect.generic.TestdataGenericEntity;
import org.optaplanner.core.impl.testdata.domain.reflect.generic.TestdataGenericSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataNoProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataReadMethodProblemFactCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverFieldOverrideSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverFieldSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverGetterOverrideSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverGetterSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverUnannotatedEntitySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataExtendedAutoDiscoverGetterSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicatePlanningEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicatePlanningScorePropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicateProblemFactCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataProblemFactCollectionPropertyWithArgumentSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataProblemFactIsPlanningEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataUnknownFactTypeSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataUnsupportedFactTypeSolution;
import org.optaplanner.core.impl.testdata.util.CodeAssertableArrayList;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SolutionDescriptorTest {

    // ************************************************************************
    // Problem fact and planning entity properties
    // ************************************************************************

    @Test
    public void problemFactProperty() {
        SolutionDescriptor<TestdataProblemFactPropertySolution> solutionDescriptor = TestdataProblemFactPropertySolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("extraObject");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "otherProblemFactList");
    }

    @Test
    public void readMethodProblemFactCollectionProperty() {
        SolutionDescriptor<TestdataReadMethodProblemFactCollectionPropertySolution> solutionDescriptor =
                TestdataReadMethodProblemFactCollectionPropertySolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "createProblemFacts");
    }

    @Test
    public void problemFactCollectionPropertyWithArgument() {
        assertThatIllegalStateException().isThrownBy(
                TestdataProblemFactCollectionPropertyWithArgumentSolution::buildSolutionDescriptor);
    }

    @Test
    public void duplicateProblemFactCollectionProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataDuplicateProblemFactCollectionPropertySolution::buildSolutionDescriptor);
    }

    @Test
    public void duplicatePlanningEntityCollectionProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataDuplicatePlanningEntityCollectionPropertySolution::buildSolutionDescriptor);
    }

    @Test
    public void duplicatePlanningScorePropertyProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataDuplicatePlanningScorePropertySolution::buildSolutionDescriptor);
    }

    @Test
    public void problemFactIsPlanningEntityCollectionProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataProblemFactIsPlanningEntityCollectionPropertySolution::buildSolutionDescriptor);
    }

    @Test
    public void noProblemFactPropertyWithEasyScoreCalculation() {
        SolverFactory<TestdataNoProblemFactPropertySolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataNoProblemFactPropertySolution.class, TestdataEntity.class);
        solverFactory.buildSolver();
    }

    @Test
    public void noProblemFactPropertyWithDroolsScoreCalculation() {
        SolverFactory<TestdataNoProblemFactPropertySolution> solverFactory = PlannerTestUtils
                .buildSolverFactoryWithDroolsScoreDirector(
                        TestdataNoProblemFactPropertySolution.class, TestdataEntity.class);
        assertThatIllegalStateException().isThrownBy(solverFactory::buildSolver);
    }

    @Test
    public void extended() {
        SolutionDescriptor<TestdataAnnotatedExtendedSolution> solutionDescriptor = TestdataAnnotatedExtendedSolution
                .buildExtendedSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "subValueList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList", "subEntityList");
    }

    @Test
    public void setProperties() {
        SolutionDescriptor<TestdataSetBasedSolution> solutionDescriptor = TestdataSetBasedSolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueSet");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entitySet");
    }

    @Test
    public void arrayProperties() {
        SolutionDescriptor<TestdataArrayBasedSolution> solutionDescriptor = TestdataArrayBasedSolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("values");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entities");
    }

    @Test
    public void generic() {
        SolutionDescriptor<TestdataGenericSolution> solutionDescriptor = TestdataGenericSolution.buildSolutionDescriptor();

        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "complexGenericValueList", "subTypeValueList");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        assertThat(solutionDescriptor.findEntityDescriptor(TestdataGenericEntity.class).getVariableDescriptorMap())
                .containsOnlyKeys("value", "subTypeValue", "complexGenericValue");
    }

    // ************************************************************************
    // Autodiscovery
    // ************************************************************************

    @Test
    public void autoDiscoverProblemFactCollectionPropertyElementTypeUnsupported() {
        assertThatIllegalArgumentException().isThrownBy(TestdataUnsupportedFactTypeSolution::buildSolutionDescriptor);
    }

    @Test
    public void autoDiscoverProblemFactCollectionPropertyElementTypeUnknown() {
        assertThatIllegalArgumentException().isThrownBy(TestdataUnknownFactTypeSolution::buildSolutionDescriptor);
    }

    @Test
    public void autoDiscoverFields() {
        SolutionDescriptor<TestdataAutoDiscoverFieldSolution> solutionDescriptor = TestdataAutoDiscoverFieldSolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getConstraintConfigurationMemberAccessor().getName())
                .isEqualTo("constraintConfiguration");
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("constraintConfiguration",
                "singleProblemFact");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("problemFactList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys("otherEntity");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        TestdataObject singleProblemFact = new TestdataObject("p1");
        List<TestdataValue> valueList = Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"));
        List<TestdataEntity> entityList = Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"));
        TestdataEntity otherEntity = new TestdataEntity("otherE1");
        TestdataAutoDiscoverFieldSolution solution = new TestdataAutoDiscoverFieldSolution(
                "s1", singleProblemFact, valueList, entityList, otherEntity);

        assertAllCodesOfCollection(solutionDescriptor.getAllFacts(solution), "otherE1", "p1", "e1", "e2", "v1", "v2");
    }

    @Test
    public void autoDiscoverGetters() {
        SolutionDescriptor<TestdataAutoDiscoverGetterSolution> solutionDescriptor = TestdataAutoDiscoverGetterSolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getConstraintConfigurationMemberAccessor().getName())
                .isEqualTo("constraintConfiguration");
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("constraintConfiguration",
                "singleProblemFact");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("problemFactList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys("otherEntity");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        TestdataObject singleProblemFact = new TestdataObject("p1");
        List<TestdataValue> valueList = Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"));
        List<TestdataEntity> entityList = Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"));
        TestdataEntity otherEntity = new TestdataEntity("otherE1");
        TestdataAutoDiscoverGetterSolution solution = new TestdataAutoDiscoverGetterSolution(
                "s1", singleProblemFact, valueList, entityList, otherEntity);

        assertAllCodesOfCollection(solutionDescriptor.getAllFacts(solution), "otherE1", "p1", "e1", "e2", "v1", "v2");
    }

    @Test
    public void autoDiscoverFieldsFactCollectionOverridenToSingleProperty() {
        SolutionDescriptor<TestdataAutoDiscoverFieldOverrideSolution> solutionDescriptor =
                TestdataAutoDiscoverFieldOverrideSolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("singleProblemFact",
                "listProblemFact");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("problemFactList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys("otherEntity");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        TestdataObject singleProblemFact = new TestdataObject("p1");
        List<TestdataValue> valueList = Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"));
        List<TestdataEntity> entityList = Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"));
        TestdataEntity otherEntity = new TestdataEntity("otherE1");
        List<String> listFact = new CodeAssertableArrayList<>("list1", Arrays.asList("x", "y"));
        TestdataAutoDiscoverFieldOverrideSolution solution = new TestdataAutoDiscoverFieldOverrideSolution(
                "s1", singleProblemFact, valueList, entityList, otherEntity, listFact);

        assertAllCodesOfCollection(solutionDescriptor.getAllFacts(solution),
                "otherE1", "list1", "p1", "e1", "e2", "v1", "v2");
    }

    @Test
    public void autoDiscoverGettersFactCollectionOverridenToSingleProperty() {
        SolutionDescriptor<TestdataAutoDiscoverGetterOverrideSolution> solutionDescriptor =
                TestdataAutoDiscoverGetterOverrideSolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("singleProblemFact",
                "listProblemFact");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("problemFactList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys("otherEntity");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        TestdataObject singleProblemFact = new TestdataObject("p1");
        List<TestdataValue> valueList = Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"));
        List<TestdataEntity> entityList = Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"));
        TestdataEntity otherEntity = new TestdataEntity("otherE1");
        List<String> listFact = new CodeAssertableArrayList<>("list1", Arrays.asList("x", "y"));
        TestdataAutoDiscoverGetterOverrideSolution solution = new TestdataAutoDiscoverGetterOverrideSolution(
                "s1", singleProblemFact, valueList, entityList, otherEntity, listFact);

        assertAllCodesOfCollection(solutionDescriptor.getAllFacts(solution),
                "otherE1", "list1", "p1", "e1", "e2", "v1", "v2");
    }

    @Test
    public void autoDiscoverUnannotatedEntitySubclass() {
        SolutionDescriptor<TestdataAutoDiscoverUnannotatedEntitySolution> solutionDescriptor =
                TestdataAutoDiscoverUnannotatedEntitySolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("singleProblemFact");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("problemFactList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys("otherEntity");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        TestdataObject singleProblemFact = new TestdataObject("p1");
        List<TestdataValue> valueList = Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2"));
        List<TestdataUnannotatedExtendedEntity> entityList = Arrays.asList(
                new TestdataUnannotatedExtendedEntity("u1"),
                new TestdataUnannotatedExtendedEntity("u2"));
        TestdataUnannotatedExtendedEntity otherEntity = new TestdataUnannotatedExtendedEntity("otherU1");
        TestdataAutoDiscoverUnannotatedEntitySolution solution = new TestdataAutoDiscoverUnannotatedEntitySolution(
                "s1", singleProblemFact, valueList, entityList, otherEntity);

        assertAllCodesOfCollection(solutionDescriptor.getAllFacts(solution), "otherU1", "p1", "u1", "u2", "v1", "v2");
    }

    @Test
    public void autoDiscoverGettersOverriddenInSubclass() {
        SolutionDescriptor<TestdataExtendedAutoDiscoverGetterSolution> solutionDescriptor =
                TestdataExtendedAutoDiscoverGetterSolution.buildSubclassSolutionDescriptor();
        assertThat(solutionDescriptor.getConstraintConfigurationMemberAccessor().getName())
                .isEqualTo("constraintConfiguration");
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("constraintConfiguration",
                "singleProblemFact", "problemFactList");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys();
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).containsOnlyKeys("otherEntity");
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList");

        TestdataObject singleProblemFact = new TestdataObject("p1");
        List<TestdataValue> listAsSingleProblemFact = new CodeAssertableArrayList<>(
                "f1", Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        List<TestdataEntity> entityList = Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"));
        TestdataEntity otherEntity = new TestdataEntity("otherE1");
        TestdataExtendedAutoDiscoverGetterSolution solution = new TestdataExtendedAutoDiscoverGetterSolution(
                "s1", singleProblemFact, listAsSingleProblemFact, entityList, otherEntity);

        assertAllCodesOfCollection(solutionDescriptor.getAllFacts(solution), "otherE1", "f1", "p1", "e1", "e2");
    }

}
