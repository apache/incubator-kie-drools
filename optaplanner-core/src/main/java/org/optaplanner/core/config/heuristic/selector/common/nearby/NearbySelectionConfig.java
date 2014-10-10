/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.config.heuristic.selector.common.nearby;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.BetaDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearEntityNearbyMethod;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.nearby.NearEntityNearbyEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.nearby.NearEntityNearbyValueSelector;

@XStreamAlias("nearbySelection")
public class NearbySelectionConfig extends SelectorConfig {

    @XStreamAlias("originEntitySelector")
    protected EntitySelectorConfig originEntitySelectorConfig = null;
    protected Class<? extends NearEntityNearbyMethod> nearEntityNearbyMethodClass = null;

    protected Double betaDistributionAlpha = null;
    protected Double betaDistributionBeta = null;

    public EntitySelectorConfig getOriginEntitySelectorConfig() {
        return originEntitySelectorConfig;
    }

    public void setOriginEntitySelectorConfig(EntitySelectorConfig originEntitySelectorConfig) {
        this.originEntitySelectorConfig = originEntitySelectorConfig;
    }

    public Class<? extends NearEntityNearbyMethod> getNearEntityNearbyMethodClass() {
        return nearEntityNearbyMethodClass;
    }

    public void setNearEntityNearbyMethodClass(Class<? extends NearEntityNearbyMethod> nearEntityNearbyMethodClass) {
        this.nearEntityNearbyMethodClass = nearEntityNearbyMethodClass;
    }

    public Double getBetaDistributionAlpha() {
        return betaDistributionAlpha;
    }

    public void setBetaDistributionAlpha(Double betaDistributionAlpha) {
        this.betaDistributionAlpha = betaDistributionAlpha;
    }

    public Double getBetaDistributionBeta() {
        return betaDistributionBeta;
    }

    public void setBetaDistributionBeta(Double betaDistributionBeta) {
        this.betaDistributionBeta = betaDistributionBeta;
    }

    public void validateNearby(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        if (originEntitySelectorConfig == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection"
                    + " but lacks a nearbyOriginEntitySelector (" + originEntitySelectorConfig + ").");
        }
        if (nearEntityNearbyMethodClass == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection"
                    + " but lacks a nearEntityNearbyMethodClass (" + nearEntityNearbyMethodClass + ").");
        }
        if (resolvedSelectionOrder != SelectionOrder.ORIGINAL && resolvedSelectionOrder != SelectionOrder.RANDOM) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with nearbyOriginEntitySelector ("  + originEntitySelectorConfig
                    + ") and nearEntityNearbyMethodClass ("  + nearEntityNearbyMethodClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.ORIGINAL + " or " + SelectionOrder.RANDOM + ".");
        }
        if (resolvedCacheType.isCached()) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with nearbyOriginEntitySelector ("  + originEntitySelectorConfig
                    + ") and nearEntityNearbyMethodClass ("  + nearEntityNearbyMethodClass
                    + ") has a resolvedCacheType (" + resolvedCacheType
                    + ") that is cached.");
        }
        if (betaDistributionAlpha != null && betaDistributionAlpha < 0.0) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ")'s betaDistributionAlpha ("  + betaDistributionAlpha
                    + ") must be positive.");
        }
        if (betaDistributionBeta != null && betaDistributionBeta < 0.0) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ")'s betaDistributionBeta ("  + betaDistributionBeta
                    + ") must be positive.");
        }
    }

    public EntitySelector applyNearbyEntitySelector(HeuristicConfigPolicy configPolicy,
        SelectionCacheType minimumCacheType, SelectionCacheType resolvedCacheType,
        SelectionOrder resolvedSelectionOrder, EntitySelector entitySelector) {
        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();
        EntitySelector originEntitySelector = originEntitySelectorConfig.buildEntitySelector(
                configPolicy,
                minimumCacheType, resolvedSelectionOrder);
        NearEntityNearbyMethod nearEntityNearbyMethod = ConfigUtils.newInstance(this,
                "nearEntityNearbyMethodClass", nearEntityNearbyMethodClass);
        // TODO Check nearEntityNearbyMethodClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom = buildNearbyRandom();
        return new NearEntityNearbyEntitySelector(entitySelector, originEntitySelector,
                nearEntityNearbyMethod, nearbyRandom, randomSelection);
    }

    public ValueSelector applyNearbyValueSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector valueSelector) {
        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();
        EntitySelector originEntitySelector = originEntitySelectorConfig.buildEntitySelector(
                configPolicy, minimumCacheType, resolvedSelectionOrder);
        NearEntityNearbyMethod nearEntityNearbyMethod = ConfigUtils.newInstance(this,
                "nearEntityNearbyMethodClass", nearEntityNearbyMethodClass);
        // TODO Check nearEntityNearbyMethodClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom = buildNearbyRandom();
        return new NearEntityNearbyValueSelector(valueSelector, originEntitySelector,
                nearEntityNearbyMethod, nearbyRandom, randomSelection);
    }

    protected NearbyRandom buildNearbyRandom() {
        double betaDistributionAlpha_ = betaDistributionAlpha == null ? 1.0 : betaDistributionAlpha;
        double betaDistributionBeta_ = betaDistributionBeta == null ? 5.0 : betaDistributionBeta;
        return new BetaDistributionNearbyRandom(betaDistributionAlpha_, betaDistributionBeta_);
    }

    public void inherit(NearbySelectionConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (originEntitySelectorConfig == null) {
            originEntitySelectorConfig = inheritedConfig.getOriginEntitySelectorConfig();
        } else if (inheritedConfig.getOriginEntitySelectorConfig() != null) {
            originEntitySelectorConfig.inherit(inheritedConfig.getOriginEntitySelectorConfig());
        }
        nearEntityNearbyMethodClass = ConfigUtils.inheritOverwritableProperty(nearEntityNearbyMethodClass,
                inheritedConfig.getNearEntityNearbyMethodClass());
        betaDistributionAlpha = ConfigUtils.inheritOverwritableProperty(betaDistributionAlpha,
                inheritedConfig.getBetaDistributionAlpha());
        betaDistributionBeta = ConfigUtils.inheritOverwritableProperty(betaDistributionBeta,
                inheritedConfig.getBetaDistributionBeta());
    }

}
