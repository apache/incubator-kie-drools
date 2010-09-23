package org.jbpm.persistence.processinstance;

import org.drools.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.event.SignalManager;
import org.jbpm.process.instance.event.SignalManagerFactory;

public class JPASignalManagerFactory implements SignalManagerFactory {

	public SignalManager createSignalManager(InternalKnowledgeRuntime kruntime) {
		return new JPASignalManager(kruntime);
	}

}
