package org.kie.api.builder.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.kie.api.builder.helper.KieModuleDeploymentHelperImpl.KJarResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.builder.helper.KieModuleDeploymentHelperImpl.internalLoadResources;

public class KieModuleDeploymentHelperLoadResourcesTest {

    @Test
    public void testInternalLoadResources() throws Exception {
        List<KJarResource> resources;
        // local
        String path = "/builder/simple_query_test.drl";
        resources = internalLoadResources(path, false);
        assertThat(resources.size()).as(path).isEqualTo(1);
        String content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();

        path = "/builder/test/";
        resources = internalLoadResources(path, true);
        assertThat(resources.size()).as(path).isEqualTo(2);
        content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();

        path = "/builder/";
        resources = internalLoadResources(path, true);
        assertThat(resources.size()).as(path).isEqualTo(1);
        content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();

        // classpath
        path = "META-INF/WorkDefinitions.conf";
        resources = internalLoadResources(path, false);
        assertThat(resources.size()).as(path).isEqualTo(1);
        content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();

        path = "META-INF/plexus/";
        resources = internalLoadResources(path, true);
        assertThat(resources.size()).as(path).isEqualTo(1);
        content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();

        // file
        content = "test file created by " + this.getClass().getSimpleName();

        final String baseTempPath = System.getProperty("java.io.tmpdir");
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tst");
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        
        resources = internalLoadResources(tempFile.getAbsolutePath(), false);
        assertThat(resources.size()).as(path).isEqualTo(1);
        content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();

        File tempDir = new File(baseTempPath + "/" + UUID.randomUUID().toString());
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempFile = new File(tempDir.getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".tst");
        fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        
        resources = internalLoadResources(tempDir.getAbsolutePath(), true);
        assertThat(resources.size()).as(path).isEqualTo(1);
        content = resources.get(0).content;
        assertThat(content != null && content.length() > 10).isTrue();
    }
}
