/*
 * Copyright 2010 JBoss Inc
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
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.localsearch.decider.forager.AcceptedForager;
import org.optaplanner.core.impl.localsearch.decider.forager.Forager;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.FinalistPodium;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.HighestScoreFinalistPodium;

@XStreamAlias("localSearchForagerConfig")
public class LocalSearchForagerConfig {

    private Class<? extends Forager> foragerClass = null;

    protected LocalSearchPickEarlyType pickEarlyType = null;
    protected Integer acceptedCountLimit = null;
    protected FinalistPodiumType finalistPodiumType = null;

    public Class<? extends Forager> getForagerClass() {
        return foragerClass;
    }

    public void setForagerClass(Class<? extends Forager> foragerClass) {
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

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Forager buildForager(HeuristicConfigPolicy configPolicy) {
        if (foragerClass != null) {
            if (pickEarlyType != null || acceptedCountLimit != null || finalistPodiumType != null) {
                throw new IllegalArgumentException("The foragerConfig with foragerClass (" + foragerClass
                        + ") must not also have a pickEarlyType (" + pickEarlyType
                        + "), acceptedCountLimit (" + acceptedCountLimit
                        + ") or finalistPodiumType (" + finalistPodiumType + ").");
            }
            return ConfigUtils.newInstance(this, "foragerClass", foragerClass);
        }
        LocalSearchPickEarlyType pickEarlyType_ = (pickEarlyType == null)
                ? LocalSearchPickEarlyType.NEVER : pickEarlyType;
        int acceptedCountLimit_ = (acceptedCountLimit == null) ? Integer.MAX_VALUE : acceptedCountLimit;
        FinalistPodium finalistPodium = finalistPodiumType == null ? new HighestScoreFinalistPodium()
                : finalistPodiumType.buildFinalistPodium();
        return new AcceptedForager(finalistPodium, pickEarlyType_, acceptedCountLimit_);
    }

    public void inherit(LocalSearchForagerConfig inheritedConfig) {
        foragerClass = ConfigUtils.inheritOverwritableProperty(foragerClass,
                inheritedConfig.getForagerClass());
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType,
                inheritedConfig.getPickEarlyType());
        acceptedCountLimit = ConfigUtils.inheritOverwritableProperty(acceptedCountLimit,
                inheritedConfig.getAcceptedCountLimit());
        finalistPodiumType = ConfigUtils.inheritOverwritableProperty(finalistPodiumType,
                inheritedConfig.getFinalistPodiumType());
    }

}
