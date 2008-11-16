package org.drools.builder;

import java.util.Properties;

public interface KnowledgeBuilderProvider {
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
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader);
    
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
}
