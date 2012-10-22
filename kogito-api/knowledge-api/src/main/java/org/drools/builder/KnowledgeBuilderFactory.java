/*
 * Copyright 2010 JBoss Inc
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

package org.drools.builder;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.util.ServiceRegistryImpl;

import com.sun.tools.xjc.Options;

/**
 * This factory is used to build the knowledge base resources that are held collectively in
 * KnowledgePackages. The KnowledgePackage also provides the role of 'namespacing'. An optional
 * KnowlegeBuilderConfiguration can be supplied. The KnowledgeBuilderConfiguration is itself
 * created from this factory. The KnowledgeBuilderConfiguration allows you to set the ClassLoader to be used
 * along with other setting like the default dialect and compiler, as well as many other options.
 * 
 * <pre>
 * KnowledgeBuilder kbuilder = KnowlegeBuilderFactory.newKnowledgeBuilder();
 * </pre>
 */
public class KnowledgeBuilderFactory {
    private static volatile KnowledgeBuilderFactoryService factoryService;

    /**
     * Create and return a new KnowledgeBuilder, using the default KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder() {
        return getKnowledgeBuilderServiceFactory().newKnowledgeBuilder();
    }

    /**
     * Create and return a new KnowledgeBuilder, using he given KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderServiceFactory().newKnowledgeBuilder( conf );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        return getKnowledgeBuilderServiceFactory().newKnowledgeBuilder( kbase );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                       KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderServiceFactory().newKnowledgeBuilder( kbase,
                                                                  conf );
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return getKnowledgeBuilderServiceFactory().newKnowledgeBuilderConfiguration();
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                                 ClassLoader... classLoaders) {
        return getKnowledgeBuilderServiceFactory().newKnowledgeBuilderConfiguration( properties,
                                                                                     classLoaders );
    }

    public static KnowledgeJarBuilder newKnowledgeJarBuilder() {
        return getKnowledgeBuilderServiceFactory().newKnowledgeJarBuilder();
    }

    /**
     * DecisionTable resources require a ResourceConfiguration, that configuration instance is created
     * here. Note that if you are passing a Reader, you must use an InputStreamReader so the encoding 
     * can be determined. 
     * 
     * <pre>
     * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
     * DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
     * dtconf.setInputType( DecisionTableInputType.XLS );
     * dtconf.setWorksheetName( "Tables_2" );
     * kbuilder.add( new URL( "file://IntegrationExampleTest.xls" ),
     *                       ResourceType.DTABLE,
     *                       dtconf );
     * assertFalse( kbuilder.hasErrors() );
     * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
     * </pre>
     * 
     * @return
     */
    public static DecisionTableConfiguration newDecisionTableConfiguration() {
        return getKnowledgeBuilderServiceFactory().newDecisionTableConfiguration();
    }

    public static JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                         String systemId) {
        return getKnowledgeBuilderServiceFactory().newJaxbConfiguration(xjcOpts,
                                                                        systemId);
    }
    
    private static synchronized void setKnowledgeBuilderFactoryService(KnowledgeBuilderFactoryService serviceFactory) {
        KnowledgeBuilderFactory.factoryService = serviceFactory;
    }

    private static synchronized KnowledgeBuilderFactoryService getKnowledgeBuilderServiceFactory() {
        if ( factoryService == null ) {
            loadServiceFactory();
        }
        return factoryService;
    }

    private static void loadServiceFactory() {
        setKnowledgeBuilderFactoryService( ServiceRegistryImpl.getInstance().get( KnowledgeBuilderFactoryService.class ) );
    }
}
