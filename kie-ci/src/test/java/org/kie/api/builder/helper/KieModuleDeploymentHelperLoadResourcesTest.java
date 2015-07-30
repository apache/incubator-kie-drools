/*
 * Copyright 2015 JBoss Inc
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.api.builder.helper.KieModuleDeploymentHelperImpl.internalLoadResources;

public class KieModuleDeploymentHelperLoadResourcesTest {

    @Test
    public void testInternalLoadResources() throws Exception {
        List<KJarResource> resources = null;
        // local
        String path = "/builder/simple_query_test.drl";
        resources = internalLoadResources(path, false);
        assertEquals( path, 1, resources.size());
        String content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );

        path = "/builder/test/";
        resources = internalLoadResources(path, true);
        assertEquals( path, 2, resources.size());
        content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );

        path = "/builder/";
        resources = internalLoadResources(path, true);
        assertEquals( path, 1, resources.size());
        content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );

        // classpath
        path = "META-INF/WorkDefinitions.conf";
        resources = internalLoadResources(path, false);
        assertEquals( path, 1, resources.size());
        content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );

        path = "META-INF/plexus/";
        resources = internalLoadResources(path, true);
        assertEquals( path, 3, resources.size());
        content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );

        // file
        content = "test file created by " + this.getClass().getSimpleName();

        final String baseTempPath = System.getProperty("java.io.tmpdir");
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tst");
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        
        resources = internalLoadResources(tempFile.getAbsolutePath(), false);
        assertEquals( path, 1, resources.size());
        content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );

        File tempDir = new File(baseTempPath + "/" + UUID.randomUUID().toString());
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempFile = new File(tempDir.getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".tst");
        fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        
        resources = internalLoadResources(tempDir.getAbsolutePath(), true);
        assertEquals( path, 1, resources.size());
        content = resources.get(0).content;
        assertTrue( content != null && content.length() > 10 );
    }
}
