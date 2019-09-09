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

package org.optaplanner.core.config.heuristic.selector.move.generic;

import java.util.Comparator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

abstract class AbstractPillarMoveSelectorConfig<T extends AbstractPillarMoveSelectorConfig<T>> extends MoveSelectorConfig<T> {

    protected SubPillarType subPillarType = null;
    protected Class<? extends Comparator> subPillarSequenceComparatorClass = null;
    @XStreamAlias("pillarSelector")
    protected PillarSelectorConfig pillarSelectorConfig = null;

    public SubPillarType getSubPillarType() {
        return subPillarType;
    }

    public void setSubPillarType(final SubPillarType subPillarType) {
        this.subPillarType = subPillarType;
    }

    public Class<? extends Comparator> getSubPillarSequenceComparatorClass() {
        return subPillarSequenceComparatorClass;
    }

    public void setSubPillarSequenceComparatorClass(final Class<? extends Comparator> subPillarSequenceComparatorClass) {
        this.subPillarSequenceComparatorClass = subPillarSequenceComparatorClass;
    }

    public PillarSelectorConfig getPillarSelectorConfig() {
        return pillarSelectorConfig;
    }

    public void setPillarSelectorConfig(PillarSelectorConfig pillarSelectorConfig) {
        this.pillarSelectorConfig = pillarSelectorConfig;
    }

    @Override
    public void inherit(T inheritedConfig) {
        super.inherit(inheritedConfig);
        subPillarType = ConfigUtils.inheritOverwritableProperty(subPillarType, inheritedConfig.getSubPillarType());
        subPillarSequenceComparatorClass = ConfigUtils.inheritOverwritableProperty(subPillarSequenceComparatorClass,
                inheritedConfig.getSubPillarSequenceComparatorClass());
        pillarSelectorConfig = ConfigUtils.inheritConfig(pillarSelectorConfig, inheritedConfig.getPillarSelectorConfig());
    }

}
