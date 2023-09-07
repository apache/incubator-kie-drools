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

package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class ManualEntityMimicRecorder<Solution_> implements EntityMimicRecorder<Solution_> {

    protected final EntitySelector<Solution_> sourceEntitySelector;
    protected final List<MimicReplayingEntitySelector<Solution_>> replayingEntitySelectorList;

    protected Object recordedEntity;

    public ManualEntityMimicRecorder(EntitySelector<Solution_> sourceEntitySelector) {
        this.sourceEntitySelector = sourceEntitySelector;
        replayingEntitySelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingEntitySelector(MimicReplayingEntitySelector<Solution_> replayingEntitySelector) {
        replayingEntitySelectorList.add(replayingEntitySelector);
    }

    public Object getRecordedEntity() {
        return recordedEntity;
    }

    public void setRecordedEntity(Object recordedEntity) {
        this.recordedEntity = recordedEntity;
        for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
            replayingEntitySelector.recordedNext(recordedEntity);
        }
    }

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return sourceEntitySelector.getEntityDescriptor();
    }

    @Override
    public boolean isCountable() {
        return sourceEntitySelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return sourceEntitySelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return sourceEntitySelector.getSize();
    }

    @Override
    public Iterator<Object> endingIterator() {
        return sourceEntitySelector.endingIterator();
    }

    @Override
    public String toString() {
        return "Manual(" + sourceEntitySelector + ")";
    }

}
