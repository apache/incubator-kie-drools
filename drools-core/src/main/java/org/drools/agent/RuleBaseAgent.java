package org.drools.agent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.util.BinaryRuleBaseLoader;

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
 *  ...
 * 
 * @author Michael Neale
 */
public class RuleBaseAgent {

    /**
     * Following are property keys to be used in the property
     * config file.
     */
    public static final String NEW_INSTANCE  = "newInstance";
    public static final String FILES         = "file";
    //public static final String DIRECTORIES = "dir";
    //public static final String URIS = "uri";
    public static final String POLL_INTERVAL = "poll";

    /**
     * This is true if the rulebase is created anew each time.
     */
    private boolean            newInstance;
    
    /**
     * The rule base that is being managed.
     */
    private RuleBase           ruleBase;
    private Timer timer;

    public RuleBaseAgent(Properties config) {
        boolean newInstance = Boolean.parseBoolean( config.getProperty( NEW_INSTANCE, "false" ) );
        int secondsToRefresh = Integer.parseInt( config.getProperty( POLL_INTERVAL , "-1") );
        List files = list( config.getProperty( FILES ) );
        
        init( newInstance, files, secondsToRefresh );
    }

    synchronized void init(boolean newInstance, List files, int secondsToRefresh) {
        this.newInstance = newInstance;
        final FileScanner fileScan = new FileScanner();
        fileScan.setFiles( (String[]) files.toArray( new String[files.size()] ) );
        
        //run it the first time
        updateRuleBase( fileScan );
        
        
        if (secondsToRefresh != -1) {
            int interval = secondsToRefresh * 1000;
            //now schedule it for polling
            timer = new Timer();
            timer.schedule( new TimerTask() {
                public void run() {
                    updateRuleBase( fileScan );
                }
            }, interval, interval );
        }

    }

    
    private synchronized void updateRuleBase(FileScanner fileScan) {
        System.err.println("SCANNING FOR CHANGE " + fileScan.toString());
        
        if (this.newInstance || this.ruleBase == null) {
            ruleBase = RuleBaseFactory.newRuleBase();
        }
        fileScan.updateRuleBase( this.ruleBase, !this.newInstance );
    }

    /**
     * Convert a space seperated list into a List of stuff.
     * @param property
     * @return
     */
    List list(String property) {
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
    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    RuleBaseAgent() {
    }

    /**
     * Stop the polling (if it is happening)
     */
    public void stopPolling() {
        if (this.timer != null) timer.cancel();
        timer = null;
    }
    
    boolean isNewInstance() {
        return newInstance;
    }
    
    boolean isPolling() {
        return this.timer != null;
    }
    
}
