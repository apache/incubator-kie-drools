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

import java.util.Date;
import java.util.List;
import org.droolsjbpm.services.api.IdentityProvider;
import org.droolsjbpm.services.api.SessionManager;
import org.jboss.seam.transaction.Transactional;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.droolsjbpm.services.impl.model.BAMProcessSummary;
import org.droolsjbpm.services.impl.model.StateHelper;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;

/**
 *
 * @author salaboy
 */
@ApplicationScoped // This should be something like DomainScoped
@Transactional
@BAM
public class CDIBAMProcessEventListener implements ProcessEventListener {
    @Inject
    private JbpmServicesPersistenceManager pm; 
    
    @Inject
    private IdentityProvider identity;
  
    public CDIBAMProcessEventListener() {
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent pse) {
        int currentState = pse.getProcessInstance().getState();

//        if (currentState == ProcessInstance.STATE_ACTIVE) {
            System.out.println("Saving event for process instance " + pse.getProcessInstance().getId());
            ProcessInstance processInstance = pse.getProcessInstance();
            int sessionId = ((StatefulKnowledgeSession)pse.getKieRuntime()).getId();
            String version = processInstance.getProcess().getVersion();
//            BAMProcessSummary processSummaryById = (BAMProcessSummary)em.createQuery("select bps from BAMProcessSummary bps where bps.processInstanceId =:processId")
//                                                    .setParameter("processId", processInstance.getId()).getSingleResult();
//            processSummaryById.setStatus(StateHelper.getProcessState(processInstance.getState()));
//            em.merge(processSummaryById);
            // FIXME this will record state pending so we might hard code it as Active to keep the right state in bam
            pm.persist(new BAMProcessSummary(processInstance.getId(), processInstance.getProcessName(), StateHelper.getProcessState(processInstance.getState()), new Date(), identity.getName(), version));
//        }
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent pse) {

    }

    public void beforeProcessCompleted(ProcessCompletedEvent pce) {
        ProcessInstance processInstance = pce.getProcessInstance();
        List<BAMProcessSummary> summaries = (List<BAMProcessSummary>)pm.queryStringWithParametersInTransaction("select bps from BAMProcessSummary bps where bps.processInstanceId =:processId",
                            pm.addParametersToMap("processId", processInstance.getId()));
        if(summaries.size() == 1){
          BAMProcessSummary processSummaryById = (BAMProcessSummary) summaries.get(0);
          processSummaryById.setStatus(StateHelper.getProcessState(processInstance.getState()));
          Date completedDate = new Date();
          Date startDate = processSummaryById.getStartDate();
          processSummaryById.setEndDate(completedDate);
          processSummaryById.setDuration(completedDate.getTime() - startDate.getTime() );
          pm.merge(processSummaryById);
        }else{
          // Log
          System.out.print("EEEE: Something went wrong with the BAM Listener");
        }
        
    }

    public void afterProcessCompleted(ProcessCompletedEvent pce) {
        
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent pnte) {
        
        
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent pnte) {
        
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent pnle) {
        
    }

    
    public void afterNodeLeft(ProcessNodeLeftEvent pnle) {
        
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent pvce) {
       
    }

    public void afterVariableChanged(ProcessVariableChangedEvent pvce) {
        
        
    }


    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public void setIdentity(IdentityProvider identity) {
        this.identity = identity;
    }
    
    
    
    
    
}
