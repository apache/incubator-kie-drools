package org.drools;

import java.util.Properties;

/**
 * KnowledgeBaseProvider is used by the KnowledgeBaseFacotry to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the Factory api, which is consiered stable.
 *
 */
public interface KnowledgeBaseProvider {

    /**
     * Instantiate and return a new KnowledgeBaseConfiguration
     * 
     * @return
     *     the KnowledgeBaseConfiguration
     */
    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration();

    /**
     * Instantiate and return a new KnowledgeBaseConfiguration
     * 
     * @param properties
     *     Properties file to process, can be null;
     * @param classLoader
     *     Provided ClassLoader, can be null and then ClassLoader defaults to Thread.currentThread().getContextClassLoader()
     * @return
     *     The KnowledgeBaseConfiguration
     */
    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration(Properties properties,
                                                                    ClassLoader classLoader);

    /**
     * Instantiate and return a KnowledgeBase using a default KnowledgeBaseConfiguration
     * 
     * @return
     *      The KnowledgeBase
     */
    KnowledgeBase newKnowledgeBase();

    /**
     * Instantiate and return a KnowledgeBase using the given KnowledgeBaseConfiguration
     * 
     * @param conf
     *     The KnowledgeBaseConfiguration to be used
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf);

}
