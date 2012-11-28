package org.jbpm.session;

import org.drools.persistence.TransactionSynchronization;
import org.kie.runtime.StatefulKnowledgeSession;

/**
 * M
 */
public class DisposeSessionTransactionSynchronization implements TransactionSynchronization {

	private StatefulKnowledgeSession ksession;
	
	public DisposeSessionTransactionSynchronization(StatefulKnowledgeSession ksession) {
		this.ksession = ksession;
	}
	
	public void beforeCompletion() {
	}

	public void afterCompletion(int status) {
		ksession.dispose();
	}

}
