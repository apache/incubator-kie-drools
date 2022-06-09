package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public interface ValueMimicRecorder<Solution_> {

    /**
     * @param replayingValueSelector never null
     */
    void addMimicReplayingValueSelector(MimicReplayingValueSelector<Solution_> replayingValueSelector);

    /**
     * @return As defined by {@link ValueSelector#getVariableDescriptor()}
     * @see ValueSelector#getVariableDescriptor()
     */
    GenuineVariableDescriptor<Solution_> getVariableDescriptor();

    /**
     * @return As defined by {@link ValueSelector#isCountable()}
     * @see ValueSelector#isCountable()
     */
    boolean isCountable();

    /**
     * @return As defined by {@link ValueSelector#isNeverEnding()}
     * @see ValueSelector#isNeverEnding()
     */
    boolean isNeverEnding();

    /**
     * @return As defined by {@link EntityIndependentValueSelector#getSize()}
     * @see EntityIndependentValueSelector#getSize()
     */
    long getSize();

    /**
     * @return As defined by {@link ValueSelector#getSize(Object)}
     * @see ValueSelector#getSize(Object)
     */
    long getSize(Object entity);

    /**
     * @return As defined by {@link ValueSelector#endingIterator(Object)}
     * @see ValueSelector#endingIterator(Object)
     */
    Iterator<Object> endingIterator(Object entity);

}
