/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.DefaultSubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;


public class SubChainChangeMoveSelectorTest {

    @Test(expected = IllegalStateException.class)
    public void differentValueDescriptorException() {
        SubChainSelector subChainSelector = mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "value");
        when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "val"));
        SubChainChangeMoveSelector testedSelector =
                new SubChainChangeMoveSelector(subChainSelector, valueSelector, true, true);
    }

    @Test(expected = IllegalStateException.class)
    public void determinedSelectionWithNeverendingChainSelector() {
        SubChainSelector subChainSelector = mock(DefaultSubChainSelector.class);
        when(subChainSelector.isNeverEnding()).thenReturn(true);
        GenuineVariableDescriptor descriptor = SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "value");
        when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "val"));
        when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector =
                new SubChainChangeMoveSelector(subChainSelector, valueSelector, false, true);
    }

    @Test(expected = IllegalStateException.class)
    public void determinedSelectionWithNeverendingValueSelector() {
        SubChainSelector subChainSelector = mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "value");
        when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "val"));
        when(valueSelector.isNeverEnding()).thenReturn(true);
        when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector =
                new SubChainChangeMoveSelector(subChainSelector, valueSelector, false, true);
    }

    @Test
    public void isCountable() {
        SubChainSelector subChainSelector = mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "value");
        when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "val"));
        when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector =
                new SubChainChangeMoveSelector(subChainSelector, valueSelector, true, true);

        when(subChainSelector.isCountable()).thenReturn(false);
        when(valueSelector.isCountable()).thenReturn(true);
        assertFalse(testedSelector.isCountable());

        when(subChainSelector.isCountable()).thenReturn(true);
        when(valueSelector.isCountable()).thenReturn(false);
        assertFalse(testedSelector.isCountable());

        when(subChainSelector.isCountable()).thenReturn(true);
        when(valueSelector.isCountable()).thenReturn(true);
        assertTrue(testedSelector.isCountable());

        when(subChainSelector.isCountable()).thenReturn(false);
        when(valueSelector.isCountable()).thenReturn(false);
        assertFalse(testedSelector.isCountable());
    }

    @Test
    public void getSize() {
        SubChainSelector subChainSelector = mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "value");
        when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                SelectorTestUtils.mockVariableDescriptor(TestdataEntity.class, "val"));
        when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector =
                new SubChainChangeMoveSelector(subChainSelector, valueSelector, true, true);

        when(subChainSelector.getSize()).thenReturn(1L);
        when(valueSelector.getSize()).thenReturn(2L);
        assertTrue(testedSelector.getSize() == 2);

        when(subChainSelector.getSize()).thenReturn(100L);
        when(valueSelector.getSize()).thenReturn(200L);
        assertTrue(testedSelector.getSize() == 20000);
    }

}
