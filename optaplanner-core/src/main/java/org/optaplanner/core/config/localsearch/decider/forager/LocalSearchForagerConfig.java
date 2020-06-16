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

package org.optaplanner.core.config.localsearch.decider.forager;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.localsearch.decider.forager.AcceptedLocalSearchForager;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForager;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("localSearchForagerConfig")
public class LocalSearchForagerConfig extends AbstractConfig<LocalSearchForagerConfig> {

    protected LocalSearchPickEarlyType pickEarlyType = null;
    protected Integer acceptedCountLimit = null;
    protected FinalistPodiumType finalistPodiumType = null;
    protected Boolean breakTieRandomly = null;

    public LocalSearchPickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(LocalSearchPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    public Integer getAcceptedCountLimit() {
        return acceptedCountLimit;
    }

    public void setAcceptedCountLimit(Integer acceptedCountLimit) {
        this.acceptedCountLimit = acceptedCountLimit;
    }

    public FinalistPodiumType getFinalistPodiumType() {
        return finalistPodiumType;
    }

    public void setFinalistPodiumType(FinalistPodiumType finalistPodiumType) {
        this.finalistPodiumType = finalistPodiumType;
    }

    public Boolean getBreakTieRandomly() {
        return breakTieRandomly;
    }

    public void setBreakTieRandomly(Boolean breakTieRandomly) {
        this.breakTieRandomly = breakTieRandomly;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public LocalSearchForagerConfig withPickEarlyType(LocalSearchPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
        return this;
    }

    public LocalSearchForagerConfig withAcceptedCountLimit(int acceptedCountLimit) {
        this.acceptedCountLimit = acceptedCountLimit;
        return this;
    }

    public LocalSearchForagerConfig withFinalistPodiumType(FinalistPodiumType finalistPodiumType) {
        this.finalistPodiumType = finalistPodiumType;
        return this;
    }

    public LocalSearchForagerConfig withBreakTieRandomly(boolean breakTieRandomly) {
        this.breakTieRandomly = breakTieRandomly;
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public LocalSearchForager buildForager(HeuristicConfigPolicy configPolicy) {
        LocalSearchPickEarlyType pickEarlyType_ = defaultIfNull(pickEarlyType, LocalSearchPickEarlyType.NEVER);
        int acceptedCountLimit_ = defaultIfNull(acceptedCountLimit, Integer.MAX_VALUE);
        FinalistPodiumType finalistPodiumType_ = defaultIfNull(finalistPodiumType, FinalistPodiumType.HIGHEST_SCORE);
        // Breaking ties randomly leads statistically to much better results
        boolean breakTieRandomly_ = defaultIfNull(breakTieRandomly, true);
        return new AcceptedLocalSearchForager(finalistPodiumType_.buildFinalistPodium(), pickEarlyType_,
                acceptedCountLimit_, breakTieRandomly_);
    }

    @Override
    public LocalSearchForagerConfig inherit(LocalSearchForagerConfig inheritedConfig) {
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType,
                inheritedConfig.getPickEarlyType());
        acceptedCountLimit = ConfigUtils.inheritOverwritableProperty(acceptedCountLimit,
                inheritedConfig.getAcceptedCountLimit());
        finalistPodiumType = ConfigUtils.inheritOverwritableProperty(finalistPodiumType,
                inheritedConfig.getFinalistPodiumType());
        breakTieRandomly = ConfigUtils.inheritOverwritableProperty(breakTieRandomly,
                inheritedConfig.getBreakTieRandomly());
        return this;
    }

    @Override
    public LocalSearchForagerConfig copyConfig() {
        return new LocalSearchForagerConfig().inherit(this);
    }

}
