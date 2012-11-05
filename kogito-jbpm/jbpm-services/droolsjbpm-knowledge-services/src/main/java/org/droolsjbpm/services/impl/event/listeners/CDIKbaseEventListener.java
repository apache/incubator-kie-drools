/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl.event.listeners;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.drools.event.knowledgebase.AfterFunctionRemovedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.AfterKnowledgePackageRemovedEvent;
import org.drools.event.knowledgebase.AfterProcessAddedEvent;
import org.drools.event.knowledgebase.AfterProcessRemovedEvent;
import org.drools.event.knowledgebase.AfterRuleAddedEvent;
import org.drools.event.knowledgebase.AfterRuleRemovedEvent;
import org.drools.event.knowledgebase.BeforeFunctionRemovedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseLockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageAddedEvent;
import org.drools.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;
import org.drools.event.knowledgebase.BeforeProcessAddedEvent;
import org.drools.event.knowledgebase.BeforeProcessRemovedEvent;
import org.drools.event.knowledgebase.BeforeRuleAddedEvent;
import org.drools.event.knowledgebase.BeforeRuleRemovedEvent;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.jboss.seam.transaction.Transactional;
import org.drools.definition.process.Process;
import org.droolsjbpm.services.impl.helpers.ProcessDescFactory;
/**
 *
 * @author salaboy
 */
@Transactional
@ApplicationScoped
public class CDIKbaseEventListener implements KnowledgeBaseEventListener{

    @Inject
    private EntityManager em; 
    
    private String domainName;
    
    public void beforeKnowledgePackageAdded(BeforeKnowledgePackageAddedEvent bkpae) {
        
    }

    public void afterKnowledgePackageAdded(AfterKnowledgePackageAddedEvent akpae) {
        
    }

    public void beforeKnowledgePackageRemoved(BeforeKnowledgePackageRemovedEvent bkpre) {
        
    }

    public void afterKnowledgePackageRemoved(AfterKnowledgePackageRemovedEvent akpre) {
        
    }

    public void beforeKnowledgeBaseLocked(BeforeKnowledgeBaseLockedEvent bkble) {
        
    }

    public void afterKnowledgeBaseLocked(AfterKnowledgeBaseLockedEvent akble) {
        
    }

    public void beforeKnowledgeBaseUnlocked(BeforeKnowledgeBaseUnlockedEvent bkbue) {
        
    }

    public void afterKnowledgeBaseUnlocked(AfterKnowledgeBaseUnlockedEvent akbue) {
        
    }

    public void beforeRuleAdded(BeforeRuleAddedEvent brae) {
        
    }

    public void afterRuleAdded(AfterRuleAddedEvent arae) {
        
    }

    public void beforeRuleRemoved(BeforeRuleRemovedEvent brre) {
        
    }

    public void afterRuleRemoved(AfterRuleRemovedEvent arre) {
        
    }

    public void beforeFunctionRemoved(BeforeFunctionRemovedEvent bfre) {
        
    }

    public void afterFunctionRemoved(AfterFunctionRemovedEvent afre) {
        
    }

    public void beforeProcessAdded(BeforeProcessAddedEvent bpae) {
        
    }

    public void afterProcessAdded(AfterProcessAddedEvent apae) {
        Process process = apae.getProcess();
        em.persist(ProcessDescFactory.newProcessDesc(this.domainName, process));
    }

    public void beforeProcessRemoved(BeforeProcessRemovedEvent bpre) {
        
    }

    public void afterProcessRemoved(AfterProcessRemovedEvent apre) {
        
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
    
    
    
}
