package org.jbpm.services.api;

import java.util.Collection;

public interface ListenerSupport {

	void addListener(DeploymentEventListener listener);

    void removeListener(DeploymentEventListener listener);
   
    Collection<DeploymentEventListener> getListeners();
}
