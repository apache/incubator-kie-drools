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

package org.drools.planner.config.heuristic.selector.move.composite;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.composite.CartesianProductMoveSelector;

@XStreamAlias("cartesianProductMoveSelector")
public class CartesianProductMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamImplicit()
    private List<MoveSelectorConfig> moveSelectorConfigList = null;

    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        List<MoveSelector> moveSelectorList = new ArrayList<MoveSelector>(moveSelectorConfigList.size());
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList) {
            moveSelectorList.add(
                    moveSelectorConfig.buildMoveSelector(environmentMode, solutionDescriptor,
                            minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection)));
        }

        return new CartesianProductMoveSelector(moveSelectorList, randomSelection);
    }

    public void inherit(CartesianProductMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveSelectorConfigList = ConfigUtils.inheritMergeableListProperty(
                moveSelectorConfigList, inheritedConfig.getMoveSelectorConfigList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveSelectorConfigList + ")";
    }

}
