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

package org.optaplanner.core.config.heuristic.selector.value.chained;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "valueSelectorConfig",
        "minimumSubChainSize",
        "maximumSubChainSize"
})
public class SubChainSelectorConfig extends SelectorConfig<SubChainSelectorConfig> {

    @XmlElement(name = "valueSelector")
    protected ValueSelectorConfig valueSelectorConfig = null;

    protected Integer minimumSubChainSize = null;
    protected Integer maximumSubChainSize = null;

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    /**
     * @return sometimes null
     */
    public Integer getMinimumSubChainSize() {
        return minimumSubChainSize;
    }

    public void setMinimumSubChainSize(Integer minimumSubChainSize) {
        this.minimumSubChainSize = minimumSubChainSize;
    }

    public Integer getMaximumSubChainSize() {
        return maximumSubChainSize;
    }

    public void setMaximumSubChainSize(Integer maximumSubChainSize) {
        this.maximumSubChainSize = maximumSubChainSize;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SubChainSelectorConfig withValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.setValueSelectorConfig(valueSelectorConfig);
        return this;
    }

    public SubChainSelectorConfig withMinimumSubChainSize(Integer minimumSubChainSize) {
        this.setMinimumSubChainSize(minimumSubChainSize);
        return this;
    }

    public SubChainSelectorConfig withMaximumSubChainSize(Integer maximumSubChainSize) {
        this.setMaximumSubChainSize(maximumSubChainSize);
        return this;
    }

    @Override
    public SubChainSelectorConfig inherit(SubChainSelectorConfig inheritedConfig) {
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        minimumSubChainSize = ConfigUtils.inheritOverwritableProperty(minimumSubChainSize,
                inheritedConfig.getMinimumSubChainSize());
        maximumSubChainSize = ConfigUtils.inheritOverwritableProperty(maximumSubChainSize,
                inheritedConfig.getMaximumSubChainSize());
        return this;
    }

    @Override
    public SubChainSelectorConfig copyConfig() {
        return new SubChainSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (valueSelectorConfig != null) {
            valueSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ")";
    }

}
