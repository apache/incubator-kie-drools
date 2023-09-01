package org.kie.internal.builder;

import java.util.Properties;

import org.kie.api.KieBase;
import org.kie.api.internal.utils.KieService;

/**
 * KnowledgeBuilderFactoryService is used by the KnowledgeBuilderFactory to "provide" it's concrete implementation.
 *
 * This class is not considered stable and may change, the user is protected from this change by using
 * the Factory api, which is considered stable.
 */
public interface KnowledgeBuilderFactoryService extends KieService {
    /**
     * Instantiate and return a new KnowledgeBuilderConfiguration
     * @return
     *     the KnowledgeBuilderConfiguration
     */
    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration();

    /**
     * Instantiate and return a new KnowledgeBuilderConfiguration
     *
     * @param classLoader
     *     Provided ClassLoader, can be null and then ClassLoader defaults to Thread.currentThread().getContextClassLoader()
     * @return
     */
    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(ClassLoader classLoader);

    /**
     * Instantiate and return a new KnowledgeBuilderConfiguration
     *
     * @param properties
     *     Properties file to process, can be null;
     * @param classLoader
     *     Provided ClassLoader, can be null and then ClassLoader defaults to Thread.currentThread().getContextClassLoader()
     * @return
     */
    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader);

    /**
     * DecisionTables need to take a configuration of the InputType and XLS based
     * ones can also take a Worksheet name.
     *
     * @return
     *     The DecisionTableConfiguration
     */
    DecisionTableConfiguration newDecisionTableConfiguration();

    /**
     * Instantiate and return a new KnowledgeBuilder using the default KnowledgeBuilderConfiguration
     *
     * @return
     *     The KnowledgeBuilder
     */
    KnowledgeBuilder newKnowledgeBuilder();

    /**
     * Instantiate and return a new KnowledgeBuilder using the given KnowledgeBuilderConfiguration
     *
     * @param conf
     *     The KnowledgeBuilderConfiguration
     * @return
     *     The KnowledgeBuilder
     */
    KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf);

    KnowledgeBuilder newKnowledgeBuilder(KieBase kbase);

    KnowledgeBuilder newKnowledgeBuilder(KieBase kbase, KnowledgeBuilderConfiguration conf);
}
