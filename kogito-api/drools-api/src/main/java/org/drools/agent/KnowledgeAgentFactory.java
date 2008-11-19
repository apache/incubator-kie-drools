package org.drools.agent;

import java.util.Properties;

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
 * CONFIG OPTIONS (to be passed in as properties):<br/>
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
 */
public class KnowledgeAgentFactory {
    private static KnowledgeAgentProvider provider;

    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   Properties config) {
        return newKnowledgeAgent( name,
                                  config,
                                  null,
                                  null );
    }

    /**
     * This allows an optional listener to be passed in.
     * The default one prints some stuff out to System.err only when really needed.
     */
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   Properties config,
                                                   KnowledgeEventListener listener,
                                                   KnowledgeBaseConfiguration ruleBaseConf) {

        return getKnowledgeAgentProvider().newKnowledgeAgent( name,
                                                              config,
                                                              listener,
                                                              ruleBaseConf );
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
