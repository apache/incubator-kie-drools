package org.drools.builder;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.Service;

import com.sun.tools.xjc.Options;

/**
 * KnowledgeBuilderFactoryService is used by the KnowledgeBuilderFacotry to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the Factory api, which is considered stable.
 *
 */
public interface KnowledgeBuilderFactoryService  extends Service {
    /**
     * Instantiate and return a new KnowledgeBuilderConfiguration
     * @return
     *     the KnowledgeBuilderConfiguration
     */
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration();

    /**
     * Instantiate and return a new KnowledgeBuilderConfiguration
     * 
     * @param properties
     *     Properties file to process, can be null;
     * @param classLoader
     *     Provided ClassLoader, can be null and then ClassLoader defaults to Thread.currentThread().getContextClassLoader()
     * @return
     */
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties,
                                                                          ClassLoader classLoader);

    /**
     * DecisionTables need to take a configuration of the InputType and XLS based
     * ones can also take a Worksheet name.
     * 
     * @return
     *     The DecisionTableConfiguration
     */
    public DecisionTableConfiguration newDecisionTableConfiguration();

    /**
     * Instantiate and return a new KnowledgeBuilder using the default KnowledgeBuilderConfiguration
     * 
     * @return
     *     The KnowledgeBuilder
     */
    public KnowledgeBuilder newKnowledgeBuilder();

    /**
     * Instantiate and return a new KnowledgeBuilder using the given KnowledgeBuilderConfiguration
     * 
     * @param conf
     *     The KnowledgeBuilderConfiguration
     * @return
     *     The KnowledgeBuilder
     */
    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf);

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase);

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf);

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId);
}
