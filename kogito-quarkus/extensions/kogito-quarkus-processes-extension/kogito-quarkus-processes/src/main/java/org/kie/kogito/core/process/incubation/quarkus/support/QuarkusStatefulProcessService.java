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
import org.kie.kogito.incubation.processes.services.StatefulProcessService;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.ProcessServiceImpl;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusStatefulProcessService implements StatefulProcessService {
    @Inject
    Instance<Processes> processesInstance;
    @Inject
    Application application;
    StatefulProcessServiceImpl delegate;

    @PostConstruct
    void startup() {
        this.delegate = new StatefulProcessServiceImpl(new ProcessServiceImpl(application), processesInstance.get());
    }

    @Override
    public ExtendedDataContext create(LocalId processId, DataContext inputContext) {
        return delegate.create(processId, inputContext);
    }

    @Override
    public ExtendedDataContext signal(LocalId processId, DataContext dataContext) {
        return delegate.signal(processId, dataContext);
    }

    @Override
    public ExtendedDataContext update(LocalId processId, DataContext dataContext) {
        return delegate.update(processId, dataContext);
    }

    @Override
    public ExtendedDataContext abort(LocalId processId) {
        return delegate.abort(processId);
    }

    @Override
    public ExtendedDataContext get(LocalId processId) {
        return delegate.get(processId);
    }
}
