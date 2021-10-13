/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.builder;

import java.util.Properties;

import com.sun.tools.xjc.Options;
import org.kie.api.KieBase;
import org.kie.api.internal.utils.ServiceRegistry;

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

    private static class FactoryServiceHolder {
        private static final KnowledgeBuilderFactoryService factoryService = ServiceRegistry.getService(KnowledgeBuilderFactoryService.class);
    }

    private static class JaxbConfFactoryServiceHolder {
        private static final JaxbConfigurationFactoryService factoryService = ServiceRegistry.getService(JaxbConfigurationFactoryService.class);
    }

    /**
     * Create and return a new KnowledgeBuilder, using the default KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder() {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilder();
    }

    /**
     * Create and return a new KnowledgeBuilder, using he given KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilder( conf );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KieBase kbase) {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilder( kbase );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KieBase kbase,
                                                       KnowledgeBuilderConfiguration conf) {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilder( kbase, conf );
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilderConfiguration();
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                                 ClassLoader... classLoaders) {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilderConfiguration( properties, classLoaders );
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
        return FactoryServiceHolder.factoryService.newDecisionTableConfiguration();
    }

    /**
     * ResourceConfiguration for score cards. It allows for the worksheet name to be specified.
     *
     * <p>
     * Simple example showing how to build a KnowledgeBase from an XLS resource.
     * <p>
     *
     * <pre>
     * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
     * ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
     * scconf.setWorksheetName( "Tables_2" );
     * kbuilder.add( ResourceFactory.newUrlResource( "file://IntegrationExampleTest.xls" ),
     *               ResourceType.SCARD,
     *               scconf );
     * assertFalse( kbuilder.hasErrors() );
     * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
     * </pre>
     */
    public static ScoreCardConfiguration newScoreCardConfiguration() {
        return FactoryServiceHolder.factoryService.newScoreCardConfiguration();
    }

    public static JaxbConfiguration newJaxbConfiguration(Options xjcOpts, String systemId) {
        return JaxbConfFactoryServiceHolder.factoryService.newJaxbConfiguration(xjcOpts, systemId);
    }
}
