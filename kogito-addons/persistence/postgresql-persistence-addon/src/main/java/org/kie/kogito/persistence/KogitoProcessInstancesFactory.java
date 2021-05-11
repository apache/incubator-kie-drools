/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.persistence;

import org.kie.kogito.persistence.postgresql.PostgreProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;

import io.vertx.pgclient.PgPool;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.persistence.KogitoProcessInstancesFactory</code>
 *
 */
public abstract class KogitoProcessInstancesFactory implements ProcessInstancesFactory {

    private final Long queryTimeout;
    private final PgPool client;
    private final boolean autoDDL;

    protected KogitoProcessInstancesFactory() {
        this(null, true, 10000L);
    }

    public KogitoProcessInstancesFactory(PgPool client, Boolean autoDDL, Long queryTimeout) {
        this.client = client;
        this.autoDDL = autoDDL;
        this.queryTimeout = queryTimeout;
    }

    public PgPool client() {
        return this.client;
    }

    @Override
    public PostgreProcessInstances createProcessInstances(Process<?> process) {
        return new PostgreProcessInstances(process, client(), autoDDL, queryTimeout);
    }
}
