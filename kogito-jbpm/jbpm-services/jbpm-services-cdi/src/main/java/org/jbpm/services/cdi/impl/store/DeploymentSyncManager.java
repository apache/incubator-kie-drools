package org.jbpm.services.cdi.impl.store;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;

import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSyncInvoker;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.internal.runtime.cdi.BootOnLoad;

@Named("DeploymentSyncManager-startable")
@BootOnLoad
@ApplicationScoped
public class DeploymentSyncManager {
	
	@Inject
	private DeploymentService deploymentService;
	
	@Inject
	private TransactionalCommandService commandService;
	
	private DeploymentSyncInvoker invoker;
	private DeploymentSynchronizer synchronizer;

	@PostConstruct
	public void configureAndStart() {
		
    	try {
    		InitialContext.doLookup("java:module/DeploymentSynchronizerCDInvoker");
    	} catch (Exception e) {
    		
    		DeploymentStore store = new DeploymentStore();
    		store.setCommandService(commandService);
    		
    		synchronizer = new DeploymentSynchronizer();
    		synchronizer.setDeploymentService(deploymentService);
    		synchronizer.setDeploymentStore(store);
            
            invoker = new DeploymentSyncInvoker(synchronizer);
            invoker.start();
    	}
	}
	
	@PreDestroy
	public void cleanup() {
		if (invoker != null) {
			invoker.stop();
		}
	}
	

}
