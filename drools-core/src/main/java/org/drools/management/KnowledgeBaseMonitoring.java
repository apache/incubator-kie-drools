/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.management;

import javax.management.ObjectName;

import org.drools.base.ClassObjectType;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;

/**
 * An implementation for the KnowledgeBaseMBean
 * 
 * @author etirelli
 */
public class KnowledgeBaseMonitoring
    implements
    KnowledgeBaseMonitoringMBean {

    private static final String KBASE_PREFIX = "org.drools.kbases";
    
    private ReteooRuleBase kbase;
    private ObjectName name;
    
    public KnowledgeBaseMonitoring(ReteooRuleBase kbase) {
        this.kbase = kbase;
        this.name = DroolsManagementAgent.createObjectName(KBASE_PREFIX + ":id="+kbase.getId());
    }
    
    public ObjectName getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeBaseMBean#getGlobals()
     */
    public String getGlobals() {
        return kbase.getGlobals().toString();
    }

    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeBaseMBean#getId()
     */
    public String getId() {
        return kbase.getId();
    }

    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeBaseMBean#getPackages()
     */
    public String getPackages() {
        return kbase.getPackagesMap().keySet().toString();
    }

    /* (non-Javadoc)
     * @see org.drools.management.KnowledgeBaseMBean#getSessionCount()
     */
    public long getSessionCount() {
        return kbase.getWorkingMemoryCounter();
    }
    
    public String getEntryPoints() {
        // the dependent mbeans are created here in order to delay their creating 
        // until the this kbase is actually inspected 
        return kbase.getRete().getEntryPointNodes().keySet().toString();
    }

    public void startInternalMBeans() {
        for( EntryPointNode epn : kbase.getRete().getEntryPointNodes().values() ) {
            for( ObjectTypeNode otn : epn.getObjectTypeNodes().values() ) {
                ObjectTypeNodeMonitor otnm = new ObjectTypeNodeMonitor( otn );
                ObjectName name = DroolsManagementAgent.createObjectName( this.name.getCanonicalName() + ",type=EntryPoints,EntryPoint=" + otnm.getNameSufix() + ",ObjectType="+((ClassObjectType)otn.getObjectType()).getClassName() );
                DroolsManagementAgent.getInstance().registerMBean( kbase, otnm, name );
            }
        }
        KBaseConfigurationMonitor kbcm = new KBaseConfigurationMonitor( kbase.getConfiguration() );
        ObjectName name = DroolsManagementAgent.createObjectName( this.name.getCanonicalName() + ",type=Configuration" );
        DroolsManagementAgent.getInstance().registerMBean( kbase, kbcm, name );
    }

    public void stopInternalMBeans() {
        DroolsManagementAgent.getInstance().unregisterDependentsMBeansFromOwner( kbase );
    }
    
}
