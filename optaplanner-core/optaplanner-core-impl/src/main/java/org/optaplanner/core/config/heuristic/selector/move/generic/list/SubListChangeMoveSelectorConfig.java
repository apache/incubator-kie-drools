/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.config.heuristic.selector.move.generic.list;

import java.util.function.Consumer;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "minimumSubListSize",
        "maximumSubListSize",
        "selectReversingMoveToo",
        "subListSelectorConfig",
        "destinationSelectorConfig"
})
public class SubListChangeMoveSelectorConfig extends MoveSelectorConfig<SubListChangeMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "subListChangeMoveSelector";

    /**
     * @deprecated The minimumSubListSize on the SubListChangeMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMinimumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    protected Integer minimumSubListSize = null;
    /**
     * @deprecated The maximumSubListSize on the SubListChangeMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMaximumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    protected Integer maximumSubListSize = null;
    private Boolean selectReversingMoveToo = null;
    @XmlElement(name = "subListSelector")
    private SubListSelectorConfig subListSelectorConfig = null;
    @XmlElement(name = "destinationSelector")
    private DestinationSelectorConfig destinationSelectorConfig = null;

    /**
     * @deprecated The minimumSubListSize on the SubListChangeMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMinimumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer getMinimumSubListSize() {
        return minimumSubListSize;
    }

    /**
     * @deprecated The minimumSubListSize on the SubListChangeMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#setMinimumSubListSize(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public void setMinimumSubListSize(Integer minimumSubListSize) {
        this.minimumSubListSize = minimumSubListSize;
    }

    /**
     * @deprecated The maximumSubListSize on the SubListChangeMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMaximumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer getMaximumSubListSize() {
        return maximumSubListSize;
    }

    /**
     * @deprecated The maximumSubListSize on the SubListChangeMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#setMaximumSubListSize(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public void setMaximumSubListSize(Integer maximumSubListSize) {
        this.maximumSubListSize = maximumSubListSize;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    public SubListSelectorConfig getSubListSelectorConfig() {
        return subListSelectorConfig;
    }

    public void setSubListSelectorConfig(SubListSelectorConfig subListSelectorConfig) {
        this.subListSelectorConfig = subListSelectorConfig;
    }

    public DestinationSelectorConfig getDestinationSelectorConfig() {
        return destinationSelectorConfig;
    }

    public void setDestinationSelectorConfig(DestinationSelectorConfig destinationSelectorConfig) {
        this.destinationSelectorConfig = destinationSelectorConfig;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SubListChangeMoveSelectorConfig withSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.setSelectReversingMoveToo(selectReversingMoveToo);
        return this;
    }

    public SubListChangeMoveSelectorConfig withSubListSelectorConfig(SubListSelectorConfig subListSelectorConfig) {
        this.setSubListSelectorConfig(subListSelectorConfig);
        return this;
    }

    public SubListChangeMoveSelectorConfig withDestinationSelectorConfig(DestinationSelectorConfig destinationSelectorConfig) {
        this.setDestinationSelectorConfig(destinationSelectorConfig);
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public SubListChangeMoveSelectorConfig inherit(SubListChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        this.minimumSubListSize =
                ConfigUtils.inheritOverwritableProperty(minimumSubListSize, inheritedConfig.minimumSubListSize);
        this.maximumSubListSize =
                ConfigUtils.inheritOverwritableProperty(maximumSubListSize, inheritedConfig.maximumSubListSize);
        this.selectReversingMoveToo =
                ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo, inheritedConfig.selectReversingMoveToo);
        this.subListSelectorConfig =
                ConfigUtils.inheritOverwritableProperty(subListSelectorConfig, inheritedConfig.subListSelectorConfig);
        this.destinationSelectorConfig =
                ConfigUtils.inheritOverwritableProperty(destinationSelectorConfig, inheritedConfig.destinationSelectorConfig);
        return this;
    }

    @Override
    public SubListChangeMoveSelectorConfig copyConfig() {
        return new SubListChangeMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (subListSelectorConfig != null) {
            subListSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (destinationSelectorConfig != null) {
            destinationSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subListSelectorConfig + ", " + destinationSelectorConfig + ")";
    }
}
