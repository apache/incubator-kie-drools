package org.drools.agent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.RuntimeDroolsException;

/**
 * This manages a single rulebase, based on the properties given
 * This one does most of the actual work !
 *
 * CONFIG OPTIONS:
 *  <code>newInstance</code>: means that each time the rules are changed
 *   a new instance of the rulebase is created (as opposed to updated in place)
 *   the default is to update in place. DEFAULT: false. If you set this to true, 
 *   then you will need to call getRuleBase() each time you want to use it. If it is false, 
 *   then it means you can keep your reference to the rulebase and it will be updated automatically
 *   (as well as any stateful sessions). 
 *
 *  <code>poll</code>The number of seconds to poll for changes. Polling 
 *  happens in a background thread.
 *
 *  <code>file</code>: a space seperated listing of files that make up the 
 *  packages of the rulebase. Each package can only be in one file. You can't have 
 *  packages spread across files.
 *  
 *  <code>dir</code>: a single file system directory to monitor for packages.
 *  As with files, each package must be in its own file.
 * 
 * @author Michael Neale
 */
public class RuleAgent {

    /**
     * Following are property keys to be used in the property
     * config file.
     */
    public static final String NEW_INSTANCE      = "newInstance";
    public static final String FILES             = "file";
    public static final String DIRECTORY         = "dir";
    public static final String URLS = "url";
    public static final String POLL_INTERVAL     = "poll";
    
    //this is needed for cold starting when BRMS is down (ie only for URL).
    public static final String LOCAL_URL_CACHE = "localCacheDir";

    /**
     * Here is where we have a map of providers to the key that appears on the configuration.
     */
    private static Map          PACKAGE_PROVIDERS = new HashMap() {
                                                     {
                                                         put( FILES,
                                                              FileScanner.class );
                                                         put( DIRECTORY,
                                                              DirectoryScanner.class );
                                                     }
                                                 };

    /**
     * This is true if the rulebase is created anew each time.
     */
    private boolean                    newInstance;

    /**
     * The rule base that is being managed.
     */
    private RuleBase           ruleBase;

    /**
     * The timer that is used to monitor for changes and deal with them. 
     */
    private Timer              timer;

    /**
     * The providers that actually do the work.
     */
    ArrayList                  providers;

    private AgentEventListener listener = getDefaultListener();
    
    /**
     * Properties configured to load up packages into a rulebase (and monitor them
     * for changes).
     */
    public RuleAgent(Properties config) {
        init( config );
    }



    private void init(Properties config) {

        boolean newInstance = Boolean.valueOf( config.getProperty( NEW_INSTANCE,
                                                                   "false" ) ).booleanValue();
        int secondsToRefresh = Integer.parseInt( config.getProperty( POLL_INTERVAL,
                                                                     "-1" ) );

        providers = new ArrayList();

        for ( Iterator iter = config.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            PackageProvider prov = getProvider( key,
                                                config );
            if ( prov != null ) {
                providers.add( prov );
            }
        }

        configure( newInstance,
                   providers,
                   secondsToRefresh );
    }

    /**
     * Pass in the name and full path to a config file that is on the classpath.
     */
    public RuleAgent(String propsFileName) {
        init( loadFromProperties( propsFileName ) );
    }

    Properties loadFromProperties(String propsFileName) {
        InputStream in = this.getClass().getResourceAsStream( propsFileName );
        Properties props = new Properties();
        try {
            props.load( in );
            return props;

        } catch ( IOException e ) {
            throw new RuntimeDroolsException( "Unable to load properties. Needs to be the path and name of a config file on your classpath.",
                                              e );
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
                                final List providers,
                                int secondsToRefresh) {
        this.newInstance = newInstance;

        //run it the first time for each.
        refreshRuleBase();

        if ( secondsToRefresh != -1 ) {
            int interval = secondsToRefresh * 1000;
            //now schedule it for polling
            timer = new Timer( true );
            timer.schedule( new TimerTask() {
                                public void run() {
                                    try {
                                        refreshRuleBase();
                                    } catch (Exception e) {
                                        //don't want to stop execution here.
                                        listener.exception( e );
                                    }
                                }
                            },
                            interval,
                            interval );
        }

    }

    public void refreshRuleBase() {
        for ( Iterator iter = providers.iterator(); iter.hasNext(); ) {
            PackageProvider prov = (PackageProvider) iter.next();
            updateRuleBase( prov );
        }
    }

    private synchronized void updateRuleBase(PackageProvider prov) {
        System.err.println( "SCANNING FOR CHANGE " + prov.toString() );
        if ( this.newInstance || this.ruleBase == null ) {
            ruleBase = RuleBaseFactory.newRuleBase();
        }
        prov.updateRuleBase( this.ruleBase,
                             !this.newInstance );
    }

    /**
     * Convert a space seperated list into a List of stuff.
     * @param property
     * @return
     */
    static List list(String property) {
        if ( property == null ) return Collections.EMPTY_LIST;
        StringTokenizer st = new StringTokenizer( property,
                                                  "\n\r\t " );
        List list = new ArrayList();
        while ( st.hasMoreTokens() ) {
            list.add( st.nextToken() );
        }
        return list;
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

    RuleAgent() {
    }

    /**
     * Stop the polling (if it is happening)
     */
    public void stopPolling() {
        if ( this.timer != null ) timer.cancel();
        timer = null;
    }

    boolean isNewInstance() {
        return newInstance;
    }

    boolean isPolling() {
        return this.timer != null;
    }

    private AgentEventListener getDefaultListener() {

        return new AgentEventListener() {

            public void exception(Exception e) {
                e.printStackTrace();
            }

            public void info(String message) {
                System.err.println("INFO: " + message);                
            }

            public void warning(String message) {
                System.err.println("WARNING: " + message);                
            }
            
        };
    }
    
}
