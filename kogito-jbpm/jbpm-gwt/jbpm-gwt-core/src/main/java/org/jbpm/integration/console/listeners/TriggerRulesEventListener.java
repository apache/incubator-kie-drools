package org.jbpm.integration.console.listeners;

import org.drools.WorkingMemory;
import org.drools.core.event.ActivationCancelledEvent;
import org.drools.core.event.ActivationCreatedEvent;
import org.drools.core.event.AfterActivationFiredEvent;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.AgendaGroupPoppedEvent;
import org.drools.core.event.AgendaGroupPushedEvent;
import org.drools.core.event.BeforeActivationFiredEvent;
import org.drools.core.event.RuleFlowGroupActivatedEvent;
import org.drools.core.event.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.StatefulKnowledgeSession;

public class TriggerRulesEventListener implements AgendaEventListener {
    
    private StatefulKnowledgeSession ksession;
    
    public TriggerRulesEventListener(StatefulKnowledgeSession ksession) {

        this.ksession = ksession;
    }

    public void activationCreated(ActivationCreatedEvent event,
            WorkingMemory workingMemory) {
        ksession.fireAllRules();
    }

    public void activationCancelled(ActivationCancelledEvent event,
            WorkingMemory workingMemory) {
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event,
            WorkingMemory workingMemory) {
    }

    public void afterActivationFired(AfterActivationFiredEvent event,
            WorkingMemory workingMemory) {
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event,
            WorkingMemory workingMemory) {
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event,
            WorkingMemory workingMemory) {
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
            WorkingMemory workingMemory) {
        workingMemory.fireAllRules();
    }

    public void beforeRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
    }

    public void afterRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
    }

}
