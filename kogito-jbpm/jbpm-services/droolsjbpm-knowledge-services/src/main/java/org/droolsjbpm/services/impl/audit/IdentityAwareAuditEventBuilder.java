package org.droolsjbpm.services.impl.audit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.IdentityProvider;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.event.AuditEvent;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.kie.api.event.process.ProcessStartedEvent;

@ApplicationScoped
public class IdentityAwareAuditEventBuilder extends DefaultAuditEventBuilderImpl {

    @Inject
    private IdentityProvider identityProvider;

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        
        ProcessInstanceLog log = (ProcessInstanceLog) super.buildEvent(pse);
        log.setIdentity(identityProvider.getName());
        
        return log;
    }
    
    
}
