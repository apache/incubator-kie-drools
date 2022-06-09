package org.optaplanner.core.impl.domain.solution.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfCollection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataArrayBasedSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataSetBasedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataAnnotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.domain.reflect.generic.TestdataGenericEntity;
import org.optaplanner.core.impl.testdata.domain.reflect.generic.TestdataGenericSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataNoProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataReadMethodProblemFactCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataWildcardSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverFieldOverrideSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverFieldSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverGetterOverrideSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverGetterSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataAutoDiscoverUnannotatedEntitySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover.TestdataExtendedAutoDiscoverGetterSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicatePlanningEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicatePlanningScorePropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataDuplicateProblemFactCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataMissingScorePropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataProblemFactCollectionPropertyWithArgumentSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataProblemFactIsPlanningEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataUnknownFactTypeSolution;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid.TestdataUnsupportedWildcardSolution;
import org.optaplanner.core.impl.testdata.util.CodeAssertableArrayList;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class SolutionDescriptorTest {

    // ************************************************************************
    // Problem fact and planning entity properties
    // ************************************************************************

    @Test
    void problemFactProperty() {
        SolutionDescriptor<TestdataProblemFactPropertySolution> solutionDescriptor = TestdataProblemFactPropertySolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("extraObject");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "otherProblemFactList");
    }

    @Test
    void readMethodProblemFactCollectionProperty() {
        SolutionDescriptor<TestdataReadMethodProblemFactCollectionPropertySolution> solutionDescriptor =
                TestdataReadMethodProblemFactCollectionPropertySolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "createProblemFacts");
    }

    @Test
    void problemFactCollectionPropertyWithArgument() {
        assertThatIllegalStateException().isThrownBy(
                TestdataProblemFactCollectionPropertyWithArgumentSolution::buildSolutionDescriptor);
    }

    @Test
    void duplicateProblemFactCollectionProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataDuplicateProblemFactCollectionPropertySolution::buildSolutionDescriptor);
    }

    @Test
    void duplicatePlanningEntityCollectionProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataDuplicatePlanningEntityCollectionPropertySolution::buildSolutionDescriptor);
    }

    @Test
    void duplicatePlanningScorePropertyProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataDuplicatePlanningScorePropertySolution::buildSolutionDescriptor);
    }

    @Test
    void missingPlanningScorePropertyProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataMissingScorePropertySolution::buildSolutionDescriptor);
    }

    @Test
    void problemFactIsPlanningEntityCollectionProperty() {
        assertThatIllegalStateException().isThrownBy(
                TestdataProblemFactIsPlanningEntityCollectionPropertySolution::buildSolutionDescriptor);
    }

    @Test
    void wildcardProblemFactAndEntityProperties() {
        SolutionDescriptor<TestdataWildcardSolution> solutionDescriptor = TestdataWildcardSolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("extendsValueList",
                "supersValueList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("extendsEntityList");
    }

    @Test
    void wildcardSupersEntityListProperty() {
        SolverFactory<TestdataUnsupportedWildcardSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataUnsupportedWildcardSolution.class, TestdataEntity.class);
        Solver<TestdataUnsupportedWildcardSolution> solver = solverFactory.buildSolver();
        TestdataUnsupportedWildcardSolution solution = new TestdataUnsupportedWildcardSolution();
        solution.setValueList(Arrays.asList(new TestdataValue("v1")));
        solution.setSupersEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataValue("v2")));
        // TODO Ideally, this already fails fast on buildSolverFactory
        assertThatIllegalArgumentException().isThrownBy(() -> solver.solve(solution));
    }

    @Test
    void noProblemFactPropertyWithEasyScoreCalculation() {
        SolverFactory<TestdataNoProblemFactPropertySolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataNoProblemFactPropertySolution.class, TestdataEntity.class);
        solverFactory.buildSolver();
    }

    @Test
    void extended() {
        SolutionDescriptor<TestdataAnnotatedExtendedSolution> solutionDescriptor = TestdataAnnotatedExtendedSolution
                .buildExtendedSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueList",
                "subValueList");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entityList", "subEntityList");
    }

    @Test
    void setProperties() {
        SolutionDescriptor<TestdataSetBasedSolution> solutionDescriptor = TestdataSetBasedSolution.buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("valueSet");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entitySet");
    }

    @Test
    void arrayProperties() {
        SolutionDescriptor<TestdataArrayBasedSolution> solutionDescriptor = TestdataArrayBasedSolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).containsOnlyKeys("values");
        assertThat(solutionDescriptor.getEntityMemberAccessorMap()).isEmpty();
        assertThat(solutionDescriptor.getEntityCollectionMemberAccessorMap()).containsOnlyKeys("entities");
    }

    @Test
    void generic() {
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
    void autoDiscoverProblemFactCollectionPropertyElementTypeUnknown() {
        assertThatIllegalArgumentException().isThrownBy(TestdataUnknownFactTypeSolution::buildSolutionDescriptor);
    }

    @Test
    void autoDiscoverFields() {
        SolutionDescriptor<TestdataAutoDiscoverFieldSolution> solutionDescriptor = TestdataAutoDiscoverFieldSolution
                .buildSolutionDescriptor();
        assertThat(solutionDescriptor.getScoreDefinition()).isInstanceOf(SimpleScoreDefinition.class);
        assertThat(solutionDescriptor.getScoreDefinition().getScoreClass()).isEqualTo(SimpleScore.class);
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
    void autoDiscoverGetters() {
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
    void autoDiscoverFieldsFactCollectionOverriddenToSingleProperty() {
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
    void autoDiscoverGettersFactCollectionOverriddenToSingleProperty() {
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
    void autoDiscoverUnannotatedEntitySubclass() {
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
    void autoDiscoverGettersOverriddenInSubclass() {
        SolutionDescriptor<TestdataExtendedAutoDiscoverGetterSolution> solutionDescriptor =
                TestdataExtendedAutoDiscoverGetterSolution.buildSubclassSolutionDescriptor();
        assertThat(solutionDescriptor.getConstraintConfigurationMemberAccessor().getName())
                .isEqualTo("constraintConfiguration");
        assertThat(solutionDescriptor.getProblemFactMemberAccessorMap()).containsOnlyKeys("constraintConfiguration",
                "singleProblemFact", "problemFactList");
        assertThat(solutionDescriptor.getProblemFactCollectionMemberAccessorMap()).isEmpty();
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

    @Test
    void countUninitializedVariables() {
        int valueCount = 10;
        int entityCount = 3;
        TestdataSolution solution = TestdataSolution.generateSolution(valueCount, entityCount);
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();

        assertThat(solutionDescriptor.countUninitialized(solution)).isZero();

        solution.getEntityList().get(0).setValue(null);
        assertThat(solutionDescriptor.countUninitialized(solution)).isOne();

        solution.getEntityList().forEach(entity -> entity.setValue(null));
        assertThat(solutionDescriptor.countUninitialized(solution)).isEqualTo(entityCount);
    }

    @Test
    void countUnassignedValues() {
        int valueCount = 10;
        int entityCount = 3;
        TestdataListSolution solution = TestdataListSolution.generateInitializedSolution(valueCount, entityCount);
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();

        assertThat(solutionDescriptor.countUninitialized(solution)).isZero();

        List<TestdataListValue> valueList = solution.getEntityList().get(0).getValueList();
        int unassignedValueCount = valueList.size();
        assertThat(valueList).hasSizeGreaterThan(10 / 3);
        valueList.forEach(value -> {
            value.setEntity(null);
            value.setIndex(null);
        });
        valueList.clear();
        assertThat(solutionDescriptor.countUninitialized(solution)).isEqualTo(unassignedValueCount);
    }

    @Test
    void problemScaleBasic() {
        int valueCount = 10;
        int entityCount = 20;
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        TestdataSolution solution = TestdataSolution.generateSolution(valueCount, entityCount);
        assertSoftly(softly -> {
            softly.assertThat(solutionDescriptor.getEntityCount(solution)).isEqualTo(entityCount);
            softly.assertThat(solutionDescriptor.getGenuineVariableCount(solution)).isEqualTo(entityCount);
            softly.assertThat(solutionDescriptor.getMaximumValueCount(solution)).isEqualTo(valueCount);
            softly.assertThat(solutionDescriptor.getProblemScale(solution)).isEqualTo(entityCount * valueCount);
        });
    }

    @Test
    void problemScaleChained() {
        int anchorCount = 20;
        int entityCount = 500;
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        TestdataChainedSolution solution = generateChainedSolution(anchorCount, entityCount);
        assertSoftly(softly -> {
            softly.assertThat(solutionDescriptor.getEntityCount(solution)).isEqualTo(entityCount);
            softly.assertThat(solutionDescriptor.getGenuineVariableCount(solution)).isEqualTo(entityCount * 2);
            softly.assertThat(solutionDescriptor.getMaximumValueCount(solution)).isEqualTo(entityCount + anchorCount);
            softly.assertThat(solutionDescriptor.getProblemScale(solution)).isEqualTo(260000);
        });
    }

    static TestdataChainedSolution generateChainedSolution(int anchorCount, int entityCount) {
        TestdataChainedSolution solution = new TestdataChainedSolution("test solution");
        List<TestdataChainedAnchor> anchorList = IntStream.range(0, anchorCount)
                .mapToObj(Integer::toString)
                .map(TestdataChainedAnchor::new).collect(Collectors.toList());
        solution.setChainedAnchorList(anchorList);
        List<TestdataChainedEntity> entityList = IntStream.range(0, entityCount)
                .mapToObj(Integer::toString)
                .map(TestdataChainedEntity::new).collect(Collectors.toList());
        solution.setChainedEntityList(entityList);
        solution.setUnchainedValueList(Collections.singletonList(new TestdataValue("v")));
        return solution;
    }

    @Test
    void problemScaleList() {
        int valueCount = 500;
        int entityCount = 20;
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        TestdataListSolution solution = TestdataListSolution.generateUninitializedSolution(valueCount, entityCount);
        assertSoftly(softly -> {
            softly.assertThat(solutionDescriptor.getEntityCount(solution)).isEqualTo(entityCount);
            softly.assertThat(solutionDescriptor.getGenuineVariableCount(solution)).isEqualTo(entityCount);
            softly.assertThat(solutionDescriptor.getMaximumValueCount(solution)).isEqualTo(valueCount);
            softly.assertThat(solutionDescriptor.getProblemScale(solution)).isEqualTo(260000);
        });
    }
}
