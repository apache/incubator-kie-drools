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

import org.drools.core.factmodel.traits.*;

import java.io.Serializable;

public class ClassBuilderFactory implements Serializable {


    // Generic beans

    private  BeanClassBuilder beanClassBuilder = new DefaultBeanClassBuilder();

    public ClassBuilder getBeanClassBuilder() {
        return beanClassBuilder;
    }

    public void setBeanClassBuilder( BeanClassBuilder bcb ) {
        beanClassBuilder = bcb;
    }

    public void setDefaultBeanClassBuilder() {
        beanClassBuilder = new DefaultBeanClassBuilder() ;
    }
    
    public static ClassBuilder getDefaultBeanClassBuilder() {
        return new DefaultBeanClassBuilder();
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

    private TraitClassBuilder traitBuilder = new TraitClassBuilderImpl();

    public ClassBuilder getTraitBuilder() {
        return traitBuilder;
    }

    public void setTraitBuilder( TraitClassBuilder tcb ) {
        traitBuilder = tcb;
    }

    public void setDefaultTraitBuilder() {
        traitBuilder = new TraitClassBuilderImpl();
    }    
    
    public static ClassBuilder getDefaultTraitBuilder() {
        return new TraitClassBuilderImpl();
    }



    // Trait property wrappers


    private ClassBuilder propertyWrapperBuilder = new TraitMapPropertyWrapperClassBuilderImpl();

    public ClassBuilder getPropertyWrapperBuilder() {
        return propertyWrapperBuilder;
    }

    public void setPropertyWrapperBuilder( TraitPropertyWrapperClassBuilder pcb ) {
        propertyWrapperBuilder = pcb;
    }

    public void setDefaultPropertyWrapperBuilder() {
        propertyWrapperBuilder = new TraitTriplePropertyWrapperClassBuilderImpl();
    }

    public static ClassBuilder getDefaultPropertyWrapperBuilder() {
        return new TraitTriplePropertyWrapperClassBuilderImpl();
    }




    // Trait proxy wrappers

    private  TraitProxyClassBuilder traitProxyBuilder = new TraitMapProxyClassBuilderImpl();

    public ClassBuilder getTraitProxyBuilder() {
        return traitProxyBuilder;
    }

    public void setTraitProxyBuilder( TraitProxyClassBuilder tpcb ) {
        traitProxyBuilder = tpcb;
    }

    public void setDefaultTraitProxyBuilder() {
        traitProxyBuilder = new TraitTripleProxyClassBuilderImpl();
    }

    public static ClassBuilder getDefaultTraitProxyBuilder() {
        return new TraitTripleProxyClassBuilderImpl();
    }




}
