package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.ProviderInitializationException;
import org.drools.io.ResourceFactory;

/**
 * <p>
 * The KnowlegeAgent is created by the KnowlegeAgentFactory. The KnowlegeAgent provides automatic loading, caching and 
 * re-loading, of resources and is configured from a properties files. The KnowledgeAgent can update or rebuild this KnowlegeBase 
 * as the resources it uses are changed. The strategy for this is determined by the configuration given to the factory, but it is 
 * typically pull based using regular polling. We hope to add push based updates and rebuilds in future versions.
 * </p>
 * 
 * <p>
 * The Following example constructs an agent that will build a new KnowledgeBase from the files specified in the path String.
 * It will poll those files every 60 seconds, which is the default when polling is enabled and the service started, to see if they are updated. 
 * If new files are found it will construct a new KnowledgeBase. If the change set specifies a resource that is a directory it's contents will be scanned for changes  too.
 * </p>
 * 
 * <pre>
 * KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "MyAgent" );
 * kagent.applyChangeSet( ResourceFactory.newUrlResource( url ) );
 * KnowledgeBase kbase = kagent.getKnowledgeBase();    
 * </pre>
 * 
 * <p>If you wish to change to change the polling time of the scanner, this can be done with the ResourceChangeScannerService on the ResourceFactory</p>
 * <pre>
 * // Set the interval on the ResourceChangeScannerService if the default of 60s is not desirable.
 * ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
 * sconf.setProperty( "drools.resource.scanner.interval",
 *                    "30" ); // set the disk scanning interval to 30s, default is 60s
 * ResourceFactory.getResourceChangeScannerService().configure( sconf );
 * </pre>
 * 
 * <p>The KnowledgeAgent can accept a configuration that allows for some of the defaults to be changed, see KnowledgeAgentConfiguration for details of 
 * all the support configuration properties. An example property is "drools.agent.scanDirectories", by default any specified directories are 
 * scanned for new additions, it is possible to disable this.
 * 
 * <pre>
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 *
 * KnowledgeAgentConfiguration kaconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
 * kaconf.setProperty( "drools.agent.scanDirectories",
 *                     "false" ); // we do not want to scan directories, just files
 *       
 * KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent", // the name of the agent
 *                                                                  kaconf );
 * kagent.applyChangeSet( ResourceFactory.newUrlResource( url ) ); // resource to the change-set xml for the resources to add
 * </pre>
 * 
 * <p>
 * You'll notice the property "drools.agent.newInstance", which defaults to true, currently this cannot be set to false. This property when
 * true means the KnowledgeBase will be rebuilt as a new instance when changes are detected and that new instance will be available when
 * kagent.getKnowledgeBase() is next called. We hope to add incremental KnowledgeBase support in the future, when a "false" value will be supported
 * for this property.
 * </p>
 * 
 * <p>
 * KnowledgeAgents can take a empty KnowledgeBase or a populated one. If a populated KnowledgeBase is provided, the KnowledgeAgent
 * will iterate KnowledgeBase and subscribe to the Resource that it finds. While it is possible for the KnowledgeBuilder to build
 * all resources found in a directory, that information is lost by the KnowledgeBuilder so those directories will not be continuously scanned.
 * Only directories specified as part of the applyChangeSet(Resource) method are monitored.
 * </p>
 * 
 * <p>
 * Resource scanning is not on by default, it's a service and must be started, the same is for notification. This can be done via the ResourceFactory.
 * </p>
 * 
 * <pre>
 * ResourceFactory.getResourceChangeNotifierService().start();
 * ResourceFactory.getResourceChangeScannerService().start();
 * </pre>
 *
 * <p>
 * For resources that are "polled" from a remote source (via http or similar) - sometimes you may want a local file based cache,
 * in case the remote service is not available for whatever reason.
 * To enable this:
 * Set the system property: "drools.resource.urlcache" to a directory which can be written to and read from
 * as a cache - so remote resources will be cached with last known good copies. This will allow the service to be restarted
 * even if the remote source is not available. 
 * For example -Ddrools.resource.urlcache=/home/rulecaches
 *
 * </p>
 * 
 * @see org.drools.agent.KnowledgeAgent
 * @see org.drools.agent.KnowledgeAgentConfiguration
 * 
 */
public class KnowledgeAgentFactory {
    private static KnowledgeAgentProvider provider;

    public static KnowledgeAgentConfiguration newKnowledgeAgentConfiguration() {
        return getKnowledgeAgentProvider().newKnowledgeAgentConfiguration();
    }

    public static KnowledgeAgentConfiguration newKnowledgeAgentConfiguration(Properties properties) {
        return getKnowledgeAgentProvider().newKnowledgeAgentConfiguration( properties );
    }
    
    public static KnowledgeAgent newKnowledgeAgent(String name) {
        return getKnowledgeAgentProvider().newKnowledgeAgent( name,
                                                              KnowledgeBaseFactory.newKnowledgeBase() );
    }    

    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   KnowledgeBase kbase) {
        return getKnowledgeAgentProvider().newKnowledgeAgent( name,
                                                              kbase );
    }

    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   KnowledgeAgentConfiguration configuration) {
        return getKnowledgeAgentProvider().newKnowledgeAgent( name,
                                                              KnowledgeBaseFactory.newKnowledgeBase(),
                                                              configuration );
    }    
    
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   KnowledgeBase kbase,
                                                   KnowledgeAgentConfiguration configuration) {
        return getKnowledgeAgentProvider().newKnowledgeAgent( name,
                                                              kbase,
                                                              configuration );
    }

    private static synchronized void setKnowledgeAgentProvider(KnowledgeAgentProvider provider) {
        KnowledgeAgentFactory.provider = provider;
    }

    private static synchronized KnowledgeAgentProvider getKnowledgeAgentProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeAgentProvider> cls = (Class<KnowledgeAgentProvider>) Class.forName( "org.drools.agent.impl.KnowledgeAgentProviderImpl" );
            setKnowledgeAgentProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.agent.KnowledgeAgentProvider could not be set." );
        }
    }
}
