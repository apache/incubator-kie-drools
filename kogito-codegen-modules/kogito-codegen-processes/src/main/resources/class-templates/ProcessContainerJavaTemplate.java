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

import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.services.jobs.impl.InMemoryProcessJobExecutorFactory;
import org.kie.kogito.services.jobs.impl.InMemoryJobService;
import org.kie.kogito.services.jobs.impl.InMemoryJobContext;
import org.kie.kogito.uow.UnitOfWorkManager;

public class Processes implements org.kie.kogito.process.Processes {

    private final Application application;
    private java.util.Map<String, org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> mappedProcesses = new java.util.concurrent.ConcurrentHashMap<>();

    public Processes(Application application) {
        this.application = application;
        JobsService jobsService = this.application.config().get(ProcessConfig.class).jobsService();
        UnitOfWorkManager unitOfWorkManager = this.application.config().get(ProcessConfig.class).unitOfWorkManager();
        if (jobsService instanceof InMemoryJobService) {
            InMemoryJobService inMemoryJobService = (InMemoryJobService) jobsService;
            InMemoryJobContext context = new InMemoryJobContext(null, unitOfWorkManager, this, null);
            inMemoryJobService.registerJobExecutorFactory(new InMemoryProcessJobExecutorFactory(context));
        }
    }

    public org.kie.kogito.process.Process<? extends org.kie.kogito.Model> processById(String processId) {
        if ("$ProcessId".equals(processId)) {
            return mappedProcesses.computeIfAbsent("$ProcessId", k -> new $ProcessClassName$(application).configure());
        }
        return null;
    }

    public java.util.Collection<String> processIds() {
        return java.util.Arrays.asList("$ProcessId$");
    }
}
