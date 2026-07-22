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
package org.kie.kogito.persistence.postgresql;

import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;

import io.vertx.pgclient.PgPool;

public abstract class AbstractProcessInstancesFactory implements ProcessInstancesFactory {

    private final Long queryTimeout;
    private final PgPool client;
    private final Boolean lock;
    private HeadersPersistentConfig headersConfig;

    // Constructor for DI
    protected AbstractProcessInstancesFactory() {
        this(null, 10000L, false);
    }

    public AbstractProcessInstancesFactory(PgPool client, Long queryTimeout, Boolean lock) {
        this(client, queryTimeout, lock, null);
    }

    public AbstractProcessInstancesFactory(PgPool client, Long queryTimeout, Boolean lock,
            HeadersPersistentConfig headersConfig) {
        this.client = client;
        this.queryTimeout = queryTimeout;
        this.lock = lock;
        this.headersConfig = headersConfig;
    }

    public PgPool client() {
        return this.client;
    }

    public boolean lock() {
        return lock;
    }

    @Override
    public PostgresqlProcessInstances createProcessInstances(Process<?> process) {
        return new PostgresqlProcessInstances(process, client(), queryTimeout, lock(), headersConfig);
    }
}
