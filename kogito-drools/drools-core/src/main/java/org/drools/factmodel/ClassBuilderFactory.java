/*
 * Copyright 2011 JBoss Inc
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

package org.drools.factmodel;

import org.drools.base.TypeResolver;
import org.drools.factmodel.traits.*;
import org.drools.util.ServiceRegistryImpl;

import java.util.concurrent.Callable;

public class ClassBuilderFactory {


    // Generic beans

    private static BeanClassBuilder beanClassBuilderProvider;

    public static synchronized ClassBuilder getBeanClassBuilderService( ) {
        if ( beanClassBuilderProvider == null) {
            loadBeanClassBuilderProvider( );
        }
        return beanClassBuilderProvider;
    }


    public static synchronized void setBeanClassBuilderService( BeanClassBuilder provider ) {
        ClassBuilderFactory.beanClassBuilderProvider = provider;
    }

    private static void loadBeanClassBuilderProvider( ) {
        String defaultName = "org.drools.factmodel.DefaultBeanClassBuilder";
        try {
            ServiceRegistryImpl.getInstance().addDefault( BeanClassBuilder.class, defaultName );
            setBeanClassBuilderService(ServiceRegistryImpl.getInstance().get(BeanClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }



    // Trait interfaces


    private static TraitClassBuilder traitBuilderProvider = new TraitClassBuilderImpl();

    public static synchronized ClassBuilder getTraitBuilderService() {
        if ( traitBuilderProvider == null) {
            loadTraitBuilderProvider();
        }
        return traitBuilderProvider;
    }

    public static synchronized void setTraitBuilderService( TraitClassBuilder provider ) {
        ClassBuilderFactory.traitBuilderProvider = provider;
    }

    private static void loadTraitBuilderProvider() {
        ServiceRegistryImpl.getInstance().addDefault( TraitClassBuilder.class, "org.drools.factmodel.traits.TraitClassBuilderImpl" );
        setTraitBuilderService(ServiceRegistryImpl.getInstance().get(TraitClassBuilder.class));
    }






    // Trait property wrappers


    private static ClassBuilder propertyWrapperBuilderProvider = new TraitPropertyWrapperClassBuilderImpl();

    public static synchronized ClassBuilder getPropertyWrapperBuilderService() {
        if ( propertyWrapperBuilderProvider == null) {
            loadPropertyWrapperClassBuilderProvider();
        }
        return propertyWrapperBuilderProvider;
    }

    public static synchronized void setPropertyWrapperBuilderService( TraitPropertyWrapperClassBuilder provider ) {
        ClassBuilderFactory.propertyWrapperBuilderProvider = provider;
    }

    private static void loadPropertyWrapperClassBuilderProvider() {
        try {
           ServiceRegistryImpl.getInstance().addDefault( TraitPropertyWrapperClassBuilder.class, "org.drools.factmodel.TraitPropertyWrapperClassBuilderImpl" );
           setPropertyWrapperBuilderService(ServiceRegistryImpl.getInstance().get(TraitPropertyWrapperClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }




    // Trait proxy wrappers


    private static TraitProxyClassBuilder traitProxyBuilderProvider = new TraitProxyClassBuilderImpl();

    public static synchronized ClassBuilder getTraitProxyBuilderService() {
        if ( traitProxyBuilderProvider == null) {
            loadTraitProxyClassBuilderProvider();
        }
        return traitProxyBuilderProvider;
    }

    public static synchronized void setTraitProxyBuilderService( TraitProxyClassBuilder provider ) {
        ClassBuilderFactory.traitProxyBuilderProvider = provider;
    }

    private static void loadTraitProxyClassBuilderProvider() {
        try {
           ServiceRegistryImpl.getInstance().addDefault( TraitProxyClassBuilder.class, "org.drools.factmodel.TraitProxyClassBuilderImpl" );
           setTraitProxyBuilderService(ServiceRegistryImpl.getInstance().get(TraitProxyClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }






    // Trait legacy core wrappers


    private static TraitCoreWrapperClassBuilder traitCoreWrapperBuilderProvider = new TraitCoreWrapperClassBuilderImpl();

    public static synchronized TraitCoreWrapperClassBuilder getTraitCoreWrapperBuilderService() {
        if ( traitCoreWrapperBuilderProvider == null) {
            loadTraitCoreWrapperClassBuilderProvider();
        }
        return traitCoreWrapperBuilderProvider;
    }

    public static synchronized void setTraitCoreWrapperBuilderService( TraitCoreWrapperClassBuilder provider ) {
        ClassBuilderFactory.traitCoreWrapperBuilderProvider = provider;
    }

    private static void loadTraitCoreWrapperClassBuilderProvider() {
        try {
           ServiceRegistryImpl.getInstance().addDefault( TraitCoreWrapperClassBuilder.class, "org.drools.factmodel.TraitCoreWrapperClassBuilderImpl" );
           setTraitCoreWrapperBuilderService(ServiceRegistryImpl.getInstance().get(TraitCoreWrapperClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }



}
