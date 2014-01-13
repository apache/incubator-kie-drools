package org.jbpm.kie.services.impl.audit;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.api.RequestScopedBackupIdentityProvider;
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
import org.kie.api.runtime.manager.RuntimeEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServicesAwareAuditEventBuilder extends DefaultAuditEventBuilderImpl {

    private static final Logger logger = LoggerFactory.getLogger(ServicesAwareAuditEventBuilder.class);

    
    private IdentityProvider identityProvider;

    private BeanManager beanManager = null;
    
    private String deploymentUnitId;

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }
    
    @Override
    public AuditEvent buildEvent(ProcessStartedEvent pse) {
        
        ProcessInstanceLog log = (ProcessInstanceLog) super.buildEvent(pse);
        log.setIdentity(getIdentityName());
        log.setExternalId(deploymentUnitId);
        return log;
    }

    @Override
    public AuditEvent buildEvent(ProcessCompletedEvent pce, Object log) {
        ProcessInstanceLog instanceLog = (ProcessInstanceLog) super.buildEvent(pce, log); 
        instanceLog.setExternalId(deploymentUnitId);
        return instanceLog;
        
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte) {
        NodeInstanceLog nodeInstanceLog = (NodeInstanceLog)super.buildEvent(pnte); 
        nodeInstanceLog.setExternalId(deploymentUnitId);
        return nodeInstanceLog;
        
        
    }

    @Override
    public AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log) {
        NodeInstanceLog nodeInstanceLog = (NodeInstanceLog) super.buildEvent(pnle, log); 
        nodeInstanceLog.setExternalId(deploymentUnitId);
        return nodeInstanceLog;
    }

    @Override
    public AuditEvent buildEvent(ProcessVariableChangedEvent pvce) {
        VariableInstanceLog variableLog = (VariableInstanceLog)super.buildEvent(pvce); 
        variableLog.setExternalId(deploymentUnitId);
        return variableLog;
    }

    public String getDeploymentUnitId() {
        return deploymentUnitId;
    }

    public void setDeploymentUnitId(String deploymentUnitId) {
        this.deploymentUnitId = deploymentUnitId;
    }
    
    /**
     * This method returns the identity of the user who initiated the command.
     * @return The identity
     */
    protected String getIdentityName() {
        String name = "unknown";
        try {
            name = identityProvider.getName();
            logger.debug( "Used original identity provider with user: {}", name);
        } catch (ContextNotActiveException e) {
            RequestScopedBackupIdentityProvider provider = getBackupIdentityProvider();
            // if the beanManager field has NOT been set, then provider == null
            if( provider != null ) { 
                name = provider.getName();
                logger.debug( "Used debug identity provider with user: {}", name);
            }
        }

        return name;
    }
    
    /**
     * Sets the {@link BeanManager} field. 
     * </p>
     * This field is necessary in order to retrieve a {@link RequestScopedBackupIdentityProvider} bean from the CDI context. 
     * A {@link RequestScopedBackupIdentityProvider} bean is necessary when the a command is issued to the a {@link RuntimeEngine}
     * in a context or scope where HTTP is *not* used. The normal {@link IdentityProvider} bean is only available if HTTP is being 
     * used, because it relies on HTTP authorization mechanisms in order to get the user (See the UberfireIdentityProvider class).
     * 
     * @param beanManager A {@link BeanManager} instance
     */
    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }
    
    /**
     * This retrieves a {@link RequestScopedBackupIdentityProvider} bean from the CDI (request scoped) context.
     * @return a {@link RequestScopedBackupIdentityProvider} instance
     */
    protected RequestScopedBackupIdentityProvider getBackupIdentityProvider() {
        Class<?> type = RequestScopedBackupIdentityProvider.class;
        logger.debug("Retrieving {} bean", type.getSimpleName() );
        if( beanManager != null ) { 
            final Bean<?> bean = beanManager.resolve(beanManager.getBeans(type));
            if (bean == null) {
                return null;
            }
            CreationalContext<?> cc = beanManager.createCreationalContext(null);
            return (RequestScopedBackupIdentityProvider) beanManager.getReference(bean, type, cc);
        } else { 
            return null;
        }
    }

}
