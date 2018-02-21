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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingSolution;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.*;

public class PillarSwapMoveTest {

    @Test
    public void isMoveDoableValueRangeProviderOnEntity() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v4, v5), null);
        TestdataEntityProvidingEntity z = new TestdataEntityProvidingEntity("z", Arrays.asList(v1, v2, v3, v4, v5), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        List<GenuineVariableDescriptor<TestdataEntityProvidingSolution>> variableDescriptorList = TestdataEntityProvidingEntity
                .buildEntityDescriptor().getGenuineVariableDescriptorList();

        PillarSwapMove<TestdataEntityProvidingSolution> abMove = new PillarSwapMove<>(variableDescriptorList, Arrays.<Object>asList(a), Arrays.<Object>asList(b));
        a.setValue(v1);
        b.setValue(v2);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v3);
        assertEquals(true, abMove.isMoveDoable(scoreDirector));
        a.setValue(v3);
        b.setValue(v2);
        assertEquals(true, abMove.isMoveDoable(scoreDirector));
        a.setValue(v3);
        b.setValue(v3);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v4);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));

        PillarSwapMove<TestdataEntityProvidingSolution> acMove = new PillarSwapMove<>(variableDescriptorList, Arrays.<Object>asList(a), Arrays.<Object>asList(c));
        a.setValue(v1);
        c.setValue(v4);
        assertEquals(false, acMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        c.setValue(v5);
        assertEquals(false, acMove.isMoveDoable(scoreDirector));

        PillarSwapMove<TestdataEntityProvidingSolution> bcMove = new PillarSwapMove<>(variableDescriptorList, Arrays.<Object>asList(b), Arrays.<Object>asList(c));
        b.setValue(v2);
        c.setValue(v4);
        assertEquals(false, bcMove.isMoveDoable(scoreDirector));
        b.setValue(v4);
        c.setValue(v5);
        assertEquals(true, bcMove.isMoveDoable(scoreDirector));
        b.setValue(v5);
        c.setValue(v4);
        assertEquals(true, bcMove.isMoveDoable(scoreDirector));
        b.setValue(v5);
        c.setValue(v5);
        assertEquals(false, bcMove.isMoveDoable(scoreDirector));

        PillarSwapMove<TestdataEntityProvidingSolution> abzMove = new PillarSwapMove<>(variableDescriptorList, Arrays.<Object>asList(a, b), Arrays.<Object>asList(z));
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v4);
        assertEquals(false, abzMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v1);
        assertEquals(false, abzMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v3);
        assertEquals(true, abzMove.isMoveDoable(scoreDirector));
        a.setValue(v3);
        b.setValue(v3);
        z.setValue(v2);
        assertEquals(true, abzMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        z.setValue(v2);
        assertEquals(false, abzMove.isMoveDoable(scoreDirector));
    }

    @Test
    public void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3, v4), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v4, v5), null);
        TestdataEntityProvidingEntity z = new TestdataEntityProvidingEntity("z", Arrays.asList(v1, v2, v3, v4, v5), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        List<GenuineVariableDescriptor<TestdataEntityProvidingSolution>> variableDescriptorList = TestdataEntityProvidingEntity
                .buildEntityDescriptor().getGenuineVariableDescriptorList();

        PillarSwapMove<TestdataEntityProvidingSolution> abMove = new PillarSwapMove<>(variableDescriptorList,
                Arrays.<Object>asList(a), Arrays.<Object>asList(b));

        a.setValue(v1);
        b.setValue(v1);
        abMove.doMove(scoreDirector);
        assertEquals(v1, a.getValue());
        assertEquals(v1, b.getValue());

        a.setValue(v2);
        b.setValue(v1);
        abMove.doMove(scoreDirector);
        assertEquals(v1, a.getValue());
        assertEquals(v2, b.getValue());

        a.setValue(v3);
        b.setValue(v2);
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v3, b.getValue());
        abMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v2, b.getValue());

        PillarSwapMove<TestdataEntityProvidingSolution> abzMove = new PillarSwapMove<>(variableDescriptorList, Arrays.asList(a, b), Arrays.<Object>asList(z));

        a.setValue(v3);
        b.setValue(v3);
        z.setValue(v2);
        abzMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, b.getValue());
        assertEquals(v3, z.getValue());
        abzMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v3, b.getValue());
        assertEquals(v2, z.getValue());

        a.setValue(v3);
        b.setValue(v3);
        z.setValue(v4);
        abzMove.doMove(scoreDirector);
        assertEquals(v4, a.getValue());
        assertEquals(v4, b.getValue());
        assertEquals(v3, z.getValue());
        abzMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v3, b.getValue());
        assertEquals(v4, z.getValue());

        PillarSwapMove<TestdataEntityProvidingSolution> abczMove = new PillarSwapMove<>(variableDescriptorList, Arrays.asList(a), Arrays.<Object>asList(b, c, z));

        a.setValue(v2);
        b.setValue(v3);
        c.setValue(v3);
        z.setValue(v3);
        abczMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v2, b.getValue());
        assertEquals(v2, c.getValue());
        assertEquals(v2, z.getValue());
        abczMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v3, b.getValue());
        assertEquals(v3, c.getValue());
        assertEquals(v3, z.getValue());

        PillarSwapMove<TestdataEntityProvidingSolution> abczMove2 = new PillarSwapMove<>(variableDescriptorList, Arrays.<Object>asList(a, b), Arrays.<Object>asList(c, z));

        a.setValue(v4);
        b.setValue(v4);
        c.setValue(v3);
        z.setValue(v3);
        abczMove2.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v3, b.getValue());
        assertEquals(v4, c.getValue());
        assertEquals(v4, z.getValue());
        abczMove2.doMove(scoreDirector);
        assertEquals(v4, a.getValue());
        assertEquals(v4, b.getValue());
        assertEquals(v3, c.getValue());
        assertEquals(v3, z.getValue());
    }

    @Test
    public void rebase() {
        EntityDescriptor<TestdataSolution> entityDescriptor = TestdataEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

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
                entityDescriptor.getSolutionDescriptor(), new Object[][]{
                        {v1, destinationV1},
                        {v2, destinationV2},
                        {v3, destinationV3},
                        {e1, destinationE1},
                        {e2, destinationE2},
                        {e3, destinationE3},
                        {e4, destinationE4},
                });

        assertSameProperties(Arrays.asList(destinationE1, destinationE3), Arrays.asList(destinationE2),
                new PillarSwapMove<>(variableDescriptorList, Arrays.asList(e1, e3), Arrays.asList(e2)).rebase(destinationScoreDirector));
        assertSameProperties(Arrays.asList(destinationE4), Arrays.asList(destinationE1, destinationE3),
                new PillarSwapMove<>(variableDescriptorList, Arrays.asList(e4), Arrays.asList(e1, e3)).rebase(destinationScoreDirector));
    }

    public void assertSameProperties(List<Object> leftPillar, List<Object> rightPillar, PillarSwapMove<?> move) {
        assertListElementsSameExactly(leftPillar, (List<Object>) move.getLeftPillar());
        assertListElementsSameExactly(rightPillar, (List<Object>) move.getRightPillar());
    }

    @Test
    public void getters() {
        GenuineVariableDescriptor<TestdataMultiVarSolution> primaryDescriptor = TestdataMultiVarEntity.buildVariableDescriptorForPrimaryValue();
        GenuineVariableDescriptor<TestdataMultiVarSolution> secondaryDescriptor = TestdataMultiVarEntity.buildVariableDescriptorForSecondaryValue();
        PillarSwapMove<TestdataMultiVarSolution> move = new PillarSwapMove<>(Arrays.asList(primaryDescriptor),
                Arrays.<Object>asList(new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b")),
                Arrays.<Object>asList(new TestdataMultiVarEntity("c"), new TestdataMultiVarEntity("d")));
        assertCollectionContainsExactly(move.getVariableNameList(), "primaryValue");
        assertAllCodesOfCollection(move.getLeftPillar(), "a", "b");
        assertAllCodesOfCollection(move.getRightPillar(), "c", "d");

        move = new PillarSwapMove<>(Arrays.asList(primaryDescriptor, secondaryDescriptor),
                Arrays.<Object>asList(new TestdataMultiVarEntity("e"), new TestdataMultiVarEntity("f")),
                Arrays.<Object>asList(new TestdataMultiVarEntity("g"), new TestdataMultiVarEntity("h")));
        assertCollectionContainsExactly(move.getVariableNameList(), "primaryValue", "secondaryValue");
        assertAllCodesOfCollection(move.getLeftPillar(), "e", "f");
        assertAllCodesOfCollection(move.getRightPillar(), "g", "h");
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
        TestdataEntity f = new TestdataEntity("f", v2);
        TestdataEntity g = new TestdataEntity("g", v2);
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList = TestdataEntity.buildEntityDescriptor()
                .getGenuineVariableDescriptorList();

        assertEquals("[a, b] {null} <-> [c, d, e] {v1}", new PillarSwapMove<>(variableDescriptorList,
                Arrays.<Object>asList(a, b), Arrays.<Object>asList(c, d, e)).toString());
        assertEquals("[b] {null} <-> [c] {v1}", new PillarSwapMove<>(variableDescriptorList,
                Arrays.<Object>asList(b), Arrays.<Object>asList(c)).toString());
        assertEquals("[f, g] {v2} <-> [c, d, e] {v1}", new PillarSwapMove<>(variableDescriptorList,
                Arrays.<Object>asList(f, g), Arrays.<Object>asList(c, d, e)).toString());
    }

}
