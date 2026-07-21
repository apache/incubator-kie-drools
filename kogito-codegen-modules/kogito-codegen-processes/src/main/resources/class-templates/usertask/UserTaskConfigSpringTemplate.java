
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.util.List;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.ProcessVersionResolver;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.events.UnitOfWorkEventListener;
import org.kie.kogito.usertask.impl.DefaultUserTaskConfig;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycles;
import org.kie.kogito.usertask.UserTaskAssignmentStrategyConfig;
import org.kie.kogito.usertask.UserTaskEventListenerConfig;
import org.kie.kogito.usertask.UserTaskInstances;

@org.springframework.stereotype.Component
public class UserTaskConfig extends DefaultUserTaskConfig {

    @org.springframework.beans.factory.annotation.Autowired
    public UserTaskConfig(
            List<UserTaskEventListenerConfig> workItemHandlerConfig,
            List<UnitOfWorkManager> unitOfWorkManager,
            List<JobsService> jobsService,
            List<IdentityProvider> identityProvider,
            List<UserTaskLifeCycles> userTaskLifeCycles,
            List<UserTaskAssignmentStrategyConfig> userTaskAssignmentStrategyConfigs,
            List<UserTaskInstances> userTaskInstances) {

        super(workItemHandlerConfig,
                unitOfWorkManager,
                jobsService,
                identityProvider,
                userTaskLifeCycles,
                userTaskAssignmentStrategyConfigs,
                userTaskInstances);
    }

}