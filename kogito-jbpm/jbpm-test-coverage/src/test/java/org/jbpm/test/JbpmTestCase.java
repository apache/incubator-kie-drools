/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test;

import org.assertj.core.api.Assertions;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.client.BugzillaClient;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;

import java.util.Map;
import java.util.Properties;

public abstract class JbpmTestCase extends JbpmJUnitBaseTestCase {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected static final String EMPTY_CASE = "EmptyCase.bpmn2";
    
    public JbpmTestCase() {
        this(true);
    }

    public JbpmTestCase(boolean persistence) {
        this(persistence, persistence);
    }

    public JbpmTestCase(boolean setupDataSource, boolean sessionPersistence) {
        this(setupDataSource, sessionPersistence, "org.jbpm.test.persistence");
    }

    public JbpmTestCase(boolean setupDataSource, boolean sessionPersistence, String persistenceUnit) {
        super(setupDataSource, sessionPersistence, persistenceUnit);
    }

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            System.out.println(" >>> " + description.getMethodName() + " <<< ");
        }

        @Override
        protected void finished(Description description) {
            System.out.println();
        }

    };

    @Rule
    public IKeeperJUnitConnector issueKeeper = new IKeeperJUnitConnector(
            new BugzillaClient("https://bugzilla.redhat.com"),
            new JiraClient("https://issues.jboss.org")
    );

    @Override
    protected PoolingDataSource setupPoolingDataSource() {        
        
        Properties dsProps = PersistenceUtil.getDatasourceProperties();
        String jdbcUrl = dsProps.getProperty("url");
        String driverClass = dsProps.getProperty("driverClassName");

        // Setup the datasource
        PoolingDataSource ds1 = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds", false);
        if (driverClass.startsWith("org.h2")) {
            ds1.getDriverProperties().setProperty("url", jdbcUrl);
        }
        ds1.init();
        return ds1;
    }

    public KieSession createKSession(String... process) {
        createRuntimeManager(process);
        return getRuntimeEngine().getKieSession();
    }

    public KieSession createKSession(Map<String, ResourceType> res) {
        createRuntimeManager(res);
        return getRuntimeEngine().getKieSession();
    }

    public KieSession restoreKSession(String... process) {
        disposeRuntimeManager();
        createRuntimeManager(process);
        return getRuntimeEngine().getKieSession();
    }

    public void assertProcessInstanceNeverRun(long processId) {
        Assertions.assertThat(getLogService().findProcessInstance(processId)).as("Process has been running").isNull();
    }

    protected static KieServices getServices() {
        return KieServices.Factory.get();
    }

    protected static KieCommands getCommands() {
        return getServices().getCommands();
    }

}

