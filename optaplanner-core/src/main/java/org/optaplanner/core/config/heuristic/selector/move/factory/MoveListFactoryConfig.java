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

package org.optaplanner.core.config.heuristic.selector.move.factory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactoryToMoveSelectorBridge;

@XStreamAlias("moveListFactory")
public class MoveListFactoryConfig extends MoveSelectorConfig<MoveListFactoryConfig> {

    protected Class<? extends MoveListFactory> moveListFactoryClass = null;

    public Class<? extends MoveListFactory> getMoveListFactoryClass() {
        return moveListFactoryClass;
    }

    public void setMoveListFactoryClass(Class<? extends MoveListFactory> moveListFactoryClass) {
        this.moveListFactoryClass = moveListFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    protected boolean isBaseInherentlyCached() {
        return true;
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (moveListFactoryClass == null) {
            throw new IllegalArgumentException("The moveListFactoryConfig (" + this
                    + ") lacks a moveListFactoryClass (" + moveListFactoryClass + ").");
        }
        MoveListFactory moveListFactory = ConfigUtils.newInstance(this,
                "moveListFactoryClass", moveListFactoryClass);
        // MoveListFactoryToMoveSelectorBridge caches by design, so it uses the minimumCacheType
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) < 0) {
            // cacheType upgrades to SelectionCacheType.STEP (without shuffling) because JIT is not supported
            minimumCacheType = SelectionCacheType.STEP;
        }
        return new MoveListFactoryToMoveSelectorBridge(moveListFactory, minimumCacheType, randomSelection);
    }

    @Override
    public void inherit(MoveListFactoryConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveListFactoryClass = ConfigUtils.inheritOverwritableProperty(
                moveListFactoryClass, inheritedConfig.getMoveListFactoryClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveListFactoryClass + ")";
    }

}
