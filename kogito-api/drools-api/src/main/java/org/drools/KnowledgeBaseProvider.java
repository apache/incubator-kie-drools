package org.drools;

import java.util.Properties;

import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;

/**
 * KnowledgeBaseProvider is used by the KnowledgeBaseFacotry to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the KnowledgeBaseFactory api, which is considered stable.
 *
 */
public interface KnowledgeBaseProvider extends Service {

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
     * Instantiate and return a new KnowledgeSessionConfiguration
     * 
     * @return
     *     the KnowledgeSessionConfiguration
     */
    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration();

    /**
     * Instantiate and return a new KnowledgeSessionConfiguration
     * 
     * @param properties
     *     Properties file to process, can be null;
     * @param classLoader
     *     Provided ClassLoader, can be null and then ClassLoader defaults to Thread.currentThread().getContextClassLoader()
     * @return
     *     The KnowledgeSessionConfiguration
     */
    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration(Properties properties);

    /**
     * Instantiate and return a KnowledgeBase using a default KnowledgeBaseConfiguration
     * 
     * @return
     *      The KnowledgeBase
     */
    KnowledgeBase newKnowledgeBase();

    /**
     * Instantiate and return a KnowledgeBase using a default KnowledgeBaseConfiguration
     * and the given KnowledgeBase ID.
     * 
     * @param kbaseId 
     *     A string Identifier for the knowledge base. Specially useful when enabling
     *     JMX monitoring and management, as that ID will be used to compose the
     *     JMX ObjectName for all related MBeans. The application must ensure all kbase 
     *     IDs are unique. 
     * @return
     *      The KnowledgeBase
     */
    KnowledgeBase newKnowledgeBase( String kbaseId );

    /**
     * Instantiate and return a KnowledgeBase using the given KnowledgeBaseConfiguration
     * 
     * @param conf
     *     The KnowledgeBaseConfiguration to be used
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf);

    /**
     * Instantiate and return a KnowledgeBase using the given KnowledgeBaseConfiguration and
     * the given KnowledgeBase ID.
     * 
     * @param kbaseId 
     *     A string Identifier for the knowledge base. Specially useful when enabling
     *     JMX monitoring and management, as that ID will be used to compose the
     *     JMX ObjectName for all related MBeans. The application must ensure all kbase 
     *     IDs are unique. 
     * @param conf
     *     The KnowledgeBaseConfiguration to be used
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase newKnowledgeBase(String kbaseId, KnowledgeBaseConfiguration conf);

    /**
     * Instantiate and return an Environment
     * 
     * @return
     *      The Environment
     */
    Environment newEnvironment();

}
