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
package org.kie.kogito.integrationtests;

import org.kie.kogito.Application;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.Processes;

import io.quarkus.runtime.Startup;

import jakarta.inject.Inject;

@Startup
public class InjectProcesses {

    @Inject
    public InjectProcesses(Processes processes, Application application) {
        if (processes != application.get(Processes.class)) {
            throw new IllegalStateException("Processes should be injectable and same as instance application.get(Processes.class)");
        }
        if (application.config().get(ProcessConfig.class) == null) {
            throw new IllegalStateException("ProcessConfig not available");
        }
    }
}
