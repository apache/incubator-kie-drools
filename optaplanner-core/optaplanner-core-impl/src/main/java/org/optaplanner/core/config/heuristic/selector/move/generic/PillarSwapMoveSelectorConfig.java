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

package org.optaplanner.core.config.heuristic.selector.move.generic;

import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "secondaryPillarSelectorConfig",
        "variableNameIncludeList"
})
public class PillarSwapMoveSelectorConfig extends AbstractPillarMoveSelectorConfig<PillarSwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "pillarSwapMoveSelector";

    @XmlElement(name = "secondaryPillarSelector")
    private PillarSelectorConfig secondaryPillarSelectorConfig = null;

    @XmlElementWrapper(name = "variableNameIncludes")
    @XmlElement(name = "variableNameInclude")
    private List<String> variableNameIncludeList = null;

    public PillarSelectorConfig getSecondaryPillarSelectorConfig() {
        return secondaryPillarSelectorConfig;
    }

    public void setSecondaryPillarSelectorConfig(PillarSelectorConfig secondaryPillarSelectorConfig) {
        this.secondaryPillarSelectorConfig = secondaryPillarSelectorConfig;
    }

    public List<String> getVariableNameIncludeList() {
        return variableNameIncludeList;
    }

    public void setVariableNameIncludeList(List<String> variableNameIncludeList) {
        this.variableNameIncludeList = variableNameIncludeList;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public PillarSwapMoveSelectorConfig withSecondaryPillarSelectorConfig(PillarSelectorConfig pillarSelectorConfig) {
        this.setSecondaryPillarSelectorConfig(pillarSelectorConfig);
        return this;
    }

    public PillarSwapMoveSelectorConfig withVariableNameIncludeList(List<String> variableNameIncludeList) {
        this.setVariableNameIncludeList(variableNameIncludeList);
        return this;
    }

    public PillarSwapMoveSelectorConfig withVariableNameIncludes(String... variableNameIncludes) {
        this.setVariableNameIncludeList(List.of(variableNameIncludes));
        return this;
    }

    @Override
    public PillarSwapMoveSelectorConfig inherit(PillarSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        secondaryPillarSelectorConfig = ConfigUtils.inheritConfig(secondaryPillarSelectorConfig,
                inheritedConfig.getSecondaryPillarSelectorConfig());
        variableNameIncludeList = ConfigUtils.inheritMergeableListProperty(
                variableNameIncludeList, inheritedConfig.getVariableNameIncludeList());
        return this;
    }

    @Override
    public PillarSwapMoveSelectorConfig copyConfig() {
        return new PillarSwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (secondaryPillarSelectorConfig != null) {
            secondaryPillarSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + pillarSelectorConfig
                + (secondaryPillarSelectorConfig == null ? "" : ", " + secondaryPillarSelectorConfig) + ")";
    }

}
