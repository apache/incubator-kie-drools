package org.drools.vsm.local;

import org.drools.vsm.HumanTaskService;
import org.drools.vsm.HumanTaskServiceProvider;

/**
 * 
 * @author Lucas Amador
 *
 */
public class HumanTaskServiceLocalProviderImpl implements HumanTaskServiceProvider {

	private final ServiceManagerLocalClient serviceManager;

	public HumanTaskServiceLocalProviderImpl(ServiceManagerLocalClient serviceManager) {
		this.serviceManager = serviceManager;
	}

	public HumanTaskService newHumanTaskServiceClient() {
		throw new UnsupportedOperationException("not implemented yet");
	}

}
