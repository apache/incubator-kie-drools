/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.impl;

import org.jbpm.process.instance.AbstractProcessRuntimeServiceProvider;
import org.kie.kogito.process.ProcessConfig;
import org.kie.services.time.impl.JDKTimerService;

public class ConfiguredProcessServices extends AbstractProcessRuntimeServiceProvider {

    public ConfiguredProcessServices(ProcessConfig config) {
        super(new JDKTimerService(),
              config.workItemHandlers(),
              config.processEventListeners(),
              config.signalManagerHub(),
              config.unitOfWorkManager());

    }
}
