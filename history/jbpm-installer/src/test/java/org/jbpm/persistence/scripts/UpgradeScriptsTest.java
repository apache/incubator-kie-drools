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

package org.jbpm.persistence.scripts;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.jbpm.persistence.scripts.util.TestsUtil;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Contains tests that test database upgrade scripts.
 */
public class UpgradeScriptsTest {

    private static final String TEST_PROCESS_ID = "minimalProcess";
    private static final Long TEST_PROCESS_INSTANCE_ID = 1L;
    private static final Integer TEST_SESSION_ID = 1;
    private static final String DB_UPGRADE_SCRIPTS_RESOURCE_PATH = "/db/upgrade-scripts";

    /**
     * Tests that DB schema is upgraded properly using database upgrade scripts.
     * @throws IOException
     */
    @Test
    public void testExecutingScripts() throws IOException, SQLException {
        testExecutingScripts("jbpm");
        testExecutingScripts("bpms");
    }
    
    public void testExecutingScripts(String type) throws IOException, SQLException {
        // Clear schema.
        TestsUtil.clearSchema();

        final TestPersistenceContext scriptRunnerContext = new TestPersistenceContext();
        scriptRunnerContext.init(PersistenceUnit.SCRIPT_RUNNER);
        try {
            // Prepare 6.0. schema
            scriptRunnerContext.executeScripts(new File(getClass().getResource("/ddl60").getFile()));
            // Execute upgrade scripts.
            scriptRunnerContext.executeScripts(new File(getClass().getResource(DB_UPGRADE_SCRIPTS_RESOURCE_PATH).getFile()), type);
        } finally {
            scriptRunnerContext.clean();
        }

        final TestPersistenceContext dbTestingContext = new TestPersistenceContext();
        dbTestingContext.init(PersistenceUnit.DB_TESTING_VALIDATE);
        try {
            dbTestingContext.startAndPersistSomeProcess(TEST_PROCESS_ID);
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 1);
        } finally {
            dbTestingContext.clean();
        }
    }

    /**
     * Tests that persisted process is not destroyed by upgrade scripts.
     * @throws IOException
     * @throws ParseException
     * @throws SQLException
     */
    @Test
    public void testPersistedProcess() throws IOException, ParseException, SQLException {
        testPersistedProcess("jbpm");
        testPersistedProcess("bpms");
    }
    
    public void testPersistedProcess(String type) throws IOException, ParseException, SQLException {
        // Clear schema.
        TestsUtil.clearSchema();

        // Prepare + upgrade schema.
        final TestPersistenceContext scriptRunnerContext = new TestPersistenceContext();
        scriptRunnerContext.init(PersistenceUnit.SCRIPT_RUNNER);
        try {
            // Prepare 6.0. schema
            scriptRunnerContext.executeScripts(new File(getClass().getResource("/ddl60").getFile()));
            scriptRunnerContext.persistOldProcessAndSession(TEST_SESSION_ID, TEST_PROCESS_ID, TEST_PROCESS_INSTANCE_ID);
            scriptRunnerContext.createSomeTask();
            // Execute upgrade scripts.
            scriptRunnerContext.executeScripts(new File(getClass().getResource(DB_UPGRADE_SCRIPTS_RESOURCE_PATH).getFile()), type);
        } finally {
            scriptRunnerContext.clean();
        }

        final TestPersistenceContext dbTestingContext = new TestPersistenceContext();
        dbTestingContext.init(PersistenceUnit.DB_TESTING_VALIDATE);
        try {
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 1);
            Assert.assertTrue(dbTestingContext.getStoredSessionsCount() == 1);

            final StatefulKnowledgeSession persistedSession = dbTestingContext.loadPersistedSession(
                    TEST_SESSION_ID.longValue(), TEST_PROCESS_ID);
            Assert.assertNotNull(persistedSession);

            // Start another process.
            persistedSession.startProcess(TEST_PROCESS_ID);
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 2);

            // Load old process instance.
            ProcessInstance processInstance = persistedSession.getProcessInstance(TEST_PROCESS_INSTANCE_ID);
            Assert.assertNotNull(processInstance);

            persistedSession.signalEvent("test", null);
            processInstance = persistedSession.getProcessInstance(TEST_PROCESS_INSTANCE_ID);
            Assert.assertNull(processInstance);
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 0);

            persistedSession.dispose();
            persistedSession.destroy();
            Assert.assertTrue(dbTestingContext.getStoredSessionsCount() == 0);
        } finally {
            dbTestingContext.clean();
        }
    }
}
