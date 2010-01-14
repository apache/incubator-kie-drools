package org.drools.builder;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.util.internal.ServiceRegistryImpl;

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
    private static volatile KnowledgeBuilderProvider provider;

    /**
     * Create and return a new KnowledgeBuilder, using the default KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder() {
        return getKnowledgeBuilderProvider().newKnowledgeBuilder();
    }

    /**
     * Create and return a new KnowledgeBuilder, using he given KnowledgeBuilderConfigurations
     * @return
     *     The KnowledgeBuilder
     */
    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderProvider().newKnowledgeBuilder( conf );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        return getKnowledgeBuilderProvider().newKnowledgeBuilder( kbase );
    }

    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                       KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderProvider().newKnowledgeBuilder( kbase,
                                                                  conf );
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return getKnowledgeBuilderProvider().newKnowledgeBuilderConfiguration();
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                                 ClassLoader classLoader) {
        return getKnowledgeBuilderProvider().newKnowledgeBuilderConfiguration( properties,
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
        return getKnowledgeBuilderProvider().newDecisionTableConfiguration();
    }

    private static synchronized void setKnowledgeBuilderProvider(KnowledgeBuilderProvider provider) {
        KnowledgeBuilderFactory.provider = provider;
    }

    private static synchronized KnowledgeBuilderProvider getKnowledgeBuilderProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        setKnowledgeBuilderProvider( ServiceRegistryImpl.getInstance().get( KnowledgeBuilderProvider.class ) );
    }
}
