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
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.management.KieManagementAgentMBean;
import org.kie.internal.runtime.StatelessKnowledgeSession;
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

	private static final String CONTAINER_NAME_PREFIX = "org.kie";
	
    private static DroolsManagementAgent  INSTANCE;
    private static MBeanServer            mbs;

    protected static final transient Logger logger = LoggerFactory.getLogger(DroolsManagementAgent.class);

    private long                          kbases;
    private long                          ksessions;
    private Map<Object, List<ObjectName>> mbeans;
    private Map<Object, Object> mbeansRefs = new HashMap<Object, Object>();

    private DroolsManagementAgent() {
        kbases = 0;
        ksessions = 0;
        mbeans = new HashMap<Object, List<ObjectName>>();
    }

    public static synchronized DroolsManagementAgent getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new DroolsManagementAgent();
        }
        return INSTANCE;
    }
    

	public static ObjectName createObjectNameFor(InternalKnowledgeBase kbase) {
		return DroolsManagementAgent.createObjectName(
					DroolsManagementAgent.createObjectNameBy(kbase.getContainerId())
					+ ",kbaseId=" + ObjectName.quote(kbase.getId())
					);
	}
	
	public static ObjectName createObjectNameFor(InternalWorkingMemory ksession) {
		return DroolsManagementAgent.createObjectName(
				DroolsManagementAgent.createObjectNameFor(ksession.getKnowledgeBase()) + 
				",group=Sessions,ksessionId=Session-"+ksession.getIdentifier());
	}
	
	public static ObjectName createObjectNameBy(String containerId) {
		return DroolsManagementAgent.createObjectName(CONTAINER_NAME_PREFIX + ":kcontainerId="+ObjectName.quote(containerId));
	}
	
    public static ObjectName createObjectNameBy(String containerId, String kbaseId, KieSessionModel.KieSessionType ksessionType, String ksessionName) {
        return DroolsManagementAgent.createObjectName(CONTAINER_NAME_PREFIX + ":kcontainerId="+ObjectName.quote(containerId) 
                    + ",kbaseId=" + ObjectName.quote(kbaseId) 
                    + ",ksessionType=" + ksessionType(ksessionType)
                    + ",ksessionName=" + ObjectName.quote(ksessionName) );
    }
    
    private static String ksessionType(KieSessionModel.KieSessionType ksessionType) {
        switch(ksessionType) {
            case STATELESS:
                return "Stateless";
            case STATEFUL:
            default:
                return "Stateful";
        }
    }

    public synchronized long getKieBaseCount() {
        return kbases;
    }

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
    
    public void unregisterKnowledgeBase(InternalKnowledgeBase kbase) {
        unregisterMBeansFromOwner(kbase);
    }
    
    public void registerKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession) {
        GenericKieSessionMonitoringImpl bean = getKnowledgeSessionBean(cbsKey, ksession);
        if (bean != null) { bean.attach(ksession); }
    }
    public void unregisterKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession) {
        GenericKieSessionMonitoringImpl bean = getKnowledgeSessionBean(cbsKey, ksession);
        if (bean != null) { bean.detach(ksession); }
    }
    
    public void unregisterKnowledgeSessionBean(CBSKey cbsKey) {
        unregisterMBeansFromOwner(cbsKey);
    }

    /**
     * Get currently registered session monitor, eventually creating it if necessary.
     * @return the currently registered or newly created session monitor, or null if unable to create and register it on the JMX server.
     */
    private GenericKieSessionMonitoringImpl getKnowledgeSessionBean(CBSKey cbsKey, KieRuntimeEventManager ksession) {
        if (mbeansRefs.get(cbsKey) != null) {
            return (GenericKieSessionMonitoringImpl) mbeansRefs.get(cbsKey);
        } else {
            if (ksession instanceof StatelessKnowledgeSession) {
                synchronized (mbeansRefs) {
                    if (mbeansRefs.get(cbsKey) != null) {
                        return (GenericKieSessionMonitoringImpl) mbeansRefs.get(cbsKey);
                    } else {
                        try {
                            StatelessKieSessionMonitoringImpl mbean = new StatelessKieSessionMonitoringImpl( cbsKey.kcontainerId, cbsKey.kbaseId, cbsKey.ksessionName );
                            registerMBean( cbsKey, mbean, mbean.getName() );
                            mbeansRefs.put(cbsKey, mbean);
                            return mbean;
                        } catch ( Exception e ) {
                            logger.error("Unable to instantiate and register StatelessKieSessionMonitoringMBean");
                        }
                        return null;
                    }
                }
            } else {
                synchronized (mbeansRefs) {
                    if (mbeansRefs.get(cbsKey) != null) {
                        return (GenericKieSessionMonitoringImpl) mbeansRefs.get(cbsKey);
                    } else {
                        try {
                            KieSessionMonitoringImpl mbean = new KieSessionMonitoringImpl( cbsKey.kcontainerId, cbsKey.kbaseId, cbsKey.ksessionName );
                            registerMBean( cbsKey, mbean, mbean.getName() );
                            mbeansRefs.put(cbsKey, mbean);
                            return mbean;
                        } catch ( Exception e ) {
                            logger.error("Unable to instantiate and register (stateful) KieSessionMonitoringMBean");
                        }
                        return null;
                    }
                }
            }
        }
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
                    if (mbean instanceof StandardMBean) {
                        mbeansRefs.put(owner, ((StandardMBean) mbean).getImplementation());
                    } else {
                        mbeansRefs.put(owner, mbean);
                    }
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
        mbeansRefs.remove(owner);
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
            logger.debug( "Unregistered from MBean Server: {}", name);
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


    public static class CBSKey {
        private final String kcontainerId;
        private final String kbaseId;
        private final String ksessionName;
        public CBSKey(String kcontainerId, String kbaseId, String ksessionName) {
            super();
            this.kcontainerId = kcontainerId;
            this.kbaseId = kbaseId;
            this.ksessionName = ksessionName;
        }
        
        public String getKcontainerId() {
            return kcontainerId;
        }
        
        public String getKbaseId() {
            return kbaseId;
        }
        
        public String getKsessionName() {
            return ksessionName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((kbaseId == null) ? 0 : kbaseId.hashCode());
            result = prime * result + ((kcontainerId == null) ? 0 : kcontainerId.hashCode());
            result = prime * result + ((ksessionName == null) ? 0 : ksessionName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof CBSKey))
                return false;
            CBSKey other = (CBSKey) obj;
            if (kbaseId == null) {
                if (other.kbaseId != null)
                    return false;
            } else if (!kbaseId.equals(other.kbaseId))
                return false;
            if (kcontainerId == null) {
                if (other.kcontainerId != null)
                    return false;
            } else if (!kcontainerId.equals(other.kcontainerId))
                return false;
            if (ksessionName == null) {
                if (other.ksessionName != null)
                    return false;
            } else if (!ksessionName.equals(other.ksessionName))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "CBSKey [kcontainerId=" + kcontainerId + ", kbaseId=" + kbaseId + ", ksessionName=" + ksessionName + "]";
        }
        
    }
}
