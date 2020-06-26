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

package org.optaplanner.core.config.phase.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.config.util.KeyAsElementMapConverter;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.custom.CustomPhase;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.phase.custom.DefaultCustomPhase;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.util.JaxbCustomPropertiesAdapter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("customPhase")
public class CustomPhaseConfig extends PhaseConfig<CustomPhaseConfig> {

    public static final String XML_ELEMENT_NAME = "customPhase";

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XmlElement(name = "customPhaseCommandClass")
    @XStreamImplicit(itemFieldName = "customPhaseCommandClass")
    protected List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> customProperties = null;

    @XmlTransient
    @XStreamOmitField
    protected List<CustomPhaseCommand<?>> customPhaseCommandList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public List<Class<? extends CustomPhaseCommand>> getCustomPhaseCommandClassList() {
        return customPhaseCommandClassList;
    }

    public void setCustomPhaseCommandClassList(List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList) {
        this.customPhaseCommandClassList = customPhaseCommandClassList;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public List<CustomPhaseCommand<?>> getCustomPhaseCommandList() {
        return customPhaseCommandList;
    }

    public void setCustomPhaseCommandList(List<CustomPhaseCommand<?>> customPhaseCommandList) {
        this.customPhaseCommandList = customPhaseCommandList;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public CustomPhaseConfig withCustomPhaseCommandClassList(
            List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList) {
        this.customPhaseCommandClassList = customPhaseCommandClassList;
        return this;
    }

    public void withCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public CustomPhaseConfig withCustomPhaseCommandList(List<CustomPhaseCommand<?>> customPhaseCommandList) {
        this.customPhaseCommandList = customPhaseCommandList;
        return this;
    }

    public CustomPhaseConfig withCustomPhaseCommands(CustomPhaseCommand<?>... customPhaseCommands) {
        this.customPhaseCommandList = Arrays.asList(customPhaseCommands);
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public CustomPhase buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        DefaultCustomPhase phase = new DefaultCustomPhase(
                phaseIndex, solverConfigPolicy.getLogIndentation(), bestSolutionRecaller,
                buildPhaseTermination(phaseConfigPolicy, solverTermination));
        if (ConfigUtils.isEmptyCollection(customPhaseCommandClassList)
                && ConfigUtils.isEmptyCollection(customPhaseCommandList)) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <customPhaseCommandClass> in the <customPhase> configuration.");
        }
        List<CustomPhaseCommand<?>> customPhaseCommandList_ = new ArrayList<>(
                (customPhaseCommandClassList == null ? 0 : customPhaseCommandClassList.size())
                        + (customPhaseCommandList == null ? 0 : customPhaseCommandList.size()));
        if (customPhaseCommandClassList != null) {
            for (Class<? extends CustomPhaseCommand> customPhaseCommandClass : customPhaseCommandClassList) {
                CustomPhaseCommand customPhaseCommand = ConfigUtils.newInstance(this,
                        "customPhaseCommandClass", customPhaseCommandClass);
                ConfigUtils.applyCustomProperties(customPhaseCommand, "customPhaseCommandClass",
                        customProperties, "customProperties");
                customPhaseCommandList_.add(customPhaseCommand);
            }
        }
        if (customPhaseCommandList != null) {
            customPhaseCommandList_.addAll(customPhaseCommandList);
        }
        phase.setCustomPhaseCommandList(customPhaseCommandList_);
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertStepScoreFromScratch(true);
        }
        return phase;
    }

    @Override
    public CustomPhaseConfig inherit(CustomPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        customPhaseCommandClassList = ConfigUtils.inheritMergeableListProperty(
                customPhaseCommandClassList, inheritedConfig.getCustomPhaseCommandClassList());
        customPhaseCommandList = ConfigUtils.inheritMergeableListProperty(
                customPhaseCommandList, inheritedConfig.getCustomPhaseCommandList());
        customProperties = ConfigUtils.inheritMergeableMapProperty(
                customProperties, inheritedConfig.getCustomProperties());
        return this;
    }

    @Override
    public CustomPhaseConfig copyConfig() {
        return new CustomPhaseConfig().inherit(this);
    }

}
