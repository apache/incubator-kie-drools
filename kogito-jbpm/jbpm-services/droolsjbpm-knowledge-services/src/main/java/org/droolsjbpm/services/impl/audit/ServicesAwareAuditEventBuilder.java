package org.droolsjbpm.services.impl.audit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.IdentityProvider;
import org.droolsjbpm.services.domain.entities.Domain;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.event.AuditEvent;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

@ApplicationScoped
public class ServicesAwareAuditEventBuilder extends DefaultAuditEventBuilderImpl {

    @Inject
    private IdentityProvider identityProvider;
    
    private Domain domain;

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
    
    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        
        ProcessInstanceLog log = (ProcessInstanceLog) super.buildEvent(pse);
        log.setIdentity(identityProvider.getName());
        log.setDomainId(domain.getName());
        return log;
    }

    @Override
    public AuditEvent buildEvent(ProcessCompletedEvent pce, Object log) {
        ProcessInstanceLog instanceLog = (ProcessInstanceLog) super.buildEvent(pce, log); 
        instanceLog.setIdentity(identityProvider.getName());
        instanceLog.setDomainId(domain.getName());
        return instanceLog;
        
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte) {
        NodeInstanceLog nodeInstanceLog = (NodeInstanceLog)super.buildEvent(pnte); 
        nodeInstanceLog.setDomainId(domain.getName());
        return nodeInstanceLog;
        
        
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log) {
        NodeInstanceLog nodeInstanceLog = (NodeInstanceLog) super.buildEvent(pnle, log); 
        nodeInstanceLog.setDomainId(domain.getName());
        return nodeInstanceLog;
    }

    @Override
    public AuditEvent buildEvent(ProcessVariableChangedEvent pvce) {
        VariableInstanceLog variableLog = (VariableInstanceLog)super.buildEvent(pvce); 
        variableLog.setDomainId(domain.getName());
        return variableLog;
    }
    
    
    
    
}
