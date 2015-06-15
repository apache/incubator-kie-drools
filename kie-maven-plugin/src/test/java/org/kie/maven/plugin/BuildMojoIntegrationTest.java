package org.kie.maven.plugin;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Integration test for kjar
 */
@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({"3.2.3"})
public class BuildMojoIntegrationTest {

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime mavenRuntime;

    public BuildMojoIntegrationTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
        this.mavenRuntime = builder.withCliOptions( "-X" ).build();
    }

    @Test
    public void buildDeployAndRun() throws Exception {
        File basedir = resources.getBasedir("kjar-1");
        MavenExecutionResult result = mavenRuntime
                .forProject(basedir)
                .execute("clean",
                        "install");

        result.assertErrorFreeLog();
    }
}

