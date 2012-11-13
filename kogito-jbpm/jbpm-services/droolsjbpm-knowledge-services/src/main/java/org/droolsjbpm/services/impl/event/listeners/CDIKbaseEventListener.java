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
import org.kie.event.knowledgebase.AfterFunctionRemovedEvent;
import org.kie.event.knowledgebase.AfterKnowledgeBaseLockedEvent;
import org.kie.event.knowledgebase.AfterKnowledgeBaseUnlockedEvent;
import org.kie.event.knowledgebase.AfterKnowledgePackageAddedEvent;
import org.kie.event.knowledgebase.AfterKnowledgePackageRemovedEvent;
import org.kie.event.knowledgebase.AfterProcessAddedEvent;
import org.kie.event.knowledgebase.AfterProcessRemovedEvent;
import org.kie.event.knowledgebase.AfterRuleAddedEvent;
import org.kie.event.knowledgebase.AfterRuleRemovedEvent;
import org.kie.event.knowledgebase.BeforeFunctionRemovedEvent;
import org.kie.event.knowledgebase.BeforeKnowledgeBaseLockedEvent;
import org.kie.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;
import org.kie.event.knowledgebase.BeforeKnowledgePackageAddedEvent;
import org.kie.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;
import org.kie.event.knowledgebase.BeforeProcessAddedEvent;
import org.kie.event.knowledgebase.BeforeProcessRemovedEvent;
import org.kie.event.knowledgebase.BeforeRuleAddedEvent;
import org.kie.event.knowledgebase.BeforeRuleRemovedEvent;
import org.kie.event.knowledgebase.KnowledgeBaseEventListener;
import org.jboss.seam.transaction.Transactional;
import org.kie.definition.process.Process;
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
