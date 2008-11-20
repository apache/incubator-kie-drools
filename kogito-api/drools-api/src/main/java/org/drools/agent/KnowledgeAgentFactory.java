package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.ProviderInitializationException;

/**
 * <p>
 * KnowledgeAgents can build and cache KnowledgeBases using resources from different locations,
 * such as file for http. It can be set to poll that location for updates and will rebuild, or update
 * depending on the configuration, the cached KnowledgeBase.
 * </p>
 * 
 * <p>
 * All KnowledgeAgents must be given a name.
 * </p>
 *  
 * <p> 
 * You should only have ONE instance of this agent per KnowledeBase configuration.
 * You can get the KnowledeBase from this agent repeatedly, as needed, or if you keep the KnowledeBase,
 * under most configurations it will be automatically updated.
 * </p>
 * 
 * <p>
 * How this behaves depends on the properties that you pass into it (documented below)
 * </p>
 * 
 * <p>
 * CONFIG OPTIONS (to be passed in as properties):
 * <ul>
 *  <li><code>newInstance</code>: setting this to "true" means that each time the rules are changed
 *   a new instance of the KnowledeBase is created (as opposed to updated in place)
 *   the default is to update in place. DEFAULT: false. If you set this to true,
 *   then you will need to call getRuleBase() each time you want to use it. If it is false,
 *   then it means you can keep your reference to the KnowledeBase and it will be updated automatically
 *   (as well as any StatefulKnowlegeSessions).
 *   </li>
 *
 *  <li>
 *  <code>poll</code>The number of seconds to poll for changes. Polling
 *  happens in a background thread. eg: poll=30 #30 second polling.
 *  </li>
 *
 *  <li>
 *  <code>file</code>: a space separated listing of files that make up the
 *  packages of the KnowledeBase. Each package can only be in one file. You can't have
 *  packages spread across files. eg: file=/your/dir/file1.pkg file=/your/dir/file2.pkg
 *  If the file has a .pkg extension, then it will be loaded as a binary Package (eg from the BRMS). If its a
 *  DRL file (ie a file with a .drl extension with rule source in it), then it will attempt to compile it (of course, you will need the drools-compiler and its dependencies
 *  available on your classpath).
 *  </li>
 *
 *  <li>
 *  <code>dir</code>: a single file system directory to monitor for packages.
 *  As with files, each package must be in its own file.
 *  eg: dir=/your/dir
 *  </li>
 *
 *  <li>
 *  <code>url</code>: A space separated URL to a binary KnowledeBase in the BRMS.
 *  eg: url=http://server/drools-guvnor/packages/somePakage/VERSION_1
 *  For URL you will also want a local cache directory setup:
 *  eg: localCacheDir=/some/dir/that/exists
 *  This is needed so that the runtime can startup and load packages even if the BRMS
 *  is not available (or the network).
 *  </li>
 *  
 *  <li>
 *  <code>name</code>
 *  the Name is used in any logging, so each agent can be differentiated (you may have one agent per KnowledeBase
 *  that you need in your application).
 *  </li>
 * </p>
 *  
 * <p>
 *  There is also an KnowledgeAgentEventListener interface which you can provide which will call back when lifecycle
 *  events happen, or errors/warnings occur. As the updating happens in a background thread, this may be important.
 *  The default event listener logs to the System.err output stream.
 * </p>
 * 
 * <p>
 * The Follow example constructs an agent that will build a new KnowledgeBase from the files specified in the path String.
 * It will poll those files every 30 seconds to see if they are updated. If new files are found it will construct a new 
 * KnowledgeBase, instead of upating the existing one, due to the "newInstance" set to "true":
 * <p/>
 * <pre>
 * Properties props = new Properties();
 * props.setProperty( "file", path );
 *
 * props.setProperty( "newInstance", "true" );
 * props.setProperty( "poll", "30" );
 * KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "agent1", props );
 * KnowledgeBase kbase = kagent.getKnowledgeBase();
 * </pre>
 * 
 * Notice the 'k' for Knowledge prefix for the variable names.
 * 
 *  * @see org.drools.agent.KnowledgeAgent
 * 
 */
public class KnowledgeAgentFactory {
    private static KnowledgeAgentProvider provider;

    /**
     * Create and return a new KnowlegeAgent using the given name and configuration.
     * 
     * @param name
     * @param config
     * @return
     *     The KnowledgeAgent
     */
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   Properties config) {
        return newKnowledgeAgent( name,
                                  config,
                                  null,
                                  null );
    }

    /**
     * Create and return a new KnowlegeAgent using the given name and configuration.
     * A listener is also specified for callback type logging on for info, warning,
     * exception and debug. The KnowledgeBaseConfiguration will be used by the 
     * KnowledgeBases that the RuleAgent creates.
     * 
     * @param name
     * @param config
     * @param listener
     * @param kbaseConf
     * @return
     *     The KnowledgeAgent
     */
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   Properties config,
                                                   KnowledgeEventListener listener,
                                                   KnowledgeBaseConfiguration kbaseConf) {

        return getKnowledgeAgentProvider().newKnowledgeAgent( name,
                                                              config,
                                                              listener,
                                                              kbaseConf );
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
            Class<KnowledgeAgentProvider> cls = (Class<KnowledgeAgentProvider>) Class.forName( "org.drools.agent.KnowledgeAgentProviderImpl" );
            setKnowledgeAgentProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.agent.KnowledgeAgentProvider could not be set." );
        }
    }
}
