/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.management;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.management.KieManagementAgentMBean;
import org.kie.api.management.KieSessionMonitoringMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The main management agent for Drools. The purpose of this 
 * agent is to serve as a singleton for knowledge base and session
 * monitoring mbeans registration and management.
 */
public class DroolsManagementAgent
    implements
    KieManagementAgentMBean {

    private static final String           MBEAN_NAME = "org.kie:type=DroolsManagementAgent";

	private static final String CONTAINER_NAME_PREFIX = "org.kie";
	
    private static DroolsManagementAgent  INSTANCE;
    private static MBeanServer            mbs;

    protected static final transient Logger logger = LoggerFactory.getLogger(DroolsManagementAgent.class);

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
                    final StandardMBean adapter = new StandardMBean(INSTANCE, KieManagementAgentMBean.class);
                    mbs.registerMBean( adapter,
                                       mbName );
                }
            } catch ( Exception e ) {
                logger.error( "Unable to register DroolsManagementAgent into the platform MBean Server", e);
            }
        }
        return INSTANCE;
    }
    

	public static ObjectName createObjectNameFor(InternalKnowledgeBase kbase) {
		return DroolsManagementAgent.createObjectName(
					DroolsManagementAgent.createObjectNameByContainerId(kbase.getContainerId())
					+ ",kbaseId=" + ObjectName.quote(kbase.getId())
					);
	}
	
	public static ObjectName createObjectNameFor(InternalWorkingMemory ksession) {
		return DroolsManagementAgent.createObjectName(
				DroolsManagementAgent.createObjectNameFor(ksession.getKnowledgeBase()) + 
				",group=Sessions,ksessionId=Session-"+ksession.getIdentifier());
	}
	
	public static ObjectName createObjectNameByContainerId(String containerId) {
		return DroolsManagementAgent.createObjectName(CONTAINER_NAME_PREFIX + ":kcontainerId="+ObjectName.quote(containerId));
	}

    /* (non-Javadoc)
     * @see org.drools.core.reteoo.monitoring.DroolsManagementAgentMBean#getRulebaseCount()
     */
    public synchronized long getKieBaseCount() {
        return kbases;
    }

    /* (non-Javadoc)
     * @see org.drools.core.reteoo.monitoring.DroolsManagementAgentMBean#getSessionCount()
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

    public void registerKnowledgeBase(InternalKnowledgeBase kbase) {
        KnowledgeBaseMonitoring mbean = new KnowledgeBaseMonitoring( kbase );
        registerMBean( kbase,
                       mbean,
                       mbean.getName() );
    }

    public void registerKnowledgeSession(InternalWorkingMemory ksession) {
        KieSessionMonitoringImpl mbean = new KieSessionMonitoringImpl( ksession );
        try {
            final StandardMBean adapter = new StandardMBean( mbean, KieSessionMonitoringMBean.class );
            registerMBean( ksession,
                           adapter,
                           mbean.getName() );
        } catch ( Exception e ) {
            logger.error("Unable to instantiate and register KieSessionMonitoringMBean");
        }
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
                logger.debug( "Registered {} into the platform MBean Server", name );
            }
        } catch ( Exception e ) {
            logger.error( "Unable to register mbean " + name + " into the platform MBean Server", e );
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
            logger.error( "Exception unregistering mbean: " + name, e);
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
            logger.error( "This is a bug. Error creating ObjectName for MBean: " + name
                + "\nPlease contact the development team and provide the following stack trace: " + e.getMessage(), e);
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
