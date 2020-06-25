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
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataOtherValue;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingSolution;

public class ChangeMoveTest {

    @Test
    public void isMoveDoable() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor<TestdataEntityProvidingSolution> entityDescriptor = TestdataEntityProvidingEntity
                .buildEntityDescriptor();

        ChangeMove<TestdataEntityProvidingSolution> aMove = new ChangeMove<>(a,
                entityDescriptor.getGenuineVariableDescriptor("value"), v2);
        a.setValue(v1);
        assertThat(aMove.isMoveDoable(scoreDirector)).isTrue();

        a.setValue(v2);
        assertThat(aMove.isMoveDoable(scoreDirector)).isFalse();

        a.setValue(v3);
        assertThat(aMove.isMoveDoable(scoreDirector)).isTrue();
    }

    @Test
    public void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);

        InnerScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(InnerScoreDirector.class);
        EntityDescriptor<TestdataEntityProvidingSolution> entityDescriptor = TestdataEntityProvidingEntity
                .buildEntityDescriptor();

        ChangeMove<TestdataEntityProvidingSolution> aMove = new ChangeMove<>(a,
                entityDescriptor.getGenuineVariableDescriptor("value"), v2);
        a.setValue(v1);
        aMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);

        a.setValue(v2);
        aMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);

        a.setValue(v3);
        aMove.doMove(scoreDirector);
        assertThat(a.getValue()).isEqualTo(v2);
    }

    @Test
    public void rebase() {
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", null);
        TestdataEntity e3 = new TestdataEntity("e3", v1);

        TestdataValue destinationV1 = new TestdataValue("v1");
        TestdataValue destinationV2 = new TestdataValue("v2");
        TestdataEntity destinationE1 = new TestdataEntity("e1", destinationV1);
        TestdataEntity destinationE2 = new TestdataEntity("e2", null);
        TestdataEntity destinationE3 = new TestdataEntity("e3", destinationV1);

        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { v2, destinationV2 },
                        { e1, destinationE1 },
                        { e2, destinationE2 },
                        { e3, destinationE3 },
                });

        assertSameProperties(destinationE1, null,
                new ChangeMove<>(e1, variableDescriptor, null).rebase(destinationScoreDirector));
        assertSameProperties(destinationE1, destinationV1,
                new ChangeMove<>(e1, variableDescriptor, v1).rebase(destinationScoreDirector));
        assertSameProperties(destinationE2, null,
                new ChangeMove<>(e2, variableDescriptor, null).rebase(destinationScoreDirector));
        assertSameProperties(destinationE3, destinationV2,
                new ChangeMove<>(e3, variableDescriptor, v2).rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object entity, Object toPlanningVariable, ChangeMove<?> move) {
        assertThat(move.getEntity()).isSameAs(entity);
        assertThat(move.getToPlanningValue()).isSameAs(toPlanningVariable);
    }

    @Test
    public void getters() {
        ChangeMove<TestdataMultiVarSolution> move = new ChangeMove<>(new TestdataMultiVarEntity("a"),
                TestdataMultiVarEntity.buildVariableDescriptorForPrimaryValue(), null);
        assertCode("a", move.getEntity());
        assertThat(move.getVariableName()).isEqualTo("primaryValue");
        assertCode(null, move.getToPlanningValue());

        move = new ChangeMove<>(new TestdataMultiVarEntity("b"),
                TestdataMultiVarEntity.buildVariableDescriptorForSecondaryValue(), new TestdataValue("1"));
        assertCode("b", move.getEntity());
        assertThat(move.getVariableName()).isEqualTo("secondaryValue");
        assertCode("1", move.getToPlanningValue());
    }

    @Test
    public void toStringTest() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity a = new TestdataEntity("a", null);
        TestdataEntity b = new TestdataEntity("b", v1);
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        assertThat(new ChangeMove<>(a, variableDescriptor, null).toString()).isEqualTo("a {null -> null}");
        assertThat(new ChangeMove<>(a, variableDescriptor, v1).toString()).isEqualTo("a {null -> v1}");
        assertThat(new ChangeMove<>(a, variableDescriptor, v2).toString()).isEqualTo("a {null -> v2}");
        assertThat(new ChangeMove<>(b, variableDescriptor, null).toString()).isEqualTo("b {v1 -> null}");
        assertThat(new ChangeMove<>(b, variableDescriptor, v1).toString()).isEqualTo("b {v1 -> v1}");
        assertThat(new ChangeMove<>(b, variableDescriptor, v2).toString()).isEqualTo("b {v1 -> v2}");
    }

    @Test
    public void toStringTestMultiVar() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");
        TestdataOtherValue w1 = new TestdataOtherValue("w1");
        TestdataOtherValue w2 = new TestdataOtherValue("w2");
        TestdataMultiVarEntity a = new TestdataMultiVarEntity("a", null, null, null);
        TestdataMultiVarEntity b = new TestdataMultiVarEntity("b", v1, v3, w1);
        TestdataMultiVarEntity c = new TestdataMultiVarEntity("c", v2, v4, w2);
        EntityDescriptor<TestdataMultiVarSolution> entityDescriptor = TestdataMultiVarEntity.buildEntityDescriptor();
        GenuineVariableDescriptor<TestdataMultiVarSolution> variableDescriptor = entityDescriptor
                .getGenuineVariableDescriptor("secondaryValue");

        assertThat(new ChangeMove<>(a, variableDescriptor, null).toString()).isEqualTo("a {null -> null}");
        assertThat(new ChangeMove<>(a, variableDescriptor, v1).toString()).isEqualTo("a {null -> v1}");
        assertThat(new ChangeMove<>(a, variableDescriptor, v2).toString()).isEqualTo("a {null -> v2}");
        assertThat(new ChangeMove<>(b, variableDescriptor, null).toString()).isEqualTo("b {v3 -> null}");
        assertThat(new ChangeMove<>(b, variableDescriptor, v1).toString()).isEqualTo("b {v3 -> v1}");
        assertThat(new ChangeMove<>(b, variableDescriptor, v2).toString()).isEqualTo("b {v3 -> v2}");
        assertThat(new ChangeMove<>(c, variableDescriptor, v3).toString()).isEqualTo("c {v4 -> v3}");
    }

}
