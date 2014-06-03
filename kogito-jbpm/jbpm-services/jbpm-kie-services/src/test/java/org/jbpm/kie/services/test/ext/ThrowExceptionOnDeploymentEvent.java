package org.jbpm.kie.services.test.ext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;

@ApplicationScoped
public class ThrowExceptionOnDeploymentEvent {

	public void checkAndThrow(@Observes @Deploy DeploymentEvent event) {
		if (event.getDeploymentId().endsWith("ksession-test")) {
			throw new IllegalStateException("Thrown on purpose to rollback deployment");
		}
	}
}
