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

import org.jbpm.persistence.scripts.util.TestsUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Contains tests that test DDL scripts.
 */
public class DDLScriptsTest {
    private static final Logger logger = LoggerFactory.getLogger(DDLScriptsTest.class);

    @BeforeClass
    public static void printHibernateVersion() {
        logger.info("Running with Hibernate " + org.hibernate.Version.getVersionString());
    }

    @Before
    public void clearSchema() {
        TestsUtil.clearSchema();
    }

    private static final String DB_DDL_SCRIPTS_RESOURCE_PATH = "/db/ddl-scripts";

    /**
     * Tests that DB schema is created properly using DDL scripts.
     */
    @Test
    public void createSchemaUsingDDLs() throws Exception {
        final TestPersistenceContext scriptRunnerContext = createAndInitPersistenceContext(PersistenceUnit.SCRIPT_RUNNER);
        try {
            scriptRunnerContext.executeScripts(new File(getClass().getResource(DB_DDL_SCRIPTS_RESOURCE_PATH).getFile()));
        } finally {
            scriptRunnerContext.clean();
        }

        final TestPersistenceContext dbTestingContext = createAndInitPersistenceContext(PersistenceUnit.DB_TESTING_VALIDATE);
        try {
            dbTestingContext.startAndPersistSomeProcess("minimalProcess");
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 1);
        } finally {
            dbTestingContext.clean();
        }
    }

    /**
     * Simulates the default config for kie-server/kie-wb when deploying the apps for the first time (and without running the DDL scripts first)
     */
    @Test
    public void runHibernateUpdateOnEmptyDB() throws Exception {
        final TestPersistenceContext dbTestingContext = createAndInitPersistenceContext(PersistenceUnit.DB_TESTING_UPDATE);
        dbTestingContext.clean();
    }

    /**
     * Simulates the case when user executes DDL scripts before deploying the kie-server/kie-wb and leaves the hibernate
     * config untouched (thus using the default 'update')
     */
    @Test
    public void createSchemaWithDDLsAndRunHibernateUpdate() throws Exception {
        final TestPersistenceContext scriptRunnerContext = createAndInitPersistenceContext(PersistenceUnit.SCRIPT_RUNNER);
        try {
            scriptRunnerContext.executeScripts(new File(getClass().getResource("/db/ddl-scripts").getFile()));
        } finally {
            scriptRunnerContext.clean();
        }
        final TestPersistenceContext dbTestingContext = createAndInitPersistenceContext(PersistenceUnit.DB_TESTING_UPDATE);
        dbTestingContext.clean();
    }

    private TestPersistenceContext createAndInitPersistenceContext(PersistenceUnit persistenceUnit) {
        TestPersistenceContext testPersistenceContext = new TestPersistenceContext();
        testPersistenceContext.init(persistenceUnit);
        return testPersistenceContext;
    }
}
