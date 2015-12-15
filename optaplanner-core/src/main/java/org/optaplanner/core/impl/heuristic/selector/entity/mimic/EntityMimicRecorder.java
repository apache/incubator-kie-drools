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

package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public interface EntityMimicRecorder {

    /**
     * @param replayingEntitySelector never null
     */
    void addMimicReplayingEntitySelector(MimicReplayingEntitySelector replayingEntitySelector);

    /**
     * @return As defined by {@link EntitySelector#getEntityDescriptor()}
     * @see EntitySelector#getEntityDescriptor()
     */
    EntityDescriptor getEntityDescriptor();

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
