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
import org.drools.event.AfterFunctionRemovedEvent;
import org.drools.event.AfterProcessAddedEvent;
import org.drools.event.AfterProcessRemovedEvent;
import org.drools.event.AfterRuleAddedEvent;
import org.drools.event.AfterRuleRemovedEvent;
import org.drools.event.BeforeFunctionRemovedEvent;
import org.drools.event.BeforeProcessAddedEvent;
import org.drools.event.BeforeProcessRemovedEvent;
import org.drools.event.BeforeRuleAddedEvent;
import org.drools.event.BeforeRuleRemovedEvent;
import org.jboss.seam.transaction.Transactional;
import org.kie.definition.process.Process;
import org.droolsjbpm.services.impl.bpmn2.ProcessDescriptionRepository;
import org.droolsjbpm.services.impl.helpers.ProcessDescFactory;
import org.kie.event.kiebase.AfterKieBaseLockedEvent;
import org.kie.event.kiebase.AfterKieBaseUnlockedEvent;
import org.kie.event.kiebase.AfterKnowledgePackageAddedEvent;
import org.kie.event.kiebase.AfterKnowledgePackageRemovedEvent;
import org.kie.event.kiebase.BeforeKieBaseLockedEvent;
import org.kie.event.kiebase.BeforeKieBaseUnlockedEvent;
import org.kie.event.kiebase.BeforeKnowledgePackageAddedEvent;
import org.kie.event.kiebase.BeforeKnowledgePackageRemovedEvent;
import org.kie.event.kiebase.KieBaseEventListener;

/**
 *
 * @author salaboy
 */
@Transactional
@ApplicationScoped
public class CDIKbaseEventListener implements KieBaseEventListener {

    @Inject
    private EntityManager em;
    @Inject
    private ProcessDescriptionRepository repository;
    private String domainName;

    public void beforeKnowledgePackageAdded(BeforeKnowledgePackageAddedEvent bkpae) {
    }

    public void afterKnowledgePackageAdded(AfterKnowledgePackageAddedEvent akpae) {
    }

    public void beforeKnowledgePackageRemoved(BeforeKnowledgePackageRemovedEvent bkpre) {
    }

    public void afterKnowledgePackageRemoved(AfterKnowledgePackageRemovedEvent akpre) {
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
        repository.removeProcessDescription(apre.getProcess().getId());
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public void beforeKnowledgeBaseLocked(BeforeKieBaseLockedEvent bkble) {
    }

    @Override
    public void afterKnowledgeBaseLocked(AfterKieBaseLockedEvent akble) {
    }

    @Override
    public void beforeKnowledgeBaseUnlocked(BeforeKieBaseUnlockedEvent bkbue) {
    }

    @Override
    public void afterKnowledgeBaseUnlocked(AfterKieBaseUnlockedEvent akbue) {
    }

    @Override
    public void beforeRuleAdded(org.kie.event.kiebase.BeforeRuleAddedEvent brae) {
    }

    @Override
    public void afterRuleAdded(org.kie.event.kiebase.AfterRuleAddedEvent arae) {
    }

    @Override
    public void beforeRuleRemoved(org.kie.event.kiebase.BeforeRuleRemovedEvent brre) {
    }

    @Override
    public void afterRuleRemoved(org.kie.event.kiebase.AfterRuleRemovedEvent arre) {
    }

    @Override
    public void beforeFunctionRemoved(org.kie.event.kiebase.BeforeFunctionRemovedEvent bfre) {
    }

    @Override
    public void afterFunctionRemoved(org.kie.event.kiebase.AfterFunctionRemovedEvent afre) {
    }

    @Override
    public void beforeProcessAdded(org.kie.event.kiebase.BeforeProcessAddedEvent bpae) {
    }

    @Override
    public void afterProcessAdded(org.kie.event.kiebase.AfterProcessAddedEvent apae) {
    }

    @Override
    public void beforeProcessRemoved(org.kie.event.kiebase.BeforeProcessRemovedEvent bpre) {
    }

    @Override
    public void afterProcessRemoved(org.kie.event.kiebase.AfterProcessRemovedEvent apre) {
    }
}
