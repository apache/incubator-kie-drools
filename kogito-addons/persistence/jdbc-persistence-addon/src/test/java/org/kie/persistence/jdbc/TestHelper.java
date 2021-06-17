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

package org.kie.persistence.jdbc;

import javax.sql.DataSource;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.persistence.jdbc.JDBCProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.spy;

@Testcontainers
public class TestHelper {

    @Container
    final static KogitoPostgreSqlContainer container = new KogitoPostgreSqlContainer();
    private static DataSource ds;

    public static SecurityPolicy securityPolicy = SecurityPolicy.of(IdentityProviders.of("john"));

    public static final String TEST_ID = "02ac3854-46ee-42b7-8b63-5186c9889d96";

    private boolean enableLock;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        container.start();
        ds = getDataSource(container);
    }

    @AfterAll
    public static void close() {
        container.stop();
    }

    public static BpmnProcess createProcess(ProcessConfig config, String fileName, boolean lock) {

        BpmnProcess process = BpmnProcess.from(config, new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(getFactory(lock));
        process.configure();
        process.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(p -> p.abort());
        return process;
    }

    private static JDBCProcessInstancesFactory getFactory(boolean lock) {
        TestHelper t = new TestHelper();
        t.setEnableLock(lock);
        return t.new JDBCProcessInstancesFactory(ds);
    }

    private static DataSource getDataSource(final PostgreSQLContainer postgreSQLContainer) {

        PGSimpleDataSource ds = new PGSimpleDataSource();

        // DataSource initialization
        ds.setUrl(postgreSQLContainer.getJdbcUrl());
        ds.setUser(postgreSQLContainer.getUsername());
        ds.setPassword(postgreSQLContainer.getPassword());
        return ds;
    }

    public static BpmnProcess configure(boolean lock) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        TestHelper t = new TestHelper();
        t.setEnableLock(lock);
        process.setProcessInstancesFactory(t.new JDBCProcessInstancesFactory(null));
        process.configure();
        return process;
    }

    public void setEnableLock(boolean enableLock) {
        this.enableLock = enableLock;
    }

    private class JDBCProcessInstancesFactory extends KogitoProcessInstancesFactory {

        public JDBCProcessInstancesFactory(DataSource dataSource) {
            super(dataSource, true);
        }

        @Override
        public JDBCProcessInstances createProcessInstances(Process<?> process) {
            JDBCProcessInstances instances = spy(super.createProcessInstances(process));
            return instances;
        }

        @Override
        public boolean lock() {
            return enableLock;
        }
    }
}
