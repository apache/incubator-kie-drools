/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.api.runtime.manager;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.task.TaskService;

/**
 * RuntimeEngine is the main entry point to interact with the process engine and task
 * service. It's responsibility is to ensure that process engine and task service
 * are properly configured and know about each other which eliminate the need to 
 * manually setup the integration between these two.<br>
 * RuntimeEngines are always produced by <code>RuntimeManager</code> and thus shall never be
 * created manually. <code>RuntimeManager</code> provides all required information to build
 * and bootstrap the <code>RuntimeEngine</code> so it is configured and ready to be used
 * regardless of when it is invoked.
 *
 */
public interface RuntimeEngine {

	/**
	 * Returns <code>KieSession</code> configured for this <code>RuntimeEngine</code>
	 * @return
	 */
    KieSession getKieSession();
    
    /**
	 * Returns <code>TaskService</code> configured for this <code>RuntimeEngine</code>
	 * @return
	 */
    TaskService getTaskService();
    
    /**
     * Returns <code>AuditService</code> that gives access to underlying runtime data such
     * as process instance, node instance and variable log entries.
     * @return
     */
    AuditService getAuditService();
}
