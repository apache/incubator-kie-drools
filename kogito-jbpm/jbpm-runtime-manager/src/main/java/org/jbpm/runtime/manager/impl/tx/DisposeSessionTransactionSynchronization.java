package org.jbpm.runtime.manager.impl.tx;

import org.drools.persistence.TransactionSynchronization;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeManager;

public class DisposeSessionTransactionSynchronization implements TransactionSynchronization {

	private RuntimeEngine runtime;
	private RuntimeManager manager;
	
	public DisposeSessionTransactionSynchronization(RuntimeManager manager, RuntimeEngine runtime) {
		this.manager = manager;
	    this.runtime = runtime;
	}
	
	public void beforeCompletion() {
	}

	public void afterCompletion(int status) {
	    try {
	        manager.disposeRuntimeEngine(runtime);
	    } catch (Throwable e) {
	        // catch exception as it's only clean up and should not affect runtime
	    }
	}

}
