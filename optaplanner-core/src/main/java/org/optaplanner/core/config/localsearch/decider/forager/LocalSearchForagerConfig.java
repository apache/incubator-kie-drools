/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.localsearch.decider.forager.AcceptedLocalSearchForager;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForager;

import static org.apache.commons.lang3.ObjectUtils.*;

@XStreamAlias("localSearchForagerConfig")
public class LocalSearchForagerConfig extends AbstractConfig<LocalSearchForagerConfig> {

    @Deprecated // TODO remove in 8.0
    private Class<? extends LocalSearchForager> foragerClass = null;

    protected LocalSearchPickEarlyType pickEarlyType = null;
    protected Integer acceptedCountLimit = null;
    protected FinalistPodiumType finalistPodiumType = null;
    protected Boolean breakTieRandomly = null;

    @Deprecated
    public Class<? extends LocalSearchForager> getForagerClass() {
        return foragerClass;
    }

    @Deprecated
    public void setForagerClass(Class<? extends LocalSearchForager> foragerClass) {
        this.foragerClass = foragerClass;
    }

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
    // Builder methods
    // ************************************************************************

    public LocalSearchForager buildForager(HeuristicConfigPolicy configPolicy) {
        if (foragerClass != null) {
            if (pickEarlyType != null || acceptedCountLimit != null || finalistPodiumType != null) {
                throw new IllegalArgumentException("The forager with foragerClass (" + foragerClass
                        + ") must not also have a pickEarlyType (" + pickEarlyType
                        + "), acceptedCountLimit (" + acceptedCountLimit
                        + ") or finalistPodiumType (" + finalistPodiumType + ").");
            }
            return ConfigUtils.newInstance(this, "foragerClass", foragerClass);
        }
        LocalSearchPickEarlyType pickEarlyType_ = defaultIfNull(pickEarlyType, LocalSearchPickEarlyType.NEVER);
        int acceptedCountLimit_ = defaultIfNull(acceptedCountLimit, Integer.MAX_VALUE);
        FinalistPodiumType finalistPodiumType_ = defaultIfNull(finalistPodiumType, FinalistPodiumType.HIGHEST_SCORE);
        // Breaking ties randomly leads statistically to much better results
        boolean breakTieRandomly_  = defaultIfNull(breakTieRandomly, true);
        return new AcceptedLocalSearchForager(finalistPodiumType_.buildFinalistPodium(), pickEarlyType_,
                acceptedCountLimit_, breakTieRandomly_);
    }

    @Override
    public void inherit(LocalSearchForagerConfig inheritedConfig) {
        foragerClass = ConfigUtils.inheritOverwritableProperty(foragerClass,
                inheritedConfig.getForagerClass());
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType,
                inheritedConfig.getPickEarlyType());
        acceptedCountLimit = ConfigUtils.inheritOverwritableProperty(acceptedCountLimit,
                inheritedConfig.getAcceptedCountLimit());
        finalistPodiumType = ConfigUtils.inheritOverwritableProperty(finalistPodiumType,
                inheritedConfig.getFinalistPodiumType());
        breakTieRandomly = ConfigUtils.inheritOverwritableProperty(breakTieRandomly,
                inheritedConfig.getBreakTieRandomly());
    }

}
