/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.factmodel;

import java.io.Serializable;

import org.drools.core.factmodel.traits.TraitCoreService;

import static org.drools.core.reteoo.KieComponentFactory.fromTraitRegistry;

public class ClassBuilderFactory implements Serializable {


    // Generic beans

    private  BeanClassBuilder beanClassBuilder = new DefaultBeanClassBuilder(true);

    public ClassBuilder getBeanClassBuilder() {
        return beanClassBuilder;
    }

    public void setBeanClassBuilder( BeanClassBuilder bcb ) {
        beanClassBuilder = bcb;
    }

    public void setDefaultBeanClassBuilder() {
        beanClassBuilder = new DefaultBeanClassBuilder(true) ;
    }

    public static ClassBuilder getDefaultBeanClassBuilder() {
        return new DefaultBeanClassBuilder(true);
    }

    private  EnumClassBuilder enumClassBuilder = new DefaultEnumClassBuilder();

    public EnumClassBuilder getEnumClassBuilder() {
        return enumClassBuilder;
    }

    public void setEnumClassBuilder( EnumClassBuilder ecb ) {
        enumClassBuilder = ecb;
    }

    public void setDefaultEnumClassBuilder() {
        enumClassBuilder = new DefaultEnumClassBuilder();
    }

    public static EnumClassBuilder getDefaultEnumClassBuilder() {
        return new DefaultEnumClassBuilder();
    }




    // Trait interfaces
    private ClassBuilder traitBuilder;

    public ClassBuilder getTraitBuilder() {
        return traitBuilder;
    }

    public void setTraitBuilder( ClassBuilder tcb ) {
        traitBuilder = tcb;
    }

    // Trait property wrappers


    private ClassBuilder propertyWrapperBuilder;

    public ClassBuilder getPropertyWrapperBuilder() {
        if (propertyWrapperBuilder == null) {
            propertyWrapperBuilder = fromTraitRegistry(TraitCoreService::createPropertyWrapperBuilder).orElse(null);
        }
        return propertyWrapperBuilder;
    }

    public void setPropertyWrapperBuilder( ClassBuilder pcb ) {
        propertyWrapperBuilder = pcb;
    }

    // Trait proxy wrappers

    private ClassBuilder traitProxyBuilder;

    public ClassBuilder getTraitProxyBuilder() {
        if (traitProxyBuilder == null) {
            traitProxyBuilder = fromTraitRegistry(TraitCoreService::createTraitProxyClassBuilder).orElse(null);
        }
        return traitProxyBuilder;
    }

    public void setTraitProxyBuilder(ClassBuilder tpcb ) {
        traitProxyBuilder = tpcb;
    }
}
