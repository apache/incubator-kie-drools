package org.drools.persistence.processinstance;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.process.instance.event.SignalManager;
import org.drools.process.instance.event.SignalManagerFactory;

public class JPASignalManagerFactory implements SignalManagerFactory {

	public SignalManager createSignalManager(InternalKnowledgeRuntime kruntime) {
		return new JPASignalManager(kruntime);
	}

}
