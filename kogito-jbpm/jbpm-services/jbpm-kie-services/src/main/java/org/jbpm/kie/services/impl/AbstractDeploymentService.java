package org.jbpm.kie.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.kie.services.impl.security.IdentityRolesSecurityManager;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

public abstract class AbstractDeploymentService implements DeploymentService {
    
    @Inject
    private BeanManager beanManager; 
    @Inject
    private RuntimeManagerFactory managerFactory; 
    @Inject
    private RuntimeDataService runtimeDataService;
    @Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;
    @Inject
    private IdentityProvider identityProvider;
    @Inject
    @Deploy
    protected Event<DeploymentEvent> deploymentEvent;
    @Inject
    @Undeploy
    protected Event<DeploymentEvent> undeploymentEvent;
    
    protected Map<String, DeployedUnit> deploymentsMap = new ConcurrentHashMap<String, DeployedUnit>();
    
    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    @Override
    public void deploy(DeploymentUnit unit) {
        if (deploymentsMap.containsKey(unit.getIdentifier())) {
            throw new IllegalStateException("Unit with id " + unit.getIdentifier() + " is already deployed");
        }
    }
    
    public void commonDeploy(DeploymentUnit unit, DeployedUnitImpl deployedUnit, RuntimeEnvironment environemnt) {

        synchronized (this) {
        
            if (deploymentsMap.containsKey(unit.getIdentifier())) {
                DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
                RuntimeManager manager = deployed.getRuntimeManager();
                manager.close();
            }
            RuntimeManager manager = null;
            deploymentsMap.put(unit.getIdentifier(), deployedUnit);
            try {
                switch (unit.getStrategy()) {
            
                    case SINGLETON:
                        manager = managerFactory.newSingletonRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                    case PER_REQUEST:
                        manager = managerFactory.newPerRequestRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                        
                    case PER_PROCESS_INSTANCE:
                        manager = managerFactory.newPerProcessInstanceRuntimeManager(environemnt, unit.getIdentifier());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid strategy " + unit.getStrategy());
                }            
                deployedUnit.setRuntimeManager(manager);
                DeploymentDescriptor descriptor = ((InternalRuntimeManager)manager).getDeploymentDescriptor();
                if (descriptor.getRequiredRoles() != null && !descriptor.getRequiredRoles().isEmpty()) {
                	((InternalRuntimeManager)manager).setSecurityManager(new IdentityRolesSecurityManager(identityProvider, descriptor.getRequiredRoles()));
                }
                
                if (deploymentEvent != null) {
                    deploymentEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
                }
            } catch (Exception e) {
                deploymentsMap.remove(unit.getIdentifier());
                manager.close();
                if (undeploymentEvent != null && deployedUnit != null) {
                    undeploymentEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
                }
                throw new RuntimeException(e);
            }
        }

        
    }
    
    @Override
    public void undeploy(DeploymentUnit unit) {
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ACTIVE);
        states.add(ProcessInstance.STATE_PENDING);
        states.add(ProcessInstance.STATE_SUSPENDED);
        Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstancesByDeploymentId(unit.getIdentifier(), states);
        if (!activeProcesses.isEmpty()) {
            throw new IllegalStateException("Undeploy forbidden - there are active processes instances for deployment " 
                                            + unit.getIdentifier());
        }
        synchronized (this) {
            DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
            if (deployed != null) {
                RuntimeManager manager = deployed.getRuntimeManager();
                ((AbstractRuntimeManager)manager).close(true);
            }
            if (undeploymentEvent != null) {
                undeploymentEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployed));
            }
        }
    }

    @Override
    public RuntimeManager getRuntimeManager(String deploymentUnitId) {
        if (deploymentsMap.containsKey(deploymentUnitId)) {
            return deploymentsMap.get(deploymentUnitId).getRuntimeManager();
        }
        
        return null;
    }

    @Override
    public DeployedUnit getDeployedUnit(String deploymentUnitId) {
        if (deploymentsMap.containsKey(deploymentUnitId)) {
            return deploymentsMap.get(deploymentUnitId);
        }
        
        return null;
    }
    
    public Map<String, DeployedUnit> getDeploymentsMap() {
        return deploymentsMap;
    }

    @Override
    public Collection<DeployedUnit> getDeployedUnits() {
        
        return Collections.unmodifiableCollection(deploymentsMap.values()) ;
    }


    public RuntimeManagerFactory getManagerFactory() {
        return managerFactory;
    }

    public void setManagerFactory(RuntimeManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    public RuntimeDataService getRuntimeDataService() {
        return runtimeDataService;
    }

    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }
    
    protected AuditEventBuilder setupAuditLogger(IdentityProvider identityProvider, String deploymentUnitId) { 
       
        ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        auditEventBuilder.setDeploymentUnitId(deploymentUnitId);
        auditEventBuilder.setBeanManager(beanManager);        
        
        return auditEventBuilder;
    }
}
