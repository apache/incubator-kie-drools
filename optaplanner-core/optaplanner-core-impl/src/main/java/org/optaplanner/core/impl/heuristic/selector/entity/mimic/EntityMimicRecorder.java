package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public interface EntityMimicRecorder<Solution_> {

    /**
     * @param replayingEntitySelector never null
     */
    void addMimicReplayingEntitySelector(MimicReplayingEntitySelector<Solution_> replayingEntitySelector);

    /**
     * @return As defined by {@link EntitySelector#getEntityDescriptor()}
     * @see EntitySelector#getEntityDescriptor()
     */
    EntityDescriptor<Solution_> getEntityDescriptor();

    /**
     * @return As defined by {@link EntitySelector#isCountable()}
     * @see EntitySelector#isCountable()
     */
    boolean isCountable();

    /**
     * @return As defined by {@link EntitySelector#isNeverEnding()}
     * @see EntitySelector#isNeverEnding()
     */
    boolean isNeverEnding();

    /**
     * @return As defined by {@link EntitySelector#getSize()}
     * @see EntitySelector#getSize()
     */
    long getSize();

    /**
     * @return As defined by {@link EntitySelector#endingIterator()}
     * @see EntitySelector#endingIterator()
     */
    Iterator<Object> endingIterator();

}
