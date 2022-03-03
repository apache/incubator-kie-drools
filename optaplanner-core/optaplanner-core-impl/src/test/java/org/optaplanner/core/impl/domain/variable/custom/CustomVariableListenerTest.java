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

package org.optaplanner.core.impl.domain.variable.custom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.shadow.cyclic.TestdataSevenNonCyclicShadowedSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.cyclic.invalid.TestdataCyclicReferencedShadowedSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.cyclic.invalid.TestdataCyclicShadowedSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.extended.TestdataExtendedShadowedChildEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.extended.TestdataExtendedShadowedParentEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.extended.TestdataExtendedShadowedSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.manytomany.TestdataManyToManyShadowedEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.manytomany.TestdataManyToManyShadowedEntityUniqueEvents;
import org.optaplanner.core.impl.testdata.domain.shadow.manytomany.TestdataManyToManyShadowedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class CustomVariableListenerTest {

    @Test
    void cyclic() {
        assertThatIllegalStateException().isThrownBy(TestdataCyclicShadowedSolution::buildSolutionDescriptor);
    }

    @Test
    void cyclicReferenced() {
        assertThatIllegalStateException().isThrownBy(TestdataCyclicReferencedShadowedSolution::buildSolutionDescriptor);
    }

    @Test()
    void nonCyclicWithSevenDisorderedShadows() {
        SolutionDescriptor<TestdataSevenNonCyclicShadowedSolution> solutionDescriptor =
                TestdataSevenNonCyclicShadowedSolution.buildSolutionDescriptor();
    }

    @Test
    void extendedZigZag() {
        GenuineVariableDescriptor<TestdataExtendedShadowedSolution> variableDescriptor =
                TestdataExtendedShadowedParentEntity.buildVariableDescriptorForValue();
        InnerScoreDirector<TestdataExtendedShadowedSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataExtendedShadowedParentEntity a = new TestdataExtendedShadowedParentEntity("a", null);
        TestdataExtendedShadowedParentEntity b = new TestdataExtendedShadowedParentEntity("b", null);
        TestdataExtendedShadowedChildEntity c = new TestdataExtendedShadowedChildEntity("c", null);

        TestdataExtendedShadowedSolution solution = new TestdataExtendedShadowedSolution("solution");
        solution.setEntityList(Arrays.asList(a, b, c));
        solution.setValueList(Arrays.asList(val1, val2, val3));
        scoreDirector.setWorkingSolution(solution);

        scoreDirector.beforeVariableChanged(variableDescriptor, a);
        a.setValue(val1);
        scoreDirector.afterVariableChanged(variableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getFirstShadow()).isEqualTo("1/firstShadow");
        assertThat(a.getThirdShadow()).isEqualTo(null);

        scoreDirector.beforeVariableChanged(variableDescriptor, a);
        a.setValue(val3);
        scoreDirector.afterVariableChanged(variableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getFirstShadow()).isEqualTo("3/firstShadow");
        assertThat(a.getThirdShadow()).isEqualTo(null);

        scoreDirector.beforeVariableChanged(variableDescriptor, c);
        c.setValue(val1);
        scoreDirector.afterVariableChanged(variableDescriptor, c);
        scoreDirector.triggerVariableListeners();
        assertThat(c.getFirstShadow()).isEqualTo("1/firstShadow");
        assertThat(c.getSecondShadow()).isEqualTo("1/firstShadow/secondShadow");
        assertThat(c.getThirdShadow()).isEqualTo("1/firstShadow/secondShadow/thirdShadow");

        scoreDirector.beforeVariableChanged(variableDescriptor, c);
        c.setValue(val3);
        scoreDirector.afterVariableChanged(variableDescriptor, c);
        scoreDirector.triggerVariableListeners();
        assertThat(c.getFirstShadow()).isEqualTo("3/firstShadow");
        assertThat(c.getSecondShadow()).isEqualTo("3/firstShadow/secondShadow");
        assertThat(c.getThirdShadow()).isEqualTo("3/firstShadow/secondShadow/thirdShadow");
    }

    @Test
    void manyToMany() {
        EntityDescriptor<TestdataManyToManyShadowedSolution> entityDescriptor =
                TestdataManyToManyShadowedEntity.buildEntityDescriptor();
        GenuineVariableDescriptor<TestdataManyToManyShadowedSolution> primaryVariableDescriptor =
                entityDescriptor.getGenuineVariableDescriptor("primaryValue");
        GenuineVariableDescriptor<TestdataManyToManyShadowedSolution> secondaryVariableDescriptor =
                entityDescriptor.getGenuineVariableDescriptor("secondaryValue");
        InnerScoreDirector<TestdataManyToManyShadowedSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(primaryVariableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataValue val4 = new TestdataValue("4");
        TestdataManyToManyShadowedEntity a = new TestdataManyToManyShadowedEntity("a", null, null);
        TestdataManyToManyShadowedEntity b = new TestdataManyToManyShadowedEntity("b", null, null);
        TestdataManyToManyShadowedEntity c = new TestdataManyToManyShadowedEntity("c", null, null);

        TestdataManyToManyShadowedSolution solution = new TestdataManyToManyShadowedSolution("solution");
        solution.setEntityList(Arrays.asList(a, b, c));
        solution.setValueList(Arrays.asList(val1, val2, val3, val4));
        scoreDirector.setWorkingSolution(solution);

        scoreDirector.beforeVariableChanged(primaryVariableDescriptor, a);
        a.setPrimaryValue(val1);
        scoreDirector.afterVariableChanged(primaryVariableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getComposedCode()).isEqualTo(null);
        assertThat(a.getReverseComposedCode()).isEqualTo(null);

        scoreDirector.beforeVariableChanged(secondaryVariableDescriptor, a);
        a.setSecondaryValue(val3);
        scoreDirector.afterVariableChanged(secondaryVariableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getComposedCode()).isEqualTo("1-3");
        assertThat(a.getReverseComposedCode()).isEqualTo("3-1");

        scoreDirector.beforeVariableChanged(secondaryVariableDescriptor, a);
        a.setSecondaryValue(val4);
        scoreDirector.afterVariableChanged(secondaryVariableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getComposedCode()).isEqualTo("1-4");
        assertThat(a.getReverseComposedCode()).isEqualTo("4-1");

        scoreDirector.beforeVariableChanged(primaryVariableDescriptor, a);
        a.setPrimaryValue(val2);
        scoreDirector.afterVariableChanged(primaryVariableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getComposedCode()).isEqualTo("2-4");
        assertThat(a.getReverseComposedCode()).isEqualTo("4-2");

        scoreDirector.beforeVariableChanged(primaryVariableDescriptor, a);
        a.setPrimaryValue(null);
        scoreDirector.afterVariableChanged(primaryVariableDescriptor, a);
        scoreDirector.triggerVariableListeners();
        assertThat(a.getComposedCode()).isEqualTo(null);
        assertThat(a.getReverseComposedCode()).isEqualTo(null);

        scoreDirector.beforeVariableChanged(primaryVariableDescriptor, c);
        c.setPrimaryValue(val1);
        scoreDirector.afterVariableChanged(primaryVariableDescriptor, c);
        scoreDirector.beforeVariableChanged(secondaryVariableDescriptor, c);
        c.setSecondaryValue(val3);
        scoreDirector.afterVariableChanged(secondaryVariableDescriptor, c);
        scoreDirector.triggerVariableListeners();
        assertThat(c.getComposedCode()).isEqualTo("1-3");
        assertThat(c.getReverseComposedCode()).isEqualTo("3-1");
    }

    @Test
    void manyToManyRequiresUniqueEntityEvents() {
        EntityDescriptor<TestdataManyToManyShadowedSolution> entityDescriptor =
                TestdataManyToManyShadowedEntityUniqueEvents.buildEntityDescriptor();
        GenuineVariableDescriptor<TestdataManyToManyShadowedSolution> primaryVariableDescriptor =
                entityDescriptor.getGenuineVariableDescriptor("primaryValue");
        GenuineVariableDescriptor<TestdataManyToManyShadowedSolution> secondaryVariableDescriptor =
                entityDescriptor.getGenuineVariableDescriptor("secondaryValue");
        InnerScoreDirector<TestdataManyToManyShadowedSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(entityDescriptor.getSolutionDescriptor());

        TestdataValue val1 = new TestdataValue("1");
        TestdataManyToManyShadowedEntityUniqueEvents b = new TestdataManyToManyShadowedEntityUniqueEvents("b", null, null);

        TestdataManyToManyShadowedSolution solution = new TestdataManyToManyShadowedSolution("solution");
        solution.setEntityList(List.of(b));
        solution.setValueList(List.of(val1));
        scoreDirector.setWorkingSolution(solution);

        scoreDirector.beforeEntityAdded(b);
        scoreDirector.afterEntityAdded(b);
        scoreDirector.beforeVariableChanged(primaryVariableDescriptor, b);
        b.setPrimaryValue(val1);
        scoreDirector.afterVariableChanged(primaryVariableDescriptor, b);
        scoreDirector.beforeVariableChanged(secondaryVariableDescriptor, b);
        b.setSecondaryValue(val1);
        scoreDirector.afterVariableChanged(secondaryVariableDescriptor, b);
        scoreDirector.triggerVariableListeners();

        verify(scoreDirector).setWorkingSolution(solution);
        verify(scoreDirector).beforeEntityAdded(b);
        verify(scoreDirector).afterEntityAdded(b);
        verify(scoreDirector).beforeVariableChanged(primaryVariableDescriptor, b);
        verify(scoreDirector).afterVariableChanged(primaryVariableDescriptor, b);
        verify(scoreDirector).beforeVariableChanged(secondaryVariableDescriptor, b);
        verify(scoreDirector).afterVariableChanged(secondaryVariableDescriptor, b);
        verify(scoreDirector).triggerVariableListeners();

        // The 1st element is caused by afterEntityAdded(). It is unique because it's the only one of the "EntityAdded" type.
        // The 2nd element is caused by the first afterVariableChanged() event.
        // The second afterVariableChangedEvent was deduplicated.
        assertThat(b.getComposedCodeLog()).containsExactly("1-1", "1-1");
    }

}
