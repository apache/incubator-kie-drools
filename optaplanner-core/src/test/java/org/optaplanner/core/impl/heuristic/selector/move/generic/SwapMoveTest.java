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
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataOtherValue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SwapMoveTest {

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

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataEntityProvidingEntity.buildEntityDescriptor();

        SwapMove abMove = new SwapMove(entityDescriptor.getGenuineVariableDescriptorList(), a, b);
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

        SwapMove acMove = new SwapMove(entityDescriptor.getGenuineVariableDescriptorList(), a, c);
        a.setValue(v1);
        c.setValue(v4);
        assertEquals(false, acMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        c.setValue(v5);
        assertEquals(false, acMove.isMoveDoable(scoreDirector));

        SwapMove bcMove = new SwapMove(entityDescriptor.getGenuineVariableDescriptorList(), b, c);
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
    }

    @Test
    public void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v1, v2, v3, v4), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v2, v3, v4), null);

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataEntityProvidingEntity.buildEntityDescriptor();

        SwapMove abMove = new SwapMove(entityDescriptor.getGenuineVariableDescriptorList(), a, b);

        a.setValue(v1);
        b.setValue(v1);
        abMove.doMove(scoreDirector);
        assertEquals(v1, a.getValue());
        assertEquals(v1, b.getValue());

        a.setValue(v1);
        b.setValue(v2);
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v1, b.getValue());

        a.setValue(v2);
        b.setValue(v3);
        abMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v2, b.getValue());
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v3, b.getValue());

        SwapMove acMove = new SwapMove(entityDescriptor.getGenuineVariableDescriptorList(), a, c);

        a.setValue(v2);
        c.setValue(v2);
        acMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, c.getValue());

        a.setValue(v3);
        c.setValue(v2);
        acMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v3, c.getValue());

        a.setValue(v3);
        c.setValue(v4);
        acMove.doMove(scoreDirector);
        assertEquals(v4, a.getValue());
        assertEquals(v3, c.getValue());
        acMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v4, c.getValue());

        SwapMove bcMove = new SwapMove(entityDescriptor.getGenuineVariableDescriptorList(), b, c);

        b.setValue(v2);
        c.setValue(v2);
        bcMove.doMove(scoreDirector);
        assertEquals(v2, b.getValue());
        assertEquals(v2, c.getValue());

        b.setValue(v2);
        c.setValue(v3);
        bcMove.doMove(scoreDirector);
        assertEquals(v3, b.getValue());
        assertEquals(v2, c.getValue());

        b.setValue(v2);
        c.setValue(v3);
        bcMove.doMove(scoreDirector);
        assertEquals(v3, b.getValue());
        assertEquals(v2, c.getValue());
        bcMove.doMove(scoreDirector);
        assertEquals(v2, b.getValue());
        assertEquals(v3, c.getValue());
    }

    @Test
    public void toStringTest() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity a = new TestdataEntity("a", null);
        TestdataEntity b = new TestdataEntity("b", v1);
        TestdataEntity c = new TestdataEntity("c", v2);
        EntityDescriptor entityDescriptor = TestdataEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

        assertEquals("a {null} <-> a {null}", new SwapMove(variableDescriptorList, a, a).toString());
        assertEquals("a {null} <-> b {v1}", new SwapMove(variableDescriptorList, a, b).toString());
        assertEquals("a {null} <-> c {v2}", new SwapMove(variableDescriptorList, a, c).toString());
        assertEquals("b {v1} <-> c {v2}", new SwapMove(variableDescriptorList, b, c).toString());
        assertEquals("c {v2} <-> b {v1}", new SwapMove(variableDescriptorList, c, b).toString());
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
        EntityDescriptor entityDescriptor = TestdataMultiVarEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

        assertEquals("a {null, null, null} <-> a {null, null, null}", new SwapMove(variableDescriptorList, a, a).toString());
        assertEquals("a {null, null, null} <-> b {v1, v3, w1}", new SwapMove(variableDescriptorList, a, b).toString());
        assertEquals("a {null, null, null} <-> c {v2, v4, w2}", new SwapMove(variableDescriptorList, a, c).toString());
        assertEquals("b {v1, v3, w1} <-> c {v2, v4, w2}", new SwapMove(variableDescriptorList, b, c).toString());
        assertEquals("c {v2, v4, w2} <-> b {v1, v3, w1}", new SwapMove(variableDescriptorList, c, b).toString());
    }

}
