package org.drools.vsm.remote;

import org.drools.task.service.HumanTaskServiceImpl;
import org.drools.vsm.GenericConnector;
import org.drools.vsm.HumanTaskService;
import org.drools.vsm.HumanTaskServiceProvider;

/**
 * 
 * @author Lucas Amador
 *
 */
public class HumanTaskServiceRemoteProviderImpl implements HumanTaskServiceProvider {

	private final ServiceManagerRemoteClient serviceManager;

	public HumanTaskServiceRemoteProviderImpl(ServiceManagerRemoteClient serviceManager) {
		this.serviceManager = serviceManager;
	}

	public HumanTaskService newHumanTaskServiceClient() {
		// TOOD: implemente a best way to identify the human task services of the other ones inexistents
		if (serviceManager.getServices()==null)
			return null;
		GenericConnector humanTaskServiceConnector = serviceManager.getServices().get(0);
		if (humanTaskServiceConnector==null)
			return null;
		return new HumanTaskServiceImpl( humanTaskServiceConnector,
										serviceManager.counter,
										serviceManager.getName(), 
										serviceManager.getSessionId());
	}

}
