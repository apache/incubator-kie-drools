package org.jbpm.integration.console.listeners;

import org.drools.WorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.kie.runtime.StatefulKnowledgeSession;

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
