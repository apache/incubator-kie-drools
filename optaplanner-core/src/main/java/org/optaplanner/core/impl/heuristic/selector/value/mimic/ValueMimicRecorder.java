/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public interface ValueMimicRecorder {

    /**
     * @param replayingValueSelector never null
     */
    void addMimicReplayingValueSelector(MimicReplayingValueSelector replayingValueSelector);

    /**
     * @return As defined by {@link ValueSelector#getVariableDescriptor()}
     * @see ValueSelector#getVariableDescriptor()
     */
    GenuineVariableDescriptor getVariableDescriptor();

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
