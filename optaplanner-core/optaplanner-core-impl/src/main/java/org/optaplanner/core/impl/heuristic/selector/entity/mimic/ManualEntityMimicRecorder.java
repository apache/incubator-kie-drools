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
