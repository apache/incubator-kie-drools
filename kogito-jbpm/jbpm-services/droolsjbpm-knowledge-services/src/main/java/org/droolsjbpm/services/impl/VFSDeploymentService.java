package org.droolsjbpm.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.droolsjbpm.services.api.DeployedUnit;
import org.droolsjbpm.services.api.DeploymentService;
import org.droolsjbpm.services.api.DeploymentUnit;
import org.droolsjbpm.services.api.IdentityProvider;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.droolsjbpm.services.impl.event.Deploy;
import org.droolsjbpm.services.impl.event.DeploymentEvent;
import org.droolsjbpm.services.impl.event.Undeploy;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.io.ResourceType;
import org.kie.commons.java.nio.file.Path;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

@ApplicationScoped
public class VFSDeploymentService implements DeploymentService {

    @Inject
    private BeanManager beanManager;
    @Inject
    private JbpmServicesPersistenceManager pm;
    @Inject
    private FileService fs;
    @Inject
    private RuntimeManagerFactory managerFactory; 
    @Inject
    private EntityManagerFactory emf;
    @Inject
    private IdentityProvider identityProvider; 
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    @Deploy
    private Event<DeploymentEvent> deploymentEvent;
    @Inject
    @Undeploy
    private Event<DeploymentEvent> undeploymentEvent;
    
    private Map<String, DeployedUnit> deploymentsMap = new ConcurrentHashMap<String, DeployedUnit>();

    @Override
    public void deploy(DeploymentUnit unit) {
        if (!(unit instanceof VFSDeploymentUnit)) {
            throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName());
        }
        if (deploymentsMap.containsKey(unit.getIdentifier())) {
            throw new IllegalStateException("Unit with id " + unit.getIdentifier() + " is already deployed");
        }
        DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
        VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
        // Create Runtime Manager Based on the Reference
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf);
        
        AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance(emf);
        ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        auditEventBuilder.setDeploymentUnitId(vfsUnit.getIdentifier());
        auditLogger.setBuilder(auditEventBuilder);
        if (beanManager != null) {
            builder.registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, auditLogger));
        }
        loadProcesses(vfsUnit, builder, deployedUnit);
        loadRules(vfsUnit, builder, deployedUnit);
          
        synchronized (this) {
        
            if (deploymentsMap.containsKey(vfsUnit.getIdentifier())) {
                DeployedUnit deployed = deploymentsMap.remove(vfsUnit.getIdentifier());
                RuntimeManager manager = deployed.getRuntimeManager();
                manager.close();
            }
            RuntimeManager manager = null;
            deploymentsMap.put(vfsUnit.getIdentifier(), deployedUnit);
            try {
                switch (vfsUnit.getStrategy()) {
            
                    case SINGLETON:
                        manager = managerFactory.newSingletonRuntimeManager(builder.get(), vfsUnit.getIdentifier());
                        break;
                    case PER_REQUEST:
                        manager = managerFactory.newPerRequestRuntimeManager(builder.get(), vfsUnit.getIdentifier());
                        break;
                        
                    case PER_PROCESS_INSTANCE:
                        manager = managerFactory.newPerProcessInstanceRuntimeManager(builder.get(), vfsUnit.getIdentifier());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid strategy " + vfsUnit.getStrategy());
                }            
                deployedUnit.setRuntimeManager(manager);
            } catch (Exception e) {
                deploymentsMap.remove(vfsUnit.getIdentifier());
                throw new RuntimeException(e);
            }
        }
        if (deploymentEvent != null) {
            deploymentEvent.fire(new DeploymentEvent(unit.getIdentifier()));
        }
        
    }

    @Override
    public void undeploy(DeploymentUnit unit) {
        synchronized (this) {
            DeployedUnit deployed = deploymentsMap.remove(unit.getIdentifier());
            if (deployed != null) {
                RuntimeManager manager = deployed.getRuntimeManager();
                manager.close();
            }
            if (undeploymentEvent != null) {
                undeploymentEvent.fire(new DeploymentEvent(unit.getIdentifier()));
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
    
    protected void loadProcesses(VFSDeploymentUnit vfsUnit, RuntimeEnvironmentBuilder builder, DeployedUnitImpl deployedUnit) {
        Iterable<Path> loadProcessFiles = null;

        try {
            Path processFolder = fs.getPath(vfsUnit.getRepository() + vfsUnit.getRepositoryFolder());
            loadProcessFiles = fs.loadFilesByType(processFolder, ".+bpmn[2]?$");
        } catch (FileException ex) {
            Logger.getLogger(VFSDeploymentService.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadProcessFiles) {
            String processString = "";
            try {
                processString = new String(fs.loadFile(p));
                builder.addAsset(ResourceFactory.newByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
                ProcessDesc process = bpmn2Service.findProcessId(processString);
                process.setOriginalPath(p.toUri().toString());
                process.setDeploymentId(vfsUnit.getIdentifier());
                deployedUnit.addAssetLocation(process.getId(), process);
                
            } catch (Exception ex) {
                Logger.getLogger(VFSDeploymentService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    protected void loadRules(VFSDeploymentUnit vfsUnit, RuntimeEnvironmentBuilder builder, DeployedUnitImpl deployedUnit) {
        Iterable<Path> loadRuleFiles = null;

        try {
            Path rulesFolder = fs.getPath(vfsUnit.getRepository() + vfsUnit.getRepositoryFolder());
            loadRuleFiles = fs.loadFilesByType(rulesFolder, ".+drl");
        } catch (FileException ex) {
            Logger.getLogger(VFSDeploymentService.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Path p : loadRuleFiles) {
            String ruleString = "";
            try {
                ruleString = new String(fs.loadFile(p));
                builder.addAsset(ResourceFactory.newByteArrayResource(ruleString.getBytes()), ResourceType.DRL);                
                
            } catch (Exception ex) {
                Logger.getLogger(VFSDeploymentService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    


    public JbpmServicesPersistenceManager getPm() {
        return pm;
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public FileService getFs() {
        return fs;
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }

    public RuntimeManagerFactory getManagerFactory() {
        return managerFactory;
    }

    public void setManagerFactory(RuntimeManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public BPMN2DataService getBpmn2Service() {
        return bpmn2Service;
    }

    public void setBpmn2Service(BPMN2DataService bpmn2Service) {
        this.bpmn2Service = bpmn2Service;
    }

    public Map<String, DeployedUnit> getDeploymentsMap() {
        return deploymentsMap;
    }

    @Override
    public Collection<DeployedUnit> getDeployedUnits() {
        
        return Collections.unmodifiableCollection(deploymentsMap.values()) ;
    }
}
