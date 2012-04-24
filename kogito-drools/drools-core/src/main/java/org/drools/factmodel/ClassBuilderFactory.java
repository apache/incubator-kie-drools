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

import org.drools.factmodel.traits.*;
import org.drools.util.ServiceRegistryImpl;

public class ClassBuilderFactory {


    // Generic beans

    private static BeanClassBuilder beanClassBuilderProvider = new DefaultBeanClassBuilder();

    public static synchronized ClassBuilder getBeanClassBuilderService() {
        if ( beanClassBuilderProvider == null) {
            loadBeanClassBuilderProvider();
        }
        return beanClassBuilderProvider;
    }


    public static synchronized void setBeanClassBuilderService( BeanClassBuilder provider ) {
        ClassBuilderFactory.beanClassBuilderProvider = provider;
    }

    public static synchronized void setDefaultBeanClassBuilderService() {
        ClassBuilderFactory.beanClassBuilderProvider = new DefaultBeanClassBuilder() ;
    }

    private static void loadBeanClassBuilderProvider() {
        String defaultName = "org.drools.factmodel.DefaultBeanClassBuilder";
        try {
            ServiceRegistryImpl.getInstance().addDefault( BeanClassBuilder.class, defaultName );
            setBeanClassBuilderService(ServiceRegistryImpl.getInstance().get(BeanClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }



    private static EnumClassBuilder enumClassBuilderProvider = new DefaultEnumClassBuilder();

    public static synchronized EnumClassBuilder getEnumClassBuilderService() {
        if ( enumClassBuilderProvider == null) {
            loadEnumClassBuilderProvider();
        }
        return enumClassBuilderProvider;
    }


    public static synchronized void setEnumClassBuilderService( EnumClassBuilder provider ) {
        ClassBuilderFactory.enumClassBuilderProvider = provider;
    }

    public static synchronized void setDefaultEnumClassBuilderService() {
        ClassBuilderFactory.enumClassBuilderProvider = new DefaultEnumClassBuilder();
    }

    private static void loadEnumClassBuilderProvider() {
        String defaultName = "org.drools.factmodel.DefaultEnumClassBuilder";
        try {
            ServiceRegistryImpl.getInstance().addDefault( EnumClassBuilder.class, defaultName );
            setEnumClassBuilderService( ServiceRegistryImpl.getInstance().get( EnumClassBuilder.class ) );
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

    public static synchronized void setDefaultTraitBuilderService() {
        ClassBuilderFactory.traitBuilderProvider = new TraitClassBuilderImpl();
    }    
    

    private static void loadTraitBuilderProvider() {
        ServiceRegistryImpl.getInstance().addDefault( TraitClassBuilder.class, "org.drools.factmodel.traits.TraitClassBuilderImpl" );
        setTraitBuilderService(ServiceRegistryImpl.getInstance().get(TraitClassBuilder.class));
    }






    // Trait property wrappers


    private static ClassBuilder propertyWrapperBuilderProvider = new TraitTriplePropertyWrapperClassBuilderImpl();

    public static synchronized ClassBuilder getPropertyWrapperBuilderService() {
        if ( propertyWrapperBuilderProvider == null) {
            loadPropertyWrapperClassBuilderProvider();
        }
        return propertyWrapperBuilderProvider;
    }

    public static synchronized void setPropertyWrapperBuilderService( TraitPropertyWrapperClassBuilder provider ) {
        ClassBuilderFactory.propertyWrapperBuilderProvider = provider;
    }

    public static synchronized void setDefaultPropertyWrapperBuilderService() {
        ClassBuilderFactory.propertyWrapperBuilderProvider = new TraitTriplePropertyWrapperClassBuilderImpl();
    }

    private static void loadPropertyWrapperClassBuilderProvider() {
        try {
            ServiceRegistryImpl.getInstance().addDefault( TraitPropertyWrapperClassBuilder.class, "org.drools.factmodel.TraitTriplePropertyWrapperClassBuilderImpl" );
            setPropertyWrapperBuilderService(ServiceRegistryImpl.getInstance().get(TraitPropertyWrapperClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }




    // Trait proxy wrappers


    private static TraitProxyClassBuilder traitProxyBuilderProvider = new TraitTripleProxyClassBuilderImpl();

    public static synchronized ClassBuilder getTraitProxyBuilderService() {
        if ( traitProxyBuilderProvider == null) {
            loadTraitProxyClassBuilderProvider();
        }
        return traitProxyBuilderProvider;
    }

    public static synchronized void setTraitProxyBuilderService( TraitProxyClassBuilder provider ) {
        ClassBuilderFactory.traitProxyBuilderProvider = provider;
    }

    public static synchronized void setDefaultTraitProxyBuilderService() {
        ClassBuilderFactory.traitProxyBuilderProvider = new TraitTripleProxyClassBuilderImpl();
    }

    private static void loadTraitProxyClassBuilderProvider() {
        try {
            ServiceRegistryImpl.getInstance().addDefault( TraitProxyClassBuilder.class, "org.drools.factmodel.TraitTripleProxyClassBuilderImpl" );
            setTraitProxyBuilderService(ServiceRegistryImpl.getInstance().get(TraitProxyClassBuilder.class));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    





//    // Trait legacy core wrappers
//
//
//    private static TraitCoreWrapperClassBuilder traitCoreWrapperBuilderProvider = new TraitCoreWrapperClassBuilderImpl();
//
//    public static synchronized TraitCoreWrapperClassBuilder getTraitCoreWrapperBuilderService() {
//        if ( traitCoreWrapperBuilderProvider == null) {
//            loadTraitCoreWrapperClassBuilderProvider();
//        }
//        return traitCoreWrapperBuilderProvider;
//    }
//
//    public static synchronized void setTraitCoreWrapperBuilderService( TraitCoreWrapperClassBuilder provider ) {
//        ClassBuilderFactory.traitCoreWrapperBuilderProvider = provider;
//    }
//
//    private static void loadTraitCoreWrapperClassBuilderProvider() {
//        try {
//           ServiceRegistryImpl.getInstance().addDefault( TraitCoreWrapperClassBuilder.class, "org.drools.factmodel.TraitCoreWrapperClassBuilderImpl" );
//           setTraitCoreWrapperBuilderService(ServiceRegistryImpl.getInstance().get(TraitCoreWrapperClassBuilder.class));
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }



}
