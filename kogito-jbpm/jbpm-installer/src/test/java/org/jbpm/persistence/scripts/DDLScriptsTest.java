package org.jbpm.persistence.scripts;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.jbpm.persistence.scripts.util.TestsUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Contains tests that test DDL scripts.
 */
public class DDLScriptsTest {

    /**
     * Tests that DB schema is created properly using DDL scripts.
     * @throws IOException
     */
    @Test
    public void testCreateSchema() throws IOException, SQLException {
        // Clear schema.
        TestsUtil.clearSchema();

        final TestPersistenceContext scriptRunnerContext = new TestPersistenceContext();
        scriptRunnerContext.init(PersistenceUnit.SCRIPT_RUNNER);
        try {
            scriptRunnerContext.executeScripts(new File(getClass().getResource("/ddl-scripts").getFile()));
        } finally {
            scriptRunnerContext.clean();
        }

        final TestPersistenceContext dbTestingContext = new TestPersistenceContext();
        dbTestingContext.init(PersistenceUnit.DB_TESTING);
        try {
            dbTestingContext.startAndPersistSomeProcess("minimalProcess");
            Assert.assertTrue(dbTestingContext.getStoredProcessesCount() == 1);
        } finally {
            dbTestingContext.clean();
        }
    }
}
