package org.drools.builder;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.util.ServiceRegistryImpl;

/**
 * This factory is used to build the knowledge base definitions that are held collectively in
 * KnowledgePackages. The KnowledgePackage also provides the role of 'namespacing'. An optional
 * KnowlegeBuilderConfiguration can be supplied. The KnowledgeBuilderConfiguration is itself
 * created from this factory. The KnowledgeBuilderConfiguration allows you to set the ClassLoader to be used
 * along with other setting like the default dialect and compiler, as well as many other options.
 * 
 * <pre>
 * KnowledgeBuilder kbuilder = KnowlegeBuilderFactory.newKnowledgeBuilder();
 * </pre>
 *
 */
public class KnowledgeBuilderFactory {
    private static volatile KnowledgeBuilderFactoryService factoryService;

    /**
     * Create and return a new KnowledgeBuilder, using the default KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder() {
        return getKnowledgeBuilderPServiceFactory().newKnowledgeBuilder();
    }

    /**
     * Create and return a new KnowledgeBuilder, using he given KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderPServiceFactory().newKnowledgeBuilder( conf );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        return getKnowledgeBuilderPServiceFactory().newKnowledgeBuilder( kbase );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                       KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderPServiceFactory().newKnowledgeBuilder( kbase,
                                                                  conf );
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return getKnowledgeBuilderPServiceFactory().newKnowledgeBuilderConfiguration();
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                                 ClassLoader classLoader) {
        return getKnowledgeBuilderPServiceFactory().newKnowledgeBuilderConfiguration( properties,
                                                                               classLoader );
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
        return getKnowledgeBuilderPServiceFactory().newDecisionTableConfiguration();
    }

    private static synchronized void setKnowledgeBuilderFactoryService(KnowledgeBuilderFactoryService serviceFactory) {
        KnowledgeBuilderFactory.factoryService = serviceFactory;
    }

    private static synchronized KnowledgeBuilderFactoryService getKnowledgeBuilderPServiceFactory() {
        if ( factoryService == null ) {
            loadServiceFactory();
        }
        return factoryService;
    }

    private static void loadServiceFactory() {
        setKnowledgeBuilderFactoryService( ServiceRegistryImpl.getInstance().get( KnowledgeBuilderFactoryService.class ) );
    }
}
