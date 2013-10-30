package org.jbpm.kie.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDeploymentService implements DeploymentService {
    
    private static Logger logger = LoggerFactory.getLogger(AbstractDeploymentService.class);

    @Inject
    private RuntimeManagerFactory managerFactory; 
    @Inject
    private RuntimeDataService runtimeDataService;
    @Inject
    private EntityManagerFactory emf;
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
            } catch (Exception e) {
                deploymentsMap.remove(unit.getIdentifier());
                throw new RuntimeException(e);
            }
        }
        if (deploymentEvent != null) {
            deploymentEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
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

    /**
     * Provides  AuditLogger implementation, JPA or JMS.
     * JPA is the default one and JMS requires to have configuration file (.properties)
     * to be available on classpath under 'jbpm.audit.jms.properties' name.
     * This file must have following properties defined:
     * <ul>
     *  <li>jbpm.audit.jms.connection.factory.jndi - JNDI name of the connection factory to look up - type String</li>
     *  <li>jbpm.audit.jms.queue.jndi - JNDI name of the queue to look up - type String</li>
     * </ul> 
     * @return instance of the audit logger
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected AbstractAuditLogger getAuditLogger() {
        
        if ("true".equals(System.getProperty("jbpm.audit.jms.enabled"))) {
            try {
                Properties properties = new Properties();
                properties.load(this.getClass().getResourceAsStream("/jbpm.audit.jms.properties"));
                
                return AuditLoggerFactory.newJMSInstance((Map)properties);
            } catch (IOException e) {
                logger.error("Unable to load jms audit properties from {}", "/jbpm.audit.jms.properties", e);
            }
        } 
        
        return AuditLoggerFactory.newJPAInstance(getEmf());
    }
}
