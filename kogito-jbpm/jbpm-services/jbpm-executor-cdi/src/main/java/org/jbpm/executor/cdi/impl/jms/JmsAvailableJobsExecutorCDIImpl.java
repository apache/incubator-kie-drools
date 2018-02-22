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

package org.jbpm.executor.cdi.impl.jms;

import javax.inject.Inject;

import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.executor.impl.jms.JmsAvailableJobsExecutor;
import org.kie.api.executor.Executor;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorStoreService;


public class JmsAvailableJobsExecutorCDIImpl extends JmsAvailableJobsExecutor {
    @Inject
    @Override
    public void setQueryService(ExecutorQueryService queryService) {
        super.setQueryService(queryService);
    }    
    
    @Inject
    @Override
    public void setClassCacheManager(ClassCacheManager classCacheManager) {
        super.setClassCacheManager(classCacheManager);
    }

    @Inject
    @Override
    public void setExecutorStoreService(ExecutorStoreService executorStoreService) {
        super.setExecutorStoreService(executorStoreService);
    }

    @Inject
    @Override
    public void setEventSupport(ExecutorEventSupport eventSupport) {
        super.setEventSupport(eventSupport);
    }

    @Inject
    @Override
    public void setExecutor(Executor executor) {
        super.setExecutor(executor);
    }
}
