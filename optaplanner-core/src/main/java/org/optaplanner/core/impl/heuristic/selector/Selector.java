/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListener;

/**
 * General interface for {@link MoveSelector}, {@link EntitySelector} and {@link ValueSelector}
 * which generates {@link Move}s or parts of them.
 */
public interface Selector extends SolverPhaseLifecycleListener {

    /**
     * If true, then {@link #isNeverEnding()} is also true.
     * @return true if any of the value ranges are continuous (as in for example every double value between 1.2 and 1.4)
     */
    boolean isContinuous();

    /**
     * Is true if {@link #isContinuous()} is true
     * or if this selector is in random order (for most cases).
     * Is never true when this selector is in shuffled order (which is less scalable but more exact).
     * @return true if the {@link Iterator#hasNext()} of the {@link Iterator} created by {@link Iterable#iterator()}
     * never returns false.
     */
    boolean isNeverEnding();

    /**
     * Unless this selector itself caches, this returns {@link SelectionCacheType#JUST_IN_TIME},
     * even if a selector child caches.
     * @return never null
     */
    SelectionCacheType getCacheType();

}
