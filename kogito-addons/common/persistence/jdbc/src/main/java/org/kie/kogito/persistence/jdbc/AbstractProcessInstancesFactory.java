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
package org.kie.kogito.persistence.jdbc;

import javax.sql.DataSource;

import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.Processes;

public abstract class AbstractProcessInstancesFactory implements ProcessInstancesFactory {

    private final DataSource dataSource;
    private final Boolean lock;
    private final HeadersPersistentConfig headersConfig;
    private Processes processes;

    protected AbstractProcessInstancesFactory() {
        this(null, false);
    }

    public AbstractProcessInstancesFactory(DataSource dataSource, Boolean lock) {
        this(dataSource, lock, null);
    }

    public AbstractProcessInstancesFactory(DataSource dataSource, Boolean lock, HeadersPersistentConfig headersConfig) {
        this(dataSource, lock, headersConfig, null);
    }

    public AbstractProcessInstancesFactory(DataSource dataSource, Boolean lock, HeadersPersistentConfig headersConfig, Processes processes) {
        this.dataSource = dataSource;
        this.lock = lock;
        this.headersConfig = headersConfig;
        this.processes = processes;
    }

    public void setProcesses(Processes processes) {
        this.processes = processes;
    }

    @Override
    public JDBCProcessInstances createProcessInstances(Process<?> process) {
        return new JDBCProcessInstances(process, dataSource, lock, headersConfig, processes);
    }
}
