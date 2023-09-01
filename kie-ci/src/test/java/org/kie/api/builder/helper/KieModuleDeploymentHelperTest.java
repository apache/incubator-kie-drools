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
package org.kie.api.builder.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.drools.core.impl.EnvironmentImpl;
import org.drools.core.test.model.Cheese;
import org.junit.After;
import org.junit.Test;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.maven.integration.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class KieModuleDeploymentHelperTest {

    protected static Logger logger = LoggerFactory.getLogger(KieModuleDeploymentHelperTest.class);
    
    private ZipInputStream zip;

    @After
    public void cleanUp() {
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                // do nothing
            }

        }
    }

    @Test
    public void testSingleDeploymentHelper() throws Exception {
        int numFiles = 0;
        int numDirs = 0;
        SingleKieModuleDeploymentHelper deploymentHelper = KieModuleDeploymentHelper.newSingleInstance();

        List<String> resourceFilePaths = new ArrayList<String>();
        resourceFilePaths.add("builder/test/");
        numFiles += 2;
        resourceFilePaths.add("builder/simple_query_test.drl");
        ++numFiles;

        List<Class<?>> kjarClasses = new ArrayList<Class<?>>();
        kjarClasses.add(KieModuleDeploymentHelper.class);
        numDirs += 5; // org.kie.api.builder.helper
        kjarClasses.add(EnvironmentImpl.class);
        numDirs += 3; // (org.)drools.core.impl
        kjarClasses.add( Cheese.class);
        numDirs += 1; // (org.drools.)compiler
        numFiles += 3;

        String groupId = "org.kie.api.builder";
        String artifactId = "test-kjar";
        String version = "0.1-SNAPSHOT";
        deploymentHelper.createKieJarAndDeployToMaven(groupId, artifactId, version, 
                "defaultKieBase", "defaultKieSession",
                resourceFilePaths, kjarClasses);
        // pom.xml, pom.properties
        numFiles += 2;
        // kmodule.xml, kmodule.info, kbase.cache
        numFiles +=3;
        
        // META-INF/maven/org.kie.api.builder/test-kjar
        numDirs += 4;
        // defaultKiebase, META-INF/defaultKieBase
        numDirs += 2;

        File artifactFile = MavenRepository.getMavenRepository().resolveArtifact(groupId + ":" + artifactId + ":" + version).getFile();
        zip = new ZipInputStream(new FileInputStream(artifactFile));

        Set<String> jarFiles = new HashSet<String>();
        Set<String> jarDirs = new HashSet<String>();
        ZipEntry ze = zip.getNextEntry();
        logger.debug("Getting files from deployed jar: " );
        while( ze != null ) { 
            String fileName = ze.getName();
            if( fileName.endsWith("drl")
                    || fileName.endsWith("class")
                    || fileName.endsWith("xml")
                    || fileName.endsWith("info")
                    || fileName.endsWith("properties")
                    || fileName.endsWith("cache") ) { 
                jarFiles.add(fileName);
                logger.debug("> " + fileName);
            } else { 
                jarDirs.add(fileName);
                logger.debug("] " + fileName);
            }
            ze = zip.getNextEntry();
        }
        assertThat(jarFiles.size()).as("Num files in kjar").isEqualTo(numFiles);
    }

    @Test
    public void testFluentDeploymentHelper() throws Exception {
        int numFiles = 0;
        int numDirs = 0;
        
        FluentKieModuleDeploymentHelper deploymentHelper = KieModuleDeploymentHelper.newFluentInstance();

        String groupId = "org.kie.api.builder.fluent";
        String artifactId = "test-kjar";
        String version = "0.1-SNAPSHOT";
        deploymentHelper = deploymentHelper.setGroupId(groupId)
                .setArtifactId(artifactId)
                .setVersion(version)
                .addResourceFilePath("builder/test/", "builder/simple_query_test.drl")
                .addResourceFilePath("/META-INF/WorkDefinitions.conf") // from the drools-core jar
                .addClass(KieModuleDeploymentHelperTest.class)
                .addClass(KieModule.class)
                .addClass(Cheese.class);
        // class dirs
        numDirs += 5; // org.kie.api.builder.helper
        numDirs += 2; // (org.)drools.compiler
        
        // pom.xml, pom.properties
        numFiles += 3;
        // kmodule.xml, kmodule.info
        numFiles += 2;
        // kbase.cache x 2
        numFiles += 2;
        // drl files
        numFiles += 2;
        // WorkDefinitions
        ++numFiles;
        // classes
        numFiles += 3;
        
        // META-INF/maven/org.kie.api.builder/test-kjar
        numDirs += 4;
        // defaultKiebase, META-INF/defaultKieBase
        numDirs += 2;
        
        KieBaseModel kbaseModel = deploymentHelper.getKieModuleModel().newKieBaseModel("otherKieBase");
        kbaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);
        kbaseModel.newKieSessionModel("otherKieSession").setClockType(ClockTypeOption.REALTIME);
        // META-INF/otherKieBase
        ++numDirs;

        deploymentHelper.getKieModuleModel().getKieBaseModels().get("defaultKieBase").newKieSessionModel("secondKieSession");

        deploymentHelper.createKieJarAndDeployToMaven();

        File artifactFile = MavenRepository.getMavenRepository().resolveArtifact(groupId + ":" + artifactId + ":" + version).getFile();
        zip = new ZipInputStream(new FileInputStream(artifactFile));

        Set<String> jarFiles = new HashSet<String>();
        Set<String> jarDirs = new HashSet<String>();
        ZipEntry ze = zip.getNextEntry();
        logger.debug("Getting files form deployed jar: ");
        while( ze != null ) { 
            String fileName = ze.getName();
            if( fileName.endsWith("drl")
                    || fileName.endsWith("class")
                    || fileName.endsWith("tst")
                    || fileName.endsWith("conf")
                    || fileName.endsWith("xml")
                    || fileName.endsWith("info")
                    || fileName.endsWith("properties")
                    || fileName.endsWith("cache") ) { 
                jarFiles.add(fileName);
                logger.debug("> " + fileName);
            } else { 
                jarDirs.add(fileName);
                logger.debug("] " + fileName);
            }
            ze = zip.getNextEntry();
        }
        assertThat(jarFiles.size()).as("Num files in kjar").isEqualTo(numFiles);
    }
}
