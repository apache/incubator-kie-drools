/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class ManualValueMimicRecorder<Solution_> implements ValueMimicRecorder<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> sourceValueSelector;
    protected final List<MimicReplayingValueSelector<Solution_>> replayingValueSelectorList;

    protected Object recordedValue;

    public ManualValueMimicRecorder(EntityIndependentValueSelector<Solution_> sourceValueSelector) {
        this.sourceValueSelector = sourceValueSelector;
        replayingValueSelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingValueSelector(MimicReplayingValueSelector<Solution_> replayingValueSelector) {
        replayingValueSelectorList.add(replayingValueSelector);
    }

    public Object getRecordedValue() {
        return recordedValue;
    }

    public void setRecordedValue(Object recordedValue) {
        this.recordedValue = recordedValue;
        for (MimicReplayingValueSelector<Solution_> replayingValueSelector : replayingValueSelectorList) {
            replayingValueSelector.recordedNext(recordedValue);
        }
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return sourceValueSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return sourceValueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return sourceValueSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return sourceValueSelector.getSize();
    }

    @Override
    public long getSize(Object entity) {
        return sourceValueSelector.getSize(entity);
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        return sourceValueSelector.endingIterator(entity);
    }

    @Override
    public String toString() {
        return "Manual(" + sourceValueSelector + ")";
    }

}
