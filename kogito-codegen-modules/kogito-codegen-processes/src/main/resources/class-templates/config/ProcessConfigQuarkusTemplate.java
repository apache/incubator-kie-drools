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
package $Package$;

import jakarta.enterprise.inject.Instance;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.ProcessVersionResolver;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.events.UnitOfWorkEventListener;

@jakarta.inject.Singleton
public class ProcessConfig extends org.kie.kogito.process.impl.AbstractProcessConfig {

    @jakarta.inject.Inject
    public ProcessConfig(
            Instance<WorkItemHandlerConfig> workItemHandlerConfig,
            Instance<UnitOfWorkManager> unitOfWorkManager,
            Instance<JobsService> jobsService,
            Instance<ProcessEventListenerConfig> processEventListenerConfigs,
            Instance<ProcessEventListener> processEventListeners,
            Instance<EventPublisher> eventPublishers,
            org.kie.kogito.config.ConfigBean configBean,
            Instance<UnitOfWorkEventListener> unitOfWorkEventListeners,
            Instance<ProcessVersionResolver> versionResolver,
            Instance<IdentityProvider> identityProvider) {

        super(workItemHandlerConfig,
                processEventListenerConfigs,
                processEventListeners,
                unitOfWorkManager,
                jobsService,
                eventPublishers,
                configBean.getServiceUrl(),
                unitOfWorkEventListeners,
                versionResolver,
                identityProvider);
    }

}
