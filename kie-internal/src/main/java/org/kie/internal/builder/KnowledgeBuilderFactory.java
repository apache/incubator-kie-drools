package org.kie.internal.builder;

import java.util.Properties;

import com.sun.tools.xjc.Options;
import org.kie.api.KieBase;
import org.kie.api.internal.utils.KieService;

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
        private static final KnowledgeBuilderFactoryService factoryService = KieService.load(KnowledgeBuilderFactoryService.class);
    }

    private static class JaxbConfFactoryServiceHolder {
        private static final JaxbConfigurationFactoryService factoryService = KieService.load(JaxbConfigurationFactoryService.class);
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

    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(ClassLoader classLoader) {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilderConfiguration( classLoader );
    }

    /**
     * Create a KnowledgeBuilderConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBuilderConfiguration.
     */
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        return FactoryServiceHolder.factoryService.newKnowledgeBuilderConfiguration( properties, classLoader );
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

    public static JaxbConfiguration newJaxbConfiguration(Options xjcOpts, String systemId) {
        return JaxbConfFactoryServiceHolder.factoryService.newJaxbConfiguration(xjcOpts, systemId);
    }
}
