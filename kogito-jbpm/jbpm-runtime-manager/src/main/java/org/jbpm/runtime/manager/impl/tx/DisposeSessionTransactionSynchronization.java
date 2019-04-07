/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl.tx;

import org.drools.persistence.api.OrderedTransactionSynchronization;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transaction synchronization that disposes of the <code>KieSession</code> instance on transaction completion during
 * the <code>afterCompletion</code> phase.
 *
 */
public class DisposeSessionTransactionSynchronization extends OrderedTransactionSynchronization {
	private static final Logger logger = LoggerFactory.getLogger(DisposeSessionTransactionSynchronization.class);

	private RuntimeEngine runtime;
	private RuntimeManager manager;
	
	public DisposeSessionTransactionSynchronization(RuntimeManager manager, RuntimeEngine runtime) {
		super(10, "DestroySessionTransactionSynchronization"+runtime.toString());
		this.manager = manager;
	    this.runtime = runtime;
	}
	
	public void beforeCompletion() {
	    // clean the manager state to make sure it's done as sometimes afterCompletion is done in another thread
	    ((AbstractRuntimeManager) manager).softDispose(runtime);
	}

	public void afterCompletion(int status) {
	    try {
	    	((RuntimeEngineImpl) runtime).setAfterCompletion(true);
	        manager.disposeRuntimeEngine(runtime);
	    } catch (Throwable e) {
	        logger.debug("Could not dispose runtime engine", e);
	    }
	}

}
