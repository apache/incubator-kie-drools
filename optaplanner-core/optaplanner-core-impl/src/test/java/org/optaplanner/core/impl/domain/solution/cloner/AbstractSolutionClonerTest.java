package org.optaplanner.core.impl.domain.solution.cloner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.backlinked.TestdataBacklinkedEntity;
import org.optaplanner.core.impl.testdata.domain.backlinked.TestdataBacklinkedSolution;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedObject;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedSolution;
import org.optaplanner.core.impl.testdata.domain.clone.deepcloning.AnnotatedTestdataVariousTypes;
import org.optaplanner.core.impl.testdata.domain.clone.deepcloning.TestdataDeepCloningEntity;
import org.optaplanner.core.impl.testdata.domain.clone.deepcloning.TestdataDeepCloningSolution;
import org.optaplanner.core.impl.testdata.domain.clone.deepcloning.TestdataVariousTypes;
import org.optaplanner.core.impl.testdata.domain.clone.deepcloning.field.TestdataFieldAnnotatedDeepCloningEntity;
import org.optaplanner.core.impl.testdata.domain.clone.deepcloning.field.TestdataFieldAnnotatedDeepCloningSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataArrayBasedEntity;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataArrayBasedSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataEntityCollectionPropertyEntity;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataSetBasedEntity;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataSetBasedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.thirdparty.TestdataExtendedThirdPartyEntity;
import org.optaplanner.core.impl.testdata.domain.extended.thirdparty.TestdataExtendedThirdPartySolution;
import org.optaplanner.core.impl.testdata.domain.extended.thirdparty.TestdataThirdPartyEntityPojo;
import org.optaplanner.core.impl.testdata.domain.list.externalized.TestdataListEntityExternalized;
import org.optaplanner.core.impl.testdata.domain.list.externalized.TestdataListSolutionExternalized;
import org.optaplanner.core.impl.testdata.domain.list.externalized.TestdataListValueExternalized;
import org.optaplanner.core.impl.testdata.domain.reflect.accessmodifier.TestdataAccessModifierSolution;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedEntity;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedSolution;

public abstract class AbstractSolutionClonerTest {

    protected abstract <Solution_> SolutionCloner<Solution_> createSolutionCloner(
            SolutionDescriptor<Solution_> solutionDescriptor);

    @Test
    void cloneSolution() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataEntity a = new TestdataEntity("a", val1);
        TestdataEntity b = new TestdataEntity("b", val1);
        TestdataEntity c = new TestdataEntity("c", val3);
        TestdataEntity d = new TestdataEntity("d", val3);

        TestdataSolution original = new TestdataSolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);

        TestdataSolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataEntity cloneA = cloneEntityList.get(0);
        TestdataEntity cloneB = cloneEntityList.get(1);
        TestdataEntity cloneC = cloneEntityList.get(2);
        TestdataEntity cloneD = cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertEntityClone(b, cloneB, "b", "1");
        assertEntityClone(c, cloneC, "c", "3");
        assertEntityClone(d, cloneD, "d", "3");

        assertThat(cloneB).isNotSameAs(b);
        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());
    }

    @Test
    void cloneListVariableSolution() {
        SolutionDescriptor<TestdataListSolutionExternalized> solutionDescriptor = SolutionDescriptor.buildSolutionDescriptor(
                TestdataListSolutionExternalized.class,
                TestdataListEntityExternalized.class);

        SolutionCloner<TestdataListSolutionExternalized> cloner = createSolutionCloner(solutionDescriptor);

        TestdataListValueExternalized val1 = new TestdataListValueExternalized("1");
        TestdataListValueExternalized val2 = new TestdataListValueExternalized("2");
        TestdataListValueExternalized val3 = new TestdataListValueExternalized("3");
        TestdataListEntityExternalized a = new TestdataListEntityExternalized("a", new ArrayList<>(List.of(val1, val3)));
        TestdataListEntityExternalized b = new TestdataListEntityExternalized("b", new ArrayList<>(List.of(val2)));

        TestdataListSolutionExternalized original = new TestdataListSolutionExternalized();
        List<TestdataListValueExternalized> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataListEntityExternalized> originalEntityList = List.of(a, b);
        original.setEntityList(originalEntityList);
        original.setScore(SimpleScore.of(1));

        TestdataListSolutionExternalized clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataListEntityExternalized> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(2);
        TestdataListEntityExternalized cloneA = cloneEntityList.get(0);
        TestdataListEntityExternalized cloneB = cloneEntityList.get(1);
        assertEntityListClone(a, cloneA, "a", List.of("1", "3"));
        assertEntityListClone(b, cloneB, "b", List.of("2"));

        assertThat(cloneA).isNotSameAs(a);
        a.getValueList().remove(val1);
        assertThat(a.getValueList()).hasSize(1);
        assertCode("3", a.getValueList().get(0));
        // Clone remains unchanged
        assertThat(cloneA.getValueList()).hasSize(2);
        assertCode("1", cloneA.getValueList().get(0));
    }

    @Test
    void cloneFieldAnnotatedSolution() {
        // can't check cloner class; it doesn't implement any additional interfaces
        Assumptions.assumeFalse(this instanceof GizmoSolutionClonerTest,
                "Gizmo cannot use reflection");

        SolutionDescriptor<TestdataFieldAnnotatedSolution> solutionDescriptor =
                TestdataFieldAnnotatedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataFieldAnnotatedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataFieldAnnotatedEntity a = new TestdataFieldAnnotatedEntity("a", val1);
        TestdataFieldAnnotatedEntity b = new TestdataFieldAnnotatedEntity("b", val1);
        TestdataFieldAnnotatedEntity c = new TestdataFieldAnnotatedEntity("c", val3);
        TestdataFieldAnnotatedEntity d = new TestdataFieldAnnotatedEntity("d", val3);

        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        List<TestdataFieldAnnotatedEntity> originalEntityList = Arrays.asList(a, b, c, d);
        TestdataFieldAnnotatedSolution original = new TestdataFieldAnnotatedSolution("solution",
                valueList, originalEntityList);

        TestdataFieldAnnotatedSolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataFieldAnnotatedEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataFieldAnnotatedEntity cloneA = cloneEntityList.get(0);
        TestdataFieldAnnotatedEntity cloneB = cloneEntityList.get(1);
        TestdataFieldAnnotatedEntity cloneC = cloneEntityList.get(2);
        TestdataFieldAnnotatedEntity cloneD = cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertEntityClone(b, cloneB, "b", "1");
        assertEntityClone(c, cloneC, "c", "3");
        assertEntityClone(d, cloneD, "d", "3");

        assertThat(cloneB).isNotSameAs(b);
    }

    @Test
    void cloneAccessModifierSolution() {
        // can't check cloner class; it doesn't implement any additional interfaces
        Assumptions.assumeFalse(this instanceof GizmoSolutionClonerTest,
                "Gizmo cannot use reflection");

        Object staticObject = new Object();
        TestdataAccessModifierSolution.setStaticField(staticObject);

        SolutionDescriptor<TestdataAccessModifierSolution> solutionDescriptor =
                TestdataAccessModifierSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataAccessModifierSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataEntity a = new TestdataEntity("a", val1);
        TestdataEntity b = new TestdataEntity("b", val1);
        TestdataEntity c = new TestdataEntity("c", val3);
        TestdataEntity d = new TestdataEntity("d", val3);

        TestdataAccessModifierSolution original = new TestdataAccessModifierSolution("solution");
        original.setWriteOnlyField("writeHello");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);

        TestdataAccessModifierSolution clone = cloner.cloneSolution(original);

        assertThat(TestdataAccessModifierSolution.getStaticFinalField()).isSameAs("staticFinalFieldValue");
        assertThat(TestdataAccessModifierSolution.getStaticField()).isSameAs(staticObject);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getFinalField()).isEqualTo(original.getFinalField());
        assertThat(clone.getReadOnlyField()).isEqualTo("readHello");
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataEntity cloneA = cloneEntityList.get(0);
        TestdataEntity cloneB = cloneEntityList.get(1);
        TestdataEntity cloneC = cloneEntityList.get(2);
        TestdataEntity cloneD = cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertEntityClone(b, cloneB, "b", "1");
        assertEntityClone(c, cloneC, "c", "3");
        assertEntityClone(d, cloneD, "d", "3");

        assertThat(cloneB).isNotSameAs(b);
        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());
    }

    @Test
    void cloneExtendedSolution() {
        // can't check cloner class; it doesn't implement any additional interfaces
        Assumptions.assumeFalse(this instanceof GizmoSolutionClonerTest,
                "Gizmo cannot handle subclasses of the class annotated with @PlanningSolution");

        SolutionDescriptor solutionDescriptor = TestdataUnannotatedExtendedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataUnannotatedExtendedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataUnannotatedExtendedEntity a = new TestdataUnannotatedExtendedEntity("a", val1, null);
        TestdataUnannotatedExtendedEntity b = new TestdataUnannotatedExtendedEntity("b", val1, "extraObjectOnEntity");
        TestdataUnannotatedExtendedEntity c = new TestdataUnannotatedExtendedEntity("c", val3);
        TestdataUnannotatedExtendedEntity d = new TestdataUnannotatedExtendedEntity("d", val3, c);
        c.setExtraObject(d);

        TestdataUnannotatedExtendedSolution original = new TestdataUnannotatedExtendedSolution("solution",
                "extraObjectOnSolution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);

        TestdataUnannotatedExtendedSolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getExtraObject()).isEqualTo("extraObjectOnSolution");
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataUnannotatedExtendedEntity cloneA = (TestdataUnannotatedExtendedEntity) cloneEntityList.get(0);
        TestdataUnannotatedExtendedEntity cloneB = (TestdataUnannotatedExtendedEntity) cloneEntityList.get(1);
        TestdataUnannotatedExtendedEntity cloneC = (TestdataUnannotatedExtendedEntity) cloneEntityList.get(2);
        TestdataUnannotatedExtendedEntity cloneD = (TestdataUnannotatedExtendedEntity) cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertThat(cloneA.getExtraObject()).isEqualTo(null);
        assertEntityClone(b, cloneB, "b", "1");
        assertThat(cloneB.getExtraObject()).isEqualTo("extraObjectOnEntity");
        assertEntityClone(c, cloneC, "c", "3");
        assertThat(cloneC.getExtraObject()).isEqualTo(cloneD);
        assertEntityClone(d, cloneD, "d", "3");
        assertThat(cloneD.getExtraObject()).isEqualTo(cloneC);

        assertThat(cloneB).isNotSameAs(b);
        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());
    }

    @Test
    void cloneExtendedThirdPartySolution() {
        SolutionDescriptor<TestdataExtendedThirdPartySolution> solutionDescriptor =
                TestdataExtendedThirdPartySolution.buildSolutionDescriptor();
        SolutionCloner<TestdataExtendedThirdPartySolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataExtendedThirdPartyEntity a = new TestdataExtendedThirdPartyEntity("a", val1, null);
        TestdataExtendedThirdPartyEntity b = new TestdataExtendedThirdPartyEntity("b", val1, "extraObjectOnEntity");
        TestdataExtendedThirdPartyEntity c = new TestdataExtendedThirdPartyEntity("c", val3);
        TestdataExtendedThirdPartyEntity d = new TestdataExtendedThirdPartyEntity("d", val3, c);
        c.setExtraObject(d);

        TestdataExtendedThirdPartySolution original = new TestdataExtendedThirdPartySolution("solution",
                "extraObjectOnSolution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataThirdPartyEntityPojo> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);

        TestdataExtendedThirdPartySolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getExtraObject()).isEqualTo("extraObjectOnSolution");
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataThirdPartyEntityPojo> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataExtendedThirdPartyEntity cloneA = (TestdataExtendedThirdPartyEntity) cloneEntityList.get(0);
        TestdataExtendedThirdPartyEntity cloneB = (TestdataExtendedThirdPartyEntity) cloneEntityList.get(1);
        TestdataExtendedThirdPartyEntity cloneC = (TestdataExtendedThirdPartyEntity) cloneEntityList.get(2);
        TestdataExtendedThirdPartyEntity cloneD = (TestdataExtendedThirdPartyEntity) cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertThat(cloneA.getExtraObject()).isEqualTo(null);
        assertEntityClone(b, cloneB, "b", "1");
        assertThat(cloneB.getExtraObject()).isEqualTo("extraObjectOnEntity");
        assertEntityClone(c, cloneC, "c", "3");
        assertThat(cloneC.getExtraObject()).isEqualTo(cloneD);
        assertEntityClone(d, cloneD, "d", "3");
        assertThat(cloneD.getExtraObject()).isEqualTo(cloneC);

        assertThat(cloneB).isNotSameAs(b);
        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());
    }

    private void assertEntityClone(TestdataEntity originalEntity, TestdataEntity cloneEntity,
            String entityCode, String valueCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertCode(valueCode, cloneEntity.getValue());
    }

    private void assertEntityClone(TestdataFieldAnnotatedEntity originalEntity, TestdataFieldAnnotatedEntity cloneEntity,
            String entityCode, String valueCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertCode(valueCode, cloneEntity.getValue());
    }

    private void assertEntityClone(TestdataThirdPartyEntityPojo originalEntity,
            TestdataThirdPartyEntityPojo cloneEntity, String entityCode, String valueCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertCode(valueCode, cloneEntity.getValue());
    }

    private void assertEntityListClone(TestdataListEntityExternalized originalEntity,
            TestdataListEntityExternalized cloneEntity, String entityCode, List<String> valueCodeList) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertThat(cloneEntity.getValueList()).isNotSameAs(originalEntity.getValueList());
        assertCode(entityCode, cloneEntity);
        assertThat(cloneEntity.getValueList()).hasSameSizeAs(valueCodeList);
        assertThat(cloneEntity.getValueList()).containsExactlyElementsOf(originalEntity.getValueList());
        assertThat(cloneEntity.getValueList()).zipSatisfy(valueCodeList,
                (value, code) -> assertThat(value.getCode()).isEqualTo(code));
    }

    @Test
    void cloneChainedSolution() {
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor =
                TestdataChainedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataChainedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        TestdataChainedSolution original = new TestdataChainedSolution("solution");
        List<TestdataChainedAnchor> anchorList = Arrays.asList(a0, b0);
        original.setChainedAnchorList(anchorList);
        List<TestdataChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        original.setChainedEntityList(originalEntityList);

        TestdataChainedSolution clone = cloner.cloneSolution(original);
        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getChainedAnchorList()).isSameAs(anchorList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataChainedEntity> cloneEntityList = clone.getChainedEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataChainedEntity cloneA1 = cloneEntityList.get(0);
        TestdataChainedEntity cloneA2 = cloneEntityList.get(1);
        TestdataChainedEntity cloneA3 = cloneEntityList.get(2);
        TestdataChainedEntity cloneB1 = cloneEntityList.get(3);
        assertChainedEntityClone(a1, cloneA1, "a1", a0);
        assertChainedEntityClone(a2, cloneA2, "a2", cloneA1);
        assertChainedEntityClone(a3, cloneA3, "a3", cloneA2);
        assertChainedEntityClone(b1, cloneB1, "b1", b0);

        a3.setChainedObject(b1);
        assertCode("b1", a3.getChainedObject());
        // Clone remains unchanged
        assertCode("a2", cloneA3.getChainedObject());
    }

    private void assertChainedEntityClone(TestdataChainedEntity originalEntity, TestdataChainedEntity cloneEntity,
            String entityCode, TestdataChainedObject value) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertThat(cloneEntity.getChainedObject()).isSameAs(value);
    }

    @Test
    void cloneShadowChainedSolution() {
        SolutionDescriptor<TestdataShadowingChainedSolution> solutionDescriptor =
                TestdataShadowingChainedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataShadowingChainedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataShadowingChainedAnchor a0 = new TestdataShadowingChainedAnchor("a0");
        TestdataShadowingChainedEntity a1 = new TestdataShadowingChainedEntity("a1", a0);
        TestdataShadowingChainedEntity a2 = new TestdataShadowingChainedEntity("a2", a1);
        TestdataShadowingChainedEntity a3 = new TestdataShadowingChainedEntity("a3", a2);

        TestdataShadowingChainedAnchor b0 = new TestdataShadowingChainedAnchor("b0");
        TestdataShadowingChainedEntity b1 = new TestdataShadowingChainedEntity("b1", b0);

        a0.setNextEntity(a1);
        a1.setNextEntity(a2);
        a2.setNextEntity(a3);
        a3.setNextEntity(null);

        b0.setNextEntity(b1);
        b1.setNextEntity(null);

        TestdataShadowingChainedSolution original = new TestdataShadowingChainedSolution("solution");
        List<TestdataShadowingChainedAnchor> originalAnchorList = Arrays.asList(a0, b0);
        original.setChainedAnchorList(originalAnchorList);
        List<TestdataShadowingChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        original.setChainedEntityList(originalEntityList);

        TestdataShadowingChainedSolution clone = cloner.cloneSolution(original);
        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataShadowingChainedAnchor> cloneAnchorList = clone.getChainedAnchorList();
        assertThat(cloneAnchorList).isNotSameAs(originalAnchorList);
        assertThat(cloneAnchorList.size()).isEqualTo(2);
        TestdataShadowingChainedAnchor cloneA0 = cloneAnchorList.get(0);
        TestdataShadowingChainedAnchor cloneB0 = cloneAnchorList.get(1);

        List<TestdataShadowingChainedEntity> cloneEntityList = clone.getChainedEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataShadowingChainedEntity cloneA1 = cloneEntityList.get(0);
        TestdataShadowingChainedEntity cloneA2 = cloneEntityList.get(1);
        TestdataShadowingChainedEntity cloneA3 = cloneEntityList.get(2);
        TestdataShadowingChainedEntity cloneB1 = cloneEntityList.get(3);
        assertChainedShadowingAnchorClone(a0, cloneA0, "a0", cloneA1);
        assertChainedShadowingEntityClone(a1, cloneA1, "a1", cloneA0, cloneA2);
        assertChainedShadowingEntityClone(a2, cloneA2, "a2", cloneA1, cloneA3);
        assertChainedShadowingEntityClone(a3, cloneA3, "a3", cloneA2, null);
        assertChainedShadowingAnchorClone(b0, cloneB0, "b0", cloneB1);
        assertChainedShadowingEntityClone(b1, cloneB1, "b1", cloneB0, null);

        a3.setChainedObject(b1);
        assertCode("b1", a3.getChainedObject());
        // Clone remains unchanged.
        assertCode("a2", cloneA3.getChainedObject());
    }

    private void assertChainedShadowingAnchorClone(TestdataShadowingChainedAnchor originalEntity,
            TestdataShadowingChainedAnchor cloneEntity,
            String entityCode, TestdataShadowingChainedEntity next) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertThat(cloneEntity.getNextEntity()).isSameAs(next);
    }

    private void assertChainedShadowingEntityClone(TestdataShadowingChainedEntity originalEntity,
            TestdataShadowingChainedEntity cloneEntity,
            String entityCode, TestdataShadowingChainedObject value, TestdataShadowingChainedEntity next) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertThat(cloneEntity.getChainedObject()).isSameAs(value);
        assertThat(cloneEntity.getNextEntity()).isSameAs(next);
    }

    @Test
    void cloneSetBasedSolution() {
        SolutionDescriptor<TestdataSetBasedSolution> solutionDescriptor =
                TestdataSetBasedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataSetBasedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataSetBasedEntity a = new TestdataSetBasedEntity("a", val1);
        TestdataSetBasedEntity b = new TestdataSetBasedEntity("b", val1);
        TestdataSetBasedEntity c = new TestdataSetBasedEntity("c", val3);
        TestdataSetBasedEntity d = new TestdataSetBasedEntity("d", val3);

        TestdataSetBasedSolution original = new TestdataSetBasedSolution("solution");
        Set<TestdataValue> valueSet = new TreeSet<>((a1, b1) -> {
            return b1.getCode().compareTo(a1.getCode()); // Reverse alphabetic
        });
        valueSet.addAll(Arrays.asList(val1, val2, val3));
        original.setValueSet(valueSet);
        Comparator<TestdataSetBasedEntity> entityComparator = (a1, b1) -> {
            return b1.getCode().compareTo(a1.getCode()); // Reverse alphabetic
        };
        Set<TestdataSetBasedEntity> originalEntitySet = new TreeSet<>(entityComparator);
        originalEntitySet.addAll(Arrays.asList(a, b, c, d));
        original.setEntitySet(originalEntitySet);

        TestdataSetBasedSolution clone = cloner.cloneSolution(original);
        assertThat(clone).isNotSameAs(original);
        assertThat(clone.getValueSet()).isSameAs(valueSet);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        Set<TestdataSetBasedEntity> cloneEntitySet = clone.getEntitySet();
        assertThat(cloneEntitySet).isNotSameAs(originalEntitySet);
        boolean b1 = cloneEntitySet instanceof SortedSet;
        assertThat(b1).isTrue();
        assertThat(((SortedSet) cloneEntitySet).comparator()).isSameAs(entityComparator);
        assertCode("solution", clone);
        assertThat(cloneEntitySet.size()).isEqualTo(4);
        Iterator<TestdataSetBasedEntity> it = cloneEntitySet.iterator();
        // Reverse order because they got sorted
        TestdataSetBasedEntity cloneD = it.next();
        TestdataSetBasedEntity cloneC = it.next();
        TestdataSetBasedEntity cloneB = it.next();
        TestdataSetBasedEntity cloneA = it.next();
        assertSetBasedEntityClone(a, cloneA, "a", "1");
        assertSetBasedEntityClone(b, cloneB, "b", "1");
        assertSetBasedEntityClone(c, cloneC, "c", "3");
        assertSetBasedEntityClone(d, cloneD, "d", "3");

        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());
    }

    private void assertSetBasedEntityClone(TestdataSetBasedEntity originalEntity, TestdataSetBasedEntity cloneEntity,
            String entityCode, String valueCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertCode(valueCode, cloneEntity.getValue());
    }

    @Test
    void cloneEntityCollectionPropertySolution() {
        SolutionDescriptor<TestdataEntityCollectionPropertySolution> solutionDescriptor =
                TestdataEntityCollectionPropertySolution.buildSolutionDescriptor();
        SolutionCloner<TestdataEntityCollectionPropertySolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataEntityCollectionPropertyEntity a = new TestdataEntityCollectionPropertyEntity("a", val1);
        TestdataEntityCollectionPropertyEntity b = new TestdataEntityCollectionPropertyEntity("b", val1);
        TestdataEntityCollectionPropertyEntity c = new TestdataEntityCollectionPropertyEntity("c", val3);
        a.setEntityList(Arrays.asList(b, c));
        a.setEntitySet(new HashSet<>(Arrays.asList(b, c)));
        a.setStringToEntityMap(new HashMap<>());
        a.getStringToEntityMap().put("b", b);
        a.getStringToEntityMap().put("c", c);
        a.setEntityToStringMap(new HashMap<>());
        a.getEntityToStringMap().put(b, "b");
        a.getEntityToStringMap().put(c, "c");
        a.setStringToEntityListMap(new HashMap<>());
        a.getStringToEntityListMap().put("bc", Arrays.asList(b, c));

        b.setEntityList(Collections.emptyList());
        b.setEntitySet(new HashSet<>());
        b.setStringToEntityMap(new HashMap<>());
        b.setEntityToStringMap(null);
        b.setStringToEntityListMap(null);

        c.setEntityList(Arrays.asList(a, c));
        c.setEntitySet(new HashSet<>(Arrays.asList(a, c)));
        c.setStringToEntityMap(new HashMap<>());
        c.getStringToEntityMap().put("a", a);
        c.getStringToEntityMap().put("c", c);
        c.setEntityToStringMap(null);
        c.setStringToEntityListMap(null);

        TestdataEntityCollectionPropertySolution original = new TestdataEntityCollectionPropertySolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntityCollectionPropertyEntity> originalEntityList = Arrays.asList(a, b, c);
        original.setEntityList(originalEntityList);

        TestdataEntityCollectionPropertySolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataEntityCollectionPropertyEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(3);
        TestdataEntityCollectionPropertyEntity cloneA = cloneEntityList.get(0);
        TestdataEntityCollectionPropertyEntity cloneB = cloneEntityList.get(1);
        TestdataEntityCollectionPropertyEntity cloneC = cloneEntityList.get(2);

        assertEntityCollectionPropertyEntityClone(a, cloneA, "a", "1");
        assertThat(cloneA.getEntityList()).isNotSameAs(a.getEntityList());
        assertThat(cloneA.getEntityList().size()).isEqualTo(2);
        assertThat(cloneA.getEntityList().get(0)).isSameAs(cloneB);
        assertThat(cloneA.getEntityList().get(1)).isSameAs(cloneC);
        assertThat(cloneA.getEntitySet()).isNotSameAs(a.getEntitySet());
        assertThat(cloneA.getEntitySet().size()).isEqualTo(2);
        assertThat(cloneA.getStringToEntityMap()).isNotSameAs(a.getStringToEntityMap());
        assertThat(cloneA.getStringToEntityMap().size()).isEqualTo(2);
        assertThat(cloneA.getStringToEntityMap().get("b")).isSameAs(cloneB);
        assertThat(cloneA.getStringToEntityMap().get("c")).isSameAs(cloneC);
        assertThat(cloneA.getEntityToStringMap()).isNotSameAs(a.getEntityToStringMap());
        assertThat(cloneA.getEntityToStringMap().size()).isEqualTo(2);
        assertThat(cloneA.getEntityToStringMap().get(cloneB)).isEqualTo("b");
        assertThat(cloneA.getEntityToStringMap().get(cloneC)).isEqualTo("c");

        assertThat(cloneA.getStringToEntityListMap()).isNotSameAs(a.getStringToEntityListMap());
        assertThat(cloneA.getStringToEntityListMap().size()).isEqualTo(1);
        List<TestdataEntityCollectionPropertyEntity> entityListOfMap = cloneA.getStringToEntityListMap().get("bc");
        assertThat(entityListOfMap.size()).isEqualTo(2);
        assertThat(entityListOfMap.get(0)).isSameAs(cloneB);
        assertThat(entityListOfMap.get(1)).isSameAs(cloneC);

        assertEntityCollectionPropertyEntityClone(b, cloneB, "b", "1");
        assertThat(cloneB.getEntityList().size()).isEqualTo(0);
        assertThat(cloneB.getEntitySet().size()).isEqualTo(0);
        assertThat(cloneB.getStringToEntityMap().size()).isEqualTo(0);
        assertThat(cloneB.getEntityToStringMap()).isNull();
        assertThat(cloneB.getStringToEntityListMap()).isNull();

        assertEntityCollectionPropertyEntityClone(c, cloneC, "c", "3");
        assertThat(cloneC.getEntityList().size()).isEqualTo(2);
        assertThat(cloneC.getEntityList().get(0)).isSameAs(cloneA);
        assertThat(cloneC.getEntityList().get(1)).isSameAs(cloneC);
        assertThat(cloneC.getEntitySet().size()).isEqualTo(2);
        assertThat(cloneC.getStringToEntityMap().size()).isEqualTo(2);
        assertThat(cloneC.getStringToEntityMap().get("a")).isSameAs(cloneA);
        assertThat(cloneC.getStringToEntityMap().get("c")).isSameAs(cloneC);
        assertThat(cloneC.getEntityToStringMap()).isNull();
        assertThat(cloneC.getStringToEntityListMap()).isNull();
    }

    private void assertEntityCollectionPropertyEntityClone(TestdataEntityCollectionPropertyEntity originalEntity,
            TestdataEntityCollectionPropertyEntity cloneEntity, String entityCode, String valueCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertCode(valueCode, cloneEntity.getValue());
    }

    @Test
    void cloneEntityArrayPropertySolution() {
        SolutionDescriptor<TestdataArrayBasedSolution> solutionDescriptor =
                TestdataArrayBasedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataArrayBasedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataArrayBasedEntity a = new TestdataArrayBasedEntity("a", val1);
        TestdataArrayBasedEntity b = new TestdataArrayBasedEntity("b", val1);
        TestdataArrayBasedEntity c = new TestdataArrayBasedEntity("c", val3);
        a.setEntities(new TestdataArrayBasedEntity[] { b, c });

        b.setEntities(new TestdataArrayBasedEntity[] {});

        c.setEntities(new TestdataArrayBasedEntity[] { a, c });

        TestdataArrayBasedSolution original = new TestdataArrayBasedSolution("solution");
        TestdataValue[] values = new TestdataValue[] { val1, val2, val3 };
        original.setValues(values);
        TestdataArrayBasedEntity[] originalEntities = new TestdataArrayBasedEntity[] { a, b, c };
        original.setEntities(originalEntities);

        TestdataArrayBasedSolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getValues()).isSameAs(values);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        TestdataArrayBasedEntity[] cloneEntities = clone.getEntities();
        assertThat(cloneEntities).isNotSameAs(originalEntities);
        assertThat(cloneEntities.length).isEqualTo(3);
        TestdataArrayBasedEntity cloneA = cloneEntities[0];
        TestdataArrayBasedEntity cloneB = cloneEntities[1];
        TestdataArrayBasedEntity cloneC = cloneEntities[2];

        assertEntityArrayPropertyEntityClone(a, cloneA, "a", "1");
        assertThat(cloneA.getEntities()).isNotSameAs(a.getEntities());
        assertThat(cloneA.getEntities().length).isEqualTo(2);
        assertThat(cloneA.getEntities()[0]).isSameAs(cloneB);
        assertThat(cloneA.getEntities()[1]).isSameAs(cloneC);

        assertEntityArrayPropertyEntityClone(b, cloneB, "b", "1");
        assertThat(cloneB.getEntities().length).isEqualTo(0);

        assertEntityArrayPropertyEntityClone(c, cloneC, "c", "3");
        assertThat(cloneC.getEntities().length).isEqualTo(2);
        assertThat(cloneC.getEntities()[0]).isSameAs(cloneA);
        assertThat(cloneC.getEntities()[1]).isSameAs(cloneC);
    }

    private void assertEntityArrayPropertyEntityClone(TestdataArrayBasedEntity originalEntity,
            TestdataArrayBasedEntity cloneEntity, String entityCode, String valueCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertCode(valueCode, cloneEntity.getValue());
    }

    @Test
    void deepPlanningClone() {
        SolutionDescriptor<TestdataDeepCloningSolution> solutionDescriptor =
                TestdataDeepCloningSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataDeepCloningSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataDeepCloningEntity a = new TestdataDeepCloningEntity("a", val1);
        a.setUnannotatedCopiedTestdataVariousTypes(new TestdataVariousTypes());
        a.setUnannotatedClonedTestdataVariousTypes(new TestdataVariousTypes());
        a.setAnnotatedTestdataVariousTypes(new AnnotatedTestdataVariousTypes());
        a.setAnnotatedClonedTestdataVariousTypes(new AnnotatedTestdataVariousTypes());
        a.setSameValueAsUnannotatedClonedTestdataVariousTypes(a.getUnannotatedClonedTestdataVariousTypes());
        List<String> aShadowVariableList = Arrays.asList("shadow a1", "shadow a2");
        a.setShadowVariableList(aShadowVariableList);
        TestdataDeepCloningEntity b = new TestdataDeepCloningEntity("b", val1);
        Map<String, String> bShadowVariableMap = new HashMap<>();
        bShadowVariableMap.put("shadow key b1", "shadow value b1");
        bShadowVariableMap.put("shadow key b2", "shadow value b2");
        b.setShadowVariableMap(bShadowVariableMap);
        TestdataDeepCloningEntity c = new TestdataDeepCloningEntity("c", val3);
        List<String> cShadowVariableList = Arrays.asList("shadow c1", "shadow c2");
        c.setShadowVariableList(cShadowVariableList);
        TestdataDeepCloningEntity d = new TestdataDeepCloningEntity("d", val3);

        TestdataDeepCloningSolution original = new TestdataDeepCloningSolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataDeepCloningEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        List<String> generalShadowVariableList = Arrays.asList("shadow g1", "shadow g2");
        original.setGeneralShadowVariableList(generalShadowVariableList);

        TestdataDeepCloningSolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataDeepCloningEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataDeepCloningEntity cloneA = cloneEntityList.get(0);
        assertDeepCloningEntityClone(a, cloneA, "a");
        TestdataDeepCloningEntity cloneB = cloneEntityList.get(1);
        assertDeepCloningEntityClone(b, cloneB, "b");
        TestdataDeepCloningEntity cloneC = cloneEntityList.get(2);
        assertDeepCloningEntityClone(c, cloneC, "c");
        TestdataDeepCloningEntity cloneD = cloneEntityList.get(3);
        assertDeepCloningEntityClone(d, cloneD, "d");

        List<String> cloneGeneralShadowVariableList = clone.getGeneralShadowVariableList();
        assertThat(cloneGeneralShadowVariableList).isNotSameAs(generalShadowVariableList);
        assertThat(cloneGeneralShadowVariableList.size()).isEqualTo(2);
        assertThat(cloneGeneralShadowVariableList.get(0)).isSameAs(generalShadowVariableList.get(0));
        assertThat(cloneGeneralShadowVariableList.get(1)).isEqualTo(generalShadowVariableList.get(1));

        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());

        b.getShadowVariableMap().put("shadow key b1", "other shadow value b1");
        assertThat(b.getShadowVariableMap().get("shadow key b1")).isEqualTo("other shadow value b1");
        // Clone remains unchanged
        assertThat(cloneB.getShadowVariableMap().get("shadow key b1")).isEqualTo("shadow value b1");

        // Assert that all the various types have been treated properly.
        assertThat(cloneA.getUnannotatedCopiedTestdataVariousTypes())
                .isSameAs(a.getUnannotatedCopiedTestdataVariousTypes());
        assertThat(cloneA.getSameValueAsUnannotatedClonedTestdataVariousTypes())
                .isSameAs(a.getSameValueAsUnannotatedClonedTestdataVariousTypes());

        TestdataVariousTypes originalUnannotatedTypes = a.getUnannotatedClonedTestdataVariousTypes();
        TestdataVariousTypes clonedUnannotatedTypes = cloneA.getUnannotatedClonedTestdataVariousTypes();
        assertThat(clonedUnannotatedTypes).isNotSameAs(originalUnannotatedTypes);
        assertTestdataVariousTypes(originalUnannotatedTypes, clonedUnannotatedTypes);

        AnnotatedTestdataVariousTypes originalAnnotatedTypes = a.getAnnotatedTestdataVariousTypes();
        AnnotatedTestdataVariousTypes clonedAnnotatedTypes = cloneA.getAnnotatedTestdataVariousTypes();
        assertThat(clonedAnnotatedTypes).isNotSameAs(originalAnnotatedTypes);
        assertTestdataVariousTypes(originalAnnotatedTypes, clonedAnnotatedTypes);

        TestdataVariousTypes originalAnnotatedClonedTypes = a.getAnnotatedClonedTestdataVariousTypes();
        TestdataVariousTypes clonedAnnotatedClonedTypes = cloneA.getAnnotatedClonedTestdataVariousTypes();
        assertSoftly(softly -> {
            softly.assertThat(clonedAnnotatedClonedTypes).isNotSameAs(originalAnnotatedClonedTypes);
            softly.assertThat(clonedAnnotatedClonedTypes).isInstanceOf(AnnotatedTestdataVariousTypes.class);
        });
        assertTestdataVariousTypes(originalAnnotatedClonedTypes, clonedAnnotatedClonedTypes);
    }

    private void assertTestdataVariousTypes(TestdataVariousTypes original, TestdataVariousTypes cloned) {
        assertSoftly(softly -> {
            softly.assertThat(cloned.booleanValue).isEqualTo(original.booleanValue);
            softly.assertThat(cloned.byteValue).isEqualTo(original.byteValue);
            softly.assertThat(cloned.charValue).isEqualTo(original.charValue);
            softly.assertThat(cloned.shortValue).isEqualTo(original.shortValue);
            softly.assertThat(cloned.intValue).isEqualTo(original.intValue);
            softly.assertThat(cloned.longValue).isEqualTo(original.longValue);
            softly.assertThat(cloned.floatValue).isEqualTo(original.floatValue);
            softly.assertThat(cloned.doubleValue).isEqualTo(original.doubleValue);
        });
        // Ensure reference types are copied, not cloned.
        assertSoftly(softly -> {
            softly.assertThat(cloned.booleanRef).isSameAs(original.booleanRef);
            softly.assertThat(cloned.byteRef).isSameAs(original.byteRef);
            softly.assertThat(cloned.charRef).isSameAs(original.charRef);
            softly.assertThat(cloned.shortRef).isSameAs(original.shortRef);
            softly.assertThat(cloned.intRef).isSameAs(original.intRef);
            softly.assertThat(cloned.longRef).isSameAs(original.longRef);
            softly.assertThat(cloned.floatRef).isSameAs(original.floatRef);
            softly.assertThat(cloned.doubleRef).isSameAs(original.doubleRef);
            softly.assertThat(cloned.bigInteger).isSameAs(original.bigInteger);
            softly.assertThat(cloned.bigDecimal).isSameAs(original.bigDecimal);
            softly.assertThat(cloned.uuidRef).isSameAs(original.uuidRef);
            softly.assertThat(cloned.stringRef).isSameAs(original.stringRef);
        });
        // Ensure that the rest is cloned properly too.
        assertSoftly(softly -> {
            softly.assertThat(cloned.deepClonedListRef).isNotSameAs(original.deepClonedListRef);
            softly.assertThat(cloned.deepClonedListRef)
                    .first()
                    .isSameAs(original.deepClonedListRef.get(0));
        });
        assertSoftly(softly -> {
            softly.assertThat(cloned.shallowClonedListRef).isSameAs(original.shallowClonedListRef);
            softly.assertThat(cloned.shallowClonedListRef)
                    .first()
                    .isSameAs(original.shallowClonedListRef.get(0));
        });
    }

    private void assertDeepCloningEntityClone(TestdataDeepCloningEntity originalEntity, TestdataDeepCloningEntity cloneEntity,
            String entityCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertThat(cloneEntity.getValue()).isSameAs(originalEntity.getValue());

        List<String> originalShadowVariableList = originalEntity.getShadowVariableList();
        List<String> cloneShadowVariableList = cloneEntity.getShadowVariableList();
        if (originalShadowVariableList == null) {
            assertThat(cloneShadowVariableList).isNull();
        } else {
            assertThat(cloneShadowVariableList).isNotSameAs(originalShadowVariableList);
            assertThat(cloneShadowVariableList.size()).isEqualTo(originalShadowVariableList.size());
            for (int i = 0; i < originalShadowVariableList.size(); i++) {
                assertThat(cloneShadowVariableList.get(i)).isSameAs(originalShadowVariableList.get(i));
            }
        }

        Map<String, String> originalShadowVariableMap = originalEntity.getShadowVariableMap();
        Map<String, String> cloneShadowVariableMap = cloneEntity.getShadowVariableMap();
        if (originalShadowVariableMap == null) {
            assertThat(cloneShadowVariableMap).isNull();
        } else {
            assertThat(cloneShadowVariableMap).isNotSameAs(originalShadowVariableMap);
            assertThat(cloneShadowVariableMap.size()).isEqualTo(originalShadowVariableMap.size());
            for (String key : originalShadowVariableMap.keySet()) {
                assertThat(cloneShadowVariableMap.get(key)).isSameAs(originalShadowVariableMap.get(key));
            }
        }
    }

    @Test
    void fieldAnnotatedDeepPlanningClone() {
        SolutionDescriptor<TestdataFieldAnnotatedDeepCloningSolution> solutionDescriptor =
                TestdataFieldAnnotatedDeepCloningSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataFieldAnnotatedDeepCloningSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataFieldAnnotatedDeepCloningEntity a = new TestdataFieldAnnotatedDeepCloningEntity("a", val1);
        List<String> aShadowVariableList = Arrays.asList("shadow a1", "shadow a2");
        a.setShadowVariableList(aShadowVariableList);
        TestdataFieldAnnotatedDeepCloningEntity b = new TestdataFieldAnnotatedDeepCloningEntity("b", val1);
        Map<String, String> bShadowVariableMap = new HashMap<>();
        bShadowVariableMap.put("shadow key b1", "shadow value b1");
        bShadowVariableMap.put("shadow key b2", "shadow value b2");
        b.setShadowVariableMap(bShadowVariableMap);
        TestdataFieldAnnotatedDeepCloningEntity c = new TestdataFieldAnnotatedDeepCloningEntity("c", val3);
        List<String> cShadowVariableList = Arrays.asList("shadow c1", "shadow c2");
        c.setShadowVariableList(cShadowVariableList);
        TestdataFieldAnnotatedDeepCloningEntity d = new TestdataFieldAnnotatedDeepCloningEntity("d", val3);

        TestdataFieldAnnotatedDeepCloningSolution original = new TestdataFieldAnnotatedDeepCloningSolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataFieldAnnotatedDeepCloningEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        List<String> generalShadowVariableList = Arrays.asList("shadow g1", "shadow g2");
        original.setGeneralShadowVariableList(generalShadowVariableList);

        TestdataFieldAnnotatedDeepCloningSolution clone = cloner.cloneSolution(original);

        assertThat(clone).isNotSameAs(original);
        assertCode("solution", clone);
        assertThat(clone.getValueList()).isSameAs(valueList);
        assertThat(clone.getScore()).isEqualTo(original.getScore());

        List<TestdataFieldAnnotatedDeepCloningEntity> cloneEntityList = clone.getEntityList();
        assertThat(cloneEntityList).isNotSameAs(originalEntityList);
        assertThat(cloneEntityList.size()).isEqualTo(4);
        TestdataFieldAnnotatedDeepCloningEntity cloneA = cloneEntityList.get(0);
        assertDeepCloningEntityClone(a, cloneA, "a");
        TestdataFieldAnnotatedDeepCloningEntity cloneB = cloneEntityList.get(1);
        assertDeepCloningEntityClone(b, cloneB, "b");
        TestdataFieldAnnotatedDeepCloningEntity cloneC = cloneEntityList.get(2);
        assertDeepCloningEntityClone(c, cloneC, "c");
        TestdataFieldAnnotatedDeepCloningEntity cloneD = cloneEntityList.get(3);
        assertDeepCloningEntityClone(d, cloneD, "d");

        List<String> cloneGeneralShadowVariableList = clone.getGeneralShadowVariableList();
        assertThat(cloneGeneralShadowVariableList).isNotSameAs(generalShadowVariableList);
        assertThat(cloneGeneralShadowVariableList.size()).isEqualTo(2);
        assertThat(cloneGeneralShadowVariableList.get(0)).isSameAs(generalShadowVariableList.get(0));
        assertThat(cloneGeneralShadowVariableList.get(1)).isEqualTo(generalShadowVariableList.get(1));

        b.setValue(val2);
        assertCode("2", b.getValue());
        // Clone remains unchanged
        assertCode("1", cloneB.getValue());

        b.getShadowVariableMap().put("shadow key b1", "other shadow value b1");
        assertThat(b.getShadowVariableMap().get("shadow key b1")).isEqualTo("other shadow value b1");
        // Clone remains unchanged
        assertThat(cloneB.getShadowVariableMap().get("shadow key b1")).isEqualTo("shadow value b1");
    }

    @Test
    void supportsEntityToSolutionBacklinking() {
        int entityCount = 2;
        SolutionCloner<TestdataBacklinkedSolution> cloner =
                createSolutionCloner(TestdataBacklinkedSolution.buildSolutionDescriptor());
        TestdataBacklinkedSolution solution = TestdataBacklinkedSolution.generateSolution(2, entityCount);

        TestdataBacklinkedSolution clonedSolution = cloner.cloneSolution(solution);
        assertThat(clonedSolution).isNotSameAs(solution);
        for (int i = 0; i < entityCount; i++) {
            TestdataBacklinkedEntity originalEntity = solution.getEntityList().get(i);
            TestdataBacklinkedEntity clonedEntity = clonedSolution.getEntityList().get(i);
            assertSoftly(softly -> {
                softly.assertThat(clonedEntity).isNotSameAs(originalEntity);
                softly.assertThat(clonedEntity.getSolution()).isSameAs(clonedSolution);
            });
        }
    }

    private void assertDeepCloningEntityClone(TestdataFieldAnnotatedDeepCloningEntity originalEntity,
            TestdataFieldAnnotatedDeepCloningEntity cloneEntity,
            String entityCode) {
        assertThat(cloneEntity).isNotSameAs(originalEntity);
        assertCode(entityCode, originalEntity);
        assertCode(entityCode, cloneEntity);
        assertThat(cloneEntity.getValue()).isSameAs(originalEntity.getValue());

        List<String> originalShadowVariableList = originalEntity.getShadowVariableList();
        List<String> cloneShadowVariableList = cloneEntity.getShadowVariableList();
        if (originalShadowVariableList == null) {
            assertThat(cloneShadowVariableList).isNull();
        } else {
            assertThat(cloneShadowVariableList).isNotSameAs(originalShadowVariableList);
            assertThat(cloneShadowVariableList.size()).isEqualTo(originalShadowVariableList.size());
            for (int i = 0; i < originalShadowVariableList.size(); i++) {
                assertThat(cloneShadowVariableList.get(i)).isSameAs(originalShadowVariableList.get(i));
            }
        }

        Map<String, String> originalShadowVariableMap = originalEntity.getShadowVariableMap();
        Map<String, String> cloneShadowVariableMap = cloneEntity.getShadowVariableMap();
        if (originalShadowVariableMap == null) {
            assertThat(cloneShadowVariableMap).isNull();
        } else {
            assertThat(cloneShadowVariableMap).isNotSameAs(originalShadowVariableMap);
            assertThat(cloneShadowVariableMap.size()).isEqualTo(originalShadowVariableMap.size());
            for (String key : originalShadowVariableMap.keySet()) {
                assertThat(cloneShadowVariableMap.get(key)).isSameAs(originalShadowVariableMap.get(key));
            }
        }
    }

}
