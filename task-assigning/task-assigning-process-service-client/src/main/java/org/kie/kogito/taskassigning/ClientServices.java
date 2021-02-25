/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.taskassigning;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClientFactory;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientFactory;

@ApplicationScoped
public class ClientServices {

    private ProcessServiceClientFactory processServiceClientFactory;
    private DataIndexServiceClientFactory indexClientFactory;

    public ClientServices() {
        //CDI proxying
    }

    @Inject
    public ClientServices(ProcessServiceClientFactory processServiceClientFactory,
            DataIndexServiceClientFactory indexClientFactory) {
        this.processServiceClientFactory = processServiceClientFactory;
        this.indexClientFactory = indexClientFactory;
    }

    public ProcessServiceClientFactory processServiceClientFactory() {
        return processServiceClientFactory;
    }

    public DataIndexServiceClientFactory dataIndexClientFactory() {
        return indexClientFactory;
    }
}
