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

package org.drools.traits.core.factmodel;

import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.factmodel.DefaultClassBuilderFactory;
import org.drools.core.rule.TypeDeclaration;
import org.drools.traits.core.factmodel.traits.TraitClassBuilderImpl;
import org.drools.traits.core.factmodel.traits.TraitMapPropertyWrapperClassBuilderImpl;
import org.drools.traits.core.factmodel.traits.TraitMapProxyClassBuilderImpl;
import org.drools.traits.core.factmodel.traits.TraitProxyClassBuilder;

public class TraitClassBuilderFactory extends DefaultClassBuilderFactory {

    // Trait property wrappers
    private ClassBuilder propertyWrapperBuilder;

    @Override
    public ClassBuilder getPropertyWrapperBuilder() {
        if (propertyWrapperBuilder == null) {
            propertyWrapperBuilder = new TraitMapPropertyWrapperClassBuilderImpl();
        }
        return propertyWrapperBuilder;
    }

    @Override
    public void setPropertyWrapperBuilder(ClassBuilder pcb) {
        propertyWrapperBuilder = pcb;
    }

    // Trait proxy wrappers
    private TraitProxyClassBuilder traitProxyBuilder;

    public TraitProxyClassBuilder getTraitProxyBuilder() {
        if (traitProxyBuilder == null) {
            traitProxyBuilder = new TraitMapProxyClassBuilderImpl();
        }
        return traitProxyBuilder;
    }

    public void setTraitProxyBuilder(TraitProxyClassBuilder tpcb) {
        traitProxyBuilder = tpcb;
    }

    private TraitClassBuilderImpl traitClassBuilder;

    public TraitClassBuilderImpl getTraitClassBuilder() {
        if(traitClassBuilder == null) {
            traitClassBuilder = new TraitClassBuilderImpl();
        }
        return traitClassBuilder;
    }


    @Override
    public ClassBuilder getClassBuilder(TypeDeclaration type) {
        switch (type.getKind()) {
            case TRAIT: return getTraitClassBuilder();
            case ENUM: return getEnumClassBuilder();
            case CLASS: default: return getBeanClassBuilder();
        }
    }
}
