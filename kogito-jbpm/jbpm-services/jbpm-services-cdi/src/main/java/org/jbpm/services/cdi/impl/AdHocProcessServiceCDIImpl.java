package org.jbpm.services.cdi.impl;

import org.jbpm.kie.services.impl.AdHocProcessServiceImpl;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AdHocProcessServiceCDIImpl extends AdHocProcessServiceImpl {

    @Inject
    @Override
    public void setDeploymentService(DeploymentService deploymentService) {
        super.setDeploymentService(deploymentService);
    }

    @Inject
    @Override
    public void setDataService(RuntimeDataService dataService) {
        super.setDataService(dataService);
    }
}
