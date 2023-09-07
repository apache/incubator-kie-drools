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

package org.optaplanner.core.config.heuristic.selector.move.factory;

import java.util.Map;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbCustomPropertiesAdapter;

@XmlType(propOrder = {
        "moveIteratorFactoryClass",
        "moveIteratorFactoryCustomProperties"
})
public class MoveIteratorFactoryConfig extends MoveSelectorConfig<MoveIteratorFactoryConfig> {

    public static final String XML_ELEMENT_NAME = "moveIteratorFactory";

    protected Class<? extends MoveIteratorFactory> moveIteratorFactoryClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> moveIteratorFactoryCustomProperties = null;

    public Class<? extends MoveIteratorFactory> getMoveIteratorFactoryClass() {
        return moveIteratorFactoryClass;
    }

    public void setMoveIteratorFactoryClass(Class<? extends MoveIteratorFactory> moveIteratorFactoryClass) {
        this.moveIteratorFactoryClass = moveIteratorFactoryClass;
    }

    public Map<String, String> getMoveIteratorFactoryCustomProperties() {
        return moveIteratorFactoryCustomProperties;
    }

    public void setMoveIteratorFactoryCustomProperties(Map<String, String> moveIteratorFactoryCustomProperties) {
        this.moveIteratorFactoryCustomProperties = moveIteratorFactoryCustomProperties;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public MoveIteratorFactoryConfig
            withMoveIteratorFactoryClass(Class<? extends MoveIteratorFactory> moveIteratorFactoryClass) {
        this.setMoveIteratorFactoryClass(moveIteratorFactoryClass);
        return this;
    }

    public MoveIteratorFactoryConfig
            withMoveIteratorFactoryCustomProperties(Map<String, String> moveIteratorFactoryCustomProperties) {
        this.setMoveIteratorFactoryCustomProperties(moveIteratorFactoryCustomProperties);
        return this;
    }

    @Override
    public MoveIteratorFactoryConfig inherit(MoveIteratorFactoryConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveIteratorFactoryClass = ConfigUtils.inheritOverwritableProperty(
                moveIteratorFactoryClass, inheritedConfig.getMoveIteratorFactoryClass());
        moveIteratorFactoryCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                moveIteratorFactoryCustomProperties, inheritedConfig.getMoveIteratorFactoryCustomProperties());
        return this;
    }

    @Override
    public MoveIteratorFactoryConfig copyConfig() {
        return new MoveIteratorFactoryConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        classVisitor.accept(moveIteratorFactoryClass);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveIteratorFactoryClass + ")";
    }

}
