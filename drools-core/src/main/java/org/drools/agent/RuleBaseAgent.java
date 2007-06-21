package org.drools.agent;

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
 *   the default is to update in place. DEFAULT: true
 *
 *  <code>file</code>: a space seperated listing of files that make up the 
 *  packages of the rulebase. 
 *  
 *  <code>dir</code>: a single file system directory to monitor for packages.
 *  ...
 * 
 * @author Michael Neale
 */
public class RuleBaseAgent {

    /**
     * Following are property keys to be used in the property
     * config file.
     */
    public static final String NEW_INSTANCE      = "newInstance";
    public static final String FILES             = "file";
    public static final String DIRECTORY = "dir";
    //public static final String URIS = "uri";
    public static final String POLL_INTERVAL     = "poll";

    /**
     * Here is where we have a map of providers to the key that appears on the configuration.
     */
    public static Map          PACKAGE_PROVIDERS = new HashMap() {
                                                     {
                                                         put( FILES, FileScanner.class );
                                                     }
                                                 };

    /**
     * This is true if the rulebase is created anew each time.
     */
    private boolean            newInstance;

    /**
     * The rule base that is being managed.
     */
    private RuleBase           ruleBase;

    /**
     * The timer that is used to monitor for changes and deal with them. 
     */
    private Timer              timer;

    public RuleBaseAgent(
                         Properties config) {
        boolean newInstance = Boolean.parseBoolean( config.getProperty( NEW_INSTANCE, "false" ) );
        int secondsToRefresh = Integer.parseInt( config.getProperty( POLL_INTERVAL, "-1" ) );

        List providers = new ArrayList();

        for ( Iterator iter = config.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            PackageProvider prov = getProvider( key, config );
            if (prov != null) {
                providers.add( prov ) ;
            }
        }

        init( newInstance, providers, secondsToRefresh );
    }

    /**
     * Return a configured provider ready to go.
     */
    private PackageProvider getProvider(String key, Properties config) {
        if (!PACKAGE_PROVIDERS.containsKey( key )) {
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

    synchronized void init(boolean newInstance, final List providers, int secondsToRefresh) {
        this.newInstance = newInstance;

        //run it the first time for each.
        for ( Iterator iter = providers.iterator(); iter.hasNext(); ) {
            PackageProvider prov = (PackageProvider) iter.next();
            updateRuleBase( prov );
        }

        if ( secondsToRefresh != -1 ) {
            int interval = secondsToRefresh * 1000;
            //now schedule it for polling
            timer = new Timer( true );
            timer.schedule( new TimerTask() {
                public void run() {
                    for ( Iterator iter = providers.iterator(); iter.hasNext(); ) {
                        PackageProvider prov = (PackageProvider) iter.next();
                        updateRuleBase( prov );
                    }
                }
            }, interval, interval );
        }

    }

    private synchronized void updateRuleBase(PackageProvider prov) {
        System.err.println( "SCANNING FOR CHANGE " + prov.toString() );
        if ( this.newInstance || this.ruleBase == null ) {
            ruleBase = RuleBaseFactory.newRuleBase();
        }
        prov.updateRuleBase( this.ruleBase, !this.newInstance );
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

    RuleBaseAgent() {
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

}
