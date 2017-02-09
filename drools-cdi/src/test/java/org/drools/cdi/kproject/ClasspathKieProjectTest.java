/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.cdi.kproject;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;

public class ClasspathKieProjectTest extends AbstractKnowledgeTest {

    private static final String MODULE_JARFILE_NAME = "jar1";
    private static final String MODULE_JARFILE_VERSION = "1.0";
    private static final String MODULE_JARDIR_NAME = "jar2";
    private static final String MODULE_JARDIR_VERSION = "2.0";

    @Test
    public void testParsePomPropertiesFromJarFile() throws Exception {
        createKieModule(MODULE_JARFILE_NAME, true, MODULE_JARFILE_VERSION);
        final File kModuleFile = getFileManager().newFile(MODULE_JARFILE_NAME + "-" + MODULE_JARFILE_VERSION + ".jar");
        final String pomProperties = ClasspathKieProject.getPomProperties( kModuleFile.getAbsolutePath() );
        checkPomProperties(pomProperties, MODULE_JARFILE_NAME, MODULE_JARFILE_VERSION);
    }

    @Test
    public void testParsePomPropertiesFromJarDir() throws Exception {
        createKieModule(MODULE_JARDIR_NAME, false, MODULE_JARDIR_VERSION);
        final File kModuleDir = getFileManager().newFile(MODULE_JARDIR_NAME + "-" + MODULE_JARDIR_VERSION);
        Assertions.assertThat(kModuleDir).isNotNull();
        Assertions.assertThat(kModuleDir).isDirectory();
        kModuleDir.renameTo(new File(kModuleDir.getAbsolutePath() + ".jar"));
        final File kModuleFile = getFileManager().newFile(MODULE_JARDIR_NAME + "-" + MODULE_JARDIR_VERSION + ".jar");
        final String pomProperties = ClasspathKieProject.getPomProperties(kModuleFile.getAbsolutePath());
        checkPomProperties(pomProperties, MODULE_JARDIR_NAME, MODULE_JARDIR_VERSION);
    }

    private void checkPomProperties(final String pomProperties, final String expectedGroupId, final String expectedVersion) {
        Assertions.assertThat(pomProperties).isNotNull();
        final ReleaseId actualReleaseId = ReleaseIdImpl.fromPropertiesString(pomProperties);
        Assertions.assertThat(actualReleaseId.getGroupId()).isEqualTo(expectedGroupId);
        Assertions.assertThat(actualReleaseId.getVersion()).isEqualTo(expectedVersion);
    }

}
