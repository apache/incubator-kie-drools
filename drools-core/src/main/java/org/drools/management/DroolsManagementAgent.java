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

package org.drools.management;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.drools.common.AbstractWorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteooRuleBase;

/**
 * The main management agent for Drools. The purpose of this 
 * agent is to serve as a singleton for knowledge base and session
 * monitoring mbeans registration and management.
 *  
 * @author etirelli
 *
 */
public class DroolsManagementAgent
    implements
    DroolsManagementAgentMBean {

    private static final String           MBEAN_NAME = "org.drools:type=DroolsManagementAgent";

    private static DroolsManagementAgent  INSTANCE;
    private static MBeanServer            mbs;

    private long                          kbases;
    private long                          ksessions;
    private Map<Object, List<ObjectName>> mbeans;

    private DroolsManagementAgent() {
        kbases = 0;
        ksessions = 0;
        mbeans = new HashMap<Object, List<ObjectName>>();
    }

    public static synchronized DroolsManagementAgent getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new DroolsManagementAgent();
            try {
                MBeanServer mbs = getMBeanServer();
                ObjectName mbName = createObjectName( MBEAN_NAME );
                if ( !mbs.isRegistered( mbName ) ) {
                    mbs.registerMBean( INSTANCE,
                                       mbName );
                }
            } catch ( Exception e ) {
                System.err.println( "Unable to register DroolsManagementAgent into the platform MBean Server" );
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.monitoring.DroolsManagementAgentMBean#getRulebaseCount()
     */
    public synchronized long getKnowledgeBaseCount() {
        return kbases;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.monitoring.DroolsManagementAgentMBean#getSessionCount()
     */
    public synchronized long getSessionCount() {
        return ksessions;
    }

    public synchronized long getNextKnowledgeBaseId() {
        return ++kbases;
    }

    public synchronized long getNextKnowledgeSessionId() {
        return ++ksessions;
    }

    public void registerKnowledgeBase(ReteooRuleBase kbase) {
        KnowledgeBaseMonitoring mbean = new KnowledgeBaseMonitoring( kbase );
        registerMBean( kbase,
                       mbean,
                       mbean.getName() );
    }

    public void registerKnowledgeSession(InternalWorkingMemory ksession) {
        KnowledgeSessionMonitoring mbean = new KnowledgeSessionMonitoring( ksession );
        registerMBean( ksession, 
                       mbean,
                       mbean.getName() );
    }

    public void unregisterKnowledgeSession(InternalWorkingMemory ksession) {
        unregisterMBeansFromOwner( ksession );
    }

    public void registerMBean(Object owner,
                              Object mbean,
                              ObjectName name) {
        try {
            MBeanServer mbs = getMBeanServer();
            if ( !mbs.isRegistered( name ) ) {
                mbs.registerMBean( mbean,
                                   name );
                List<ObjectName> mbl = mbeans.get( owner );
                if ( mbl == null ) {
                    mbl = new LinkedList<ObjectName>();
                    mbeans.put( owner,
                                mbl );
                }
                mbl.add( name );
            }
        } catch ( Exception e ) {
            System.err.println( "Unable to register mbean " + name + " into the platform MBean Server" );
            e.printStackTrace();
        }
    }

    public void unregisterMBeansFromOwner(Object owner) {
        List<ObjectName> mbl = mbeans.remove( owner );
        if ( mbl != null ) {
            MBeanServer mbs = getMBeanServer();
            for ( ObjectName name : mbl ) {
                unregisterMBeanFromServer( mbs,
                                           name );
            }
        }
    }

    private void unregisterMBeanFromServer(MBeanServer mbs,
                                           ObjectName name) {
        try {
            mbs.unregisterMBean( name );
        } catch ( Exception e ) {
            System.err.println( "Exception unregistering mbean: " + name );
            e.printStackTrace();
        }
    }
    
    public void unregisterMBean( Object owner, ObjectName mbean ) {
        List<ObjectName> mbl = mbeans.get( owner );
        if( mbl != null ) {
            mbl.remove( mbean );
        }
        MBeanServer mbs = getMBeanServer();
        unregisterMBeanFromServer( mbs,
                                   mbean );
    }

    public void unregisterDependentsMBeansFromOwner(Object owner) {
        List<ObjectName> mbl = mbeans.get( owner );
        if ( mbl != null ) {
            MBeanServer mbs = getMBeanServer();
            for ( ObjectName name : mbl.subList( 1, mbl.size() ) ) {
                unregisterMBeanFromServer( mbs,
                                           name );
            }
            mbl.subList( 1, mbl.size() ).clear();
        }
    }

    public static ObjectName createObjectName(String name) {
        try {
            return new ObjectName( name );
        } catch ( Exception e ) {
            System.err.println( "This is a bug. Error creating ObjectName for MBean: " + name );
            System.err.println( "Please contact the development team and provide the following stack trace: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
    }

    private static MBeanServer getMBeanServer() {
        if ( mbs == null ) {
            mbs = ManagementFactory.getPlatformMBeanServer();
        }
        return mbs;
    }

}
