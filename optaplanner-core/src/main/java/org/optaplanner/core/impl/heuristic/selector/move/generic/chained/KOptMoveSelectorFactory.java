/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.KOptMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class KOptMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, KOptMoveSelectorConfig> {

    private static final int K = 3;

    public KOptMoveSelectorFactory(KOptMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ =
                config.getEntitySelectorConfig() == null ? new EntitySelectorConfig() : config.getEntitySelectorConfig();
        EntitySelector<Solution_> entitySelector =
                EntitySelectorFactory.<Solution_> create(entitySelectorConfig_)
                        .buildEntitySelector(configPolicy, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelectorConfig valueSelectorConfig_ =
                config.getValueSelectorConfig() == null ? new ValueSelectorConfig() : config.getValueSelectorConfig();
        ValueSelector<Solution_>[] valueSelectors = new ValueSelector[K - 1];
        for (int i = 0; i < valueSelectors.length; i++) {
            valueSelectors[i] = ValueSelectorFactory.<Solution_> create(valueSelectorConfig_)
                    .buildValueSelector(configPolicy, entitySelector.getEntityDescriptor(), minimumCacheType,
                            SelectionOrder.fromRandomSelectionBoolean(randomSelection));

        }
        return new KOptMoveSelector<>(entitySelector, valueSelectors, randomSelection);
    }
}
