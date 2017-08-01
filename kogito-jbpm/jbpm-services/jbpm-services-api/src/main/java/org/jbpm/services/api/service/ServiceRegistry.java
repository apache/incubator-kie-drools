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

package org.jbpm.services.api.service;

import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.admin.UserTaskAdminService;
import org.jbpm.services.api.query.QueryService;

/**
 * Single ServiceRegistry that allows any service to register itself 
 * so other components can easily look them up
 *
 */
public class ServiceRegistry {
    private static ServiceRegistry INSTANCE = new ServiceRegistry();

    protected volatile ConcurrentHashMap<String, Object> services = new ConcurrentHashMap<String, Object>();
    
    public static final String DEFINITION_SERVICE = DefinitionService.class.getSimpleName();
    public static final String DEPLOYMENT_SERVICE = DeploymentService.class.getSimpleName();
    public static final String PROCESS_SERVICE = ProcessService.class.getSimpleName();
    public static final String RUNTIME_DATA_SERVICE = RuntimeDataService.class.getSimpleName();
    public static final String USER_TASK_SERVICE = UserTaskService.class.getSimpleName();
    public static final String QUERY_SERVICE = QueryService.class.getSimpleName();
    
    public static final String PROCESS_ADMIN_SERVICE = ProcessInstanceAdminService.class.getSimpleName();
    public static final String MIGRATION_SERVICE = ProcessInstanceMigrationService.class.getSimpleName();
    public static final String USER_TASK_ADMIN_SERVICE = UserTaskAdminService.class.getSimpleName();
    
    public static final String CASE_SERVICE = "CaseService";
    public static final String CASE_RUNTIME_DATA_SERVICE = "CaseRuntimeDataService";

    private ServiceRegistry() {

    }

    /**
     * Returns single instance of the registry used to register and retrieve services.
     * @return instance of the registry
     */
    public static ServiceRegistry get() {
        return INSTANCE;
    }

    /**
     * Registers service under given name. 
     * @param name name of the service
     * @param service actual service instance
     */
    public synchronized void register(String name, Object service) {
        this.services.put(name, service);
    }

    /**
     * Removes service registered under given name
     * @param name name of the service
     */
    public synchronized void remove(String name) {
        this.services.remove(name);
    }
    
    /**
     * Retrieves service registered under given name
     * @param name name of the service
     * @return instance of the service registered with given name
     * @throws IllegalArgumentException thrown in case service with given name is not registered
     */
    public Object service(String name) {
        Object service = this.services.get(name);
        if (service == null) {
            throw new IllegalArgumentException("Service '" + name + "' not found");
        }
        return service; 
    }
    
    /**
     * Removes all services from the registry
     */
    public void clear() {
        this.services.clear();
    }
}
