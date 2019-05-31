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

package org.jbpm.process.instance;

import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.services.signal.DefaultSignalManagerHub;
import org.kie.services.time.impl.JDKTimerService;

public class LightProcessRuntimeServiceProvider extends AbstractProcessRuntimeServiceProvider {

    public LightProcessRuntimeServiceProvider() {
        super(new JDKTimerService(),
              new DefaultWorkItemHandlerConfig(),
              new DefaultProcessEventListenerConfig(),
              new DefaultSignalManagerHub());
    }
}
