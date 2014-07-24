package org.jbpm.kie.services.impl;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

public class IdentityProviderAwareProcessListener implements ProcessEventListener {
	
	private KieSession ksession;
	private IdentityProvider identityProvider;
	
	public IdentityProviderAwareProcessListener(KieSession ksession) {
		this.ksession = ksession;
	}
	
	private void resolveIdentityProvider() {
		if (identityProvider != null) {
			return;
		}
		Object identityProvider = ksession.getEnvironment().get("IdentityProvider");
		Environment env = ksession.getEnvironment();
		if (identityProvider instanceof IdentityProvider) {
			this.identityProvider = (IdentityProvider) identityProvider;
		}
	}

	public void beforeProcessStarted(ProcessStartedEvent event) {
		resolveIdentityProvider();
		if (identityProvider != null) {
			((ProcessInstanceImpl) event.getProcessInstance()).getMetaData().put("OwnerId", identityProvider.getName());
		}
	}

	public void afterProcessStarted(ProcessStartedEvent event) {
	}

	public void beforeProcessCompleted(ProcessCompletedEvent event) {
	}

	public void afterProcessCompleted(ProcessCompletedEvent event) {
	}

	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
	}

	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
	}

	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
	}

	public void afterNodeLeft(ProcessNodeLeftEvent event) {
	}

	public void beforeVariableChanged(ProcessVariableChangedEvent event) {
	}

	public void afterVariableChanged(ProcessVariableChangedEvent event) {
	}

}