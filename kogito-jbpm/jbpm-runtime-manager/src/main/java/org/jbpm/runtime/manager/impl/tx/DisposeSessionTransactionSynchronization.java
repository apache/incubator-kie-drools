/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl.tx;

import org.drools.persistence.OrderedTransactionSynchronization;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

/**
 * Transaction synchronization that disposed <code>KieSession</code> instance on transaction completion during
 * afterCompletion phase.
 *
 */
public class DisposeSessionTransactionSynchronization extends OrderedTransactionSynchronization {

	private RuntimeEngine runtime;
	private RuntimeManager manager;
	
	public DisposeSessionTransactionSynchronization(RuntimeManager manager, RuntimeEngine runtime) {
		super(10);
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
