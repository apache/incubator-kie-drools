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
package org.kie.kogito.core.process.incubation.quarkus.support;

import org.kie.kogito.Application;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.ExtendedDataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.MetaDataContext;
import org.kie.kogito.incubation.processes.services.humantask.HumanTaskService;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.ProcessServiceImpl;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusHumanTaskService implements HumanTaskService {
    @Inject
    Instance<Processes> processesInstance;
    @Inject
    Application application;
    HumanTaskServiceImpl delegate;

    @PostConstruct
    void startup() {
        this.delegate = new HumanTaskServiceImpl(application, new ProcessServiceImpl(application), processesInstance.get());
    }

    @Override
    public ExtendedDataContext get(LocalId id, MetaDataContext meta) {
        return delegate.get(id, meta);
    }

    @Override
    public ExtendedDataContext create(LocalId taskId) {
        return delegate.create(taskId);
    }

    @Override
    public ExtendedDataContext abort(LocalId taskId, MetaDataContext meta) {
        return delegate.abort(taskId, meta);
    }

    @Override
    public ExtendedDataContext complete(LocalId processId, DataContext dataContext) {
        return delegate.complete(processId, dataContext);
    }

    @Override
    public ExtendedDataContext update(LocalId processId, DataContext dataContext) {
        return null;
    }

    @Override
    public ExtendedDataContext transition(LocalId taskId, DataContext dataContext) {
        return delegate.transition(taskId, dataContext);
    }
}
