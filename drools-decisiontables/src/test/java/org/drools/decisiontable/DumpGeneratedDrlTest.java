/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.decisiontable;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FileUtils;
import org.drools.util.IoUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.DumpDirOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DumpGeneratedDrlTest {

    private final String DUMMY_DTABLE_CSV_SOURCE = "\"RuleSet\",\"org.drools.decisiontable\",,,\n" +
            ",,,,\n" +
            ",,,,\n" +
            "\"RuleTable agenda-group\",,,,\n" +
            "\"NAME\",\"CONDITION\",\"Lock-On-Active\",\"Auto-Focus\",\"ACTION\"\n" +
            ",,,,\n" +
            ",\"String(this == \"\"$param\"\")\",,,\n" +
            "\"rule names\",\"string for test\",,,\n" +
            "\"lockOnActiveRule\",\"lockOnActiveRule\",\"true\",,\n" +
            "\"autoFocusRule\",\"autoFocusRule\",,\"true\",";

    private File dumpDir;
    private String dumpDirPropOrigValue;

    @Before
    public void setUp() {
        dumpDir = new File("target/drools-dump-dir");
        // delete the dir before test to remove possible leftovers from previous runs
        // deleting the dir before the test and not after also helps with debugging - the dir stays there after
        // the test is executed and the content can be examined
        if (dumpDir.exists()) {
            FileUtils.deleteQuietly(dumpDir);
        }
        dumpDir.mkdirs();
        dumpDirPropOrigValue = System.getProperty(DumpDirOption.PROPERTY_NAME);
        System.setProperty(DumpDirOption.PROPERTY_NAME, dumpDir.getAbsolutePath());
    }

    @After
    public void tearDown() {
        if (dumpDirPropOrigValue != null) {
            System.setProperty(DumpDirOption.PROPERTY_NAME, dumpDirPropOrigValue);
        }
    }


    @Test
    public void testGeneratedDrlFromIsDumpedIfSpecified() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newByteArrayResource(DUMMY_DTABLE_CSV_SOURCE.getBytes(IoUtils.UTF8_CHARSET));
        resource.setSourcePath("some/source/path/dummy-dtable.csv");
        kbuilder.add(resource, ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail("Unexpected Drools compilation errors: " + kbuilder.getErrors().toString());
        }
        assertGeneratedDrlExists(dumpDir, "some_source_path_dummy-dtable.csv.drl");
    }

    @Test
    public void testDTableWithNullSrcPathIsCorrectlyDumped() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newByteArrayResource(DUMMY_DTABLE_CSV_SOURCE.getBytes(IoUtils.UTF8_CHARSET));
        kbuilder.add(resource, ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail("Unexpected Drools compilation errors: " + kbuilder.getErrors().toString());
        }
        assertGeneratedDrlExists(dumpDir, null);
    }

    @Test
    public void testUseReleaseIdInGeneratedDumpForProjectResource() {
        ReleaseId releaseId = KieServices.get().getRepository().getDefaultReleaseId();
        new KieHelper().addContent(DUMMY_DTABLE_CSV_SOURCE, "some/source/path/project-dtable.drl.csv").build();
        assertGeneratedDrlExists(dumpDir, releaseId.getGroupId() + "_" + releaseId.getArtifactId() + "_" + "some_source_path_project-dtable.drl.csv.drl");
    }

    private void assertGeneratedDrlExists(File dumpDir, String expectedFilename) {
        assertThat(dumpDir.exists()).as("Dump dir should exist!").isTrue();
        File[] generatedDrls = dumpDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".drl");
            }
        });
        assertThat(generatedDrls).as("There should be exactly one generated DRL file!").hasSize(1);
        if (expectedFilename != null) {
            assertThat(generatedDrls[0].getName()).as("Unexpected name of the file with generated DRL!").isEqualTo(expectedFilename);
        }
    }
    

}
