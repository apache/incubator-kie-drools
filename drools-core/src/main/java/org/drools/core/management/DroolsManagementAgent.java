/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.management;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.drools.base.RuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.management.KieManagementAgentMBean;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.base.util.Drools.isNativeImage;

/**
 * The main management agent for Drools. The purpose of this 
 * agent is to serve as a singleton for knowledge base and session
 * monitoring mbeans registration and management.
 */
public interface DroolsManagementAgent extends KieManagementAgentMBean {

    String CONTAINER_NAME_PREFIX = "org.kie";
    
    Logger logger = LoggerFactory.getLogger(DroolsManagementAgent.class);

    class DroolsManagementAgentHolder {
        private static final DroolsManagementAgent INSTANCE = isNativeImage() ? new Dummy() : new Impl();
    }

    static DroolsManagementAgent getInstance() {
        return DroolsManagementAgentHolder.INSTANCE;
    }

    static ObjectName createObjectNameFor(RuleBase kbase) {
        return DroolsManagementAgent.createObjectName(
                    DroolsManagementAgent.createObjectNameBy(kbase.getContainerId())
                    + ",kbaseId=" + ObjectName.quote(kbase.getId())
                    );
    }
    
    static ObjectName createObjectNameFor(InternalWorkingMemory ksession) {
        return DroolsManagementAgent.createObjectName(
                DroolsManagementAgent.createObjectNameFor(ksession.getKnowledgeBase()) + 
                ",group=Sessions,ksessionId=Session-"+ksession.getIdentifier());
    }

    static ObjectName createObjectNameBy(String containerId) {
        return DroolsManagementAgent.createObjectName(CONTAINER_NAME_PREFIX + ":kcontainerId="+ObjectName.quote(containerId));
    }

    static ObjectName createObjectNameBy(String containerId, String kbaseId, KieSessionModel.KieSessionType ksessionType, String ksessionName) {
        return DroolsManagementAgent.createObjectName(CONTAINER_NAME_PREFIX + ":kcontainerId="+ObjectName.quote(containerId) 
                    + ",kbaseId=" + ObjectName.quote(kbaseId) 
                    + ",ksessionType=" + ksessionType(ksessionType)
                    + ",ksessionName=" + ObjectName.quote(ksessionName) );
    }
    
    static String ksessionType(KieSessionModel.KieSessionType ksessionType) {
        switch(ksessionType) {
            case STATELESS:
                return "Stateless";
            case STATEFUL:
            default:
                return "Stateful";
        }
    }

    long getKieBaseCount();

    long getSessionCount();

    long getNextKnowledgeBaseId();

    long getNextKnowledgeSessionId();

    void registerKnowledgeBase(RuleBase kbase);
    
    void unregisterKnowledgeBase(RuleBase kbase);
    
    void registerKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession);

    void unregisterKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession);
    
    void unregisterKnowledgeSessionBean(CBSKey cbsKey);

    void registerMBean(Object owner, Object mbean, ObjectName name);

    void unregisterMBeansFromOwner(Object owner);

    void unregisterMBean( Object owner, ObjectName mbean );

    void unregisterDependentsMBeansFromOwner(Object owner);

    static ObjectName createObjectName(String name) {
        try {
            return new ObjectName( name );
        } catch ( Exception e ) {
            logger.error( "This is a bug. Error creating ObjectName for MBean: " + name
                + "\nPlease contact the development team and provide the following stack trace: " + e.getMessage(), e);
            return null;
        }
    }

    class CBSKey {
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
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof CBSKey)) {
                return false;
            }
            CBSKey other = (CBSKey) obj;
            if (kbaseId == null) {
                if (other.kbaseId != null) {
                    return false;
                }
            } else if (!kbaseId.equals(other.kbaseId)) {
                return false;
            }
            if (kcontainerId == null) {
                if (other.kcontainerId != null) {
                    return false;
                }
            } else if (!kcontainerId.equals(other.kcontainerId)) {
                return false;
            }
            if (ksessionName == null) {
                if (other.ksessionName != null) {
                    return false;
                }
            } else if (!ksessionName.equals(other.ksessionName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CBSKey [kcontainerId=" + kcontainerId + ", kbaseId=" + kbaseId + ", ksessionName=" + ksessionName + "]";
        }
    }

    class Impl implements DroolsManagementAgent {

        private static MBeanServer            mbs;

        private long                          kbases;
        private long                          ksessions;
        private Map<Object, List<ObjectName>> mbeans;
        private Map<Object, Object> mbeansRefs = new HashMap<>();

        private Impl() {
            kbases = 0;
            ksessions = 0;
            mbeans = new HashMap<>();
        }

        @Override
        public synchronized long getKieBaseCount() {
            return kbases;
        }

        @Override
        public synchronized long getSessionCount() {
            return ksessions;
        }

        @Override
        public synchronized long getNextKnowledgeBaseId() {
            return ++kbases;
        }

        @Override
        public synchronized long getNextKnowledgeSessionId() {
            return ++ksessions;
        }

        @Override
        public void registerKnowledgeBase(RuleBase kbase) {
            KnowledgeBaseMonitoring mbean = new KnowledgeBaseMonitoring( kbase );
            registerMBean( kbase,
                    mbean,
                    mbean.getName() );
        }

        @Override
        public void unregisterKnowledgeBase(RuleBase kbase) {
            unregisterMBeansFromOwner(kbase);
        }

        @Override
        public void registerKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession) {
            GenericKieSessionMonitoringImpl bean = getKnowledgeSessionBean(cbsKey, ksession);
            if (bean != null) { bean.attach(ksession); }
        }

        @Override
        public void unregisterKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession) {
            GenericKieSessionMonitoringImpl bean = getKnowledgeSessionBean(cbsKey, ksession);
            if (bean != null) { bean.detach(ksession); }
        }

        @Override
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
                if (ksession instanceof StatelessKieSession) {
                    synchronized (mbeansRefs) {
                        if (mbeansRefs.get(cbsKey) != null) {
                            return (GenericKieSessionMonitoringImpl) mbeansRefs.get(cbsKey);
                        } else {
                            try {
                                GenericKieSessionMonitoringImpl mbean = RuntimeComponentFactory.get().createStatelessSessionMonitor( cbsKey );
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
                                GenericKieSessionMonitoringImpl mbean = RuntimeComponentFactory.get().createStatefulSessionMonitor( cbsKey );
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

        @Override
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
                        mbl = new ArrayList<>();
                        mbeans.put( owner, mbl );
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

        @Override
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

        @Override
        public void unregisterMBean( Object owner, ObjectName mbean ) {
            List<ObjectName> mbl = mbeans.get( owner );
            if( mbl != null ) {
                mbl.remove( mbean );
            }
            MBeanServer mbs = getMBeanServer();
            unregisterMBeanFromServer( mbs,
                    mbean );
        }

        @Override
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

        private static MBeanServer getMBeanServer() {
            if ( mbs == null ) {
                mbs = ManagementFactory.getPlatformMBeanServer();
            }
            return mbs;
        }
    }

    class Dummy implements DroolsManagementAgent {
        @Override
        public long getKieBaseCount() {
            return 0;
        }

        @Override
        public long getSessionCount() {
            return 0;
        }

        @Override
        public long getNextKnowledgeBaseId() {
            return 0;
        }

        @Override
        public long getNextKnowledgeSessionId() {
            return 0;
        }

        @Override
        public void registerKnowledgeBase(RuleBase kbase) {

        }

        @Override
        public void unregisterKnowledgeBase(RuleBase kbase) {

        }

        @Override
        public void registerKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession) {

        }

        @Override
        public void unregisterKnowledgeSessionUnderName(CBSKey cbsKey, KieRuntimeEventManager ksession) {

        }

        @Override
        public void unregisterKnowledgeSessionBean(CBSKey cbsKey) {

        }

        @Override
        public void registerMBean(Object owner, Object mbean, ObjectName name) {

        }

        @Override
        public void unregisterMBeansFromOwner(Object owner) {

        }

        @Override
        public void unregisterMBean(Object owner, ObjectName mbean) {

        }

        @Override
        public void unregisterDependentsMBeansFromOwner(Object owner) {

        }
    }
}
