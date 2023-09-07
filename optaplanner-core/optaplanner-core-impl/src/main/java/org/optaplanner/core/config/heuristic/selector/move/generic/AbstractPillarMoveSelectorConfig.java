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

import java.util.Comparator;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "subPillarType",
        "subPillarSequenceComparatorClass",
        "pillarSelectorConfig"
})
public abstract class AbstractPillarMoveSelectorConfig<Config_ extends AbstractPillarMoveSelectorConfig<Config_>>
        extends MoveSelectorConfig<Config_> {

    protected SubPillarType subPillarType = null;
    protected Class<? extends Comparator> subPillarSequenceComparatorClass = null;
    @XmlElement(name = "pillarSelector")
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

    // ************************************************************************
    // With methods
    // ************************************************************************

    public Config_ withSubPillarType(SubPillarType subPillarType) {
        this.setSubPillarType(subPillarType);
        return (Config_) this;
    }

    public Config_ withSubPillarSequenceComparatorClass(Class<? extends Comparator> subPillarSequenceComparatorClass) {
        this.setSubPillarSequenceComparatorClass(subPillarSequenceComparatorClass);
        return (Config_) this;
    }

    public Config_ withPillarSelectorConfig(PillarSelectorConfig pillarSelectorConfig) {
        this.setPillarSelectorConfig(pillarSelectorConfig);
        return (Config_) this;
    }

    @Override
    public Config_ inherit(Config_ inheritedConfig) {
        super.inherit(inheritedConfig);
        subPillarType = ConfigUtils.inheritOverwritableProperty(subPillarType, inheritedConfig.getSubPillarType());
        subPillarSequenceComparatorClass = ConfigUtils.inheritOverwritableProperty(subPillarSequenceComparatorClass,
                inheritedConfig.getSubPillarSequenceComparatorClass());
        pillarSelectorConfig = ConfigUtils.inheritConfig(pillarSelectorConfig, inheritedConfig.getPillarSelectorConfig());
        return (Config_) this;
    }

    @Override
    protected void visitCommonReferencedClasses(Consumer<Class<?>> classVisitor) {
        super.visitCommonReferencedClasses(classVisitor);
        classVisitor.accept(subPillarSequenceComparatorClass);
        if (pillarSelectorConfig != null) {
            pillarSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

}
