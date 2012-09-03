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

package org.drools.planner.config.heuristic.selector.common;

import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;

/**
 * Defines in which order
 */
public enum SelectionOrder {
    /**
     * Inherit the value from the parent {@value SelectorConfig}. If the parent is {@link #SHUFFLED},
     * the value is set to {@link #ORIGINAL}.
     * <p/>
     * This is the default. If there is no such parent, then it defaults to {@link #RANDOM}.
     */
    INHERIT,
    /**
     * Select in the elements in original order.
     */
    ORIGINAL,
    /**
     * Select in random order, without shuffling the elements at the beginning of the step.
     * Each element might be selected multiple times.
     */
    RANDOM,
    /**
     * Select in random order, with shuffling the elements at the beginning of the step.
     * Each element will be selected exactly once (if all elements end up being selected).
     * Requires {@link SelectionCacheType#STEP} or higher.
     */
    SHUFFLED;

    public static SelectionOrder resolve(SelectionOrder selectionOrder, SelectionOrder inheritedSelectionOrder) {
        if (selectionOrder == null || selectionOrder == INHERIT) {
            if (inheritedSelectionOrder == SHUFFLED) {
                return ORIGINAL;
            }
            return inheritedSelectionOrder;
        } else {
            return selectionOrder;
        }
    }

}
