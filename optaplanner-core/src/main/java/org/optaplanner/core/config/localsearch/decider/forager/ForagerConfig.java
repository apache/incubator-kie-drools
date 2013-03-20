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
import org.optaplanner.core.config.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.localsearch.decider.forager.AcceptedForager;
import org.optaplanner.core.impl.localsearch.decider.forager.Forager;
import org.optaplanner.core.impl.localsearch.decider.forager.PickEarlyType;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

@XStreamAlias("forager")
public class ForagerConfig {

    private Class<? extends Forager> foragerClass = null;
    @XStreamAlias("deciderScoreComparatorFactory")
    private DeciderScoreComparatorFactoryConfig deciderScoreComparatorFactoryConfig = null;
    private PickEarlyType pickEarlyType = null;

    protected Integer minimalAcceptedSelection = null;

    public Class<? extends Forager> getForagerClass() {
        return foragerClass;
    }

    public void setForagerClass(Class<? extends Forager> foragerClass) {
        this.foragerClass = foragerClass;
    }

    public DeciderScoreComparatorFactoryConfig getDeciderScoreComparatorFactoryConfig() {
        return deciderScoreComparatorFactoryConfig;
    }

    public void setDeciderScoreComparatorFactoryConfig(
            DeciderScoreComparatorFactoryConfig deciderScoreComparatorFactoryConfig) {
        this.deciderScoreComparatorFactoryConfig = deciderScoreComparatorFactoryConfig;
    }

    public PickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(PickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    public Integer getMinimalAcceptedSelection() {
        return minimalAcceptedSelection;
    }

    public void setMinimalAcceptedSelection(Integer minimalAcceptedSelection) {
        this.minimalAcceptedSelection = minimalAcceptedSelection;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Forager buildForager(ScoreDefinition scoreDefinition) {
        if (foragerClass != null) {
            return ConfigUtils.newInstance(this, "foragerClass", foragerClass);
        }
        PickEarlyType pickEarlyType = (this.pickEarlyType == null) ? PickEarlyType.NEVER : this.pickEarlyType;
        int minimalAcceptedSelection = (this.minimalAcceptedSelection == null)
                ? Integer.MAX_VALUE : this.minimalAcceptedSelection;

        AcceptedForager forager = new AcceptedForager(pickEarlyType, minimalAcceptedSelection);
        DeciderScoreComparatorFactoryConfig deciderScoreComparatorFactoryConfig_
                = deciderScoreComparatorFactoryConfig == null ? new DeciderScoreComparatorFactoryConfig()
                : deciderScoreComparatorFactoryConfig;
        forager.setDeciderScoreComparatorFactory(
                deciderScoreComparatorFactoryConfig_.buildDeciderScoreComparatorFactory());
        return forager;
    }

    public void inherit(ForagerConfig inheritedConfig) {
        // TODO this is messed up
        if (foragerClass == null && pickEarlyType == null && minimalAcceptedSelection == null) {
            foragerClass = inheritedConfig.getForagerClass();
            pickEarlyType = inheritedConfig.getPickEarlyType();
            minimalAcceptedSelection = inheritedConfig.getMinimalAcceptedSelection();
        }
        if (deciderScoreComparatorFactoryConfig == null) {
            deciderScoreComparatorFactoryConfig = inheritedConfig.getDeciderScoreComparatorFactoryConfig();
        } else if (inheritedConfig.getDeciderScoreComparatorFactoryConfig() != null) {
            deciderScoreComparatorFactoryConfig.inherit(inheritedConfig.getDeciderScoreComparatorFactoryConfig());
        }
    }

}
