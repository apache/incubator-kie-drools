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

package org.optaplanner.core.config.constructionheuristic.decider.forager;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.DefaultConstructionHeuristicForager;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("constructionHeuristicForager")
public class ConstructionHeuristicForagerConfig extends AbstractConfig<ConstructionHeuristicForagerConfig> {

    private ConstructionHeuristicPickEarlyType pickEarlyType = null;

    public ConstructionHeuristicPickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(ConstructionHeuristicPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public ConstructionHeuristicForager buildForager(HeuristicConfigPolicy configPolicy) {
        ConstructionHeuristicPickEarlyType pickEarlyType_;
        if (pickEarlyType == null) {
            pickEarlyType_ = configPolicy.getScoreDirectorFactory().getInitializingScoreTrend().isOnlyDown()
                    ? ConstructionHeuristicPickEarlyType.FIRST_NON_DETERIORATING_SCORE
                    : ConstructionHeuristicPickEarlyType.NEVER;
        } else {
            pickEarlyType_ = pickEarlyType;
        }
        return new DefaultConstructionHeuristicForager(pickEarlyType_);
    }

    @Override
    public ConstructionHeuristicForagerConfig inherit(ConstructionHeuristicForagerConfig inheritedConfig) {
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType, inheritedConfig.getPickEarlyType());
        return this;
    }

    @Override
    public ConstructionHeuristicForagerConfig copyConfig() {
        return new ConstructionHeuristicForagerConfig().inherit(this);
    }

}
