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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfCollection;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertListElementsSameExactly;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingSolution;

public class PillarChangeMoveTest {

    @Test
    public void isMoveDoableValueRangeProviderOnEntity() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        GenuineVariableDescriptor<TestdataEntityProvidingSolution> variableDescriptor = TestdataEntityProvidingEntity
                .buildVariableDescriptorForValue();

        PillarChangeMove<TestdataEntityProvidingSolution> abMove;
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove<>(Arrays.asList(a, b), variableDescriptor, v1);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove<>(Arrays.asList(a, b), variableDescriptor, v2);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove<>(Arrays.asList(a, b), variableDescriptor, v3);
        assertThat(abMove.isMoveDoable(scoreDirector)).isTrue();
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove<>(Arrays.asList(a, b), variableDescriptor, v4);
        assertThat(abMove.isMoveDoable(scoreDirector)).isFalse();
    }

    @Test
    public void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v3, v4, v5), null);

        InnerScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(InnerScoreDirector.class);
        GenuineVariableDescriptor<TestdataEntityProvidingSolution> variableDescriptor = TestdataEntityProvidingEntity
                .buildVariableDescriptorForValue();

        PillarChangeMove<TestdataEntityProvidingSolution> abMove = new PillarChangeMove<>(Arrays.asList(a, b),
                variableDescriptor, v2);

        a.setValue(v3);
        b.setValue(v3);
        abMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v2);

        a.setValue(v2);
        b.setValue(v2);
        abMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v2);

        PillarChangeMove<TestdataEntityProvidingSolution> abcMove = new PillarChangeMove<>(Arrays.asList(a, b, c),
                variableDescriptor, v2);

        a.setValue(v2);
        b.setValue(v2);
        c.setValue(v2);
        abcMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v2);
        assertThat(c.getValue()).isEqualTo(v2);

        a.setValue(v3);
        b.setValue(v3);
        c.setValue(v3);
        abcMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
        assertThat(b.getValue()).isEqualTo(v2);
        assertThat(c.getValue()).isEqualTo(v2);
    }

    @Test
    public void rebase() {
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", null);
        TestdataEntity e3 = new TestdataEntity("e3", v1);
        TestdataEntity e4 = new TestdataEntity("e4", v3);

        TestdataValue destinationV1 = new TestdataValue("v1");
        TestdataValue destinationV2 = new TestdataValue("v2");
        TestdataValue destinationV3 = new TestdataValue("v3");
        TestdataEntity destinationE1 = new TestdataEntity("e1", destinationV1);
        TestdataEntity destinationE2 = new TestdataEntity("e2", null);
        TestdataEntity destinationE3 = new TestdataEntity("e3", destinationV1);
        TestdataEntity destinationE4 = new TestdataEntity("e4", destinationV3);

        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { v2, destinationV2 },
                        { v3, destinationV3 },
                        { e1, destinationE1 },
                        { e2, destinationE2 },
                        { e3, destinationE3 },
                        { e4, destinationE4 },
                });

        assertSameProperties(Arrays.asList(destinationE1, destinationE3), null,
                new PillarChangeMove<>(Arrays.asList(e1, e3), variableDescriptor, null).rebase(destinationScoreDirector));
        assertSameProperties(Arrays.asList(destinationE1, destinationE3), destinationV3,
                new PillarChangeMove<>(Arrays.asList(e1, e3), variableDescriptor, v3).rebase(destinationScoreDirector));
        assertSameProperties(Arrays.asList(destinationE2), destinationV1,
                new PillarChangeMove<>(Arrays.asList(e2), variableDescriptor, v1).rebase(destinationScoreDirector));
        assertSameProperties(Arrays.asList(destinationE1), destinationV2,
                new PillarChangeMove<>(Arrays.asList(e1), variableDescriptor, v2).rebase(destinationScoreDirector));
    }

    public void assertSameProperties(List<Object> pillar, Object toPlanningVariable, PillarChangeMove<?> move) {
        assertListElementsSameExactly(pillar, move.getPillar());
        assertThat(move.getToPlanningValue()).isSameAs(toPlanningVariable);
    }

    @Test
    public void getters() {
        PillarChangeMove<TestdataMultiVarSolution> move = new PillarChangeMove<>(
                Arrays.asList(new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b")),
                TestdataMultiVarEntity.buildVariableDescriptorForPrimaryValue(), null);
        assertAllCodesOfCollection(move.getPillar(), "a", "b");
        assertThat(move.getVariableName()).isEqualTo("primaryValue");
        assertCode(null, move.getToPlanningValue());

        move = new PillarChangeMove<>(
                Arrays.asList(new TestdataMultiVarEntity("c"), new TestdataMultiVarEntity("d")),
                TestdataMultiVarEntity.buildVariableDescriptorForSecondaryValue(), new TestdataValue("1"));
        assertAllCodesOfCollection(move.getPillar(), "c", "d");
        assertThat(move.getVariableName()).isEqualTo("secondaryValue");
        assertCode("1", move.getToPlanningValue());
    }

    @Test
    public void toStringTest() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity a = new TestdataEntity("a", null);
        TestdataEntity b = new TestdataEntity("b", null);
        TestdataEntity c = new TestdataEntity("c", v1);
        TestdataEntity d = new TestdataEntity("d", v1);
        TestdataEntity e = new TestdataEntity("e", v1);
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        assertThat(new PillarChangeMove<>(Arrays.asList(a, b), variableDescriptor, v1).toString())
                .isEqualTo("[a, b] {null -> v1}");
        assertThat(new PillarChangeMove<>(Arrays.asList(a, b), variableDescriptor, v2).toString())
                .isEqualTo("[a, b] {null -> v2}");
        assertThat(new PillarChangeMove<>(Arrays.asList(c, d, e), variableDescriptor, null).toString())
                .isEqualTo("[c, d, e] {v1 -> null}");
        assertThat(new PillarChangeMove<>(Arrays.asList(c, d, e), variableDescriptor, v2).toString())
                .isEqualTo("[c, d, e] {v1 -> v2}");
        assertThat(new PillarChangeMove<>(Arrays.asList(d), variableDescriptor, v2).toString()).isEqualTo("[d] {v1 -> v2}");
    }

}
