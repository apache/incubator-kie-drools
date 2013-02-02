package org.drools;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

@Ignore
public class BuildMojoTest extends AbstractMojoTestCase {

    @Before
    protected void setUp() throws Exception {
        super.setUp();
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSomething()
            throws Exception {
        File pom = getTestFile("src/test/resources/unit/project-to-test/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        BuildMojo myMojo = (BuildMojo) lookupMojo("touch", pom);
        assertNotNull(myMojo);
        myMojo.execute();
    }
}
    
