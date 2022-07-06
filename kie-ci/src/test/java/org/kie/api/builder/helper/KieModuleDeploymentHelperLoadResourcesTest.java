/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.api.builder.helper;

import org.junit.Test;
import org.kie.api.builder.helper.KieModuleDeploymentHelperImpl.KJarResource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.builder.helper.KieModuleDeploymentHelperImpl.internalLoadResources;

public class KieModuleDeploymentHelperLoadResourcesTest {

    @Test
    public void testInternalLoadResources() throws Exception {
        List<KJarResource> resources = null;
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
        assertThat(resources.size()).as(path).isEqualTo(3);
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
