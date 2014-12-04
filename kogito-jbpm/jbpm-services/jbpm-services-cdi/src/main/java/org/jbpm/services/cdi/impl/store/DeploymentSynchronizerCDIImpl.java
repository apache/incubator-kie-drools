package org.jbpm.services.cdi.impl.store;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.store.DeploymentStore;
import org.jbpm.kie.services.impl.store.DeploymentSynchronizer;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Activate;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.Undeploy;
import org.jbpm.shared.services.impl.TransactionalCommandService;

@ApplicationScoped
public class DeploymentSynchronizerCDIImpl extends DeploymentSynchronizer {

	@Inject
	private TransactionalCommandService commandService;
	
	@PostConstruct
	public void configure() {
		DeploymentStore store = new DeploymentStore();
		store.setCommandService(commandService);
		
		setDeploymentStore(store);
	}
	
	@Inject
	@Override
	public void setDeploymentService(DeploymentService deploymentService) {
		super.setDeploymentService(deploymentService);
	}

	public void onDeploy(@Observes@Deploy DeploymentEvent event) {
		super.onDeploy(event);
    }
    
    public void onUnDeploy(@Observes@Undeploy DeploymentEvent event) {    	
    	super.onUnDeploy(event);
    }
    
    @Override
	public void onActivate(@Observes@Activate DeploymentEvent event) {
		super.onActivate(event);
	}

	@Override
	public void onDeactivate(@Observes@Deactivate DeploymentEvent event) {
		super.onDeactivate(event);
	}
	
}
