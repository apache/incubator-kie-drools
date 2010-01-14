package org.drools;

import java.util.Properties;

import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.util.internal.ServiceLocatorImpl;

/**
 * <p>
 * This factory will create and return a KnowledgeBase instance, an optional KnowledgeBaseConfiguration
 * can be provided. The KnowlegeBaseConfiguration is also itself created from this factory.
 * </p>
 * <pre>
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * 
 * <p>
 * Create sequential KnowledgeBase using the given ClassLoader.
 * </p>
 * <pre>
 * Properties properties = new Properties();
 * properties.setOption( SequentialOption.YES );
 * KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(properties, myClassLoader);
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConf);
 * </pre>
 *
 * <p>
 * The above could also have used the supported property
 * </p>
 * <pre>
 * properties.setProperty( "org.drools.sequential", "true");
 * </pre>
 *
 * @see org.drools.KnowledgeBase
 */
public class KnowledgeBaseFactory  {
    private static KnowledgeBaseProvider provider;

    /**
     * Create a new KnowledgeBase using the default KnowledgeBaseConfiguration
     * @return
     *     The KnowledgeBase
     */
    public static KnowledgeBase newKnowledgeBase() {
        return getKnowledgeBaseProvider().newKnowledgeBase();
    }

    /**
     * Create a new KnowledgeBase using the default KnowledgeBaseConfiguration and
     * the given KnowledgeBase ID.
     * 
     * @param kbaseId 
     *     A string Identifier for the knowledge base. Specially useful when enabling
     *     JMX monitoring and management, as that ID will be used to compose the
     *     JMX ObjectName for all related MBeans. The application must ensure all kbase 
     *     IDs are unique. 
     * @return
     *     The KnowledgeBase
     */
    public static KnowledgeBase newKnowledgeBase(String kbaseId) {
        return getKnowledgeBaseProvider().newKnowledgeBase(kbaseId);
    }

    /**
     * Create a new KnowledgeBase using the given KnowledgeBaseConfiguration
     * @return
     *     The KnowledgeBase
     */
    public static KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        return getKnowledgeBaseProvider().newKnowledgeBase( conf );
    }

    /**
     * Create a new KnowledgeBase using the given KnowledgeBaseConfiguration and
     * the given KnowledgeBase ID.
     * 
     * @param kbaseId 
     *     A string Identifier for the knowledge base. Specially useful when enabling
     *     JMX monitoring and management, as that ID will be used to compose the
     *     JMX ObjectName for all related MBeans. The application must ensure all kbase 
     *     IDs are unique. 
     * @return
     *     The KnowledgeBase
     */
    public static KnowledgeBase newKnowledgeBase(String kbaseId,
                                                 KnowledgeBaseConfiguration conf) {
        return getKnowledgeBaseProvider().newKnowledgeBase( kbaseId, conf );
    }

    /**
     * Create a KnowledgeBaseConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBaseConfiguration.
     */
    public static KnowledgeBaseConfiguration newKnowledgeBaseConfiguration() {
        return getKnowledgeBaseProvider().newKnowledgeBaseConfiguration();
    }

    /**
     * Create a KnowledgeBaseConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBaseConfiguration.
     */
    public static KnowledgeBaseConfiguration newKnowledgeBaseConfiguration(Properties properties,
                                                                           ClassLoader classLoader) {
        return getKnowledgeBaseProvider().newKnowledgeBaseConfiguration( properties,
                                                                         classLoader );
    }

    /**
     * Create a KnowledgeSessionConfiguration on which properties can be set.
     * @return
     *     The KnowledgeSessionConfiguration.
     */
    public static KnowledgeSessionConfiguration newKnowledgeSessionConfiguration() {
        return getKnowledgeBaseProvider().newKnowledgeSessionConfiguration();
    }

    /**
     * Create a KnowledgeSessionConfiguration on which properties can be set.
     * @return
     *     The KnowledgeSessionConfiguration.
     */
    public static KnowledgeSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        return getKnowledgeBaseProvider().newKnowledgeSessionConfiguration( properties );
    }

    public static Environment newEnvironment() {
        return getKnowledgeBaseProvider().newEnvironment();
    }

//    private static synchronized KnowledgeBaseProvider getKnowledgeBaseProvider() {
//        if ( provider == null ) {
//            provider = newProviderFor( KnowledgeBaseProvider.class );
//        }
//        return provider;
//    }
    
    private static synchronized void setKnowledgeBaseProvider(KnowledgeBaseProvider provider) {
        KnowledgeBaseFactory.provider = provider;
    }

    private static synchronized KnowledgeBaseProvider getKnowledgeBaseProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeBaseProvider> cls = (Class<KnowledgeBaseProvider>) Class.forName( "org.drools.impl.KnowledgeBaseProviderImpl" );
            setKnowledgeBaseProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.impl.KnowledgeBaseProviderImpl could not be set.", e );
        }
    }
}
