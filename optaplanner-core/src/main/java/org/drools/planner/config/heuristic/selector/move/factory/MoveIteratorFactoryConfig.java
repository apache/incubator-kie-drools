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

package org.drools.planner.config.heuristic.selector.move.factory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.optaplanner.core.domain.solution.SolutionDescriptor;
import org.optaplanner.core.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.heuristic.selector.move.factory.MoveIteratorFactoryToMoveSelectorBridge;

@XStreamAlias("moveIteratorFactory")
public class MoveIteratorFactoryConfig extends MoveSelectorConfig {

    protected Class<? extends MoveIteratorFactory> moveIteratorFactoryClass = null;

    public Class<? extends MoveIteratorFactory> getMoveIteratorFactoryClass() {
        return moveIteratorFactoryClass;
    }

    public void setMoveIteratorFactoryClass(Class<? extends MoveIteratorFactory> moveIteratorFactoryClass) {
        this.moveIteratorFactoryClass = moveIteratorFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        MoveIteratorFactory moveIteratorFactory = ConfigUtils.newInstance(this,
                "moveIteratorFactoryClass", moveIteratorFactoryClass);
        return new MoveIteratorFactoryToMoveSelectorBridge(moveIteratorFactory, randomSelection);
    }

    public void inherit(MoveIteratorFactoryConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveIteratorFactoryClass = ConfigUtils.inheritOverwritableProperty(
                moveIteratorFactoryClass, inheritedConfig.getMoveIteratorFactoryClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveIteratorFactoryClass + ")";
    }

}
