package org.drools.compiler;

/*
 * Copyright 2005 JBoss Inc
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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.drools.RuntimeDroolsException;
import org.drools.base.accumulators.AccumulateFunction;
import org.drools.util.ChainedProperties;

/**
 * This class configures the package compiler. 
 * Dialects and their DialectConfigurations  are handled by the DialectRegistry
 * Normally you will not need to look at this class, unless you want to override the defaults.
 * 
 * drools.dialect.default = <String>
 * drools.accumulate.function.<function name> = <qualified class>
 * 
 * default dialect is java.
 * Available preconfigured Accumulate functions are:
 * drools.accumulate.function.average = org.drools.base.accumulators.AverageAccumulateFunction
 * drools.accumulate.function.max = org.drools.base.accumulators.MaxAccumulateFunction
 * drools.accumulate.function.min = org.drools.base.accumulators.MinAccumulateFunction
 * drools.accumulate.function.count = org.drools.base.accumulators.CountAccumulateFunction
 * drools.accumulate.function.sum = org.drools.base.accumulators.SumAccumulateFunction 
 */
public class PackageBuilderConfiguration {


    private static final String  ACCUMULATE_FUNCTION_PREFIX = "drools.accumulate.function.";

    private Map                  dialects;
    
    private DialectRegistry      dialectRegistry;

    private String               defaultDialect;

    private ClassLoader          classLoader;


    private ChainedProperties    chainedProperties;

    private Map                  accumulateFunctions;

    /**
     * Programmatic properties file, added with lease precedence
     * @param properties
     */
    public PackageBuilderConfiguration(Properties properties) {
        init( null,
              properties );
    }

    /**
     * Programmatic properties file, added with lease precedence
     * @param classLoader
     * @param properties
     */
    public PackageBuilderConfiguration(ClassLoader classLoader,
                                       Properties properties) {
        init( classLoader,
              properties );
    }

    public PackageBuilderConfiguration() {
        init( null,
              null );
    }

    private void init(ClassLoader classLoader,
                      Properties properties) {
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = this.getClass().getClassLoader();
            }
        }
        setClassLoader( classLoader );

        this.chainedProperties = new ChainedProperties( this.classLoader,
                                                        "packagebuilder.conf" );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }              

        this.dialects = new HashMap();
        this.chainedProperties.mapStartsWith( this.dialects,
                                              "drools.dialect",
                                              false );
        setDefaultDialect( (String) this.dialects.remove( "drools.dialect.default" ) );
        
        this.dialectRegistry = buildDialectRegistry( );

        buildAccumulateFunctionsMap();
    }
    
    public ChainedProperties getChainedProperties() {
        return this.chainedProperties;
    }

    public DialectRegistry buildDialectRegistry() {
        DialectRegistry registry = new DialectRegistry();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for ( Iterator it = this.dialects.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            String str = (String) entry.getKey();
            String dialectName = str.substring( str.lastIndexOf( "." ) + 1 );
            String dialectClass = (String) entry.getValue();
            try {
                Class cls = classLoader.loadClass( dialectClass );
                DialectConfiguration dialectConf = ( DialectConfiguration ) cls.newInstance();
                dialectConf.init( this );
                registry.addDialectConfiguration( dialectName,
                                     dialectConf );
            } catch ( Exception e ) {
                throw new RuntimeDroolsException( "Unable to load dialect '" + dialectClass + ":" + dialectName + "'", e );
            }
        }
        return registry;
    }
    
    public DialectRegistry getDialectRegistry() {
        return this.dialectRegistry;
    }

    public Dialect getDefaultDialect() {
        return this.dialectRegistry.getDialectConfiguration( this.defaultDialect ).getDialect();
    }

    public void setDefaultDialect(String defaultDialect) {
        this.defaultDialect = defaultDialect;
    }
    
    public DialectConfiguration getDialectConfiguration(String name) {
        return ( DialectConfiguration ) this.dialectRegistry.getDialectConfiguration( name );
    }
    
    public void setDialectConfiguration(String name, DialectConfiguration configuration) {
        this.dialects.put( name, 
                           configuration );
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /** Use this to override the classloader that will be used for the rules. */
    public void setClassLoader(final ClassLoader classLoader) {
        if ( classLoader != null ) {
            this.classLoader = classLoader;
        }
    }

    private void buildAccumulateFunctionsMap() {
        this.accumulateFunctions = new HashMap();
        Map temp = new HashMap();
        this.chainedProperties.mapStartsWith( temp,
                                              ACCUMULATE_FUNCTION_PREFIX,
                                              true );
        for ( Iterator it = temp.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String identifier = ((String) entry.getKey()).trim().substring( ACCUMULATE_FUNCTION_PREFIX.length() );
            this.accumulateFunctions.put( identifier,
                                           entry.getValue() );
        }
    }
    
    public Map getAccumulateFunctionsMap() {
        return Collections.unmodifiableMap( this.accumulateFunctions );
    }

    public void addAccumulateFunction(String identifier,
                                      String className) {
        this.accumulateFunctions.put( identifier,
                                       className );
    }

    public void addAccumulateFunction(String identifier,
                                      Class clazz) {
        this.accumulateFunctions.put( identifier,
                                       clazz.getName() );
    }

    public AccumulateFunction getAccumulateFunction(String identifier) {
        String className = (String) this.accumulateFunctions.get( identifier );
        if ( className == null ) {
            throw new RuntimeDroolsException( "No accumulator function found for identifier: " + identifier );
        }
        try {
            Class clazz = this.classLoader.loadClass( className );
            return (AccumulateFunction) clazz.newInstance();
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeDroolsException( "Error loading accumulator function for identifier " + identifier + ". Class " + className + " not found",
                                              e );
        } catch ( InstantiationException e ) {
            throw new RuntimeDroolsException( "Error loading accumulator function for identifier " + identifier + ". Class " + className + " not found",
                                              e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Error loading accumulator function for identifier " + identifier + ". Class " + className + " not found",
                                              e );
        }
    }

}