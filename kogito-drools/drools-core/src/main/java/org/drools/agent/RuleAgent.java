/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.RuntimeDroolsException;
import org.drools.rule.Package;

/**
 * This manages a single rulebase, based on the properties given.
 * You should only have ONE instance of this agent per rulebase configuration.
 * You can get the rulebase from this agent repeatedly, as needed, or if you keep the rulebase,
 * under most configurations it will be automatically updated.
 *
 * How this behaves depends on the properties that you pass into it (documented below)
 *
 * CONFIG OPTIONS (to be passed in as properties):
 *  <code>newInstance</code>: setting this to "true" means that each time the rules are changed
 *   a new instance of the rulebase is created (as opposed to updated in place)
 *   the default is to update in place. DEFAULT: false. If you set this to true,
 *   then you will need to call getRuleBase() each time you want to use it. If it is false,
 *   then it means you can keep your reference to the rulebase and it will be updated automatically
 *   (as well as any stateful sessions).
 *
 *  <code>poll</code>The number of seconds to poll for changes. Polling
 *  happens in a background thread. eg: poll=30 #30 second polling.
 *
 *  <code>file</code>: a space seperated listing of files that make up the
 *  packages of the rulebase. Each package can only be in one file. You can't have
 *  packages spread across files. eg: file=/your/dir/file1.pkg file=/your/dir/file2.pkg
 *  If the file has a .pkg extension, then it will be loaded as a binary Package (eg from the BRMS). If its a
 *  DRL file (ie a file with a .drl extension with rule source in it), then it will attempt to compile it (of course, you will need the drools-compiler and its dependencies
 *  available on your classpath).
 *
 *  <code>dir</code>: a single file system directory to monitor for packages.
 *  As with files, each package must be in its own file.
 *  eg: dir=/your/dir
 *
 *  <code>url</code>: A space seperated URL to a binary rulebase in the BRMS.
 *  eg: url=http://server/drools-guvnor/packages/somePakage/VERSION_1
 *  For URL you will also want a local cache directory setup:
 *  eg: localCacheDir=/some/dir/that/exists
 *  This is needed so that the runtime can startup and load packages even if the BRMS
 *  is not available (or the network).
 *
 *  <code>name</code>
 *  the Name is used in any logging, so each agent can be differentiated (you may have one agent per rulebase
 *  that you need in your application).
 *
 *  There is also an AgentEventListener interface which you can provide which will call back when lifecycle
 *  events happen, or errors/warnings occur. As the updating happens in a background thread, this may be important.
 *  The default event listener logs to the System.err output stream.
 *
 * @author Michael Neale
 */
public class RuleAgent {

    /**
     * Following are property keys to be used in the property
     * config file.
     */
    public static final String    NEW_INSTANCE      = "newInstance";
    public static final String    FILES             = "file";
    public static final String    DIRECTORY         = "dir";
    public static final String    URLS              = "url";
    public static final String    POLL_INTERVAL     = "poll";
    public static final String    CONFIG_NAME       = "name";              //name is optional
    public static final String    USER_NAME       = "username"; 
    public static final String    PASSWORD       = "password"; 
    public static final String    ENABLE_BASIC_AUTHENTICATION = "enableBasicAuthentication"; 
   
    //this is needed for cold starting when BRMS is down (ie only for URL).
    public static final String    LOCAL_URL_CACHE   = "localCacheDir";

    /**
     * Here is where we have a map of providers to the key that appears on the configuration.
     */
    public static Map             PACKAGE_PROVIDERS = new HashMap() {
                                                        {
                                                            put( FILES,
                                                                 FileScanner.class );
                                                            put( DIRECTORY,
                                                                 DirectoryScanner.class );
                                                            put( URLS,
                                                                 URLScanner.class );
                                                        }
                                                    };

    String                        name;

    /**
     * This is true if the rulebase is created anew each time.
     */
    private boolean               newInstance;

    /**
     * The rule base that is being managed.
     */
    private RuleBase              ruleBase;

    /**
     * the configuration for the RuleBase
     */
    private RuleBaseConfiguration ruleBaseConf;

    /**
     * The timer that is used to monitor for changes and deal with them.
     */
    private Timer                 timer;

    /**
     * The providers that actually do the work.
     */
    List                          providers;

    /**
     * This keeps the packages around that have been loaded.
     */
    Map                           packages          = new HashMap();

    /**
     * For logging events (important for stuff that happens in the background).
     */
    AgentEventListener            listener          = getDefaultListener();

    /**
     * Polling interval value, in seconds, used in the Timer.
     */
    private int                   secondsToRefresh;

    /**
     * Properties configured to load up packages into a rulebase (and monitor them
     * for changes).
     */
    public static RuleAgent newRuleAgent(Properties config) {
        return newRuleAgent( config,
                             null,
                             null );
    }

    /**
     * Properties configured to load up packages into a rulebase with the provided
     * configuration (and monitor them for changes).
     */
    public static RuleAgent newRuleAgent(Properties config,
                                         RuleBaseConfiguration ruleBaseConf) {
        return newRuleAgent( config,
                             null,
                             ruleBaseConf );
    }

    /**
     * This allows an optional listener to be passed in.
     * The default one prints some stuff out to System.err only when really needed.
     */
    public static RuleAgent newRuleAgent(Properties config,
                                         AgentEventListener listener) {
        return newRuleAgent( config,
                             listener,
                             null );
    }

    /**
     * This allows an optional listener to be passed in.
     * The default one prints some stuff out to System.err only when really needed.
     */
    public static RuleAgent newRuleAgent(Properties config,
                                         AgentEventListener listener,
                                         RuleBaseConfiguration ruleBaseConf) {
        RuleAgent agent = new RuleAgent( ruleBaseConf );
        if ( listener != null ) {
            agent.listener = listener;
        }

        if ( ruleBaseConf == null ) {
            agent.init( config,
                        true );
        } else {
            agent.init( config );
        }

        return agent;
    }

    void init(Properties config) {
        init( config,
              false );
    }

    /**
     * 
     * @param config
     * @param lookForRuleBaseConfigurations true if config contains rule base configuration data that should be used.
     */
    void init(Properties config,
              boolean lookForRuleBaseConfigurations) {

        boolean newInstance = Boolean.valueOf( config.getProperty( NEW_INSTANCE,
                                                                   "false" ) ).booleanValue();
        int secondsToRefresh = Integer.parseInt( config.getProperty( POLL_INTERVAL,
                                                                     "-1" ) );
        final String name = config.getProperty( CONFIG_NAME,
                                                "default" );

        listener.setAgentName( name );

        listener.info( "Configuring with newInstance=" + newInstance + ", secondsToRefresh=" + secondsToRefresh );

        List provs = new ArrayList();

        Properties droolsProperties = new Properties();

        for ( Iterator iter = config.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();

            if ( ruleBaseConf != null && key.startsWith( "drools." ) ) {

                droolsProperties.setProperty( key,
                                              config.getProperty( key ) );

            } else {
                PackageProvider prov = getProvider( key,
                                                    config );
                if ( prov != null ) {
                    listener.info( "Configuring package provider : " + prov.toString() );
                    provs.add( prov );
                }
            }
        }

        // If there is no ruleBase and config file had rule base properties, set properties.
        if ( lookForRuleBaseConfigurations && !droolsProperties.isEmpty() ) {
            ruleBaseConf = new RuleBaseConfiguration( droolsProperties );
        }

        configure( newInstance,
                   provs,
                   secondsToRefresh );
    }

    /**
     * Pass in the name and full path to a config file that is on the classpath.
     */
    public static RuleAgent newRuleAgent(String propsFileName) {
        return newRuleAgent( loadFromProperties( propsFileName ) );
    }

    /**
     * Pass in the name and full path to a config file that is on the classpath.
     */
    public static RuleAgent newRuleAgent(String propsFileName,
                                         RuleBaseConfiguration ruleBaseConfiguration) {
        return newRuleAgent( loadFromProperties( propsFileName ),
                             ruleBaseConfiguration );
    }

    /**
     * This takes in an optional listener. Listener must not be null in this case.
     */
    public static RuleAgent newRuleAgent(String propsFileName,
                                         AgentEventListener listener) {
        return newRuleAgent( loadFromProperties( propsFileName ),
                             listener );
    }

    /**
     * This takes in an optional listener and RuleBaseConfiguration. Listener must not be null in this case.
     */
    public static RuleAgent newRuleAgent(String propsFileName,
                                         AgentEventListener listener,
                                         RuleBaseConfiguration ruleBaseConfiguration) {
        return newRuleAgent( loadFromProperties( propsFileName ),
                             listener,
                             ruleBaseConfiguration );
    }

    public void setName(String name) {
        this.name = name;
        if ( this.listener != null ) {
            this.listener.setAgentName( this.name );
        }
    }

    static Properties loadFromProperties(String propsFileName) {
        InputStream in = RuleAgent.class.getResourceAsStream( propsFileName );
        Properties props = new Properties();
        try {
            props.load( in );
            return props;
        } catch ( IOException e ) {
            throw new RuntimeDroolsException( "Unable to load properties. Needs to be the path and name of a config file on your classpath.",
                                              e );
        } finally {
            if ( null != in ) {
                try {
                    in.close();
                } catch ( IOException e ) {
                    throw new RuntimeDroolsException( "Unable to load properties. Could not close InputStream.",
                                                      e );
                }
            }
        }

    }

    /**
     * Return a configured provider ready to go.
     */
    private PackageProvider getProvider(String key,
                                        Properties config) {
        if ( !PACKAGE_PROVIDERS.containsKey( key ) ) {
            return null;
        }
        Class clz = (Class) PACKAGE_PROVIDERS.get( key );
        try {
            PackageProvider prov = (PackageProvider) clz.newInstance();
            prov.setAgentListener( listener );
            prov.configure( config );
            return prov;
        } catch ( InstantiationException e ) {
            throw new RuntimeDroolsException( "Unable to load up a package provider for " + key,
                                              e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Unable to load up a package provider for " + key,
                                              e );
        }
    }

    synchronized void configure(boolean newInstance,
                                List provs,
                                int secondsToRefresh) {
        this.newInstance = newInstance;
        this.providers = provs;

        //run it the first time for each.
        refreshRuleBase();

        if ( secondsToRefresh != -1 ) {
            startPolling( secondsToRefresh );
        }

    }

    public void refreshRuleBase() {

        List<Package> changedPackages = new ArrayList<Package>();
        List<String> removedPackages = new ArrayList<String>();

        for ( Iterator iter = providers.iterator(); iter.hasNext(); ) {
            PackageProvider prov = (PackageProvider) iter.next();
            PackageChangeInfo info = checkForChanges( prov );
            Collection<Package> changes = info.getChangedPackages();
            Collection<String> removed = info.getRemovedPackages();
            if ( changes != null && changes.size() > 0 ) {
                changedPackages.addAll( changes );
            }
            if ( removed != null && removed.size() > 0 ) {
                removedPackages.addAll( removed );
            }
        }

        // Update changes.
        if ( changedPackages.size() > 0 || removedPackages.size() > 0 ) {
            listener.info( "Applying changes to the rulebase." );
            //we have a change
            if ( this.newInstance ) {
                listener.info( "Creating a new rulebase as per settings." );
                //blow away old
                this.ruleBase = RuleBaseFactory.newRuleBase( this.ruleBaseConf );

                // Remove removed packages.
                for ( String name : removedPackages ) {
                    this.packages.remove( name );
                }
                //need to store ALL packages
                for ( Package element : changedPackages ) {
                    this.packages.put( element.getName(),
                                       element ); //replace
                }
                //get packages from full name
                PackageProvider.applyChanges( this.ruleBase,
                                              false,
                                              this.packages.values(),
                                              this.listener );
            } else {
                PackageProvider.applyChanges( this.ruleBase,
                                              true,
                                              changedPackages,
                                              removedPackages,
                                              this.listener );
            }
        }

    }

    private synchronized PackageChangeInfo checkForChanges(PackageProvider prov) {
        listener.debug( "SCANNING FOR CHANGE " + prov.toString() );
        if ( this.ruleBase == null ) ruleBase = RuleBaseFactory.newRuleBase( this.ruleBaseConf );
        PackageChangeInfo info = prov.loadPackageChanges();
        return info;
    }

    /**
     * Convert a space separated list into a List of stuff.
     * If a filename or whatnot has a space in it, you can put double quotes around it
     * and it will read it in as one token.
     */
    static List list(String property) {
        if ( property == null ) return Collections.EMPTY_LIST;
        char[] cs = property.toCharArray();
        boolean inquotes = false;
        List items = new ArrayList();
        String current = "";
        for ( int i = 0; i < cs.length; i++ ) {
            char c = cs[i];
            switch ( c ) {
                case '\"' :
                    if ( inquotes ) {
                        items.add( current );
                        current = "";
                    }
                    inquotes = !inquotes;
                    break;

                default :
                    if ( !inquotes && (c == ' ' || c == '\n' || c == '\r' || c == '\t') ) {
                        if ( !"".equals( current.trim() ) ) {
                            items.add( current );
                            current = "";
                        }
                    } else {
                        current = current + c;
                    }
                    break;
            }
        }
        if ( !"".equals( current.trim() ) ) {
            items.add( current );
        }

        return items;
    }

    /**
     * Return a current rulebase.
     * Depending on the configuration, this may be a new object each time
     * the rules are updated.
     *
     */
    public synchronized RuleBase getRuleBase() {
        return this.ruleBase;
    }

    RuleAgent(RuleBaseConfiguration ruleBaseConf) {
        if ( ruleBaseConf == null ) {
            this.ruleBaseConf = new RuleBaseConfiguration();
        } else {
            this.ruleBaseConf = ruleBaseConf;
        }
    }

    /**
     * Stop the polling (if it is happening)
     */
    public synchronized void stopPolling() {
        if ( this.timer != null ) timer.cancel();
        timer = null;
    }

    /**
     * Will start polling. If polling is already running it does nothing.
     *
     */
    public synchronized void startPolling() {
        if ( this.timer == null ) {
            startPolling( this.secondsToRefresh );
        }
    }

    /**
     * Will start polling. If polling is already happening and of the same interval
     * it will do nothing, if the interval is different it will stop the current Timer
     * and create a new Timer for the new interval.
     * @param secondsToRefresh
     */
    public synchronized void startPolling(int secondsToRefresh) {
        if ( this.timer != null ) {
            if ( this.secondsToRefresh != secondsToRefresh ) {
                stopPolling();
            } else {
                // do nothing.
                return;
            }
        }

        this.secondsToRefresh = secondsToRefresh;
        int interval = this.secondsToRefresh * 1000;
        //now schedule it for polling
        timer = new Timer( true );
        timer.schedule( new TimerTask() {
                            public void run() {
                                try {

                                    listener.debug( "Checking for updates." );
                                    refreshRuleBase();

                                } catch ( Exception e ) {
                                    //don't want to stop execution here.
                                    listener.exception( e );
                                }
                            }
                        },
                        interval,
                        interval );
    }

    boolean isNewInstance() {
        return newInstance;
    }

    public synchronized boolean isPolling() {
        return this.timer != null;
    }

    /**
     * This should only be used once, on setup.
     * @return
     */
    private AgentEventListener getDefaultListener() {

        return new AgentEventListener() {

            private String name;

            public String time() {
                Date d = new Date();
                return d.toString();
            }

            public void exception(String message, Throwable e) {
                System.err.println( "RuleAgent(" + name + ") EXCEPTION (" + time() + "): " + e.getMessage() + ". Stack trace should follow." );
                e.printStackTrace( System.err );
            }

            public void exception(Throwable e) {
                System.err.println( "RuleAgent(" + name + ") EXCEPTION (" + time() + "): " + e.getMessage() + ". Stack trace should follow." );
                e.printStackTrace( System.err );
            }

            public void info(String message) {
                System.err.println( "RuleAgent(" + name + ") INFO (" + time() + "): " + message );
            }

            public void warning(String message) {
                System.err.println( "RuleAgent(" + name + ") WARNING (" + time() + "): " + message );
            }

            public void debug(String message) {
                //do nothing...
            }

            public void setAgentName(String name) {
                this.name = name;

            }

            public void debug(String message,
                              Object object) {
            }

            public void info(String message,
                             Object object) {
            }

            public void warning(String message,
                                Object object) {
            }

        };
    }

    RuleBaseConfiguration getRuleBaseConfiguration() {
        return ruleBaseConf;
    }
}
