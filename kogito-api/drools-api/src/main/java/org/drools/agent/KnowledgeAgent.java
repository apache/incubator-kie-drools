package org.drools.agent;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListener;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;

/**
 * <p>
 * The KnowlegeAgent is created by the KnowlegeAgentFactory. It's role is to provide a cached
 * KnowlegeBase and to update or rebuild this KnowlegeBase as the resources it uses are changed.
 * The strategy for this is determined by the configuration given to the factory, but it is 
 * typically pull based using regular polling. We hope to add push based updates and rebuilds in future
 * versions.
 * </p>
 * <p>
 * The Follow example constructs an agent that will build a new KnowledgeBase from the files specified in the path String.
 * It will poll those files every 30 seconds to see if they are updated. If new files are found it will construct a new 
 * KnowledgeBase, instead of upating the existing one, due to the "newInstance" set to "true":
 * <p/>
 * <pre>
 * // Set the interval on the ResourceChangeScannerService if you are to use it and default of 60s is not desirable.
 * ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
 * sconf.setProperty( "drools.resource.scanner.interval",
 *                    "30" ); // set the disk scanning interval to 30s, default is 60s
 * ResourceFactory.getResourceChangeScannerService().configure( sconf );
 * 
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 *
 * KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
 *       aconf.setProperty( "drools.agent.scanDirectories",
 *                          "true" ); // we want to scan directories, not just files, turning this on turns on file scanning
 *       aconf.setProperty( "drools.agent.newInstance",
 *                          "true" ); // resource changes results in a new instance of the KnowledgeBase being built, 
 *                                    // this cannot currently be set to false for incremental building
 *       
 *       KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent", // the name of the agent
 *                                                                        kbase, // the rulebase to use, the Agent will also monitor any exist knowledge definitions
 *                                                                        aconf );
 *       kagent.applyChangeSet( ResourceFactory.newUrlResource( url ) ); // resource to the change-set xml for the resources to add
 * </pre>
 * 
 * KnowledgeAgents can take a empty KnowledgeBase or a populated one. If a populated KnowledgeBase is provided, the KnowledgeAgent
 * will iterate KnowledgeBase and subscribe to the Resource that it finds. While it is possible for the KnowledgeBuilder to build
 * all resources found in a directory, that information is lost by the KnowledgeBuilder so those directories will not be continously scanned.
 * Only directories specified as part of the applyChangeSet(Resource) method are monitored.
 * 
 * @see org.drools.agent.KnowledgeAgentFactory
 * 
 */
public interface KnowledgeAgent {
    /**
     * 
     * @return
     *    The name
     */
    String getName();

    /**
     * Returns the cached KnowledgeBase
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase getKnowledgeBase();
    
    void monitorResourceChangeEvents(boolean monitor);
    
    void applyChangeSet(Resource resource);
    
    void setSystemEventListener(SystemEventListener listener);    
}
