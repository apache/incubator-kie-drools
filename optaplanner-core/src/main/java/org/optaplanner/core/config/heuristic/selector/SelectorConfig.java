/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.heuristic.selector;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;

/**
 * General superclass for {@link MoveSelectorConfig}, {@link EntitySelectorConfig} and {@link ValueSelectorConfig}.
 */
public abstract class SelectorConfig<C extends SelectorConfig> extends AbstractConfig<C> {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected void validateCacheTypeVersusSelectionOrder(
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        switch (resolvedSelectionOrder) {
            case INHERIT:
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") which should have been resolved by now.");
            case ORIGINAL:
            case RANDOM:
                break;
            case SORTED:
            case SHUFFLED:
            case PROBABILISTIC:
                if (resolvedCacheType.isNotCached()) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") which does not support the resolvedCacheType (" + resolvedCacheType + ").");
                }
                break;
            default:
                throw new IllegalStateException("The resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public void inherit(C inheritedConfig) {
    }

}
