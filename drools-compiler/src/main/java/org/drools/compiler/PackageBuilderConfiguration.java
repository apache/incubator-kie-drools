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

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.base.accumulators.AccumulateFunction;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.process.builder.ProcessNodeBuilder;
import org.drools.process.builder.ProcessNodeBuilderRegistry;
import org.drools.util.ChainedProperties;
import org.drools.util.ClassUtils;
import org.drools.util.ConfFileUtils;
import org.drools.workflow.core.Node;
import org.drools.xml.DefaultSemanticModule;
import org.drools.xml.Handler;
import org.drools.xml.ProcessSemanticModule;
import org.drools.xml.RulesSemanticModule;
import org.drools.xml.SemanticModule;
import org.drools.xml.SemanticModules;
import org.mvel.MVEL;

/**
 * This class configures the package compiler. 
 * Dialects and their DialectConfigurations  are handled by the DialectRegistry
 * Normally you will not need to look at this class, unless you want to override the defaults.
 * 
 * This class is not thread safe and it also contains state. Once it is created and used 
 * in one or more PackageBuilders it should be considered immutable. Do not modify its 
 * properties while it is being used by a PackageBuilder.
 * 
 * drools.dialect.default = <String>
 * drools.accumulate.function.<function name> = <qualified class>
 * drools.evaluator.<ident> = <qualified class>
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

    private static final String        ACCUMULATE_FUNCTION_PREFIX  = "drools.accumulate.function.";

    private static final String        EVALUATOR_DEFINITION_PREFIX = "drools.evaluator.";

    private Map                        dialectConfigurations;

    private String                     defaultDialect;

    private ClassLoader                classLoader;

    private ChainedProperties          chainedProperties;

    private Map<String, String>        accumulateFunctions;

    private EvaluatorRegistry          evaluatorRegistry;

    private SemanticModules            semanticModules;

    private ProcessNodeBuilderRegistry nodeBuilderRegistry;

    /**
     * Constructor that sets the parent class loader for the package being built/compiled
     * @param classLoader
     */
    public PackageBuilderConfiguration(ClassLoader classLoader) {
        init( classLoader,
              null );
    }

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

        this.dialectConfigurations = new HashMap();

        buildDialectConfigurationMap();

        buildAccumulateFunctionsMap();

        buildEvaluatorRegistry();
    }

    public ChainedProperties getChainedProperties() {
        return this.chainedProperties;
    }

    private void buildDialectConfigurationMap() {
        //DialectRegistry registry = new DialectRegistry();
        Map dialectProperties = new HashMap();
        this.chainedProperties.mapStartsWith( dialectProperties,
                                              "drools.dialect",
                                              false );
        setDefaultDialect( (String) dialectProperties.remove( "drools.dialect.default" ) );

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for ( Iterator it = dialectProperties.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            String str = (String) entry.getKey();
            String dialectName = str.substring( str.lastIndexOf( "." ) + 1 );
            String dialectClass = (String) entry.getValue();
            addDialect( dialectName,
                        dialectClass );
        }
    }

    public void addDialect(String dialectName,
                           String dialectClass) {
        try {
            Class cls = classLoader.loadClass( dialectClass );
            DialectConfiguration dialectConf = (DialectConfiguration) cls.newInstance();
            dialectConf.init( this );
            addDialect( dialectName,
                        dialectConf );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( "Unable to load dialect '" + dialectClass + ":" + dialectName + "'",
                                              e );
        }
    }

    public void addDialect(String dialectName,
                           DialectConfiguration dialectConf) {
        dialectConfigurations.put( dialectName,
                                   dialectConf );
    }

    public DialectRegistry buildDialectRegistry() {
        DialectRegistry registry = new DialectRegistry();
        for ( Iterator it = this.dialectConfigurations.values().iterator(); it.hasNext(); ) {
            DialectConfiguration conf = (DialectConfiguration) it.next();
            Dialect dialect = conf.getDialect();
            registry.addDialect( conf.getDialect().getId(),
                                 dialect );
        }
        return registry;
    }

    public String getDefaultDialect() {
        return this.defaultDialect;
    }

    public void setDefaultDialect(String defaultDialect) {
        this.defaultDialect = defaultDialect;
    }

    public DialectConfiguration getDialectConfiguration(String name) {
        return (DialectConfiguration) this.dialectConfigurations.get( name );
    }

    public void setDialectConfiguration(String name,
                                        DialectConfiguration configuration) {
        this.dialectConfigurations.put( name,
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

    public void addSemanticModule(SemanticModule module) {
        if ( this.semanticModules == null ) {
            initSemanticModules();
        }
        this.semanticModules.addSemanticModule( module );
    }

    public SemanticModules getSemanticModules() {
        if ( this.semanticModules == null ) {
            initSemanticModules();
        }
        return this.semanticModules;
    }

    public void initSemanticModules() {
        this.semanticModules = new SemanticModules();

        this.semanticModules.addSemanticModule( new ProcessSemanticModule() );
        this.semanticModules.addSemanticModule( new RulesSemanticModule() );

        // split on each space
        String locations[] = this.chainedProperties.getProperty( "semanticModules",
                                                                 "" ).split( "\\s" );

        int i = 0;
        // load each SemanticModule
        for ( String moduleLocation : locations ) {
            // trim leading/trailing spaces and quotes
            moduleLocation = moduleLocation.trim();
            if ( moduleLocation.startsWith( "\"" ) ) {
                moduleLocation = moduleLocation.substring( 1 );
            }
            if ( moduleLocation.endsWith( "\"" ) ) {
                moduleLocation = moduleLocation.substring( 0,
                                                           moduleLocation.length() - 1 );
            }
            if ( !moduleLocation.equals( "" ) ) {
                loadSemanticModule( moduleLocation );
            }
        }
    }

    public void loadSemanticModule(String moduleLocation) {
        URL url = ConfFileUtils.getURL( moduleLocation,
                                        this.classLoader,
                                        getClass() );
        if ( url == null ) {
            throw new IllegalArgumentException( moduleLocation + " is specified but cannot be found.'" );
        }

        Properties properties = ConfFileUtils.getProperties( url );
        if ( properties == null ) {
            throw new IllegalArgumentException( moduleLocation + " is specified but cannot be found.'" );
        }

        loadSemanticModule( properties );
    }

    public void loadSemanticModule(Properties properties) {
        String uri = properties.getProperty( "uri",
                                             null );
        if ( uri == null || uri.trim().equals( "" ) ) {
            throw new RuntimeException( "Semantic Module URI property must not be empty" );
        }

        DefaultSemanticModule module = new DefaultSemanticModule( uri );

        for ( Entry<Object, Object> entry : properties.entrySet() ) {
            String elementName = (String) entry.getKey();

            //uri is processed above, so skip
            if ( "uri".equals( elementName ) ) {
                continue;
            }

            if ( elementName == null || elementName.trim().equals( "" ) ) {
                throw new RuntimeException( "Element name must be specified for Semantic Module handler" );
            }
            String handlerName = (String) entry.getValue();
            if ( handlerName == null || handlerName.trim().equals( "" ) ) {
                throw new RuntimeException( "Handler name must be specified for Semantic Module" );
            }

            Handler handler = (Handler) ClassUtils.instantiateObject( handlerName,
                                                                      this.classLoader );

            if ( handler == null ) {
                throw new RuntimeException( "Unable to load Semantic Module handler '" + elementName + ":" + handlerName + "'" );
            } else {
                module.addHandler( elementName,
                                   handler );
            }
        }
        this.semanticModules.addSemanticModule( module );
    }

    public ProcessNodeBuilderRegistry getProcessNodeBuilderRegistry() {
        if ( this.nodeBuilderRegistry == null ) {
            initProcessNodeBuilderRegistry();
        }
        return this.nodeBuilderRegistry;

    }

    private void initProcessNodeBuilderRegistry() {
        this.nodeBuilderRegistry = new ProcessNodeBuilderRegistry();

        // split on each space
        String locations[] = this.chainedProperties.getProperty( "processNodeBuilderRegistry",
                                                                 "" ).split( "\\s" );

        int i = 0;
        // load each SemanticModule
        for ( String builderLocation : locations ) {
            // trim leading/trailing spaces and quotes
            builderLocation = builderLocation.trim();
            if ( builderLocation.startsWith( "\"" ) ) {
                builderLocation = builderLocation.substring( 1 );
            }
            if ( builderLocation.endsWith( "\"" ) ) {
                builderLocation = builderLocation.substring( 0,
                                                             builderLocation.length() - 1 );
            }
            if ( !builderLocation.equals( "" ) ) {
                loadProcessNodeBuilderRegistry( builderLocation );
            }
        }
    }

    private void loadProcessNodeBuilderRegistry(String factoryLocation) {
        String content = ConfFileUtils.URLContentsToString( ConfFileUtils.getURL( factoryLocation,
                                                                                  null,
                                                                                  RuleBaseConfiguration.class ) );

        Map<Class< ? extends Node>, ProcessNodeBuilder> map = (Map<Class< ? extends Node>, ProcessNodeBuilder>) MVEL.eval( content,
                                                                                                                           new HashMap() );

        if ( map != null ) {
            for ( Entry<Class< ? extends Node>, ProcessNodeBuilder> entry : map.entrySet() ) {
                this.nodeBuilderRegistry.register( entry.getKey(),
                                                   entry.getValue() );
            }
        }
    }

    private void buildAccumulateFunctionsMap() {
        this.accumulateFunctions = new HashMap<String, String>();
        Map temp = new HashMap();
        this.chainedProperties.mapStartsWith( temp,
                                              ACCUMULATE_FUNCTION_PREFIX,
                                              true );
        for ( Iterator it = temp.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String identifier = ((String) entry.getKey()).trim().substring( ACCUMULATE_FUNCTION_PREFIX.length() );
            this.accumulateFunctions.put( identifier,
                                          (String) entry.getValue() );
        }
    }

    public Map<String, String> getAccumulateFunctionsMap() {
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
        String className = this.accumulateFunctions.get( identifier );
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
            throw new RuntimeDroolsException( "Error loading accumulator function for identifier " + identifier + ". Instantiation failed for class " + className,
                                              e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Error loading accumulator function for identifier " + identifier + ". Illegal access to class " + className,
                                              e );
        }
    }

    private void buildEvaluatorRegistry() {
        this.evaluatorRegistry = new EvaluatorRegistry( this.classLoader );
        Map temp = new HashMap();
        this.chainedProperties.mapStartsWith( temp,
                                              EVALUATOR_DEFINITION_PREFIX,
                                              true );
        for ( Iterator it = temp.values().iterator(); it.hasNext(); ) {
            String className = (String) it.next();
            this.evaluatorRegistry.addEvaluatorDefinition( className );
        }
    }

    /**
     * Returns the evaluator registry for this package builder configuration
     * @return
     */
    public EvaluatorRegistry getEvaluatorRegistry() {
        return this.evaluatorRegistry;
    }

    /**
     * Adds an evaluator definition class to the registry using the
     * evaluator class name. The class will be loaded and the corresponting
     * evaluator ID will be added to the registry. In case there exists
     * an implementation for that ID already, the new implementation will
     * replace the previous one.
     * 
     * @param className the name of the class for the implementation definition.
     *                  The class must implement the EvaluatorDefinition interface.
     * 
     */
    public void addEvaluatorDefinition(String className) {
        this.evaluatorRegistry.addEvaluatorDefinition( className );
    }

    /**
     * Adds an evaluator definition class to the registry. In case there exists
     * an implementation for that evaluator ID already, the new implementation will
     * replace the previous one.
     * 
     * @param def the evaluator definition to be added.
     * 
     */
    public void addEvaluatorDefinition(EvaluatorDefinition def) {
        this.evaluatorRegistry.addEvaluatorDefinition( def );
    }

}