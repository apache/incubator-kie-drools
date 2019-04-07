/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.api.utils;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.admin.ProcessInstanceAdminService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.query.QueryService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;

public interface KieServiceConfigurator {
    
    void configureServices(String puName, IdentityProvider identityProvider, UserGroupCallback userGroupCallback);
    
    DeploymentUnit createDeploymentUnit(String groupId, String artifactid, String version);
    
    void close();

    DeploymentService getDeploymentService();

    DefinitionService getBpmn2Service();

    RuntimeDataService getRuntimeDataService();

    ProcessService getProcessService();

    UserTaskService getUserTaskService();

    QueryService getQueryService();
    
    ProcessInstanceAdminService getProcessAdminService();

    IdentityProvider getIdentityProvider();

    UserGroupCallback getUserGroupCallback();    

}