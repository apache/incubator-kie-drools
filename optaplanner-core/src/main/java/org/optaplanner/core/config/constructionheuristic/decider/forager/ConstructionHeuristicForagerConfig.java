/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.DefaultConstructionHeuristicForager;
import org.optaplanner.core.impl.score.definition.FeasibilityScoreDefinition;

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
                    ? ConstructionHeuristicPickEarlyType.FIRST_NON_DETERIORATING_SCORE : ConstructionHeuristicPickEarlyType.NEVER;
        } else {
            if ((pickEarlyType == ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE
                    || pickEarlyType == ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD)
                    && !(configPolicy.getScoreDefinition() instanceof FeasibilityScoreDefinition)) {
                throw new IllegalArgumentException("The pickEarlyType (" + pickEarlyType
                        + ") is not compatible with the scoreDefinition (" + configPolicy.getScoreDefinition() + ").");

            }
            pickEarlyType_ = pickEarlyType;
        }
        return new DefaultConstructionHeuristicForager(pickEarlyType_);
    }

    @Override
    public void inherit(ConstructionHeuristicForagerConfig inheritedConfig) {
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType, inheritedConfig.getPickEarlyType());
    }

}
