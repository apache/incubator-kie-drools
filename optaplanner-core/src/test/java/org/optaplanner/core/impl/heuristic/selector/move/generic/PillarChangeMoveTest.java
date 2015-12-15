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

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.entityproviding.TestdataEntityProvidingEntity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        GenuineVariableDescriptor variableDescriptor = TestdataEntityProvidingEntity.buildVariableDescriptorForValue();

        PillarChangeMove abMove;
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v1);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v2);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v3);
        assertEquals(true, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        abMove = new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v4);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
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

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        GenuineVariableDescriptor variableDescriptor = TestdataEntityProvidingEntity.buildVariableDescriptorForValue();

        PillarChangeMove abMove = new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v2);

        a.setValue(v3);
        b.setValue(v3);
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, b.getValue());

        a.setValue(v2);
        b.setValue(v2);
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, b.getValue());

        PillarChangeMove abcMove = new PillarChangeMove(Arrays.<Object>asList(a, b, c), variableDescriptor, v2);

        a.setValue(v2);
        b.setValue(v2);
        c.setValue(v2);
        abcMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, b.getValue());
        assertEquals(v2, c.getValue());

        a.setValue(v3);
        b.setValue(v3);
        c.setValue(v3);
        abcMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, b.getValue());
        assertEquals(v2, c.getValue());
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
        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();

        assertEquals("[a, b] {null -> v1}", new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v1).toString());
        assertEquals("[a, b] {null -> v2}", new PillarChangeMove(Arrays.<Object>asList(a, b), variableDescriptor, v2).toString());
        assertEquals("[c, d, e] {v1 -> null}", new PillarChangeMove(Arrays.<Object>asList(c, d, e), variableDescriptor, null).toString());
        assertEquals("[c, d, e] {v1 -> v2}", new PillarChangeMove(Arrays.<Object>asList(c, d, e), variableDescriptor, v2).toString());
        assertEquals("[d] {v1 -> v2}", new PillarChangeMove(Arrays.<Object>asList(d), variableDescriptor, v2).toString());
    }

}
